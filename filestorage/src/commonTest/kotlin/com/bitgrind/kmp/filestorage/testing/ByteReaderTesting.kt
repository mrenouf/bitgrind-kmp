package com.bitgrind.kmp.filestorage.testing

import com.bitgrind.kmp.filestorage.ByteReader

expect fun byteReaderOf(vararg chunks: ByteArray): ByteReader