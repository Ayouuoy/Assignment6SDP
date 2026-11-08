
package visitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class TreeWalker {
    public void walk(Element root, Visitor visitor) {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(visitor, "visitor");

        visitor.begin(root);

        Deque<Element> pending = new ArrayDeque<>();
        pending.push(root);

        while (!pending.isEmpty()) {
            Element current = pending.pop();
            current.accept(visitor);

            List<Element> children = current.getChildren();

            for (int index = children.size() - 1;
                 index >= 0; index--) {
                pending.push(children.get(index));
            }
        }
    }
}
