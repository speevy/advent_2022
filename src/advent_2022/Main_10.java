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

import lombok.*;

public class Main_10 {

	private static final String ADDX = "addx ";

	private static final String NOOP = "noop";


	public static void main(String[] args) throws Exception {
		final Main_10 main = new Main_10();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_10"))) {
			List<CpuStatus> cicles = main.execute(main.parse(reader));
			System.out.println(main.getTotal(cicles));
			System.out.println(cicles.size());
			List<String> draw = main.draw(cicles);
			for (var s : draw) {
				System.out.println(s);
			}
		}
	}

	enum InstructionType { ADDX, NOOP };
	
	record Instruction (InstructionType type, Integer value) {}
	record CpuStatus (Integer x) {}

	@SneakyThrows
	private List<Instruction> parse(BufferedReader reader) {
		String line;
		List<Instruction> movements = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			movements.add(parse(line));
		}
		return movements;
	}

	private Instruction parse(String line) {
		if (line.startsWith(NOOP)) {
			return new Instruction(InstructionType.NOOP, null);
		}
		
		if (line.startsWith(ADDX)) {
			return new Instruction(InstructionType.ADDX, Integer.parseInt(line.substring(ADDX.length())));
		}
		
		throw new IllegalArgumentException();
	}
	
	private List<CpuStatus> execute(List<Instruction> instructions) {
		List<CpuStatus> result = new ArrayList<>();
		CpuStatus status = new CpuStatus(1); 
		result.add(status);
		for (Instruction instruction: instructions) {
			switch(instruction.type()) {
			case NOOP -> result.add(status);
			case ADDX -> {
				result.add(status);
				status = new CpuStatus(status.x() + instruction.value());
				result.add(status);
			}
			}
		}
		return result;
	}	

	private int getValue(List<CpuStatus> cicles, int cicle) {
		return cicle * cicles.get(cicle - 1).x();
	}
	
	private int getTotal(List<CpuStatus> cicles) {
		int res = 0;
		
		for (int i = 20; i < cicles.size(); i+=40) {
			res += getValue(cicles, i);
		}
		
		return res;
	}
	
	private List<String> draw(List<CpuStatus> cicles) {
		int pos = 0;
		List<StringBuilder> result = new ArrayList<>();
		StringBuilder line = new StringBuilder(); 
		for (CpuStatus status : cicles.subList(0, 240)) {
			if (pos % 40 == 0) {
				pos = 0;
				line = new StringBuilder();
				result.add(line);
			}
			if (status.x() >= pos - 1 && status.x() <= pos + 1) {
				line.append("#");
			} else {
				line.append(".");
			}
			pos++;
		}

		return result.stream().map(StringBuilder::toString).collect(toList());
	}
	
	private static final String TEST_MESSAGE = "noop\r\n"
			+ "addx 3\r\n"
			+ "addx -5";
	
	private static final List<Instruction> testInstructions = List.of(
			new Instruction(InstructionType.NOOP, null),
			new Instruction(InstructionType.ADDX, 3),
			new Instruction(InstructionType.ADDX, -5)
			);

	private static final List<CpuStatus> testCicles = List.of(1, 1, 1, 4, 4, -1).stream().map(CpuStatus::new).collect(toList());


	@Test 
	void parseTest() throws IOException {
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			assertEquals(testInstructions, parse(reader));
		}
	}

	@Test
	void executionTest() {
		assertEquals(testCicles, execute(testInstructions));
	}

	@Test @SneakyThrows
	void executionTest2() {
		List<CpuStatus> cicles;
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_10.test"))) {
			cicles = execute(parse(reader));
			                   
			testStatus(cicles, 20,  21);
			testStatus(cicles, 60,  19);
			testStatus(cicles, 100, 18);
			testStatus(cicles, 140, 21);
			testStatus(cicles, 180, 16);
			testStatus(cicles, 220, 18);
		}
	}
	
	void testStatus(List<CpuStatus> cicles, int cicle, int x) {
		System.out.println(" ----- " + cicle + " ------");
		for (int i = cicle - 3; i <= cicle + 3 && i < cicles.size(); i++) {
			System.out.print(cicles.get(i).x());
			if (i == cicle - 1) {
				System.out.print(" * " + x);
			}
			System.out.println();
		}
		assertEquals(new CpuStatus(x), cicles.get(cicle - 1));
	}
	
	@Test @SneakyThrows
	void valuesTest() {
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_10.test"))) {
			List<CpuStatus> cicles = execute(parse(reader));
			testValue(cicles, 20,  420);
			testValue(cicles, 60,  1140);
			testValue(cicles, 100, 1800);
			testValue(cicles, 140, 2940);
			testValue(cicles, 180, 2880);
			testValue(cicles, 220, 3960);
			assertEquals(13140, getTotal(cicles));
		}
	}

	private void testValue(List<CpuStatus> cicles, int cicle, int value) {
		assertEquals(value, getValue(cicles, cicle));
	}

	List<String> testDraw = List.of(
			"##..##..##..##..##..##..##..##..##..##..",
			"###...###...###...###...###...###...###.",
			"####....####....####....####....####....",
			"#####.....#####.....#####.....#####.....",
			"######......######......######......####",
			"#######.......#######.......#######....."	
			); 
			
	@Test @SneakyThrows
	void drawTest() {
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_10.test"))) {
			List<CpuStatus> cicles = execute(parse(reader));
			assertEquals(testDraw, draw(cicles));
		}
	}


}
