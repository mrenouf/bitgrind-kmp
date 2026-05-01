package com.bitgrind.kmp.filestorage

import com.bitgrind.kmp.filestorage.impl.OpfsFileStorage
import web.navigator.navigator

@Suppress("unused")
actual fun getFileStorage(): FileStorage = OpfsFileStorage(storage = navigator.storage)
