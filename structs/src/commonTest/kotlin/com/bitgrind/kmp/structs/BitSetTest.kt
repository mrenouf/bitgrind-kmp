package com.bitgrind.kmp.structs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitSetTest {

    @Test
    fun `test empty bitset operations`() {
        val empty = BitSet()
        assertTrue(empty.isEmpty())
        assertFalse(empty.isNotEmpty())
        assertEquals(0, empty.size)
    }

    @Test
    fun `test set and get operations`() {
        var bs = BitSet()
        bs = bs.set(0)
        bs = bs.set(3)
        bs = bs.set(63)

        assertTrue(bs[0])
        assertTrue(bs[3])
        assertTrue(bs[63])
        assertFalse(bs[1])
        assertFalse(bs[2])
        assertEquals(3, bs.size)
    }

    @Test
    fun `test clear operation`() {
        var bs = BitSet()
        bs = bs.set(5).set(10)
        assertTrue(bs[5])
        assertTrue(bs[10])

        bs = bs.clear(5)
        assertFalse(bs[5])
        assertTrue(bs[10])
    }

    @Test
    fun `test toggle operation`() {
        var bs = BitSet()
        bs = bs.toggle(1)
        assertTrue(bs[1])

        bs = bs.toggle(1)
        assertFalse(bs[1])
    }

    @Test
    fun `test bitwise operations`() {
        val bs1 = BitSet().set(1).set(2)
        val bs2 = BitSet().set(2).set(3)

        val andResult = bs1 and bs2
        assertTrue(andResult[2])
        assertFalse(andResult[1])
        assertFalse(andResult[3])

        val orResult = bs1 or bs2
        assertTrue(orResult[1])
        assertTrue(orResult[2])
        assertTrue(orResult[3])

        val xorResult = bs1 xor bs2
        assertTrue(xorResult[1])
        assertFalse(xorResult[2])
        assertTrue(xorResult[3])
    }

    @Test
    fun `test contains operation`() {
        val bs = BitSet().set(1).set(2)
        assertTrue(1 in bs)
        assertTrue(2 in bs)
        assertFalse(3 in bs)
    }

    @Test
    fun `test not operation`() {
        val bs = BitSet().set(0)
        val notResult = bs.not()
        assertFalse(notResult[0])
        assertTrue(notResult[1])
        assertTrue(notResult[63])
    }

    @Test
    fun `test iterator`() {
        val bs = BitSet().set(1).set(3).set(5)
        assertEquals(0b101010, bs.bits)
    }

    @Test
    fun `test ones function`() {
        val nine = BitSet.ones(9)
        assertEquals(0b111111111, nine.bits)
    }

    @Test
    fun `test toString format`() {
        val bs = BitSet().set(0).set(63)
        val str = bs.toString()
        assertEquals("BitSet(1" + "0".repeat(62) + "1)", str)
    }
}