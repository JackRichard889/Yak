package dev.jackrichard.yak.net

import dev.jackrichard.yak.packets.Packets
import java.io.OutputStream
import java.util.logging.Logger

/**
 * The ClientContext class holds the Logger object and OutputStream allocated for a specific client device.
 *
 * Use extension methods and properties to add data to the ClientContext object.
 */
class ClientContext(private val outputStream: OutputStream, val log: Logger) {
    fun sendPacket(id: Int, vararg args: Any) =
        outputStream.write(Packets.find(id).encode(args))
}