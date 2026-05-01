package com.bitgrind.kmp.filestorage.testing

import com.bitgrind.kmp.filestorage.ByteReader
import com.bitgrind.kmp.filestorage.impl.SourceReader
import kotlinx.io.buffered

actual fun byteReaderOf(vararg chunks: ByteArray): ByteReader {
    return SourceReader(ChunkedRawSource(chunks.toList()).buffered())
}