package ch.awae.binfiles.hex;

/**
 * Exception class indicating a format-level processing error in the {@link HexRecordReader}.
 *
 * @author Andreas Wälchli
 * @since 0.1.0
 */
public class HexRecordParsingException extends RuntimeException {
    public HexRecordParsingException(String message) {
        super(message);
    }
}
