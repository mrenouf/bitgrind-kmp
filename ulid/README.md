# kmp-ulid

A Kotlin Multiplatform implementation of Universally Unique Lexicographically Sortable Identifiers (ULID). Generate compact, URL-safe, sortable IDs across JVM, Android, Native, and web targets.

> **This library implements the ULID spec**: https://github.com/ulid/spec

## Features

- **Lexicographically sortable** — timestamp-prefixed encoding means ULIDs sort chronologically as plain strings
- **Monotonic** — multiple ULIDs generated within the same millisecond increment the random component, preserving strict ordering
- **Cryptographically random** — uses `kotlincrypto` for platform-native secure randomness
- **Compact** — 26-character Crockford Base32 string encoding a 48-bit timestamp + 80-bit random component
- **Case-insensitive** — decodes both upper and lower case

## Usage

### Generate a ULID

```kotlin
import com.bitgrind.kmp.ulid.ulid

val id: String = ulid()
// e.g. "01HW3K2J5R8WVN4FQDPZ6M1B3C"
```

### Decode the timestamp

```kotlin
import com.bitgrind.kmp.ulid.ULID

val timestamp: Long = ULID.decodeTime("01HW3K2J5R8WVN4FQDPZ6M1B3C")  // milliseconds since epoch
```

### Validate a ULID string

```kotlin
import com.bitgrind.kmp.ulid.ULID

val valid: Boolean = ULID.isValid("01HW3K2J5R8WVN4FQDPZ6M1B3C")
```

### Dedicated factory instance

`ULID` is an interface — use `ULID.monotonicFactory()` to create an instance. Each factory tracks its own monotonic state.

```kotlin
import com.bitgrind.kmp.ulid.ULID

val factory: ULID = ULID.monotonicFactory()
val id: String = factory()
```

> **Thread safety**: each factory instance is not thread-safe due to the monotonic state it tracks. Create a separate instance per thread for concurrent use.

### Custom time

Pass an explicit timestamp (milliseconds since epoch) to either the top-level function or a factory:

```kotlin
import com.bitgrind.kmp.ulid.ULID
import com.bitgrind.kmp.ulid.ulid

val id: String = ulid(time = fixedTimestampMs)

val factory: ULID = ULID.monotonicFactory()
val id2: String = factory(time = fixedTimestampMs)
```

### Custom randomness (e.g. for testing)

Supply a custom `random` function to `monotonicFactory` to inject a deterministic random source:

```kotlin
import com.bitgrind.kmp.ulid.ULID

val factory: ULID = ULID.monotonicFactory(random = { buffer -> buffer.fill(42) })
val id: String = factory()
```

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

| Library              | Version | Purpose                           |
|----------------------|---------|-----------------------------------|
| Kotlin Multiplatform | 2.3.20  | Language & multiplatform tooling  |
| kotlincrypto/random  | 0.6.0   | Platform-native secure randomness |

## License

[MIT License](../LICENSE.txt)