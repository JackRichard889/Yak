import dev.jackrichard.yak.Yak
import dev.jackrichard.yak.packets.Packets

fun main() {
    Yak.config {
        register {
            id = 1001

            val name = string(10)
            val age = int()
            val terms = boolean()
            val gender = char()

            handle { data, out, log, packet ->
                val dat = Packets.find(1002).encode(packet[name]!!).toByteArray()
                out.write(dat).also { log("Sending packet 1002.") }

                return@handle data
            }
        }

        register {
            id = 1002

            val name = string(10)
        }
    }.also { Yak.start(port = 2424) }
}