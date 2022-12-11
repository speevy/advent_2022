package advent_2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Main_1 {

	public static void main(String[] args) throws IOException {
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_1"))) {
			List<List<Long>> elfes = parseElfesFood(reader);
			Long max = elfes.stream().map(elf -> elf.stream().mapToLong(Long::valueOf).sum())
				.mapToLong(Long::valueOf).max().orElse(0L);
			System.out.println(max);
			System.out.println(elfes.stream()
					.map(elf -> elf.stream().mapToLong(Long::valueOf).sum())
					.sorted(Comparator.reverseOrder())
					.limit(3)
					.mapToLong(Long::valueOf).sum());
					
		}
	}

	private static List<List<Long>> parseElfesFood(BufferedReader reader) throws IOException {
		List<List<Long>> elfes = new ArrayList<>();
		List<Long> elf = nextElf(elfes);
		String line;
		while ((line = reader.readLine()) != null) {
			if (StringUtils.isBlank(line)) {
				elf = nextElf(elfes);
			} else {
				elf.add(Long.parseLong(line));
			}
		}
		return elfes;
	}
	
	private static List<Long> nextElf(List<List<Long>> elfes) {
		List<Long> elf = new ArrayList<>();
		elfes.add(elf);
		return elf;
	}

}
