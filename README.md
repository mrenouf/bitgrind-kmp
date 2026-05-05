# bitgrind-kmp

A collection of Kotlin Multiplatform libraries targeting JVM, Android, Native, and web (JS/WASM) platforms. All libraries are published to Maven Central under the `com.bitgrind.kmp` group.

## Libraries

| Library | Version | Description                                                                                                                                             |
|---------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| [ulid](ulid/README.md) | `0.1.0` | Lexicographically sortable, monotonic, cryptographically random IDs — 26-char Crockford Base32 encoding of a 48-bit timestamp + 80-bit random component |
| [structs](structs/README.md) | `0.1.0` | Zero-overhead data structures; currently a 64-bit inline `BitSet` with full bitwise ops and set-bit iteration                                           |
| [filestorage](filestorage/README.md) | `0.1.3` | Unified async file I/O API with native browser support using OPFS.                                                                                      |

## Platform Support

All libraries share the same target matrix:

| Platform | Target |
|----------|--------|
| JVM 21 | `jvm` |
| Android (minSdk 28) | `androidTarget` |
| Linux x86-64 | `linuxX64` |
| JavaScript / Browser | `js` (ES2015 modules + TypeScript definitions) |
| WebAssembly / Browser | `wasmJs` (ES2015 + TypeScript definitions) |

## Getting started

**`build.gradle.kts`**
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.bitgrind.kmp:ulid:0.1.0")
            implementation("com.bitgrind.kmp:structs:0.1.0")
            implementation("com.bitgrind.kmp:filestorage:0.1.3")
        }
    }
}
```

## License

[MIT License](LICENSE.txt)