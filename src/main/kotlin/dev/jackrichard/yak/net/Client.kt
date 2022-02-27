package dev.jackrichard.yak.net

import dev.jackrichard.yak.Yak
import dev.jackrichard.yak.packets.Packets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket
import java.nio.ByteBuffer
import java.util.logging.Logger
import kotlin.reflect.full.createInstance

suspend fun serveClient(identifier: Int, clientSocket: Socket) {
    val logger = Logger.getLogger("[T-$identifier]")
    var persistentData = Yak.configuration.sessionDataType.createInstance()

    logger.info("Thread created! Opening streams...")

    val outputStream = withContext(Dispatchers.IO) {
        clientSocket.getOutputStream()
    }
    val inputStream = withContext(Dispatchers.IO) {
        clientSocket.getInputStream()
    }

    Delegator.incrementConnection()

    logger.info("Input and output streams opened.")

    while (clientSocket.isConnected) {
        val packetID = withContext(Dispatchers.IO) {
            inputStream.readNBytes(4)
        }
        if (packetID.size != 4) {
            logger.info("Input stream has been closed!")

            withContext(Dispatchers.IO) { clientSocket.close() }
            break
        }

        Packets.find(ByteBuffer.wrap(packetID).int.also { logger.info("Received packet $it.") }).also {
            try {
                persistentData = it.decode(
                    persistentData,
                    outputStream,
                    logger::info,
                    inputStream.readNBytes(it.length())
                )
            } catch (e: Exception) {
                logger.severe(e.localizedMessage.toString())
                clientSocket.close()
            }
        }
    }

    Delegator.decrementConnection()
    Delegator.releaseIdentifier(identifier)
    logger.info("Thread lifecycle ended.")
}