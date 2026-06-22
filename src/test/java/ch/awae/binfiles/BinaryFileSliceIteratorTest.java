package ch.awae.binfiles;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BinaryFileSliceIteratorTest {

    @Test
    void testStandardIterator() {
        BinaryFile file = buildFile();

        List<DataSlice> slices = file.slices(64).toList();

        assertEquals(4, slices.size());

        assertEquals(64, slices.get(0).getSize());
        for (int i = 0; i < 64; i++) {
            assertNull(slices.getFirst().getData().get(i), "byte[" + i + "] must be null");
        }

        assertEquals(64, slices.get(1).getSize());
        for (int i = 0; i < 64; i++) {
            assertEquals((byte) i, slices.get(1).getData().get(i), "byte[" + (64 + i) + "] must be " + i);
        }

        assertEquals(64, slices.get(1).getSize());
        for (int i = 0; i < 32; i++) {
            assertEquals((byte) i, slices.get(1).getData().get(i), "byte[" + (128 + i) + "] must be " + i);
        }
        for (int i = 32; i < 42; i++) {
            assertNull(slices.get(2).getData().get(i), "byte[" + (128 + i) + "] must be null");
        }
        assertEquals((byte) 1, slices.get(2).getData().get(42), "byte[170] must be 1");
        assertEquals((byte) 2, slices.get(2).getData().get(43), "byte[171] must be 2");
        for (int i = 44; i < 63; i++) {
            assertNull(slices.get(2).getData().get(i), "byte[" + (128 + i) + "] must be null");
        }
        assertEquals((byte) 3, slices.get(2).getData().get(63), "byte[191] must be 3");

        assertEquals(64, slices.get(3).getSize());
        for (int i = 0; i < 63; i++) {
            assertNull(slices.get(3).getData().get(i), "byte[" + (192 + i) + "] must be null");
        }
        assertEquals((byte) 1, slices.get(3).getData().get(63), "byte[255] must be 1");
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
