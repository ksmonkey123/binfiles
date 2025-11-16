package ch.awae.binfiles.hex;

import ch.awae.binfiles.BinaryFile;
import ch.awae.binfiles.DataFragment;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Objects;

/**
 * Writer for writing {@link BinaryFile} instances to an {@link OutputStream}.
 * <p>
 * The reader follows the <a href="https://archive.org/details/IntelHEXStandard">Intel Hexadecimal Object File Format Specification</a>.
 *
 * @author Andreas Wälchli
 * @since 0.1.0
 */
public class HexFileWriter implements Closeable {

    private final HexRecordWriter writer;

    /**
     * Creates a new writer instance.
     * <p>
     * Individual records will be terminated with a line break ('\n').
     *
     * @param stream The output stream to write to. May not be null.
     * @implNote A {@link HexRecordWriter} is constructed internally.
     */
    public HexFileWriter(@NotNull OutputStream stream) {
        this.writer = new HexRecordWriter(Objects.requireNonNull(stream, "stream must not be null"));
    }

    /**
     * Creates a new writer instance.
     *
     * @param writer the record writer to write to. May not be null.
     */
    public HexFileWriter(@NotNull HexRecordWriter writer) {
        this.writer = Objects.requireNonNull(writer, "writer must not be null");
    }

    /**
     * Writes a {@link BinaryFile} with a maximum record length of 16.
     *
     * @param file the file to write
     * @throws IOException if an I/O exception occurs in the underlying stream.
     * @implNote While the maximum record length is 16, no effort is expended to determine the smallest number records
     * required to write the entire file. The records that are written are calculated directly from the {@link DataFragment}s
     * returned by iterating over the file with an iterator with step size 16. (see {@link BinaryFile#iterator(int)})
     */
    public void write(@NotNull BinaryFile file) throws IOException {
        write(file, 16);
    }

    /**
     * Writes a {@link BinaryFile} with a customizable recordLength.
     *
     * @param file         the file to write
     * @param recordLength the maximum length of a single data record. Range: 1-255.
     * @throws IOException if an I/O exception occurs in the underlying stream.
     * @implNote While the maximum record length is definable, no effort is expended to determine the smallest number records
     * required to write the entire file. The records that are written are calculated directly from the {@link DataFragment}s
     * returned by iterating over the file with an iterator with the corresponding step size. (see {@link BinaryFile#iterator(int)})
     */
    public void write(@NotNull BinaryFile file, int recordLength) throws IOException {
        if (recordLength < 1 || recordLength > 255) {
            throw new IllegalArgumentException("record length must be between 1 and 255");
        }
        // write records
        Iterator<DataFragment> iterator = file.iterator(recordLength);
        while (iterator.hasNext()) {
            writeFragment(iterator.next());
        }
        // write "EOF" record
        writer.write(new HexRecord(1, 0, new byte[0]));
    }

    private void writeFragment(DataFragment fragment) throws IOException {
        writer.write(new HexRecord(0, fragment.getPosition(), fragment.getData()));
    }

    /**
     * Flushes and closes the underlying stream.
     *
     * @throws IOException if any I/O exception occurs in the underlying stream.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    /**
     * Flushes the underlying stream.
     *
     * @throws IOException if any I/O exception occurs in the underlying stream.
     */
    public void flush() throws IOException {
        writer.flush();
    }
}
