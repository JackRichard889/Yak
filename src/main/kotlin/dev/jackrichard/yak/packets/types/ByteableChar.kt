package dev.jackrichard.yak.packets.types

import dev.jackrichard.yak.packets.Packet

/**
 * Char that can be encoded to bytes for packet transfer. Takes exactly one byte. If decode is given more than one byte, only the first will be processed.
 */
class ByteableChar : ByteableType<Char>() {
    override val length: Int = 1
    override fun decode(bytes: List<Byte>): Char = bytes.first().toInt().toChar()
    override fun encode(data: Any): List<Byte> = listOf((data as Char).code.toByte())
}

fun Packet.char() = ByteableChar().also { segments.add(it) }