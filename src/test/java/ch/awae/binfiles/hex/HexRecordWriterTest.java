package ch.awae.binfiles.hex;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexRecordWriterTest {

    @Test
    public void testWritingSingleRecord() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream, new byte[0]);
        HexRecord record = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(record);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088E", string);
    }

    @Test
    public void testWritingSingleRecordWithLineBreak() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream);
        HexRecord record = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(record);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088E\n", string);
    }

    @Test
    public void testWritingSingleRecordWithCustomString() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream, "myseparator");
        HexRecord record = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(record);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088Emyseparator", string);
    }

    @Test
    public void testWritingSingleRecordWithCustomBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexRecordWriter writer = new HexRecordWriter(stream, new byte[] {40, 41});
        HexRecord record = new HexRecord(0, 0x1234, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        writer.write(record);
        String string = new String(stream.toByteArray());

        assertEquals(":0812340001020304050607088E()", string);
    }

}
