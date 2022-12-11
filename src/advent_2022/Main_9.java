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
import advent_2022.Main_9.Position;
import lombok.*;

public class Main_9 {

	public static void main(String[] args) throws Exception {
		final Main_9 main = new Main_9();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_9"))) {
			final List<Movement> movements = main.parse(reader);
			Rope rope = main.simulate(movements);
			System.out.println(rope.getTailPositions().size());
		}
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_9"))) {
			final List<Movement> movements = main.parse(reader);
			Rope rope = main.simulate(movements, 10);
			final Set<Position> tailPositions = rope.getTailPositions();
			System.out.println(tailPositions.size());
			main.print(tailPositions);
		}
	}

	record Position(int x, int y) {}
	enum Direction { 
		LEFT, RIGHT, UP, DOWN;
		
		static Direction getFromLetter(Character c) {
			return switch(c) {
			case 'R' -> RIGHT;
			case 'L' -> LEFT;
			case 'U' -> UP;
			case 'D' -> DOWN;
			default -> throw new IllegalArgumentException();
			};
		}
	}
	record Movement(Direction direction, int steps) { }
	
	@Getter
	class Rope {
		private Position start;
		private Position positions[];
		private Set<Position> tailPositions = new HashSet<>();
		
		public Rope(int length) {
			positions = new Position[length];
			this.start = new Position(0, 0);
			for (int i = 0; i < length; i++) {
				positions[i] = start;
			}
			tailPositions.add(start);
		}
		
		public Rope() {
			this(2);
		}
		
		public void move(Movement movement) {
			for (int i = 0; i < movement.steps(); i++) {
				Position head = positions[0]; 
				positions[0] = switch(movement.direction) {
				case DOWN -> new Position(head.x(), head.y() - 1);
				case UP -> new Position(head.x(), head.y() + 1);
				case RIGHT -> new Position(head.x() + 1, head.y());
				case LEFT -> new Position(head.x() - 1, head.y());
				};
				moveTail();
			}
		}

		private void moveTail() {
			for (int i = 1; i < positions.length; i++) {
				
				final Position head = positions[i -1];
				final Position tail = positions[i];
				
				int deltaX = head.x() - tail.x();
				int deltaY = head.y() - tail.y();
			
				if (deltaX > 1) {
					positions[i] = new Position(tail.x() + 1, tail.y() + limit(deltaY));
				} else	if (deltaX < -1) {
					positions[i] = new Position(tail.x() - 1, tail.y() + limit(deltaY));
				} else if (deltaY > 1) {
					positions[i] = new Position(tail.x() + limit(deltaX), tail.y() + 1);
				} else	if (deltaY < -1) {
					positions[i] = new Position(tail.x() + limit(deltaX), tail.y() - 1);
				};
				
			}
			
			tailPositions.add(positions[positions.length - 1]);
		}

		private int limit(final int value) {
			if (value == 0) return 0;
			if (value > 0) return 1;
			return -1;
		}


	}
	
	private static final String TEST_MESSAGE = 
			  "R 4\r\n"
			  + "U 4\r\n"
			  + "L 3\r\n"
			  + "D 1\r\n"
			  + "R 4\r\n"
			  + "D 1\r\n"
			  + "L 5\r\n"
			  + "R 2";
	
	private static final List<Movement> testMovements = List.of(
			new Movement(Direction.RIGHT, 4),
			new Movement(Direction.UP, 4),
			new Movement(Direction.LEFT, 3),
			new Movement(Direction.DOWN, 1),
			new Movement(Direction.RIGHT, 4),
			new Movement(Direction.DOWN, 1),
			new Movement(Direction.LEFT, 5),
			new Movement(Direction.RIGHT, 2)
			);
			

	@Test 
	void parseTest() throws IOException {
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(testMovements, parse(reader));
		}
	}

	@SneakyThrows
	private List<Movement> parse(BufferedReader reader) {
		String line;
		List<Movement> movements = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			movements.add(parse(line));
		}
		return movements;
	}

	private Movement parse(String line) {
		
		return new Movement(
				Direction.getFromLetter(line.charAt(0)),
				Integer.parseInt(line.substring(2))
				);
	}
	
	private static final Set<Position> testPositions = Set.of(
			new Position(0, 0),
			new Position(1, 0),
			new Position(2, 0),
			new Position(3, 0),
			new Position(4, 1),
			new Position(1, 2),
			new Position(2, 2),
			new Position(3, 2),
			new Position(4, 2),
			new Position(3, 3),
			new Position(4, 3),
			new Position(2, 4),
			new Position(3, 4)
			);
	
	@Test
	void movementsTest() {
		Rope rope = simulate(testMovements);
		assertEquals (testPositions, rope.getTailPositions());
	}

	private Rope simulate(List<Movement> movements) {
		return simulate(movements, 2);
	}
	private Rope simulate(List<Movement> movements, int size) {
		Rope rope = new Rope(size);
		
		for (int i = 0; i < movements.size(); i++) {
			rope.move(movements.get(i));
		}
		
		return rope;
	}
	
	@Test
	void testTailMoves() {

		testTailMove (1, 0, 0, 0, 0, 0);
		testTailMove (2, 0, 0, 0, 1, 0);
		testTailMove (4, 1, 3, 0, 3, 0);
		testTailMove (4, 2, 3, 0, 4, 1);
		testTailMove (3, 4, 4, 3, 4, 3);
		testTailMove (2, 4, 4, 3, 3, 4);
		testTailMove (1, 4, 3, 4, 2, 4);
		testTailMove (2, 2, 0, 0, 1, 1);

	}
	
	void testTailMove(int headX, int headY, int tailX, int tailY, int expectedTailX, int expectedTailY) {
		Rope rope = new Rope();
		rope.positions =  new Position[] { new Position(headX, headY), new Position(tailX, tailY)};
		rope.moveTail();
		assertEquals(new Position(expectedTailX, expectedTailY), rope.positions[1]);
	}
	
	final String TEST_MESSAGE_2 = 
			  "R 5\r\n"
			+ "U 8\r\n"
			+ "L 8\r\n"
			+ "D 3\r\n"
			+ "R 17\r\n"
			+ "D 10\r\n"
			+ "L 25\r\n"
			+ "U 20";
	
	@Test @SneakyThrows
	void test2() {
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE_2))) {
			final List<Movement> movements = parse(reader);
			Rope rope = simulate(movements, 10);
			System.out.println(rope.getTailPositions().size());
			
			print(rope.getTailPositions());
		}
	}

	private void print(Set<Position> tailPositions) {
		int maxX = tailPositions.stream().mapToInt(Position::x).max().orElse(0);
		int minX = tailPositions.stream().mapToInt(Position::x).min().orElse(0);
		int maxY = tailPositions.stream().mapToInt(Position::y).max().orElse(0);
		int minY = tailPositions.stream().mapToInt(Position::y).min().orElse(0);
		
		for (int y = maxY; y >= minY; y--) {
			for (int x = minX; x <= maxX; x++) {
				System.out.print(tailPositions.contains(new Position(x, y))? "#":".");
			}
			System.out.println();
		}
	}
}
