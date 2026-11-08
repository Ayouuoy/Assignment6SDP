
package visitor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSizeBytes;
    private final Set<String> matchingPaths = new TreeSet<>();
    private Element root;

    public SearchVisitor(
            Predicate<String> filenameCriterion,
            long minimumSizeBytes) {

        this.filenameCriterion = Objects.requireNonNull(
                filenameCriterion, "filenameCriterion"
        );

        if (minimumSizeBytes < 0) {
            throw new IllegalArgumentException(
                    "Minimum size cannot be negative"
            );
        }

        this.minimumSizeBytes = minimumSizeBytes;
    }

    @Override
    public void begin(Element root) {
        this.root = Objects.requireNonNull(root, "root");
        matchingPaths.clear();
    }

    @Override
    public void visit(FileNode file) {
        if (filenameCriterion.test(file.getName())
                && file.getSizeBytes() >= minimumSizeBytes) {

            matchingPaths.add(
                    NodeLocation.relativeFilePath(root, file)
            );
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    public List<String> getMatchingPaths() {
        return List.copyOf(matchingPaths);
    }
}
