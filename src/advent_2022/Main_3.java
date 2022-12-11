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

import advent_2022.Main_2.Round;
import lombok.Value;

public class Main_3 {


	public static void main(String[] args) throws Exception {
		final Main_3 main = new Main_3();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_3"))) {
			List<Character> repeatedLetters = main.parse(reader);
			List<Integer> priorities = main.getPriorities(repeatedLetters);
			System.out.println(main.totalPriorities(priorities));
		}
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_3"))) {
			List<Character> repeatedLetters = main.parse2(reader);
			List<Integer> priorities = main.getPriorities(repeatedLetters);
			System.out.println(main.totalPriorities(priorities));
		}

	}

	List<Character> parse(BufferedReader reader) throws IOException {
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		return lines.stream().map(this::findRepeated).collect(toList());
	}
	
	List<Character> parse2(BufferedReader reader) throws IOException {
		String line;
		List<String> lines = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		List<Character> result = new ArrayList<>();
		
		for (int i = 0; i < lines.size(); i+=3) {
			result.add(findRepeated(lines.get(i), lines.get(i + 1), lines.get(i + 2)));
		}
		
		return result;
	}

	private Character findRepeated(String line1, String line2, String line3) {
		int l = line1.length();
				
		for (int i = 0; i < l; i++) {
			Character c = line1.charAt(i);
			final String s = "" + c;
			if (line2.contains(s) && line3.contains(s)) return c;
		}
		
		throw new IllegalArgumentException();
	}

	Character findRepeated(String line) {
		int l = line.length() / 2;
		String other = line.substring(l);
		
		for (int i = 0; i < l; i++) {
			Character c = line.charAt(i);
			if (other.contains("" + c)) return c;
		}
		
		throw new IllegalArgumentException();
	}
	
	private List<Integer> getPriorities(List<Character> repeatedLetters) {
		
		return repeatedLetters.stream().map(c -> {
			if (c >= 'a' && c <= 'z') return c - 'a' + 1;
			if (c >= 'A' && c <= 'Z') return c - 'A' + 27;
			throw new IllegalArgumentException();
		}).collect(toList());
	}

	int totalPriorities(List<Integer> rounds) {
		return rounds.stream().mapToInt(Integer::valueOf).sum();
	}
	
	private static final String TEST_MESSAGE = "vJrwpWtwJgWrhcsFMMfFFhFp\r\n"
			+ "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL\r\n"
			+ "PmmdzqPrVvPwwTWBwg\r\n"
			+ "wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn\r\n"
			+ "ttgJtRGJQctTZtZT\r\n"
			+ "CrZsJsPPZsGzwwsLwLmpwMDw";

	final List<Character> testRepeatedLetters = List.of('p', 'L', 'P', 'v', 't', 's');
	final List<Integer> testPriorities = List.of(16, 38, 42, 22, 20, 19);
	final int testTotal = 157;
	
	@Test 
	void findRepeatedTest() throws IOException {
		Main_3 main = new Main_3();
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(
					testRepeatedLetters,
					main.parse(reader)
				);
		}
	}

	@Test 
	void getPrioritiesTest() throws IOException {
		Main_3 main = new Main_3();
			assertEquals(
					testPriorities,
					main.getPriorities(testRepeatedLetters)
				);
	}

	@Test 
	void totalPrioritiesTest() throws IOException {
		Main_3 main = new Main_3();
			assertEquals(
					testTotal,
					main.totalPriorities(testPriorities)
				);
	}

	final List<Character> testRepeatedLetters2 = List.of('r', 'Z');
	@Test 
	void findRepeated2Test() throws IOException {
		Main_3 main = new Main_3();
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(
					testRepeatedLetters2,
					main.parse2(reader)
				);
		}
	}

}
