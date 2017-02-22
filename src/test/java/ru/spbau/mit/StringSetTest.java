package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringSetTest {

    private String padRightString(String unpadded, int length, char pad) {
        String padded = unpadded;

        while (padded.length() < length) {
            padded += pad;
        }

        return padded;
    }

    @Test
    public void testSimple() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.contains("abc"));
        assertEquals(1, stringSet.size());
        assertEquals(1, stringSet.howManyStartsWithPrefix("abc"));
    }

    @Test
    public void testPrefix() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("working"));
        assertEquals(1, stringSet.howManyStartsWithPrefix("work"));
        assertTrue(stringSet.remove("working"));
        assertEquals(0, stringSet.howManyStartsWithPrefix("work"));
    }

    @Test
    public void testNonExistentStrings() {
        StringSet stringSet = instance();

        assertFalse(stringSet.contains("abacaba"));
        assertFalse(stringSet.contains(""));
        assertTrue(stringSet.add("ILoveJava"));
        assertEquals(0, stringSet.howManyStartsWithPrefix("nonexistentPrefix"));
    }

    @Test
    public void testBigPadding() {
        StringSet stringSet = instance();
        final int padLength = 100500;
        final int addedStrings = 3;

        assertTrue(stringSet.add(padRightString("qwerty", padLength, 'a')));
        assertTrue(stringSet.add(padRightString("BinGOBinGO", padLength, 'a')));
        assertTrue(stringSet.add(padRightString("abacadabacaba", padLength, 'a')));
        assertEquals(addedStrings, stringSet.howManyStartsWithPrefix("aaaaaa"));
    }

    @Test
    public void testAddRemoveBinaryStrings() {
        StringSet stringSet = instance();
        final int maxBinaryLength = 13;

        for (int mask = 0; mask < (1 << maxBinaryLength); mask++) {
            String unpadded = Integer.toBinaryString(mask);

            assertTrue(stringSet.add(padRightString(unpadded, maxBinaryLength, '0')));
        }
        assertEquals(1 << maxBinaryLength, stringSet.size());

        for (int mask = 0; mask < (1 << maxBinaryLength); mask++) {

            // Some strings may repeat without padding.
            assertTrue(stringSet.contains(Integer.toBinaryString(mask)));
        }

        String prefix = "";
        for (int prefixLength = 0; prefixLength <= maxBinaryLength; prefixLength++) {
            assertEquals(1 << (maxBinaryLength - prefixLength), stringSet.howManyStartsWithPrefix(prefix));
        }

        for (int mask = 0; mask < (1 << maxBinaryLength); mask++) {
            String unpadded = Integer.toBinaryString(mask);

            assertTrue(stringSet.remove(padRightString(unpadded, maxBinaryLength, '0')));
            assertEquals((1 << maxBinaryLength) - mask - 1, stringSet.size());
        }

        for (int mask = 0; mask < (1 << maxBinaryLength); mask++) {
            assertFalse(stringSet.contains(Integer.toBinaryString(mask)));
        }
    }

    @Test
    public void testAddSize() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("xyz"));
        assertFalse(stringSet.add("xyz"));
        assertEquals(1, stringSet.size());
        assertTrue(stringSet.add(""));
        assertEquals(2, stringSet.size());
        assertEquals(2, stringSet.howManyStartsWithPrefix(""));
    }

    @Test
    public void testRemovePrefix() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("AbCDeF"));
        assertEquals(1, stringSet.howManyStartsWithPrefix("AbC"));
        assertTrue(stringSet.add("Abxyz"));
        assertEquals(1, stringSet.howManyStartsWithPrefix("AbC"));
        assertEquals(2, stringSet.howManyStartsWithPrefix("Ab"));
        assertTrue(stringSet.remove("Abxyz"));
        assertEquals(1, stringSet.howManyStartsWithPrefix("Ab"));
        assertTrue(stringSet.remove("AbCDeF"));
        assertEquals(0, stringSet.howManyStartsWithPrefix("Ab"));
    }

    @Test
    public void testDoubleAddPrefix() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abcd"));
        assertTrue(stringSet.add("ab"));
        assertEquals(2, stringSet.howManyStartsWithPrefix("a"));
        assertFalse(stringSet.add("ab"));
        assertEquals(2, stringSet.howManyStartsWithPrefix("a"));
    }

    @Test
    public void testUpperLowerCase() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.add("Abc"));
        assertEquals(2, stringSet.size());
    }

    public static StringSet instance() {
        try {
            return (StringSet) Class.forName("ru.spbau.mit.StringSetImpl").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Error while class loading");
    }
}
