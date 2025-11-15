package ch.awae.binfiles.hex;

import ch.awae.binfiles.BinaryFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class HexFileReaderTest {

    @Test
    public void testReadSimpleFile() throws IOException {
        try (
                InputStream stream = this.getClass().getResourceAsStream("/ch/awae/binfiles/hex/valid_simple.hex");
                HexFileReader reader = new HexFileReader(stream)
        ) {
            BinaryFile file = reader.read();
            validateReferenceFile(file);
            // there's no more to be read, reader should complete
            assertNull(reader.read());
            assertNull(reader.read());
        }
    }

    @Test
    public void testReadFileWithSuperfluousData() throws IOException {
        try (
                InputStream stream = this.getClass().getResourceAsStream("/ch/awae/binfiles/hex/valid_with_superfluous_data.hex");
                HexFileReader reader = new HexFileReader(stream)
        ) {
            BinaryFile file = reader.read();
            validateReferenceFile(file);
            // there's no more to be read, reader should complete
            assertNull(reader.read());
            assertNull(reader.read());
        }
    }

    @Test
    public void testReadFileCorrupt() throws IOException {
        try (
                InputStream stream = this.getClass().getResourceAsStream("/ch/awae/binfiles/hex/invalid_corrupt.hex");
                HexFileReader reader = new HexFileReader(stream)
        ) {
            assertThrows(HexFileParsingException.class, reader::read);
            // additional reads also fail
            assertThrows(HexFileParsingException.class, reader::read);

        }
    }

    @Test
    public void testReadFileMoreAfterEOF() throws IOException {
        try (
                InputStream stream = this.getClass().getResourceAsStream("/ch/awae/binfiles/hex/valid_with_more_after_eof.hex");
                HexFileReader reader = new HexFileReader(stream)
        ) {
            BinaryFile file = reader.read();
            validateReferenceFile(file);
            // there's an additional block, but without an EOF.
            // the next file read should therefore fail
            assertThrows(HexFileParsingException.class, reader::read);
            // additional reads should also fail
            assertThrows(HexFileParsingException.class, reader::read);
        }
    }

    @Test
    public void testInitNullReader() {
        assertThrows(NullPointerException.class, () -> new HexFileReader((HexRecordReader) null));
    }

    @Test
    public void testInitNullStream() {
        assertThrows(NullPointerException.class, () -> new HexFileReader((InputStream) null));
    }

    @Test
    public void testIOException() throws IOException {
        HexRecordReader mockReader = Mockito.mock(HexRecordReader.class);

        Mockito.when(mockReader.readNext()).thenThrow(new IOException());

        HexFileReader fileReader = new HexFileReader(mockReader);

        // first call should propagate IOException
        assertThrows(IOException.class, fileReader::read);
        // reader should now be "closed" and re-throw the IOException
        assertThrows(IOException.class, fileReader::read);

        // verify only a single call went out to the mockReader
        Mockito.verify(mockReader).readNext();
        Mockito.verifyNoMoreInteractions(mockReader);
    }

    private static void validateReferenceFile(BinaryFile file) {
        // validate read file
        assertNotNull(file);
        assertEquals(512, file.getSizeLimit());
        assertEquals(320, file.getCurrentSize());

        // bytes in range 0x0000 - 0x00ff should all be null
        for (int i = 0; i < 0x100; i++) {
            assertNull(file.getByte(i), "file[" + i + "] must be null");
        }

        byte[] referenceData = {0x21, 0x46, 0x01, 0x36, 0x01, 0x21, 0x47, 0x01, 0x36, 0x00, 0x7E, (byte) 0xFE, 0x09, (byte) 0xD2, 0x19, 0x01,
                0x21, 0x46, 0x01, 0x7E, 0x17, (byte) 0xC2, 0x00, 0x01, (byte) 0xFF, 0x5F, 0x16, 0x00, 0x21, 0x48, 0x01, 0x19,
                0x19, 0x4E, 0x79, 0x23, 0x46, 0x23, (byte) 0x96, 0x57, 0x78, 0x23, (byte) 0x9E, (byte) 0xDA, 0x3F, 0x01, (byte) 0xB2, (byte) 0xCA,
                0x3F, 0x01, 0x56, 0x70, 0x2B, 0x5E, 0x71, 0x2B, 0x72, 0x2B, 0x73, 0x21, 0x46, 0x01, 0x34, 0x21};

        // bytes in range 0x0100 - 0x013f should be set
        for (int i = 0; i < 64; i++) {
            assertEquals(referenceData[i], file.getByte(i + 0x0100), "file[" + (i + 0x100) + "] must be correct");
        }

        // bytes in range 0x0140 - 0x01ff should be null
        for (int i = 0x140; i < 0x200; i++) {
            assertNull(file.getByte(i), "file[" + i + "] must be null");
        }

        // byte 0x200 must be out of range of file
        assertThrows(IndexOutOfBoundsException.class, () -> file.getByte(0x200));
    }

}
