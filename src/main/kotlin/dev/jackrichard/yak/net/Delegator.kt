package dev.jackrichard.yak.net

import java.util.ArrayList
import java.util.logging.Logger
import kotlin.random.Random

object Delegator {
    internal val logger = Logger.getLogger("DELEGATE")

    private val identifiers: ArrayList<Int> = arrayListOf()
    private var currentConnections: Int = 0
    private var stopSignal: Boolean = false
    const val MAX_CONNECTIONS: Int = 200

    fun getCurrentConnections() : Int = currentConnections
    fun isHalted() : Boolean = stopSignal
    fun incrementConnection() = currentConnections++
    fun decrementConnection() = currentConnections--
    fun halt() { stopSignal = true }
    fun generateIdentifier() : Int {
        var identifier = Random.nextInt(0, MAX_CONNECTIONS)
        while (identifier in identifiers) {
            identifier = Random.nextInt(0, MAX_CONNECTIONS)
        }
        return identifier
    }
    fun releaseIdentifier(id: Int) { identifiers.remove(id) }
}