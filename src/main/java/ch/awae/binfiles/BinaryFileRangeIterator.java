package ch.awae.binfiles;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

class BinaryFileRangeIterator implements Iterator<List<@Nullable Byte>> {

    private final @NotNull BinaryFile file;
    private final int stepSize;
    private int nextStart;

    public BinaryFileRangeIterator(@NotNull BinaryFile file, int stepSize) {
        this.file = file;
        this.stepSize = stepSize;
    }

    @Override
    public boolean hasNext() {
        return this.nextStart < this.file.getSizeLimit();
    }

    @Override
    public List<@Nullable Byte> next() {
        if (!hasNext()) {
            return null;
        }

        int size = Math.min(stepSize, file.getSizeLimit() - nextStart);
        var result = file.getRangeOfBytes(nextStart, size);
        nextStart = nextStart + stepSize;
        return result;
    }
}
