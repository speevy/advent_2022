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

public class Main_8 {

	private static final String DIR = "dir ";

	private static final String LS = "$ ls";

	private static final String CD = "$ cd ";

	public static void main(String[] args) throws Exception {
		final Main_8 main = new Main_8();
		try(BufferedReader reader = new BufferedReader(new FileReader("files/input_8"))) {
			final int [][] heights = main.parse(reader);
			final boolean [][] visibles = main.checkVisibles(heights);
			System.out.println(main.visibleCount(visibles));
			System.out.println(main.maxScore(main.scenicScores(heights)));
		}
	}

	private static final String TEST_MESSAGE = 
			  "30373\r\n"
			  + "25512\r\n"
			  + "65332\r\n"
			  + "33549\r\n"
			  + "35390";
	
	private static final int heightTest[][] = {
			{3, 0, 3, 7, 3},
			{2, 5, 5, 1, 2},
			{6, 5, 3, 3, 2},
			{3, 3, 5, 4, 9},
			{3, 5, 3, 9, 0}
	};

	private static final boolean visibleTest[][] = {
			{true, true, true, true, true},
			{true, true, true, false, true},
			{true, true, false, true, true},
			{true, false, true, false, true},
			{true, true, true, true, true}
	};
	

	@Test 
	void parseTest() throws IOException {
		final int[][] treeHeights;
		
		try (BufferedReader reader = new BufferedReader(new StringReader(TEST_MESSAGE))) {
			treeHeights = parse(reader);
		}
		
		for (int i=0; i < heightTest.length; i++) {
			int[] row = heightTest[i];
			for (int j = 0; j < row.length; j++) {
				assertEquals(heightTest[i][j], treeHeights[i][j]);
			}
		}
		
	}
	
	@SneakyThrows
	private int[][]  parse(BufferedReader reader) {
		String line;
		List<int[]> res = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			int numbers[] = new int[line.length()];
			for (int i = 0; i< line.length(); i++) {
				numbers[i] = Integer.parseInt(line, i, i + 1, 10);
			}
			res.add(numbers);
		}
		
		return res.toArray(new int[][] {});
	}
	
	@Test
	void visibleTest() {
		boolean visibles[][] = checkVisibles(heightTest);
		for (int i=0; i < visibleTest.length; i++) {
			boolean[] row = visibleTest[i];
			for (int j = 0; j < row.length; j++) {
				assertEquals(visibleTest[i][j], visibles[i][j], String.format("row: %d, column %d", i, j));
			}
		}
	}

	private boolean[][] checkVisibles(int[][] treeHeights) {
		final int rowCount = treeHeights.length;
		boolean [][] result = new boolean[rowCount][];
		
		result[0] = rowVisible(treeHeights[0].length);
		result[rowCount - 1] = rowVisible(treeHeights[rowCount - 1].length);
		
		for (int i = 1; i < rowCount - 1; i++) {
			boolean row[] = new boolean[treeHeights[i].length];
			row[0] = true;
			row[row.length - 1] = true;
			for (int j = 1; j < row.length - 1; j++) {
				row[j] = checkVisible(treeHeights, i, j);
			}
			result[i] = row;
		}
		return result;
	}

	private boolean checkVisible(int[][] treeHeights, int row, int column) {
		int height = treeHeights[row][column];
		boolean top = true,  bottom = true, left = true, right = true;
		
		return checkLeft(treeHeights, row, column, height) ||
			checkRight(treeHeights, row, column, height) ||
			checkTop(treeHeights, row, column, height) ||
			checkBottom(treeHeights, row, column, height);
	}

	private boolean checkLeft(int[][] treeHeights, int row, int column, int height) {
		for (int i = row - 1; i >= 0; i--) {
			if (height <= treeHeights[i][column]) {
				return false;
			}
		}
		return true;
	}

	private boolean checkRight(int[][] treeHeights, int row, int column, int height) {
		for (int i = row + 1; i < treeHeights.length; i++) {
			if (height <= treeHeights[i][column]) {
				return false;
			}
		}
		return true;
	}

	private boolean checkTop(int[][] treeHeights, int row, int column, int height) {
		for (int i = column - 1; i >= 0; i--) {
			if (height <= treeHeights[row][i]) {
				return false;
			}
		}
		return true;
	}

	private boolean checkBottom(int[][] treeHeights, int row, int column, int height) {
		for (int i = column + 1; i < treeHeights[row].length; i++) {
			if (height <= treeHeights[row][i]) {
				return false;
			}
		}
		return true;
	}

	private boolean[] rowVisible(int length) {
		boolean res[] = new boolean[length];
		for (int i = 0; i < length; i++) {
			res[i] = true;
		}
		return res;
	}
	
	@Test
	void visibleCount()
	{
		assertEquals(21, visibleCount(visibleTest));
	}

	private int visibleCount(boolean[][] visible) {
		int count = 0;
		for (int i=0; i < visible.length; i++) {
			boolean[] row = visible[i];
			for (int j = 0; j < row.length; j++) {
				if (row[j]) {
					count++;
				}
			}
		}	
		return count;
	}

	@Test
	void scenicScores() {
		assertEquals(1, scoreTop(heightTest, 1, 2, heightTest[1][2]));
		assertEquals(1, scoreLeft(heightTest, 1, 2, heightTest[1][2]));
		assertEquals(2, scoreRight(heightTest, 1, 2, heightTest[1][2]));
		assertEquals(2, scoreBottom(heightTest, 1, 2, heightTest[1][2]));
		
		assertEquals(2, scoreTop(heightTest, 3, 2, heightTest[3][2]));
		assertEquals(2, scoreLeft(heightTest, 3, 2, heightTest[3][2]));
		assertEquals(2, scoreRight(heightTest, 3, 2, heightTest[3][2]));
		assertEquals(1, scoreBottom(heightTest, 3, 2, heightTest[3][2]));
	}

	@Test
	void scenicScore() {
		int[][] scores = scenicScores(heightTest);
		
		assertEquals(4, scores[1][2]);
		assertEquals(8, scores[3][2]);
		
	}

	private int[][] scenicScores(int[][] heights) {
		int scores[][] = new int[heights.length][];
		for (int i=0; i < scores.length; i++) {
			int[] row = new int[heights[i].length];
			for (int j = 0; j < row.length; j++) {
				row[j] = calcScore(heights, i, j);
			}
			scores[i] = row;
		}
		return scores;
	}

	private int calcScore(int[][] heights, int i, int j) {
		//margins
		if (i == 0 || j == 0 || i == heights.length -1 || j == heights[i].length -1 ) {
			return 0;
		}
		
		final int height = heights[i][j];
		return scoreLeft(heights, i, j, height) 
				* scoreRight(heights, i, j, height) 
				* scoreTop(heights, i, j, height) 
				* scoreBottom(heights, i, j, height);
	}
	
	private int scoreTop(int[][] treeHeights, int row, int column, int height) {
		int count = 1;
		for (int i = row - 1; i > 0; i--) {
			if (height <= treeHeights[i][column]) {
				break;
			}
			count++;
		}
		return count;
	}

	private int scoreBottom(int[][] treeHeights, int row, int column, int height) {
		int count = 1;
		for (int i = row + 1; i < treeHeights.length - 1; i++) {
			if (height <= treeHeights[i][column]) {
				break;
			}
			count++;
		}
		return count;
	}

	private int scoreLeft(int[][] treeHeights, int row, int column, int height) {
		int count = 1;
		for (int i = column - 1; i > 0; i--) {
			if (height <= treeHeights[row][i]) {
				break;
			}
			count++;
		}
		return count;
	}

	private int scoreRight(int[][] treeHeights, int row, int column, int height) {
		int count = 1;
		for (int i = column + 1; i < treeHeights[row].length - 1; i++) {
			if (height <= treeHeights[row][i]) {
				break;
			}
			count++;
		}
		return count;
	}

	@Test
	void maxScore() {
		assertEquals(8, maxScore(scenicScores(heightTest)));
	}

	private int maxScore(int[][] scenicScores) {
		int max = 0;
		for (int i=0; i < scenicScores.length; i++) {
			int[] row = scenicScores[i];
			for (int j = 0; j < row.length; j++) {
				if (row[j] > max) {
					max = row[j];
				}
			}
		}
		return max;
	}
}
