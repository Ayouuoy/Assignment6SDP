
package visitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeIntegrityTest {
    @Test
    void negativeFileSizeIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FileNode("bad.txt", -1)
        );

        assertEquals(
                0L,
                new FileNode("zero.txt", 0).getSizeBytes()
        );
    }

    @Test
    void invalidNamesAreRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FileNode("", 1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new DirectoryNode("  ")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new FileNode("a/b.txt", 1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new DirectoryNode("..")
        );
    }

    @Test
    void duplicateSiblingNamesAreRejectedAcrossElementTypes() {
        DirectoryNode root = new DirectoryNode("root");

        root.add(new FileNode("same", 1));

        assertThrows(
                IllegalArgumentException.class,
                () -> root.add(new DirectoryNode("same"))
        );

        assertEquals(1, root.getChildren().size());
    }

    @Test
    void multipleParentOwnershipIsRejected() {
        DirectoryNode left = new DirectoryNode("left");
        DirectoryNode right = new DirectoryNode("right");

        FileNode shared = new FileNode("shared.bin", 2);

        left.add(shared);

        assertThrows(
                IllegalStateException.class,
                () -> right.add(shared)
        );

        assertEquals(left, shared.getParent());
        assertTrue(right.getChildren().isEmpty());
    }

    @Test
    void directAndIndirectCyclesAreRejected() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode branch = new DirectoryNode("branch");
        DirectoryNode leaf = new DirectoryNode("leaf");

        root.add(branch);
        branch.add(leaf);

        assertThrows(
                IllegalArgumentException.class,
                () -> root.add(root)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> leaf.add(root)
        );

        assertTrue(leaf.getChildren().isEmpty());
    }

    @Test
    void childrenCannotBeModifiedFromOutside() {
        DirectoryNode root = new DirectoryNode("root");

        root.add(new FileNode("a.txt", 3));

        List<Element> children = root.getChildren();

        assertThrows(
                UnsupportedOperationException.class,
                () -> children.add(new FileNode("b.txt", 1))
        );

        assertEquals(1, root.getChildren().size());
    }

    @Test
    void validNestedStructureIsStillTraversable() {
        DirectoryNode root = new DirectoryNode("root");

        root.add(
                new DirectoryNode("one").add(
                        new DirectoryNode("two").add(
                                new FileNode("deep.txt", 13)
                        )
                )
        );

        TotalSizeVisitor visitor = new TotalSizeVisitor();

        new TreeWalker().walk(root, visitor);

        assertEquals(13L, visitor.getTotalSizeBytes());
    }
}
