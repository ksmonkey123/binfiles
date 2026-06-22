package ch.awae.binfiles.hex;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HexRecordWriterTest {

    @Test
    void testWritingSingleRecord() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream, new byte[0]);
        HexRecord rec = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(rec);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088E", string);
    }

    @Test
    void testWritingSingleRecordWithLineBreak() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream);
        HexRecord rec = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(rec);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088E\n", string);
    }

    @Test
    void testWritingSingleRecordWithCustomString() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream, "myseparator");
        HexRecord rec = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(rec);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088Emyseparator", string);
    }

    @Test
    void testWritingSingleRecordWithCustomBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream, new byte[] {40, 41});
        HexRecord rec = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(rec);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088E()", string);
    }

}
