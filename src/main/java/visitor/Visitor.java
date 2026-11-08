
package visitor;

public interface Visitor {
    void begin(Element root);
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}
