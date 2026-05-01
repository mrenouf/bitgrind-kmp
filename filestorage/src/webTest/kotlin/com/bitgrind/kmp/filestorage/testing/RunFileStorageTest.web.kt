@file:OptIn(ExperimentalWasmJsInterop::class)

package com.bitgrind.kmp.filestorage.testing

import com.bitgrind.kmp.filestorage.FileStorage
import com.bitgrind.kmp.filestorage.impl.OpfsFileStorage
import com.bitgrind.kmp.filestorage.impl.fileSystemGetDirectoryOptions
import com.bitgrind.kmp.filestorage.impl.fileSystemRemoveOptions
import com.bitgrind.kmp.filestorage.tempName
import web.fs.getDirectoryHandle
import web.fs.removeEntry
import web.navigator.navigator
import web.storage.getDirectory
import kotlin.js.ExperimentalWasmJsInterop

/**
 * Obtains the OPFS root, runs [block] with a scoped [OpfsFileStorage], then
 * recursively removes all entries created during the test.
 * Skips silently if OPFS is not available in the current environment.
 */
actual suspend fun runFileStorageTest(block: suspend (FileStorage, path: String) -> Unit) {
    val storageManager = try {
        navigator.storage
    } catch (_: Exception) {
        println("OPFS not available — skipping")
        return
    }
    val root = storageManager.getDirectory()
    val storage: FileStorage = OpfsFileStorage(storage = storageManager)

    val testRoot = storage.tempName(path = "/", prefix = "filestorage-test-").substringAfter('/')
    root.getDirectoryHandle(testRoot, fileSystemGetDirectoryOptions(create = true))
    try {
        block(storage, testRoot)
    } finally {
        root.removeEntry(testRoot, fileSystemRemoveOptions(recursive = true))
    }
}
