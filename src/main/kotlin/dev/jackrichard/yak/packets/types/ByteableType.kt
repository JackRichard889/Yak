package dev.jackrichard.yak.packets.types

/**
 * An abstract type that can be encoded to bytes for data transfer in packets.
 */
abstract class ByteableType<E> {
    abstract val length: Int
    abstract fun encode(data: Any) : List<Byte>
    abstract fun decode(bytes: List<Byte>) : E
}