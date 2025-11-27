package ch.awae.binfiles;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryFileRangeIteratorTest {

    @Test
    public void testStandardIterator() {
        BinaryFile file = buildFile();

        // evaluate
        List<@NotNull List<Byte>> fragments = new ArrayList<>();
        var iterator = file.rangeIterator(64);
        while (iterator.hasNext()) {
            fragments.add(iterator.next());
        }

        assertEquals(4, fragments.size());

        assertEquals(64, fragments.get(0).size());
        for (int i = 0; i < 64; i++) {
            assertNull(fragments.getFirst().get(i), "byte[" + i + "] must be null");
        }

        assertEquals(64, fragments.get(1).size());
        for (int i = 0; i < 64; i++) {
            assertEquals((byte) i, fragments.get(1).get(i), "byte[" + (64 + i) + "] must be " + i);
        }

        assertEquals(64, fragments.get(1).size());
        for (int i = 0; i < 32; i++) {
            assertEquals((byte) i, fragments.get(1).get(i), "byte[" + (128 + i) + "] must be " + i);
        }
        for (int i = 32; i < 42; i++) {
            assertNull(fragments.get(2).get(i), "byte[" + (128 + i) + "] must be null");
        }
        assertEquals((byte) 1, fragments.get(2).get(42), "byte[170] must be 1");
        assertEquals((byte) 2, fragments.get(2).get(43), "byte[171] must be 2");
        for (int i = 44; i < 63; i++) {
            assertNull(fragments.get(2).get(i), "byte[" + (128 + i) + "] must be null");
        }
        assertEquals((byte) 3, fragments.get(2).get(63), "byte[191] must be 3");

        assertEquals(64, fragments.get(3).size());
        for (int i = 0; i < 63; i++) {
            assertNull(fragments.get(3).get(i), "byte[" + (192 + i) + "] must be null");
        }
        assertEquals((byte) 1, fragments.get(3).get(63), "byte[255] must be 1");
    }

    private static @NotNull BinaryFile buildFile() {
        BinaryFile file = new BinaryFile(256);

        // "block 1" stays empty
        // fill "block 2" completely
        for (int i = 0; i < 64; i++) {
            file.addByte(64 + i, (byte) i);
        }

        // fill "block 3" with 3 disjoint sections
        for (int i = 0; i < 32; i++) {
            file.addByte(128 + i, (byte) i);
        }
        file.addByte(170, (byte) 1);
        file.addByte(171, (byte) 2);
        file.addByte(191, (byte) 3);

        // fill "block 4" at end only
        file.addByte(255, (byte) 1);
        return file;
    }

}
