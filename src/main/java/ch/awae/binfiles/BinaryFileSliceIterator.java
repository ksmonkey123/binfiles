package ch.awae.binfiles;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

class BinaryFileSliceIterator implements Iterator<DataSlice> {

    private final @NotNull BinaryFile file;
    private final int stepSize;
    private int nextStart;

    public BinaryFileSliceIterator(@NotNull BinaryFile file, int stepSize) {
        this.file = file;
        this.stepSize = stepSize;
    }

    @Override
    public boolean hasNext() {
        return this.nextStart < this.file.getSizeLimit();
    }

    @Override
    public DataSlice next() {
        if (!hasNext()) {
            return null;
        }

        int size = Math.min(stepSize, file.getSizeLimit() - nextStart);
        var result = file.getSlice(nextStart, size);
        nextStart = nextStart + stepSize;
        return result;
    }
}
