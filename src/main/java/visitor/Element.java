
package visitor;

import java.util.List;

public interface Element {
    String getName();
    DirectoryNode getParent();
    void accept(Visitor visitor);

    default List<Element> getChildren() {
        return List.of();
    }
}
