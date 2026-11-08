
package visitor;

import java.util.ArrayDeque;
import java.util.Deque;

final class NodeLocation {
    private NodeLocation() {
    }

    static int depthFrom(Element root, Element node) {
        int depth = 0;
        Element current = node;

        while (current != root) {
            if (current == null) {
                throw new IllegalArgumentException(
                        "Node is outside the chosen root"
                );
            }
            current = current.getParent();
            depth++;
        }

        return depth;
    }

    static String relativeFilePath(Element root, FileNode file) {
        if (root == file) {
            return file.getName();
        }

        Deque<String> segments = new ArrayDeque<>();
        Element current = file;

        while (current != root) {
            if (current == null) {
                throw new IllegalArgumentException(
                        "File is outside the chosen root"
                );
            }
            segments.addFirst(current.getName());
            current = current.getParent();
        }

        return String.join("/", segments);
    }
}
