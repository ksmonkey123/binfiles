package ch.awae.binfiles.hex;

import ch.awae.binfiles.DataFragment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class HexFileReader implements Closeable {

    private enum State {OPEN, COMPLETED, ERROR}

    private final BufferedInputStream in;
    private State state = State.OPEN;

    public HexFileReader(@NotNull InputStream in) {
        this.in = new BufferedInputStream(in);
    }

    /**
     * reads the next DataFragment from the input stream.
     *
     * @return the next fragment or null if no more fragments are present.
     */
    public @Nullable DataFragment readNext() throws IOException {
        if (state == State.COMPLETED) {
            return null;
        }
        if (state == State.ERROR) {
            throw new IOException("reader invalid. error encountered previously");
        }
        try {
            // seek the next ':'
            while (true) {
                int c = in.read();
                if (c == -1) {
                    throw new IOException("unexpected EOF");
                }
                if (c == ':') {
                    break;
                }
            }

            in.mark(1000);
            // we are at the start of a block.
            int length = readHexChar();
            int address = (readHexChar() << 8) | readHexChar();
            int recordType = readHexChar();
            int[] dataField = readHexChars(length);

            in.reset();
            int[] rawData = readHexChars(length + 5);
            validateBlock(rawData);

            if (recordType == 1) {
                state = State.COMPLETED;
                return null;
            }
            if (recordType != 0) {
                throw new IOException("Invalid record type: " + recordType);
            }

            byte[] blockData = new byte[length];
            for (int i = 0; i < length; i++) {
                blockData[i] = (byte) dataField[i];
            }
            return new DataFragment(address, blockData);
        } catch (IOException | RuntimeException e) {
            state = State.ERROR;
            throw e;
        }
    }

    private static void validateBlock(int[] block) throws IOException {
        int sum = 0;
        for (int i = 0; i < block.length; i++) {
            sum += block[i];
        }
        if ((sum & 0x00ff) != 0) {
            throw new IOException("bad data: checksum error");
        }
    }

    private int[] readHexChars(int count) throws IOException {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = readHexChar();
        }
        return result;
    }

    private int readHexChar() throws IOException {
        int high = in.read();
        int low = in.read();

        if (high == -1 || low == -1) {
            throw new IOException("unexpected EOF");
        }

        return ((hexCharToInt(high) << 4) & 0x00f0) | (hexCharToInt(low) & 0x000f);
    }

    private static int hexCharToInt(int hexChar) {
        return switch (hexChar) {
            case '0' -> 0;
            case '1' -> 1;
            case '2' -> 2;
            case '3' -> 3;
            case '4' -> 4;
            case '5' -> 5;
            case '6' -> 6;
            case '7' -> 7;
            case '8' -> 8;
            case '9' -> 9;
            case 'A' -> 10;
            case 'B' -> 11;
            case 'C' -> 12;
            case 'D' -> 13;
            case 'E' -> 14;
            case 'F' -> 15;
            default -> throw new NumberFormatException("bad HEX char: " + ((char) hexChar));
        };
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }
}
