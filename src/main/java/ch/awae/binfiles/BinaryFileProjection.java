package ch.awae.binfiles;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * A "projection" of a {@link BinaryFile} that implements the {@link Iterable} interface for iterative access to the
 * file contents using a given "access format".
 * <p>
 * Instances of this class cannot be created manually, all instances must be produced through methods on a
 * {@link BinaryFile}.
 *
 * @author Andreas Wälchli
 * @see BinaryFile#fragments(int)
 * @see BinaryFile#slices(int)
 * @since 0.2.0
 */
@SuppressWarnings("ClassCanBeRecord")
public final class BinaryFileProjection<T> implements Iterable<T> {
    private final Supplier<Iterator<T>> source;

    BinaryFileProjection(@NotNull Supplier<Iterator<T>> source) {
        this.source = source;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return source.get();
    }

    /**
     * Returns a list of all the elements of the projection.
     *
     * @return a list with all the elements
     */
    public @NotNull List<T> toList() {
        List<T> result = new ArrayList<>();
        for (T item : this) {
            result.add(item);
        }
        return result;
    }
}
