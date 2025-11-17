package ch.awae.binfiles.hex;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Writer for writing {@link HexRecord} instances to an {@link OutputStream}.
 * <p>
 * The reader follows the <a href="https://archive.org/details/IntelHEXStandard">Intel Hexadecimal Object File Format Specification</a>.
 * After writing a single record, a customizable separator string (e.g. a line-break) can be written to the stream.
 *
 * @author Andreas Wälchli
 * @since 0.1.0
 */
public class HexRecordWriter implements Closeable {

    private final static HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();
    private final OutputStream stream;
    private final byte[] separator;

    /**
     * Creates a new Writer instance writing to the provided OutputStream.
     * <p>
     * A separator sequence can be provided. This byte sequence will be written at the end of every record.
     * If no separator sequence is provided, a subsequent record will immediately follow a preceding one.
     *
     * @param stream    The stream to write to. May not be null.
     * @param separator The separator byte sequence to be written at the end of every record.
     *                  May not contain the byte value 58 (0x3A / ':'). May be null.
     * @apiNote The byte value 58 is forbidden in the separator, as this is the <em>record mark</em>,
     * i.e. the starting byte of a new record.
     * @implNote Internally a <b>copy</b> of the provided separator is stored. This ensured immutability.
     */
    public HexRecordWriter(@NotNull OutputStream stream, byte @Nullable [] separator) {
        this.stream = Objects.requireNonNull(stream);

        if (separator != null && separator.length > 0) {
            this.separator = Arrays.copyOf(separator, separator.length);
            for (int i = 0; i < separator.length; i++) {
                if (separator[i] == 58) {
                    throw new IllegalArgumentException("separator may not contain byte value 58. encountered value at position " + i);
                }
            }
        } else {
            this.separator = null;
        }

    }

    /**
     * Creates a new Writer instance writing to the provided OutputStream.
     * <p>
     * A separator string can be provided. This string will be written at the end of every record.
     * If no separator string is provided, a subsequent record will immediately follow a preceding one.
     * The separator string will be written in the UTF-8 charset. If any other charset is required, the constructor
     * {@link #HexRecordWriter(OutputStream, byte[])} must be used.
     *
     * @param stream    The stream to write to. May not be null.
     * @param separator The separator string to be written at the end of every record.
     *                  May not contain the character ':'. May be null.
     * @apiNote The character ':' is forbidden in the separator as this is the <em>record mark</em>,
     * i.e. the starting byte of a new record.
     */
    public HexRecordWriter(@NotNull OutputStream stream, @Nullable String separator) {
        this.stream = Objects.requireNonNull(stream);
        if (separator != null && !separator.isEmpty()) {
            if (separator.contains(":")) {
                throw new IllegalArgumentException("separator string may not contain ':'");
            }
            this.separator = separator.getBytes(StandardCharsets.US_ASCII);
        } else {
            this.separator = null;
        }
    }

    /**
     * Creates a new Writer instance writing to the provided OutputStream.
     * <p>
     * A single line-break character ('\n') is used as a separator.
     *
     * @param stream The stream to write to. May not be null.
     */
    public HexRecordWriter(@NotNull OutputStream stream) {
        this(stream, "\n");
    }

    /**
     * Writes a {@link HexRecord} to the stream, followed by the configured separator sequence.
     *
     * @param record The record to write. May not be null.
     * @throws IOException Any I/O exception occurs in the underlying stream.
     */
    public void write(@NotNull HexRecord record) throws IOException {
        Objects.requireNonNull(record, "record must not be null");
        byte[] data = record.data();

        String output = ":"
                + HEX_FORMAT.toHexDigits((byte) data.length)
                + HEX_FORMAT.toHexDigits((short) record.address())
                + HEX_FORMAT.toHexDigits((byte) record.type())
                + HEX_FORMAT.formatHex(data)
                + HEX_FORMAT.toHexDigits((byte) record.calculateChecksum());

        stream.write(output.getBytes(StandardCharsets.US_ASCII));
        if (separator != null) {
            stream.write(separator);
        }
    }

    /**
     * Flushes and closes the underlying stream.
     *
     * @throws IOException if any I/O exception occurs in the underlying stream.
     */
    @Override
    public void close() throws IOException {
        try {
            stream.flush();
        } finally {
            // try to close the stream no matter what
            stream.close();
        }
    }

    /**
     * Flushes the underlying stream.
     *
     * @throws IOException if any I/O exception occurs in the underlying stream.
     */
    public void flush() throws IOException {
        this.stream.flush();
    }

}
