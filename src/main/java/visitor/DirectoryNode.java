
package visitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DirectoryNode extends AbstractNode {
    private final Map<String, Element> children = new LinkedHashMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public DirectoryNode add(FileNode file) {
        addNode(file);
        return this;
    }

    public DirectoryNode add(DirectoryNode directory) {
        addNode(directory);
        return this;
    }

    private void addNode(AbstractNode child) {
        Objects.requireNonNull(child, "child");

        if (child.getParent() != null) {
            throw new IllegalStateException(
                    "Node already belongs to a directory"
            );
        }

        for (DirectoryNode current = this;
             current != null;
             current = current.getParent()) {
            if (current == child) {
                throw new IllegalArgumentException(
                        "Cycle detected"
                );
            }
        }

        if (children.containsKey(child.getName())) {
            throw new IllegalArgumentException(
                    "Duplicate child name: " + child.getName()
            );
        }

        children.put(child.getName(), child);
        child.attachTo(this);
    }

    @Override
    public List<Element> getChildren() {
        return List.copyOf(children.values());
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "visitor").visit(this);
    }
}
