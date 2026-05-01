package com.bitgrind.kmp.filestorage.testing

import com.bitgrind.kmp.filestorage.FileStorage

expect suspend fun runFileStorageTest(block: suspend (FileStorage, path: String) -> Unit)
