
package visitor;

import java.util.Objects;

public final class StatisticsVisitor implements Visitor {
    private Element root;
    private long fileCount;
    private long directoryCount;
    private int maximumDepth;

    @Override
    public void begin(Element root) {
        this.root = Objects.requireNonNull(root, "root");
        fileCount = 0;
        directoryCount = 0;
        maximumDepth = 0;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        recordDepth(file);
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        recordDepth(directory);
    }

    private void recordDepth(Element element) {
        maximumDepth = Math.max(
                maximumDepth,
                NodeLocation.depthFrom(root, element)
        );
    }

    public long getFileCount() {
        return fileCount;
    }

    public long getDirectoryCount() {
        return directoryCount;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }
}
