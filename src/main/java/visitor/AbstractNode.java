
package visitor;

import java.util.Objects;

abstract class AbstractNode implements Element {
    private final String name;
    private DirectoryNode parent;

    protected AbstractNode(String name) {
        if (name == null || name.isBlank()
                || name.equals(".") || name.equals("..")
                || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("Invalid node name");
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final DirectoryNode getParent() {
        return parent;
    }

    final void attachTo(DirectoryNode newParent) {
        Objects.requireNonNull(newParent, "newParent");
        if (parent != null) {
            throw new IllegalStateException(
                    "Node already belongs to a directory"
            );
        }
        parent = newParent;
    }
}
