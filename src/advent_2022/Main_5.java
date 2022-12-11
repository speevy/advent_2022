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
import lombok.*;

public class Main_5 {

	public static void main(String[] args) throws Exception {
		final Main_5 main = new Main_5();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_5"))) {
			Problem problem = main.parse(reader);
			List<List<Character>> stacks = problem.getStacks();
			final List<Move> moves = problem.getMoves();
			for (int i = 0; i < moves.size(); i++) {
				stacks = main.move(stacks, moves.get(i));
			}
			System.out.println(main.getResult(stacks));
		}
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_5"))) {
			Problem problem = main.parse(reader);
			List<List<Character>> stacks = problem.getStacks();
			final List<Move> moves = problem.getMoves();
			for (int i = 0; i < moves.size(); i++) {
				stacks = main.move2(stacks, moves.get(i));
			}
			System.out.println(main.getResult(stacks));
		}
	}

	@Value
	class Move {
		int number;
		int from;
		int to;
	}
	
	@Value
	class Problem {
		List<List<Character>> stacks;
		List<Move> moves;
	}
	
	private static final String TEST_MESSAGE = 
			  "    [D]    \r\n"
			+ "[N] [C]    \r\n"
			+ "[Z] [M] [P]\r\n"
			+ " 1   2   3 \r\n"
			+ "\r\n"
			+ "move 1 from 2 to 1\r\n"
			+ "move 3 from 1 to 3\r\n"
			+ "move 2 from 2 to 1\r\n"
			+ "move 1 from 1 to 2";

	final List<List<Character>> testParseStacks = List.of(
			List.of('N', 'Z'),
			List.of('D', 'C', 'M'),
			List.of('P')
			);

	final List<Move> testParseMoves = List.of(
			new Move(1, 2, 1),
			new Move(3, 1, 3),
			new Move(2, 2, 1),
			new Move(1, 1, 2)
			);

	final List<List<List<Character>>> testMoveStacks = List.of(
			List.of(
				List.of('D', 'N', 'Z'),
				List.of('C', 'M'),
				List.of('P')
				),
			List.of(
				List.of(),
				List.of('C', 'M'),
				List.of('Z', 'N', 'D', 'P')
				),

			List.of(
				List.of('M', 'C'),
				List.of(),
				List.of('Z', 'N', 'D', 'P')
				),
			List.of(
				List.of('C'),
				List.of('M'),
				List.of('Z', 'N', 'D', 'P')
				)
	);
	
	final List<List<List<Character>>> testMoveStacks2 = List.of(
			List.of(
				List.of('D', 'N', 'Z'),
				List.of('C', 'M'),
				List.of('P')
				),
			List.of(
				List.of(),
				List.of('C', 'M'),
				List.of('D', 'N', 'Z', 'P')
				),

			List.of(
				List.of('C', 'M'),
				List.of(),
				List.of('D', 'N', 'Z', 'P')
				),
			List.of(
				List.of('M'),
				List.of('C'),
				List.of('D', 'N', 'Z', 'P')
				)
	);

	final String testResult = "CMZ";
	
	@Test 
	void parseTest() throws IOException {
		Main_5 main = new Main_5();
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(
					new Problem(testParseStacks, testParseMoves),
					main.parse(reader)
				);
		}
	}

	@Test
	void moveTest() {
		moveTest(testMoveStacks, this::move);
		moveTest(testMoveStacks2, this::move2);
	}
	
	void moveTest(List<List<List<Character>>> testStacks, BiFunction<List<List<Character>>, Move, List<List<Character>>> moveFn) {
		List<List<Character>> stacks = testParseStacks
				.stream().map(ArrayList::new)
				.collect(toCollection(ArrayList::new));
		
		for (int i=0; i < testParseMoves.size(); i++) {
			stacks = moveFn.apply(stacks, testParseMoves.get(i));
			assertEquals(testStacks.get(i), stacks);
		}
	}
	
	@Test
	void resultTest() {
		assertEquals(testResult, getResult(testMoveStacks.get(testMoveStacks.size() - 1)));
	}
	
	private String getResult(List<List<Character>> list) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			res.append(list.get(i).get(0));
		}
		return res.toString();
	}
	private List<List<Character>> move(List<List<Character>> stacks, Move move) {
		final List<Character> to = stacks.get(move.getTo() - 1);
		final List<Character> from = stacks.get(move.getFrom() - 1);
		final int number = move.getNumber();
		
		for (int i = 0; i < number; i++) {
			to.add(0, from.remove(0));
		}
		return stacks;
	}
	
	private List<List<Character>> move2(List<List<Character>> stacks, Move move) {
		final List<Character> to = stacks.get(move.getTo() - 1);
		final List<Character> from = stacks.get(move.getFrom() - 1);
		final int number = move.getNumber();
		to.addAll(0, from.subList(0, number));
		for (int i = 0; i < number; i++) { 
			from.remove(0); 
		}
		return stacks;
	}

	private Problem parse(BufferedReader reader) throws IOException {
		String line;
		List<List<Character>> stacks = new ArrayList<>();
		List<Move> moves = new ArrayList<>();

		while (StringUtils.isNotBlank(line = reader.readLine())) {
			List<Optional<Character>> parsedStack = parseStack(line);
			final int size = parsedStack.size();
			for (int i = stacks.size(); i < size; i++) {
				stacks.add(new ArrayList<>());
			}
			for (int i = 0; i < size; i++) {
				Optional<Character> crate = parsedStack.get(i);
				if (crate.isPresent()) {
					stacks.get(i).add(crate.get());
				}
			}
		}
		while ((line = reader.readLine()) != null) {
			moves.add(parseMove(line));
		}
		
		return new Problem(stacks, moves);
	}

	private Move parseMove(String line) {
		String[] values = line.replaceAll("[^ 0-9]*", "").split(" ");
		return new Move(Integer.parseInt(values[1]), Integer.parseInt(values[3]), Integer.parseInt(values[5]));
	}

	private List<Optional<Character>> parseStack(String line) {
		int length = (line.length() + 1) / 4;
		List<Optional<Character>> result = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			if (line.substring(i * 4, i * 4 + 3).matches("\\[.\\]")) {
				result.add(Optional.of(line.charAt(i * 4 + 1)));
			} else {
				result.add(Optional.empty());
			}
		}
		return result;
	}

}
