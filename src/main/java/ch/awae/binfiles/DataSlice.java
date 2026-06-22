package ch.awae.binfiles;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Read-Only representation of a section of address space.
 * <p>
 * A section has a starting address and a list of values for the individual bytes.
 * It is possible that not addresses have a set value.
 * <p>
 * A {@code DataSlice} is different from a {@link DataFragment} in that a {@code DataSlice} may be discontinuous
 * (i.e., may contain "gaps"), while a {@code DataFragment} must be continuous. (i.e., may not contain any gaps)
 *
 * @author Andreas Wälchli
 * @since 0.2.0
 */
@SuppressWarnings("ClassCanBeRecord")
public class DataSlice {

    private final int startAddress;
    private final List<@Nullable Byte> data;

    /**
     * Creates a new instance.
     *
     * @param startAddress the starting address of the slice. must be in the 16-bit address range (0 - 65,535)
     * @param data         the data of the slice. A read-only copy of the provided list is created internally.
     *                     Must contain at least 1 element.
     * @throws IllegalArgumentException if the startAddress or data is invalid, or the entire data does not fit into the
     *                                  16-bit address range. (The effective address of the last byte of data is above
     *                                  65,535.)
     */
    public DataSlice(int startAddress, @NotNull List<@Nullable Byte> data) {
        this.data = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(data, "data must not be null")));
        this.startAddress = startAddress;

        if (this.data.isEmpty()) {
            throw new IllegalArgumentException("data must not be empty");
        }
        if (this.startAddress < 0 || (this.startAddress + this.data.size() > 65535)) {
            throw new IllegalArgumentException("data slice does not fit into the 16-bit address range");
        }

    }

    /**
     * Returns the starting address of this slice.
     *
     * @return the starting address
     */
    public int getStartAddress() {
        return startAddress;
    }

    /**
     * Returns the data of this slice.
     * <p>
     * Bytes without a set value are represented by {@code null}.
     * The returned list is unmodifiable. ({@link Collections#unmodifiableList(List)})
     *
     * @return a read-only view of the data contained in this slice
     */
    public List<@Nullable Byte> getData() {
        return data;
    }

    /**
     * Returns the size of this slice.
     *
     * @return the size
     */
    public int getSize() {
        return data.size();
    }
}
