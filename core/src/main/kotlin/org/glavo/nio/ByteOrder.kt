package org.glavo.nio

import java.nio.ByteOrder

val BigEndian: ByteOrder = ByteOrder.BIG_ENDIAN
val LittleEndian: ByteOrder = ByteOrder.LITTLE_ENDIAN

fun ByteOrder.reverse(): ByteOrder {
    return when (this) {
        ByteOrder.BIG_ENDIAN -> ByteOrder.LITTLE_ENDIAN
        ByteOrder.LITTLE_ENDIAN -> ByteOrder.BIG_ENDIAN
        else -> throw IllegalArgumentException("unknown byte order")
    }
}