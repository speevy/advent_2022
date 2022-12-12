package advent_2022;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;
import static advent_2022.Main_11.OperationType.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import lombok.*;

public class Main_11 {

	private static final String IF_FALSE = "    If false: throw to monkey ";

	private static final String IF_TRUE = "    If true: throw to monkey ";

	private static final String TEST = "  Test: divisible by ";

	private static final String OPERATION = "  Operation: new = old ";

	private static final String STARTING_ITEMS = "  Starting items: ";

	private static final String MONKEY = "Monkey ";

	private static final String ADDX = "addx ";

	private static final String NOOP = "noop";


	public static void main(String[] args) throws Exception {
		final Main_11 main = new Main_11();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_11.test"))) {
			final List<Monkey> monkeys = main.parse(reader);
			main.execute(monkeys, 20);
			System.out.println(main.monkeyBusiness(monkeys));
		}
	}

	enum OperationType {
		SUM, MULTIPLY, MOD, SQUARE
	}
	
	record Operation(OperationType type, int value) {}
	
	record Testing(Operation operation, int trueThrowId, int falseThrowId) {
		int test(int value) {
			if ( switch (operation.type()) {
			case MOD -> value % operation.value == 0;
			default -> throw new IllegalArgumentException();
			} ) {
				return trueThrowId;
			} else {
				return falseThrowId;
			}
		}
	}
	
	record MonkeyBehaviour(Operation operation, Testing test) {
		int applyTo(int value) {
			return switch (operation.type) {
			case SUM -> value + operation.value;
			case MULTIPLY -> value * operation.value;
			case MOD -> value % operation.value;
			case SQUARE -> value * value;
			};
		}
	}
	
	@ToString
	@EqualsAndHashCode
	static class Monkey {
		@Getter
		private final int id;
		private final MonkeyBehaviour behaviour;
		@Getter
		private final List<Integer> items;
		@Getter
		private int inspectedCount = 0;
		private static final Map<Integer, Monkey> monkeys = new HashMap<>();
		
		private Monkey(int id, MonkeyBehaviour behaviour, List<Integer> items) {
			super();
			this.id = id;
			this.behaviour = behaviour;

			// Make shure is mutable
			this.items = new ArrayList<>(items);
			
			monkeys.put(id, this);
		}
		
		public void executeRound() {
			for (int item : items) {
				inspectedCount++;
				int value = behaviour.applyTo(item) / 3;
				int monkey = behaviour.test().test(value);
				monkeys.get(monkey).items.add(value);
			}
			items.clear();
		}
	}

	@SneakyThrows
	private List<Monkey> parse(BufferedReader reader) {
		String line;
		List<String> notes = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			notes.add(line);
		}
		
		List<Monkey> monkeys = new ArrayList<>();
		for (int i = 0; i< notes.size(); i+=7) {
			monkeys.add(parse(notes.subList(i, i + 6)));
		}
		return monkeys;
	}

	private Monkey parse(List<String> lines) {

		int id = getMonkeyId(lines);
		
		List<Integer> items = getItems(lines);
		
		Operation operation = getOperation(lines);
		
		int test = parseInt(lines, 3, TEST);
		int ifTrue = parseInt(lines, 4, IF_TRUE);
		int ifFalse = parseInt(lines, 5, IF_FALSE);
		
		return new Monkey(id, new MonkeyBehaviour(operation, new Testing(new Operation(MOD, test), ifTrue, ifFalse)), items);		
	}

	private int parseInt(List<String> lines, int index, String prefix) {
		return Integer.parseInt(trim(lines, index, prefix));
	}
	
	private Operation getOperation(List<String> lines) {
		String trimmed = trim(lines, 2, OPERATION);
		if ("* old".equals(trimmed)) {
			return new Operation(SQUARE, 0);
		}
		
		int value = Integer.parseInt(trimmed.substring(2));
				
		return switch(trimmed.charAt(0)) {
		case '+' -> new Operation(SUM, value);
		case '*' -> new Operation(MULTIPLY, value);
		default -> throw new IllegalArgumentException();
		};
	}

	private List<Integer> getItems(List<String> lines) {
		return Arrays.stream(trim(lines, 1, STARTING_ITEMS).split(", "))
				.filter(StringUtils::isNotBlank).map(Integer::parseInt).collect(toList());
	}

	private int getMonkeyId(List<String> lines) {
		if (!lines.get(0).startsWith(MONKEY)) throw new IllegalArgumentException();
		final String trimmed = trim (lines, 0, MONKEY);
		return Integer.parseInt(trimmed.substring(0, trimmed.indexOf(':')));
	}
	
	private String trim (List<String> lines, int index, String prefix) {
		if (!lines.get(index).startsWith(prefix)) {
			throw new IllegalArgumentException(lines.toString());
		}
		
		return lines.get(index).substring(prefix.length());
	}
	

	private List<List<List<Integer>>> execute(List<Monkey> monkeys, int rounds) {
		List<List<List<Integer>>> result = new ArrayList<>();
		
		for (int round = 0; round < rounds; round++) {
			List<List<Integer>> roundResult = new ArrayList<>();
			for (Monkey monkey : monkeys) {
				monkey.executeRound();
			}
			for (Monkey monkey : monkeys) {
				roundResult.add(new ArrayList<>(monkey.getItems()));
			}
			result.add(roundResult);
		}
		
		return result;
	}

	private Integer monkeyBusiness(List<Monkey> monkeys) {
		return monkeys.stream().map(Monkey::getInspectedCount)
			.sorted(Comparator.reverseOrder())
			.limit(2)
			.reduce(1, (a,b) -> a * b);
	}


	////////////////// TESTS ////////////
	
	private static final String TEST_MESSAGE = 
			  "Monkey 0:\r\n"
			+ "  Starting items: 79, 98\r\n"
			+ "  Operation: new = old * 19\r\n"
			+ "  Test: divisible by 23\r\n"
			+ "    If true: throw to monkey 2\r\n"
			+ "    If false: throw to monkey 3\r\n"
			+ "\r\n"
			+ "Monkey 1:\r\n"
			+ "  Starting items: 54, 65, 75, 74\r\n"
			+ "  Operation: new = old + 6\r\n"
			+ "  Test: divisible by 19\r\n"
			+ "    If true: throw to monkey 2\r\n"
			+ "    If false: throw to monkey 0\r\n"
			+ "\r\n"
			+ "Monkey 2:\r\n"
			+ "  Starting items: 79, 60, 97\r\n"
			+ "  Operation: new = old * old\r\n"
			+ "  Test: divisible by 13\r\n"
			+ "    If true: throw to monkey 1\r\n"
			+ "    If false: throw to monkey 3\r\n"
			+ "\r\n"
			+ "Monkey 3:\r\n"
			+ "  Starting items: 74\r\n"
			+ "  Operation: new = old + 3\r\n"
			+ "  Test: divisible by 17\r\n"
			+ "    If true: throw to monkey 0\r\n"
			+ "    If false: throw to monkey 1";
	
	private static final Supplier<List<Monkey>> testMonkeys = () -> List.of(
			new Monkey(0, new MonkeyBehaviour(new Operation(MULTIPLY, 19), new Testing(new Operation(MOD, 23), 2, 3)), List.of(79, 98)),
			new Monkey(1, new MonkeyBehaviour(new Operation(SUM, 6), new Testing(new Operation(MOD, 19), 2, 0)), List.of(54, 65, 75, 74)),
			new Monkey(2, new MonkeyBehaviour(new Operation(SQUARE, 0), new Testing(new Operation(MOD, 13), 1, 3)), List.of(79, 60, 97)),
			new Monkey(3, new MonkeyBehaviour(new Operation(SUM, 3), new Testing(new Operation(MOD, 17), 0, 1)), List.of(74))
			);

	@Test 
	void parseTest() throws IOException {
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(testMonkeys.get(), parse(reader));
		}
	}

	private static final List<List<List<Integer>>> testRounds = List.of(
			List.of(List.of(20, 23, 27, 26), List.of(2080, 25, 167, 207, 401, 1046),  List.of(),  List.of()),
			List.of(List.of(695, 10, 71, 135, 350), List.of(43, 49, 58, 55, 362),  List.of(),  List.of()),
			List.of(List.of(16, 18, 21, 20, 122), List.of(1468, 22, 150, 286, 739),  List.of(),  List.of()),
			List.of(List.of(491, 9, 52, 97, 248, 34), List.of(39, 45, 43, 258),  List.of(),  List.of()),
			List.of(List.of(15, 17, 16, 88, 1037), List.of(20, 110, 205, 524, 72),  List.of(),  List.of()),
			List.of(List.of(8, 70, 176, 26, 34), List.of(481, 32, 36, 186, 2190),  List.of(),  List.of()),
			List.of(List.of(162, 12, 14, 64, 732, 17), List.of(148, 372, 55, 72),  List.of(),  List.of()),
			List.of(List.of(51, 126, 20, 26, 136), List.of(343, 26, 30, 1546, 36),  List.of(),  List.of()),
			List.of(List.of(116, 10, 12, 517, 14), List.of(108, 267, 43, 55, 288),  List.of(),  List.of()),
			List.of(List.of(91, 16, 20, 98), List.of(481, 245, 22, 26, 1092, 30),  List.of(),  List.of())
			);
	private static final List<List<Integer>> testRound15 = List.of(List.of(83, 44, 8, 184, 9, 20, 26, 102), List.of(110, 36),  List.of(),  List.of());
	private static final List<List<Integer>> testRound20 = List.of(List.of(10, 12, 14, 26, 34), List.of(245, 93, 53, 199, 115),  List.of(),  List.of());

	

	@Test
	void executionTest() {
		List<List<List<Integer>>> rounds = execute(testMonkeys.get(), 20);
		
		assertEquals(20, rounds.size());
		assertEquals(testRounds, rounds.subList(0, 10));
		assertEquals(testRound15, rounds.get(14));
		assertEquals(testRound20, rounds.get(19));
	}
	
	static final List<Integer> inspected = List.of (101, 95, 7, 105);
	
	@Test
	void inspectionCount() {
		final List<Monkey> monkeys = testMonkeys.get();
		List<List<List<Integer>>> rounds = execute(monkeys, 20);
		
		assertEquals(inspected, monkeys.stream().map(Monkey::getInspectedCount).collect(toList()));
		assertEquals(10605, monkeyBusiness(monkeys));
	}

}
