
package visitor;

import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    "Usage: [.extension] [minimumSizeBytes]"
            );
        }

        String extension = args.length >= 1
                ? args[0] : ".log";

        long minimumSize = args.length == 2
                ? Long.parseLong(args[1]) : 100;

        DirectoryNode root = buildSampleTree();
        TreeWalker walker = new TreeWalker();

        TotalSizeVisitor size = new TotalSizeVisitor();

        SearchVisitor search = new SearchVisitor(
                name -> name.endsWith(extension),
                minimumSize
        );

        StatisticsVisitor statistics = new StatisticsVisitor();

        walker.walk(root, size);
        walker.walk(root, search);
        walker.walk(root, statistics);

        System.out.println("Root: " + root.getName());
        System.out.println(
                "Total size: " + size.getTotalSizeBytes() + " bytes"
        );
        System.out.println(
                "Files: " + statistics.getFileCount()
        );
        System.out.println(
                "Directories: " + statistics.getDirectoryCount()
        );
        System.out.println(
                "Maximum depth: " + statistics.getMaximumDepth()
        );
        System.out.println(
                "Search: " + extension
                        + ", minimum " + minimumSize + " bytes"
        );

        List<String> matches = search.getMatchingPaths();

        if (matches.isEmpty()) {
            System.out.println("No matches");
        } else {
            for (String path : matches) {
                System.out.println(path);
            }
        }
    }

    private static DirectoryNode buildSampleTree() {
        DirectoryNode root = new DirectoryNode("workspace");

        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode engine = new DirectoryNode("engine");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode old = new DirectoryNode("old");
        DirectoryNode reports = new DirectoryNode("reports");

        root.add(src)
                .add(docs)
                .add(reports)
                .add(new DirectoryNode("empty"));

        src.add(new FileNode("Main.java", 900))
                .add(engine);

        engine.add(new FileNode("Parser.java", 620))
                .add(new FileNode("trace.log", 120));

        docs.add(new FileNode("specs.md", 310))
                .add(old);

        old.add(new FileNode("legacy.log", 40));

        reports.add(new FileNode("run.log", 250));

        return root;
    }
}
