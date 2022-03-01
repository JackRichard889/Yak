package dev.jackrichard.yak

import dev.jackrichard.yak.net.Delegator
import dev.jackrichard.yak.net.serveClient
import dev.jackrichard.yak.packets.Packet
import dev.jackrichard.yak.packets.Packets
import kotlinx.coroutines.*
import java.net.ServerSocket
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Main class for communicating with the Yak service.
 * Yak configuration is set using the `config {}` block.
 * Run the `start()` function with the optional port parameter to open the socket and start the blocking service.
 */
object Yak {
    private var configuration = Config()
    private val logger = Logger.getLogger("SERVER")

    /**
     * This class is meant to be used via the DSL `config {}` block.
     * Stores the database configuration and session data that will be stored with each client connection.
     */
    class Config {
        fun register(block: Packet.() -> Unit) {
            val packet = Packet().also { it.block() }
            Packets.register(packet.id, packet)
        }
    }

    /**
     * Stores the database configuration and session data that will be stored with each client connection.
     * Also registers server packets through the DSL `packets {}` block.
     * Packets must have a unique ID set, via `id = 120` in the `packet {}` block.
     *
     * @param init accepts a Yak.Config block.
     */
    fun config(init: Config.() -> Unit) { configuration = Config().also(init) }

    /**
     * Blocking call to start SocKit services. Optional port parameter, default is 2424.
     *
     * @param port the port for the socket server. Default is 12345.
     */
    fun start(port: Int = 12345) {
        Yak.logger.info("Opening socket on port $port...")
        val serverSocket = ServerSocket(port)

        Yak.logger.info("Starting thread delegator service...")
        runBlocking {
            Delegator.logger.info("Started.")
            while (!Delegator.isHalted()) {
                if (Delegator.getCurrentConnections() >= Delegator.MAX_CONNECTIONS) {
                    Delegator.logger.info("Max connections reached! Freezing new clients...")
                    while (Delegator.getCurrentConnections() >= Delegator.MAX_CONNECTIONS) {
                        Delegator.logger.info("Freezing...")
                        delay(200)
                    }
                    Delegator.logger.info("Unfreezing delegate!")
                }

                withContext(Dispatchers.IO) {
                    serverSocket.accept()
                        .also { Delegator.logger.info("Accepted client connection, opening thread...") }
                }.also {
                    launch {
                        serveClient(Delegator.generateIdentifier(), it)
                    }
                }
            }
        }
    }
}