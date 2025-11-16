package ch.awae.binfiles.hex;

import ch.awae.binfiles.BinaryFile;
import ch.awae.binfiles.DataFragment;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexFileWriterTest {

    @Test
    public void testWrite8() throws IOException {
        BinaryFile file = new BinaryFile();
        file.addFragment(new DataFragment(0, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
        file.addFragment(new DataFragment(8, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
        file.addFragment(new DataFragment(20, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexFileWriter writer = new HexFileWriter(stream);
        writer.write(file, 8);

        String output = stream.toString();

        String reference = """
                :080000000102030405060708D4
                :080008000102030405060708CC
                :0400140001020304DE
                :0400180005060708CA
                :00000001FF
                """;
        assertEquals(reference, output);
    }

    @Test
    public void testWrite16() throws IOException {
        BinaryFile file = new BinaryFile();
        file.addFragment(new DataFragment(0, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
        file.addFragment(new DataFragment(8, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
        file.addFragment(new DataFragment(20, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexFileWriter writer = new HexFileWriter(stream);
        writer.write(file);

        String output = stream.toString();

        String reference = """
                :1000000001020304050607080102030405060708A8
                :080014000102030405060708C0
                :00000001FF
                """;
        assertEquals(reference, output);
    }

    @Test
    public void testWrite32() throws IOException {
        BinaryFile file = new BinaryFile();
        file.addFragment(new DataFragment(0, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
        file.addFragment(new DataFragment(8, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
        file.addFragment(new DataFragment(20, new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexFileWriter writer = new HexFileWriter(stream);
        writer.write(file, 32);

        String output = stream.toString();

        String reference = """
                :1000000001020304050607080102030405060708A8
                :080014000102030405060708C0
                :00000001FF
                """;
        assertEquals(reference, output);
    }

}
