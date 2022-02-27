package dev.jackrichard.yak.packets

import dev.jackrichard.yak.packets.types.*
import java.nio.ByteBuffer

/**
 * Generic packet class. Packet stores data in segments that have fixed lengths, so the packet can be reconstructed on the client/server side after transfer.
 * Not to be used directly. Use DSL function, packet, to create instances of packet class.
 */
open class Packet {
    private val segments: MutableList<ByteableType<*>> = mutableListOf()
    private lateinit var handler: PacketHandler
    var id: Int = -1

    fun length() : Int {
        var length = 0
        segments.forEach { length += it.length }
        return length
    }

    fun string(length: Int = 16) = ByteableString(length).also { segments.add(it) }
    fun char() = ByteableChar().also { segments.add(it) }
    fun int() = ByteableInt().also { segments.add(it) }
    fun boolean() = ByteableBoolean().also { segments.add(it) }

    fun handle(func: PacketHandler) { handler = func }
    fun encode(vararg segs: Any) : List<Byte> = mutableListOf<Byte>().also { list ->
        list.addAll(ByteBuffer.allocate(4).also { it.putInt(id) }.array().toList())
        segments.zip(segs).forEach { list.addAll(it.first.encode(it.second)) }
    }
    fun decode(data: Any, out: SocketOutputStream, logger: (String) -> Unit = { }, array: ByteArray, doneHandling: PacketHandler = handler) : Any {
        var currentBuffer = array.toList()
        val dataSegs = mutableListOf<Any>()

        segments.forEach {
            it.decode(currentBuffer.take(it.length)).also { dat -> dataSegs.add(dat!!) }
            currentBuffer = currentBuffer.drop(it.length)
        }

        return doneHandling(data, out, logger, segments.zip(dataSegs).toMap())
    }
}

/**
 * DSL packet creation.
 *
 * Set the packet id using `id = 1`
 * Store packet segments in immutable variables, declared as follows:
 *
 * `val name = string(length = 16)`
 *
 * Use the handle DSL afterwards to define the action to be taken when the packet is received.
 *
 * `handle {
 *
 * }`
 */
fun packet(block: Packet.() -> Unit) : Packet = Packet().also { it.block() }