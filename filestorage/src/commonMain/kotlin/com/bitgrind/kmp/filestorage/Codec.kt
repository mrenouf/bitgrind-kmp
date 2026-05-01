package com.bitgrind.kmp.filestorage

/**
 * Combines both a [Encoder] of [V] and a [Decoder] to [V]
 */
interface Codec<V> : Encoder<V>, Decoder<V>

/**
 * Knows how to encode a value of type [V] into a [ByteWriter].
 */
fun interface Encoder<V> {
    suspend fun encode(value: V, writer: ByteWriter)
}

/**
 * Knows how to decode a value of type [V] from a [ByteReader].
 */
fun interface Decoder<V> {
    suspend fun decode(reader: ByteReader): V
}
