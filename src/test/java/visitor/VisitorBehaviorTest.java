
package visitor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisitorBehaviorTest {
    private final TreeWalker walker = new TreeWalker();

    @Test
    void doubleDispatchCallsCorrectOverloads() {
        DirectoryNode root = new DirectoryNode("root");

        root.add(new DirectoryNode("nested")
                .add(new FileNode("a.txt", 5)));

        root.add(new FileNode("b.txt", 8));

        List<String> visits = new ArrayList<>();

        Visitor recorder = new Visitor() {
            @Override
            public void begin(Element root) {
                visits.clear();
            }

            @Override
            public void visit(FileNode file) {
                visits.add("file:" + file.getName());
            }

            @Override
            public void visit(DirectoryNode directory) {
                visits.add("directory:" + directory.getName());
            }
        };

        walker.walk(root, recorder);

        assertEquals(
                List.of(
                        "directory:root",
                        "directory:nested",
                        "file:a.txt",
                        "file:b.txt"
                ),
                visits
        );
    }

    @Test
    void sizeSumsFilesAtSeveralLevels() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        walker.walk(sampleTree(), visitor);

        assertEquals(2240L, visitor.getTotalSizeBytes());
    }

    @Test
    void searchCombinesFilenameAndInclusiveMinimum() {
        SearchVisitor visitor = new SearchVisitor(
                name -> name.endsWith(".log"), 120
        );

        walker.walk(sampleTree(), visitor);

        assertEquals(
                List.of(
                        "reports/run.log",
                        "src/engine/trace.log"
                ),
                visitor.getMatchingPaths()
        );
    }

    @Test
    void searchPathsAreRootRelativeAndSorted() {
        DirectoryNode root = new DirectoryNode("top");

        root.add(new FileNode("z.log", 1));
        root.add(new DirectoryNode("a")
                .add(new FileNode("x.log", 2)));
        root.add(new FileNode("b.log", 3));

        SearchVisitor visitor = new SearchVisitor(
                name -> name.endsWith(".log"), 0
        );

        walker.walk(root, visitor);

        assertEquals(
                List.of(
                        "a/x.log",
                        "b.log",
                        "z.log"
                ),
                visitor.getMatchingPaths()
        );
    }

    @Test
    void statisticsCountsRootAndMaximumDepth() {
        StatisticsVisitor visitor = new StatisticsVisitor();

        walker.walk(sampleTree(), visitor);

        assertEquals(6L, visitor.getFileCount());
        assertEquals(7L, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaximumDepth());
    }

    @Test
    void emptyDirectoryAndNoMatchesAreSupported() {
        DirectoryNode root = new DirectoryNode("empty");

        TotalSizeVisitor size = new TotalSizeVisitor();

        SearchVisitor search = new SearchVisitor(
                name -> name.endsWith(".json"), 0
        );

        StatisticsVisitor statistics = new StatisticsVisitor();

        walker.walk(root, size);
        walker.walk(root, search);
        walker.walk(root, statistics);

        assertEquals(0L, size.getTotalSizeBytes());
        assertTrue(search.getMatchingPaths().isEmpty());

        assertEquals(0L, statistics.getFileCount());
        assertEquals(1L, statistics.getDirectoryCount());
        assertEquals(0, statistics.getMaximumDepth());
    }

    @Test
    void fileRootHasDepthZeroAndOwnRelativePath() {
        FileNode file = new FileNode("single.log", 9);

        TotalSizeVisitor size = new TotalSizeVisitor();

        SearchVisitor search = new SearchVisitor(
                name -> name.endsWith(".log"), 9
        );

        StatisticsVisitor statistics = new StatisticsVisitor();

        walker.walk(file, size);
        walker.walk(file, search);
        walker.walk(file, statistics);

        assertEquals(9L, size.getTotalSizeBytes());

        assertEquals(
                List.of("single.log"),
                search.getMatchingPaths()
        );

        assertEquals(1L, statistics.getFileCount());
        assertEquals(0L, statistics.getDirectoryCount());
        assertEquals(0, statistics.getMaximumDepth());
    }

    @Test
    void visitorsResetBetweenDifferentRuns() {
        TotalSizeVisitor size = new TotalSizeVisitor();

        SearchVisitor search = new SearchVisitor(
                name -> name.endsWith(".log"), 100
        );

        StatisticsVisitor statistics = new StatisticsVisitor();

        walker.walk(sampleTree(), size);
        walker.walk(sampleTree(), search);
        walker.walk(sampleTree(), statistics);

        FileNode secondRoot = new FileNode("tiny.log", 10);

        walker.walk(secondRoot, size);
        walker.walk(secondRoot, search);
        walker.walk(secondRoot, statistics);

        assertEquals(10L, size.getTotalSizeBytes());
        assertTrue(search.getMatchingPaths().isEmpty());

        assertEquals(1L, statistics.getFileCount());
        assertEquals(0L, statistics.getDirectoryCount());
        assertEquals(0, statistics.getMaximumDepth());
    }

    @Test
    void repeatAndChangedInputsProduceFreshResults() {
        DirectoryNode root = new DirectoryNode("root");

        TotalSizeVisitor size = new TotalSizeVisitor();
        StatisticsVisitor stats = new StatisticsVisitor();

        SearchVisitor search = new SearchVisitor(
                name -> name.endsWith(".log"), 0
        );

        walker.walk(root, stats);
        walker.walk(root, size);
        walker.walk(root, search);

        assertEquals(0L, size.getTotalSizeBytes());

        root.add(new DirectoryNode("data")
                .add(new FileNode("new.log", 31)));

        walker.walk(root, search);
        walker.walk(root, size);
        walker.walk(root, stats);

        assertEquals(31L, size.getTotalSizeBytes());

        assertEquals(
                List.of("data/new.log"),
                search.getMatchingPaths()
        );

        assertEquals(1L, stats.getFileCount());
        assertEquals(2L, stats.getDirectoryCount());
        assertEquals(2, stats.getMaximumDepth());

        walker.walk(root, size);
        walker.walk(root, search);

        assertEquals(31L, size.getTotalSizeBytes());

        assertEquals(
                List.of("data/new.log"),
                search.getMatchingPaths()
        );
    }

    @Test
    void totalSizeRejectsLongOverflow() {
        DirectoryNode root = new DirectoryNode("root");

        root.add(new FileNode("a.bin", Long.MAX_VALUE));
        root.add(new FileNode("b.bin", 1));

        assertThrows(
                ArithmeticException.class,
                () -> walker.walk(root, new TotalSizeVisitor())
        );
    }

    private DirectoryNode sampleTree() {
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
