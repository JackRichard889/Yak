import dev.jackrichard.yak.Yak
import dev.jackrichard.yak.packets.Packets
import dev.jackrichard.yak.packets.types.string
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.concurrent.thread

fun main() {
    print("Enter a message to relay: ")
    val message = readLine()!!

    Yak.config {
        register {
            id = 101

            val name = string()

            handle {
                sendPacket(id = 102, "Hello, $name!")
            }
        }

        register {
            id = 102

            val messaged = string()

            handle {
                println(messaged)
            }
        }
    }

    thread {
        // Wait for server to be started, just in case.
        Thread.sleep(4000)

        // Connect to the socket.
        val socket = Socket("localhost", 2424)

        val out = socket.getOutputStream()
        val inp = socket.getInputStream()

        // Send packet with name data.
        out.write(Packets.find(101).encode(message))

        // Start listening loop.
        while (socket.isConnected) {
            val packetID = inp.readNBytes(4)
            Packets.find(ByteBuffer.wrap(packetID).int).also {
                // Decode and handle received packets.
                it.decode(inp.readNBytes(it.length()))
            }
        }
    }

    Yak.start(port = 2424)
}