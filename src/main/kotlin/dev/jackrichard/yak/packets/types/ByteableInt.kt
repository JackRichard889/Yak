package dev.jackrichard.yak.packets.types

import java.nio.ByteBuffer

/**
 * Int that can be encoded to bytes for packet transfer. Takes 4 bytes of space.
 */
class ByteableInt : ByteableType<Int>() {
    override val length: Int = 4
    override fun decode(bytes: List<Byte>): Int = ByteBuffer.wrap(bytes.toByteArray()).int
    override fun encode(data: Any): List<Byte> = ByteBuffer.allocate(4).also { it.putInt(data as Int) }.array().toList()
}