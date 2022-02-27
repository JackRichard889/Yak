package dev.jackrichard.yak.packets

/**
 * Object to manage registering packets using a unique identifier.
 */
internal object Packets {
    private val packets : MutableMap<Int, Packet> = mutableMapOf()

    fun find(id: Int) : Packet = packets[id]!!
    fun register(id: Int, packet: Packet) = packets.put(id, packet)
}