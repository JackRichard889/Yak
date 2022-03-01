import dev.jackrichard.yak.Yak
import dev.jackrichard.yak.net.ClientContext
import dev.jackrichard.yak.packets.Packets
import dev.jackrichard.yak.packets.types.boolean
import dev.jackrichard.yak.packets.types.char
import dev.jackrichard.yak.packets.types.int
import dev.jackrichard.yak.packets.types.string
import kotlin.concurrent.thread

var ClientContext.name: String
    get() = ""
    set(value) {}

fun main() {
    Yak.config {
        register {
            id = 101

            val name = string(50)

            handle {
                val dat = Packets.find(1002).encode("Hello world!").toByteArray()
                outputStream.write(dat).also { log.info("Sending packet 102.") }

                this.name = "Hello"
            }
        }

        register {
            id = 102

            val b = boolean()
            val i = int()
            val s = string()
            val c = char()
        }
    }

    Yak.start(port = 2424)
}