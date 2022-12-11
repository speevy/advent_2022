package advent_2022;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import advent_2022.Main_2.Round;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public class Main_4 {

	public static void main(String[] args) throws Exception {
		final Main_4 main = new Main_4();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_4"))) {
			final List<ElvesPair> elvesPairs = main.parse(reader);
			System.out.println(main.totalOverlapped(elvesPairs.stream().map(main::allContained)));
			System.out.println(main.totalOverlapped(elvesPairs.stream().map(main::overlap)));
		}
	}

	@Value
	class SectionRange {
		int start;
		int end;
	}
	
	@Value
	class ElvesPair {
		final SectionRange first;
		final SectionRange last;
	}
	
	List<ElvesPair> parse(BufferedReader reader) throws IOException {
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		return lines.stream().map(this::parse).collect(toList());
	}
	
	ElvesPair parse(String line) {
		String[] numbers = line.split("[,-]");
		return new ElvesPair(
				new SectionRange(Integer.valueOf(numbers[0]), Integer.valueOf(numbers[1])),
				new SectionRange(Integer.valueOf(numbers[2]), Integer.valueOf(numbers[3])));
	}
	

	boolean allContained(ElvesPair elves) {
		return allContained(elves.getFirst(), elves.getLast()) || allContained(elves.getLast(), elves.getFirst());
	}
	
	
	private boolean allContained(SectionRange first, SectionRange last) {
		return first.getStart() <= last.getStart() && first.getEnd() >= last.getEnd();
	}

	boolean overlap(ElvesPair elves) {
		return overlap(elves.getFirst(), elves.getLast()) || overlap(elves.getLast(), elves.getFirst());
	}
	
	
	private boolean overlap(SectionRange first, SectionRange last) {
		return first.getStart() <= last.getEnd() && first.getEnd() >= last.getStart();
	}
	
	
	private long totalOverlapped(Stream<Boolean> testAllContained) {
		return testAllContained.filter(x -> x).count();
	}
	
	private static final String TEST_MESSAGE = "2-4,6-8\r\n"
			+ "2-3,4-5\r\n"
			+ "5-7,7-9\r\n"
			+ "2-8,3-7\r\n"
			+ "6-6,4-6\r\n"
			+ "2-6,4-8";

	final List<ElvesPair> testParse = List.of(
			new ElvesPair(new SectionRange(2, 4), new SectionRange(6, 8)),
			new ElvesPair(new SectionRange(2, 3), new SectionRange(4, 5)),
			new ElvesPair(new SectionRange(5, 7), new SectionRange(7, 9)),
			new ElvesPair(new SectionRange(2, 8), new SectionRange(3, 7)),
			new ElvesPair(new SectionRange(6, 6), new SectionRange(4, 6)),
			new ElvesPair(new SectionRange(2, 6), new SectionRange(4, 8))
			);
	final List<Boolean> testAllContained = List.of(false, false, false, true, true, false);
	final List<Boolean> testOverlap = List.of(false, false, true, true, true, true);

	final long testTotal = 2;
	
	@Test 
	void parseTest() throws IOException {
		Main_4 main = new Main_4();
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(
					testParse,
					main.parse(reader)
				);
		}
	}

	@Test
	void allContainedTest() {
		assertEquals(
				testAllContained,
				testParse.stream().map(this::allContained).collect(toList())
				);
	}
	
	@Test
	void overlapTest() {
		assertEquals(
				testOverlap,
				testParse.stream().map(this::overlap).collect(toList())
				);
	}
	

	@Test
	void countOverlappedTest() {
		assertEquals(testTotal, totalOverlapped(testAllContained.stream()));
	}

}
