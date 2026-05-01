package com.bitgrind.kmp.filestorage

import com.bitgrind.kmp.filestorage.impl.MultiPlatformFileStorage

@Suppress("unused")
actual fun getFileStorage(): FileStorage = MultiPlatformFileStorage()
