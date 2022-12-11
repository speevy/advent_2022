package advent_2022;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.Value;

public class Main_2 {


	public static void main(String[] args) throws Exception {
		final Main_2 main_2 = new Main_2();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_2"))) {
			List<Round> rounds = main_2.parse(reader);
			
			System.out.println(main_2.totalPoints(rounds));
		}
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_2"))) {
			List<Round> rounds = main_2.parse2(reader);
			
			System.out.println(main_2.totalPoints(rounds));
		}

	}

	int totalPoints(List<Round> rounds) {
		return rounds.stream().mapToInt(Round::points).sum();
	}
	
	List<Round> parse2(BufferedReader reader) throws IOException {
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		return lines.stream().map(this::lineToRound2).collect(toList());
	}
	
	Round lineToRound2(String line) {
		Shape ours, theirs;
		switch (line.charAt(0)) {
		case 'A': theirs = Shape.ROCK; break;
		case 'B': theirs = Shape.PAPER; break;
		case 'C': theirs = Shape.SCISSORS; break;
		default: throw new IllegalArgumentException();
		}

		switch (line.charAt(2)) {
		case 'X': switch(theirs) {
			case ROCK: ours = Shape.SCISSORS; break;
			case PAPER: ours = Shape.ROCK; break;
			case SCISSORS: ours = Shape.PAPER; break;
			default: throw new IllegalArgumentException();
		} break;
		case 'Y': ours = theirs; break;
		case 'Z':  switch(theirs) {
			case ROCK: ours = Shape.PAPER; break;
			case PAPER: ours = Shape.SCISSORS; break;
			case SCISSORS: ours = Shape.ROCK; break;
			default: throw new IllegalArgumentException();
		} break;
		default: throw new IllegalArgumentException();
		}

		return new Round(ours, theirs);
	}
	
	
	List<Round> parse(BufferedReader reader) throws IOException {
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		return lines.stream().map(this::lineToRound).collect(toList());
	}
	
	Round lineToRound(String line) {
		Shape ours, theirs;
		switch (line.charAt(0)) {
		case 'A': theirs = Shape.ROCK; break;
		case 'B': theirs = Shape.PAPER; break;
		case 'C': theirs = Shape.SCISSORS; break;
		default: throw new IllegalArgumentException();
		}

		switch (line.charAt(2)) {
		case 'X': ours = Shape.ROCK; break;
		case 'Y': ours = Shape.PAPER; break;
		case 'Z': ours = Shape.SCISSORS; break;
		default: throw new IllegalArgumentException();
		}

		return new Round(ours, theirs);
	}
	
	enum Shape {
		ROCK,
		PAPER,
		SCISSORS;
	};
	
	enum Result {
		WIN,
		DRAW,
		LOSE;
	}
	
	@Value
	class Round {
		private final Shape ours;
		private final Shape theirs;
		
		Result play() {
			switch (ours) {
			case ROCK: 
				switch(theirs) {
				case ROCK: return Result.DRAW;
				case PAPER: return Result.LOSE;
				case SCISSORS: return Result.WIN;
				}
			case PAPER: 
				switch(theirs) {
				case ROCK: return Result.WIN;
				case PAPER: return Result.DRAW;
				case SCISSORS: return Result.LOSE;
				}
			case SCISSORS: 
				switch(theirs) {
				case ROCK: return Result.LOSE;
				case PAPER: return Result.WIN;
				case SCISSORS: return Result.DRAW;
				}
			}
			throw new IllegalArgumentException();
		}
		
		int pointsShape() {
			switch (ours) {
			case ROCK: return 1;
			case PAPER: return 2;
			case SCISSORS: return 3;
			}
			throw new IllegalArgumentException();
		}
		
		int pointsPlay() {
			switch(play()) {
			case WIN: return 6;
			case DRAW: return 3;
			case LOSE: return 0;
			}
			throw new IllegalArgumentException();
		}
		
		public int points() {
			return pointsShape() + pointsPlay();
		}
	}
	
	private static final String TEST_MESSAGE = "A Y\nB X\nC Z\n";
	private final List<Round> testRounds = List.of(new Round(Shape.PAPER, Shape.ROCK), new Round(Shape.ROCK, Shape.PAPER), new Round(Shape.SCISSORS, Shape.SCISSORS));
	private final List<Round> testRounds2 = List.of(new Round(Shape.ROCK, Shape.ROCK), new Round(Shape.ROCK, Shape.PAPER), new Round(Shape.ROCK, Shape.SCISSORS));

	@Test 
	void shapeDecode() throws IOException {
		Main_2 main = new Main_2();
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(
					testRounds,
					main.parse(reader)
				);
		}
	}

	@Test 
	void shapeDecode2() throws IOException {
		Main_2 main = new Main_2();
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(
					testRounds2,
					main.parse2(reader)
				);
		}
	}


	@Test 
	void points() throws IOException {
		Round round = testRounds.get(0);
		assertEquals(2, round.pointsShape());
		assertEquals(6, round.pointsPlay());
		assertEquals(8, round.points());
		
		round = testRounds.get(1);
		assertEquals(1, round.pointsShape());
		assertEquals(0, round.pointsPlay());
		assertEquals(1, round.points());

		round = testRounds.get(2);
		assertEquals(3, round.pointsShape());
		assertEquals(3, round.pointsPlay());
		assertEquals(6, round.points());
		
		assertEquals(15, totalPoints(testRounds));
	}

}
