package dev.jackrichard.yak.packets.types

import dev.jackrichard.yak.packets.Packet

/**
 * Boolean that can be encoded to bytes for packet transfer. Takes 1 byte of space. Either 0x01 for true or 0x00 for false.
 */
class ByteableBoolean : ByteableType<Boolean>() {
    override val length: Int = 1
    override fun decode(bytes: List<Byte>): Boolean = (bytes.first() == (0x01).toByte())
    override fun encode(data: Any): List<Byte> = listOf(if (data as Boolean) 0x01 else 0x00)
}

fun Packet.boolean() = ByteableBoolean().also { segments.add(it) }