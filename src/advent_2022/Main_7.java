package advent_2022;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import advent_2022.Main_2.Round;
import advent_2022.Main_5.*;
import lombok.*;

public class Main_7 {

	private static final String DIR = "dir ";

	private static final String LS = "$ ls";

	private static final String CD = "$ cd ";

	public static void main(String[] args) throws Exception {
		final Main_7 main = new Main_7();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_7"))) {
			final Map<String, Long> parsed = main.parse(reader);
			System.out.println(main.sumSizes(parsed));
			System.out.println(main.findDirToDelete(parsed));
		}
	}

	private static final String TEST_MESSAGE = 
			  "$ cd /\r\n"
			+ "$ ls\r\n"
			+ "dir a\r\n"
			+ "14848514 b.txt\r\n"
			+ "8504156 c.dat\r\n"
			+ "dir d\r\n"
			+ "$ cd a\r\n"
			+ "$ ls\r\n"
			+ "dir e\r\n"
			+ "29116 f\r\n"
			+ "2557 g\r\n"
			+ "62596 h.lst\r\n"
			+ "$ cd e\r\n"
			+ "$ ls\r\n"
			+ "584 i\r\n"
			+ "$ cd ..\r\n"
			+ "$ cd ..\r\n"
			+ "$ cd d\r\n"
			+ "$ ls\r\n"
			+ "4060174 j\r\n"
			+ "8033020 d.log\r\n"
			+ "5626152 d.ext\r\n"
			+ "7214296 k";
	
	private static final Map<String, Long> directorySizesTest = Map.of(
			"/", 48381165L,
			"/a", 94853L,
			"/a/e", 584L,
			"/d", 24933642L
			);
	
	@Test 
	void parseTest() throws IOException {
		final Map<String, Long> directorySizes;
		
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			directorySizes = parse(reader);
		}
		
		assertEquals(directorySizesTest.size(), directorySizes.size());
		
		directorySizesTest.forEach((dir, size) -> assertEquals(size, directorySizes.get(dir)));
	}

	enum NodeType {DIR, FILE};
	
	record Node(NodeType type, String name, Long size, List<Node> children, Optional<Node> parent) {
		public static Node dir(String name, Optional<Node> parent) {
			return new Node(NodeType.DIR, name, 0L, new ArrayList<>(), parent);
		}
		
		public static Node file(String name, Long size, Optional<Node> parent) {
			return new Node(NodeType.FILE, name, size, Collections.emptyList(), parent);
		}
		
		public Long getTotalSize() {
			if (NodeType.FILE.equals(type)) return size;
			return children.stream().mapToLong(Node::getTotalSize).sum();
		}
	}
	
	private final Node root = Node.dir("/", Optional.empty());
	private Node currentNode = root;
	
	@SneakyThrows
	private Map<String, Long>  parse(BufferedReader reader) {
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(CD)) {
				changeDir(line);
			} else if (line.startsWith(LS)) {
				// do nothing?
			} else if (line.startsWith(DIR)) {
				// do nothing?
			} else if (line.matches("^[0-9]+ .+$")) {
				addFile(line);
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		final Map<String, Long> directorySizes = new HashMap<>();
		
		addDirectoryTreeToMap(root, directorySizes);
		
		
		return directorySizes;
	}
	
	private void addDirectoryTreeToMap(Node dir, Map<String, Long> directorySizes) {
		directorySizes.put(getFullPath(dir), dir.getTotalSize());
		dir.children().stream().filter(x -> NodeType.DIR.equals(x.type())).forEach(x -> addDirectoryTreeToMap(x, directorySizes));
	}

	String getFullPath(Node node) {
		String path = node.name();
		for (Optional<Node> parent = node.parent; parent.isPresent(); parent = parent.get().parent()) {
			path = parent.get().name() + "/" + path;
		}
		
		if (path.startsWith("//")) return path.substring(1);
		
		return path;
	}
	
	private void addFile(String line) {
		String split[] = line.split(" ");
		currentNode.children().add(Node.file(split[1], Long.parseLong(split[0]), Optional.of(currentNode)));
	}

	private void changeDir(String line) {
		String dir = line.substring(CD.length());
		currentNode = switch(dir) {
		case "/" -> root;
		case ".." -> currentNode.parent().orElseThrow(IllegalArgumentException::new); 
		default -> getChild(dir);
		};
	}

	private Node getChild(String dir) {
		Optional<Node> existing = currentNode.children().stream().filter((x) -> dir.equals(x.name())).findAny();
		if (existing.isPresent()) return existing.get();
		Node newNode = Node.dir(dir, Optional.of(currentNode));
		currentNode.children().add(newNode);
		return newNode;
	}
	
	@Test 
	void SumSizesTest() {
		assertEquals(95437L, sumSizes(directorySizesTest));
	}

	private Long sumSizes(Map<String, Long> directorySizes) {
		return directorySizes.values().stream().mapToLong(Long::longValue).filter(x -> x <= 100000L).sum();
	}
	
	private static final Long totalDiskSpace = 70000000L;
	private static final Long requiredDiskSpace = 30000000L;
	
	@Test
	void dirToDeleteTest() {
		assertEquals(24933642L, findDirToDelete(directorySizesTest));
	}

	private Long findDirToDelete(Map<String, Long> directorySizes) {
		Long used = directorySizes.get(root.name());
		Long free = totalDiskSpace - used;
		Long requiredToFree = requiredDiskSpace - free;
		
		return directorySizes.entrySet().stream()
			.mapToLong(Map.Entry::getValue)
			.filter(e -> e >= requiredToFree)
			.sorted().findFirst()
			.orElseThrow(IllegalArgumentException::new);
	}
}
