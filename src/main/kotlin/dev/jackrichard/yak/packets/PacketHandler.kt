package dev.jackrichard.yak.packets

import dev.jackrichard.yak.packets.types.ByteableType
import java.io.OutputStream

typealias SocketOutputStream = OutputStream
typealias PacketHandler = (data: Any, out: SocketOutputStream, logger: (String) -> Unit, packet: Map<ByteableType<*>, Any>) -> Any
