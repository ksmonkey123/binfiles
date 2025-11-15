package ch.awae.binfiles.hex;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class HexRecordReader implements Closeable {

    enum State { VALID, COMPLETED, IO_ERROR, PARSING_ERROR}

    private final InputStream stream;

    public HexRecordReader(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    public HexRecord readNext() throws IOException {

    }

    private int readNextHexEncodedByte() throws IOException {
        int high = stream.read();
        int low = stream.read();
        if (high == -1 || low == -1) {
            throw new HexRecordParsingException("reached end of stream unexpectedly");
        }

        try {
            return (hexCharToInt(high) << 4) + hexCharToInt(low);
        } catch (NumberFormatException e) {
            throw new HexRecordParsingException("unable to parse hex chars: " + e.getMessage());
        }

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

}
