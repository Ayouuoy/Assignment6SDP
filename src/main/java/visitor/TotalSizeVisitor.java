
package visitor;

import java.util.Objects;

public final class TotalSizeVisitor implements Visitor {
    private long totalSizeBytes;

    @Override
    public void begin(Element root) {
        Objects.requireNonNull(root, "root");
        totalSizeBytes = 0;
    }

    @Override
    public void visit(FileNode file) {
        totalSizeBytes = Math.addExact(
                totalSizeBytes, file.getSizeBytes()
        );
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    public long getTotalSizeBytes() {
        return totalSizeBytes;
    }
}
