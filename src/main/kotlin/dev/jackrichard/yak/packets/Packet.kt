package dev.jackrichard.yak.packets

import dev.jackrichard.yak.net.ClientContext
import dev.jackrichard.yak.packets.types.*
import java.nio.ByteBuffer

/**
 * Generic packet class. Packet stores data in segments that have fixed lengths, so the packet can be reconstructed on the client/server side after transfer.
 * Not to be used directly. Use DSL function, packet, to create instances of packet class.
 */
open class Packet {
    internal val segments: MutableList<ByteableType<*>> = mutableListOf()
    private lateinit var handler: ClientContext.() -> Unit

    /**
     * A packet identifier must be unique from any other packet identifier defined in this Yak instance.
     * The default is -1, but do not use this.
     */
    var id: Int = -1

    /**
     * The packet length is determined by a sum of the bytes needed for each data type defined.
     *
     * @return packet size in bytes
     */
    fun length() : Int = segments.sumOf { it.length }

    /**
     * Defines the handler that will receive the active context and packet data when this specific packet type is received.
     *
     * @param func ClientContext DSL block
     */
    fun handle(func: ClientContext.() -> Unit) { handler = func }

    /**
     * Encodes data passed as arguments to the structure of the packet. This data *must* be in the order that the segments are defined in.
     *
     * @param segs the packet data to be encoded
     *
     * @return an encoded ByteArray
     */
    fun encode(vararg segs: Any) : ByteArray = mutableListOf<Byte>().also { list ->
        list.addAll(ByteBuffer.allocate(4).also { it.putInt(id) }.array().toList())
        segments.zip(segs).forEach { list.addAll(it.first.encode(it.second)) }
    }.toByteArray()

    /**
     * Decodes the provided ByteArray and passes the data to the current handler for that packet type.
     *
     * @param array an array of bytes
     */
    fun decode(array: ByteArray) : ClientContext.() -> Unit {
        var currentBuffer = array.toList()
        val dataSegs = mutableListOf<Any>()

        segments.forEach {
            it.decode(currentBuffer.take(it.length)).also { dat -> dataSegs.add(dat!!) }
            currentBuffer = currentBuffer.drop(it.length)
        }

        return handler
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