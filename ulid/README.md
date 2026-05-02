# kmp-ulid

A Kotlin Multiplatform implementation of Universally Unique Lexicographically Sortable Identifiers (ULID). Generate compact, URL-safe, sortable IDs across JVM, Android, Native, and web targets.

> **This library implements the ULID spec**: https://github.com/ulid/spec

## Features

- **Lexicographically sortable** — timestamp-prefixed encoding means ULIDs sort chronologically as plain strings
- **Monotonic** — multiple ULIDs generated within the same millisecond increment the random component, preserving strict ordering
- **Cryptographically random** — uses `kotlincrypto` for platform-native secure randomness
- **Compact** — 26-character Crockford Base32 string encoding a 48-bit timestamp + 80-bit random component
- **Case-insensitive** — decodes both upper and lower case

## Platform Support

| Platform     | Randomness Source          |
|--------------|---------------------------|
| JVM          | `java.security.SecureRandom` |
| Android      | `java.security.SecureRandom` |
| Native       | Platform secure random     |
| JS/Browser   | `crypto.getRandomValues`   |
| WASM/Browser | `crypto.getRandomValues`   |

## Usage

### Generate a ULID

```kotlin
val id: String = ULID.create()
// e.g. "01hw3k2j5r8wvn4fqdpz6m1b3c"
```

### Decode a ULID

```kotlin
val values: ULID.Values = ULID.decode("01hw3k2j5r8wvn4fqdpz6m1b3c")

val timestamp: Long = values.timestamp   // milliseconds since epoch
val randomness: ByteArray = values.randomness  // 10 bytes of randomness

values.encode() // round-trip back to string
```

### Parse from constructor

```kotlin
val values = ULID.Values("01hw3k2j5r8wvn4fqdpz6m1b3c")
```

### Custom instance (e.g. for testing)

`ULID` is a `sealed class` — subclass it to inject a fixed clock or deterministic random source:

```kotlin
val ulid = object : ULID(
    now = { fixedTimestampMs },
    random = { buffer -> buffer.fill(42) }
) {}

val id = ulid.create()
```

> **Thread safety**: the default `ULID.Default` instance is not thread-safe due to the monotonic state it tracks. Create a separate instance per thread for concurrent use.

## Adding to Your Project

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.bitgrind.kmp:ulid:0.1.0")
        }
    }
}
```

## Dependencies

| Library              | Version | Purpose                          |
|----------------------|---------|----------------------------------|
| Kotlin Multiplatform | 2.3.20  | Language & multiplatform tooling |
| kotlincrypto/random  | 0.6.0   | Platform-native secure randomness |

## License

Apache License 2.0
