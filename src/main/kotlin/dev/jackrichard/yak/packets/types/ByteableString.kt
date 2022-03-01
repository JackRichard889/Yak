package dev.jackrichard.yak.packets.types

import dev.jackrichard.yak.packets.Packet

/**
 * String that can be encoded to bytes for packet transfer. Length parameter determines maximum and minimum length. If the string is shorter than the length, the byte 0x00 will fill the array until the end point. If it is longer, the string will be cut off.
 */
class ByteableString(override val length: Int) : ByteableType<String>() {
    override fun decode(bytes: List<Byte>): String = String(bytes.takeWhile { it != (0x00).toByte() }.toByteArray())
    override fun encode(data: Any): List<Byte> {
        val str = data as String

        val byteRes = ByteArray(length) { 0x00 }
        str.substring(0, length.coerceAtMost(str.length))
            .toByteArray()
            .zip(0..length.coerceAtMost(str.length))
            .forEach {
                byteRes[it.second] = it.first
            }

        return byteRes.toList()
    }
}

fun Packet.string(length: Int = 16) = ByteableString(length).also { segments.add(it) }