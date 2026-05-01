package com.bitgrind.structs

import kotlin.jvm.JvmInline

@Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")
@JvmInline
value class BitSet(val bits: Long = 0L) {

    companion object {
        val ONES = BitSet(-1L)
        fun ones(bits: Int) = BitSet((1L shl bits) - 1)
    }

    inline fun isEmpty(): Boolean = bits == 0L
    inline fun isNotEmpty(): Boolean = bits != 0L
    inline operator fun contains(index: Int): Boolean = bits and (1L shl index) != 0L
    inline operator fun get(index: Int): Boolean = bits and (1L shl index) != 0L
    inline fun set(index: Int): BitSet = BitSet(bits or (1L shl index))
    inline fun clear(index: UByte): BitSet = BitSet(bits and (1L shl index.toInt()).inv())
    inline fun clear(index: Int): BitSet = BitSet(bits and (1L shl index).inv())
    inline fun toggle(index: Int): BitSet = BitSet(bits xor (1L shl index))
    inline infix fun and(other: BitSet): BitSet = BitSet(bits and other.bits)
    inline infix fun or(other: BitSet): BitSet = BitSet(bits or other.bits)
    inline infix fun xor(other: BitSet): BitSet = BitSet(bits xor other.bits)
    inline fun not(): BitSet = BitSet(bits.inv())
    inline val size: Int
        get() = bits.countOneBits()

    override fun toString(): String {
        return "BitSet(${bits.toULong().toString(2).padStart(ULong.SIZE_BITS, '0')})"
    }

    inline fun forEach(block: (Int) -> Unit) {
        if (bits == 0L) return
        // copy to mutate
        var bits = bits
        var pos = 0
        while (bits != 0L) {
            if (bits and 1 == 1L) {
                block(pos)
            }
            bits = bits shr 1
            pos++
        }
    }
}