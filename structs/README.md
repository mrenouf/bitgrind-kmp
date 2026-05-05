# structs
![Maven Central Version](https://img.shields.io/maven-central/v/com.bitgrind.kmp/structs)

Kotlin Multiplatform data structures implemented as zero-overhead value classes.

## BitSet

A 64-bit bitset backed by a `Long`. All operations are inlined.

```kotlin
import com.bitgrind.kmp.structs.BitSet

var flags = BitSet()
flags = flags.set(3)
flags = flags.set(7)
println(3 in flags)   // true
println(flags.size)   // 2
flags.forEach { index -> println(index) }  // 3, 7
```

### API

| Operation | Description |
|-----------|-------------|
| `BitSet()` | Empty bitset (`0L`) |
| `BitSet.ONES` | All bits set |
| `BitSet.ones(n)` | Lowest `n` bits set |
| `set(index)` | Returns copy with bit set |
| `clear(index)` | Returns copy with bit cleared |
| `toggle(index)` | Returns copy with bit toggled |
| `contains(index)` / `get(index)` | Test a bit (`index in flags`) |
| `and(other)` / `or(other)` / `xor(other)` / `not()` | Bitwise operations |
| `size` | Count of set bits |
| `isEmpty()` / `isNotEmpty()` | Check if any bits are set |
| `forEach(block)` | Iterate over indices of set bits |

## Adding to Your Project

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.bitgrind.kmp:structs:0.1.0")
        }
    }
}
```

## License

[MIT License](../LICENSE.txt)