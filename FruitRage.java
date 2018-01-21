package com.example.student.reimbursement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FruitRage {
	private static BufferedReader bufferedReader;
	private static int dimension = 0;
	private static int fruitTypesCount = 0;
	private static double remainingTimeInSec = 0;
	private static int[][] input = null;
	private static int[][] visited = null;
	private static char[][] output = null;
	private static String inputFilePath = "/Users/shravya/Documents/workspace-sts-3.9.0.RELEASE/StudentReimbursement/src/test/java/com/example/student/reimbursement/input1.txt";
	private static String outputFilePath = "/Users/shravya/Documents/workspace-sts-3.9.0.RELEASE/StudentReimbursement/src/test/java/com/example/student/reimbursement/output.txt";
	private static int[] dx = { -1, 1, 0, 0 };
	private static int[] dy = { 0, 0, -1, 1 };
	private static int depthLimit = 0;
	private static Boolean reachedEndState = false;
	private static Boolean immediateReturn = false;
	static FruitRage homewrk = new FruitRage();
	static BoardState finalBoard = homewrk.new BoardState();
	static Double endTime;

	public class Coordinates {
		private int row;
		private int col;

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}

	}

	public class BoardState {
		private int[][] initBoard = new int[dimension][dimension];
		private int[][] childBoard = new int[dimension][dimension];
		private int chosenRowVal;
		private int chosenColVal;
		private int childRow;
		private int childCol;
		private int ourAgentScore;
		private int oppAgentScore;
		private int boardDepth;
		private Double boardValue;
		private Double alpha;
		private Double beta;

		public BoardState(BoardState s) {
			for (int i = 0; i < dimension; i++) {
				for (int j = 0; j < dimension; j++) {
					this.initBoard[i][j] = s.initBoard[i][j];
				}
			}
			this.chosenRowVal = s.chosenRowVal;
			this.chosenColVal = s.chosenColVal;
			this.ourAgentScore = s.ourAgentScore;
			this.oppAgentScore = s.oppAgentScore;
			this.boardDepth = s.boardDepth;
			this.boardValue = s.boardValue;
			this.alpha = s.alpha;
			this.beta = s.beta;
			this.childCol = s.childCol;
			this.childRow = s.childRow;
		}

		public BoardState() {
			// TODO Auto-generated constructor stub
		}

		public int[][] getChildBoard() {
			return childBoard;
		}

		public void setChildBoard(int[][] childBoard) {
			this.childBoard = childBoard;
		}

		public int[][] getInitBoard() {
			return initBoard;
		}

		public void setInitBoard(int[][] initBoard) {
			this.initBoard = initBoard;
		}

		public int getChosenRowVal() {
			return chosenRowVal;
		}

		public void setChosenRowVal(int chosenRowVal) {
			this.chosenRowVal = chosenRowVal;
		}

		public int getChosenColVal() {
			return chosenColVal;
		}

		public void setChosenColVal(int chosenColVal) {
			this.chosenColVal = chosenColVal;
		}

		public int getOurAgentScore() {
			return ourAgentScore;
		}

		public void setOurAgentScore(int ourAgentScore) {
			this.ourAgentScore = ourAgentScore;
		}

		public int getOppAgentScore() {
			return oppAgentScore;
		}

		public void setOppAgentScore(int oppAgentScore) {
			this.oppAgentScore = oppAgentScore;
		}

		public int getBoardDepth() {
			return boardDepth;
		}

		public void setBoardDepth(int boardDepth) {
			this.boardDepth = boardDepth;
		}

		public Double getBoardValue() {
			return boardValue;
		}

		public void setBoardValue(Double boardValue) {
			this.boardValue = boardValue;
		}

		public Double getAlpha() {
			return alpha;
		}

		public void setAlpha(Double alpha) {
			this.alpha = alpha;
		}

		public Double getBeta() {
			return beta;
		}

		public void setBeta(Double beta) {
			this.beta = beta;
		}

		public int getChildRow() {
			return childRow;
		}

		public void setChildRow(int childRow) {
			this.childRow = childRow;
		}

		public int getChildCol() {
			return childCol;
		}

		public void setChildCol(int childCol) {
			this.childCol = childCol;
		}

		public List<List<Coordinates>> getAllPossibleMoves(BoardState state) {
			visited = new int[dimension][dimension];
			List<List<Coordinates>> allPossMoves = new ArrayList<List<Coordinates>>();
			for (int i = 0; i < dimension; i++) {
				for (int j = 0; j < dimension; j++) {
					if (visited[i][j] == 0 && state.getInitBoard()[i][j] != -1) {
						List<Coordinates> adjFruitCoordinates = new ArrayList<Coordinates>();
						adjFruitCoordinates = getAdjFruitList(state, i, j, state.getInitBoard()[i][j],
								adjFruitCoordinates);
						allPossMoves.add(adjFruitCoordinates);
					}
				}
			}

			// heuristic - sorting moves based on depth
			if (allPossMoves != null) {
				allPossMoves = sortBySublistSizeDesc(allPossMoves);
			}
			return allPossMoves;
		}

		public <T> List<List<T>> sortBySublistSizeDesc(List<List<T>> superList) {
			Collections.sort(superList, new Comparator<List<T>>() {
				@Override
				public int compare(List<T> o1, List<T> o2) {
					return Integer.compare(o2.size(), o1.size());
				}
			});

			return superList;
		}

		// applying gravity and making fruits fall down if * are present

		public int[][] applyGravity(int[][] matrix) {
			int colCount = dimension;
			int i = 0;
			int j = 0;
			for (int col = 0; col < dimension; col++) {
				i = colCount - 1;
				j = colCount - 2;
				while (i >= 0 && j >= 0) {
					if (matrix[i][col] != -1) {
						i--;
						j--;
					} else if (matrix[i][col] == -1 && matrix[j][col] == -1) {
						j--;
					} else if (matrix[i][col] == -1 && matrix[j][col] != -1) {
						matrix[i][col] = matrix[j][col];
						matrix[j][col] = -1;
						i--;
						j--;
					}
				}
			}
			return matrix;
		}

		public void evalFunction() {
			List<List<Coordinates>> allPossMoves = this.getAllPossibleMoves(this);
			if (this.boardDepth % 2 == 0) {
				this.setOurAgentScore((int) (this.getOurAgentScore() + Math.pow(allPossMoves.get(0).size(), 2)));
			} else {
				this.setOppAgentScore((int) (this.getOppAgentScore() + Math.pow(allPossMoves.get(0).size(), 2)));
			}
		}

		public List<Coordinates> getAdjFruitList(BoardState state, int row, int col, int val,
				List<Coordinates> coordinatesList) {
			if (state.getInitBoard()[row][col] == -1 || visited[row][col] != 0)
				return coordinatesList;

			visited[row][col] = 1;
			Coordinates coordinate = new Coordinates();
			coordinate.setRow(row);
			coordinate.setCol(col);
			coordinatesList.add(coordinate);

			// pos can be left, right, up, down
			for (int pos = 0; pos < 4; pos++) {
				int newRowPos = row + dx[pos];
				int newColPos = col + dy[pos];

				if (newRowPos >= 0 && newColPos >= 0 && newRowPos < dimension && newColPos < dimension
						&& state.getInitBoard()[newRowPos][newColPos] == val && visited[newRowPos][newColPos] == 0) {

					getAdjFruitList(state, newRowPos, newColPos, val, coordinatesList);
				}
			}
			return coordinatesList;
		}

		public BoardState generateChild(List<Coordinates> possMove) {
			BoardState childState = new BoardState(this);
			if ((this.getBoardDepth() % 2) == 0) {
				childState.setOurAgentScore((int) (this.getOurAgentScore() + Math.pow(possMove.size(), 2)));
			} else {
				childState.setOppAgentScore((int) (this.getOppAgentScore() + Math.pow(possMove.size(), 2)));
			}
			childState.setBoardDepth(this.getBoardDepth() + 1);
			childState.setChosenRowVal(possMove.get(0).getRow());
			childState.setChosenColVal(possMove.get(0).getCol());
			int[][] boardMatrix = new int[dimension][dimension];
			for (int i = 0; i < dimension; i++) {
				for (int j = 0; j < dimension; j++) {
					boardMatrix[i][j] = this.getInitBoard()[i][j];
				}
			}

			for (Coordinates coord : possMove) {
				boardMatrix[coord.getRow()][coord.getCol()] = -1;
			}
			boardMatrix = applyGravity(boardMatrix);
			childState.setInitBoard(boardMatrix);
			return childState;
		}

		public Boolean terminalTest() {
			if ((System.nanoTime() / 1000000000.0) > endTime) {
				immediateReturn = true;
				return true;
			}
			for (int i = 0; i < dimension; i++) {
				for (int j = 0; j < dimension; j++) {
					if (this.getInitBoard()[i][j] != -1) {
						if (this.getBoardDepth() < depthLimit) {
							return false;
						} else {
							this.evalFunction();
							this.setBoardValue((double) this.getOurAgentScore() - this.getOppAgentScore());
							return true;
						}
					}
				}
			}
			reachedEndState = true;
			this.setBoardValue((double) this.getOurAgentScore() - this.getOppAgentScore());
			return true;
		}

	}

	public static BoardState maxVal(BoardState state, Double alpha, Double beta) {
		if (state.terminalTest()) {
			return state;
		}
		double val = Double.NEGATIVE_INFINITY;
		state.setBoardValue(val);
		int row = state.getChosenRowVal(), col = state.getChosenColVal();
		int[][] board = new int[dimension][dimension];
		List<List<Coordinates>> allPossMoves = state.getAllPossibleMoves(state);
		for (List<Coordinates> possMove : allPossMoves) {
			BoardState possBoardState = state.generateChild(possMove);
			BoardState minBoardState = minVal(possBoardState, alpha, beta);
			val = Math.max(minBoardState.getBoardValue(), state.getBoardValue());
			if (minBoardState.getBoardValue() > state.getBoardValue()) {
				row = minBoardState.getChosenRowVal();
				col = minBoardState.getChosenColVal();
				board = minBoardState.getInitBoard();
			}
			if (val >= beta) {
				val = minBoardState.getBoardValue();
				row = minBoardState.getChosenRowVal();
				col = minBoardState.getChosenColVal();
				board = minBoardState.getInitBoard();
				state.setBoardValue(minBoardState.getBoardValue());
				state.setChildRow(minBoardState.getChosenRowVal());
				state.setChildCol(minBoardState.getChosenColVal());
				state.setChildBoard(board);
				return state;
			}
			state.setBoardValue(val);
			state.setChildRow(row);
			state.setChildCol(col);
			state.setChildBoard(board);
			alpha = Math.max(alpha, minBoardState.getBoardValue());
		}
		return state;
	}

	public static BoardState minVal(BoardState state, Double alpha, Double beta) {
		if (state.terminalTest()) {
			return state;
		}
		double val = Double.POSITIVE_INFINITY;
		state.setBoardValue(val);
		int row = state.getChosenRowVal(), col = state.getChosenColVal();
		int[][] board = new int[dimension][dimension];
		List<List<Coordinates>> allPossMoves = state.getAllPossibleMoves(state);
		for (List<Coordinates> possMove : allPossMoves) {
			BoardState possBoardState = state.generateChild(possMove);
			BoardState minBoardState = maxVal(possBoardState, alpha, beta);
			val = Math.min(minBoardState.getBoardValue(), state.getBoardValue());
			if (minBoardState.getBoardValue() < state.getBoardValue()) {
				row = minBoardState.getChosenRowVal();
				col = minBoardState.getChosenColVal();
				board = minBoardState.getInitBoard();
			}
			if (val <= alpha) {
				val = minBoardState.getBoardValue();
				row = minBoardState.getChosenRowVal();
				col = minBoardState.getChosenColVal();
				board = minBoardState.getInitBoard();
				state.setChildBoard(board);
				state.setBoardValue(minBoardState.getBoardValue());
				state.setChildRow(minBoardState.getChosenRowVal());
				state.setChildCol(minBoardState.getChosenColVal());
				return state;
			}
			state.setBoardValue(val);
			state.setChildRow(row);
			state.setChildCol(col);
			state.setChildBoard(board);
			beta = Math.min(beta, minBoardState.getBoardValue());
		}
		return state;
	}

	public static int[][] markAdjFruits(int[][] tempMatrix, int row, int col, int val) {
		if (tempMatrix[row][col] == -1 || visited[row][col] != 0)
			return tempMatrix;

		visited[row][col] = 1;
		tempMatrix[row][col] = -1;

		// pos can be left, right, up, down
		for (int pos = 0; pos < 4; pos++) {
			int newRowPos = row + dx[pos];
			int newColPos = col + dy[pos];

			if (newRowPos >= 0 && newColPos >= 0 && newRowPos < dimension && newColPos < dimension
					&& tempMatrix[newRowPos][newColPos] == val && visited[newRowPos][newColPos] == 0) {
				markAdjFruits(tempMatrix, newRowPos, newColPos, val);
			}
		}
		return tempMatrix;
	}

	public static void generateOutput(int rowNo, int colNo) {
		try {
			int a = 65;
			char outputString = (char) (a + colNo);
			List<String> lines = new ArrayList<String>();
			lines.add(String.valueOf(outputString) + (rowNo + 1));
			for (int aa = 0; aa < dimension; aa++) {
				String res = "";
				for (int bb = 0; bb < dimension; bb++) {
					res = res + output[aa][bb];
				}
				lines.add(res);
			}
			Path file = Paths.get(outputFilePath);
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// The name of the file to open.
		String fileName = inputFilePath;
		String line = null;
		try {
			FileReader fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			int lineNo = 0;

			while ((line = bufferedReader.readLine()) != null) {
				switch (lineNo) {
				case 0: {
					dimension = Integer.parseInt(line.trim());
					input = new int[dimension][dimension];
					visited = new int[dimension][dimension];
					output = new char[dimension][dimension];
					break;
				}
				case 1: {
					fruitTypesCount = Integer.parseInt(line.trim());
					break;
				}
				case 2: {
					remainingTimeInSec = Double.parseDouble(line.trim());
					break;
				}
				default: {
					String nodes = line;
					int rowNo = lineNo - 3;
					for (int i = 0; i < nodes.trim().length(); i++) {
						if (!Double.isNaN(Character.getNumericValue(nodes.trim().charAt(i))))
							input[rowNo][i] = Character.getNumericValue(nodes.trim().charAt(i));
						else
							input[rowNo][i] = -1;
					}
				}
				}
				lineNo++;
			}

			if (input == null || dimension == 0)
				return;
			FruitRage fr = new FruitRage();
			BoardState bs = fr.new BoardState();
			Double start = System.nanoTime() / 1000000000.0;

			int[][] tempBoard = new int[dimension][dimension];
			for (int i = 0; i < dimension; i++) {
				for (int j = 0; j < dimension; j++) {
					tempBoard[i][j] = input[i][j];
				}
			}
			bs.setInitBoard(tempBoard);
			bs.getAllPossibleMoves(bs);

			/**
			 * heuristic to calculate the time we can effectively use for current move based
			 * on branching factor and total time to play the entire game.
			 */
			double branchingFactor = bs.getAllPossibleMoves(bs).size();
			int bf = (int) Math.ceil(branchingFactor / 2.0);
			double numerator = Math.pow(Math.E, Math.log(remainingTimeInSec) / bf);
			double dividingFactor = numerator / (numerator - 1);

			/**
			 * finding out the time remaining, applying different strategies if the
			 * remaining time is more than 5 sec, if remaining time is between 5 to 0.5 sec
			 * and less than 0.5 sec
			 **/

			if (remainingTimeInSec > 5 && (Math.floor(remainingTimeInSec / dividingFactor) > 1)) {
				remainingTimeInSec = Math.floor(remainingTimeInSec / dividingFactor);
			} else if ((remainingTimeInSec > 5 && (Math.floor(remainingTimeInSec / dividingFactor) < 1))
					|| (remainingTimeInSec <= 5 && remainingTimeInSec >= 0.5)) {
				remainingTimeInSec = remainingTimeInSec / 2;
			} else {
				/** select one non-* move as the time remaining is very less (instead of losing
				 the game) **/
				for (int i = 0; i < dimension; i++) {
					for (int j = 0; j < dimension; j++) {
						if (input[i][j] != -1) {
							visited = new int[dimension][dimension];
							input = markAdjFruits(input, i, j, input[i][j]);
							input = bs.applyGravity(input);
							for (int row = 0; row < dimension; row++) {
								for (int col = 0; col < dimension; col++) {
									if (input[row][col] == -1)
										output[row][col] = '*';
									else
										output[row][col] = (char) (input[row][col] + '0');
								}
							}
							generateOutput(i, j);
							return;
						}
					}
				}
			}

			// calculating end time to be on safe side
			endTime = (start) + (remainingTimeInSec - 0.2);

			// using iterative deepening technique to decide on till what depth the minimax
			// algo should run
			while ((System.nanoTime() / 1000000000.0) < endTime && !reachedEndState) {
				depthLimit++;
				BoardState state = fr.new BoardState();
				state.setAlpha(Double.NEGATIVE_INFINITY);
				state.setBeta(Double.POSITIVE_INFINITY);
				state.setBoardValue(Double.NEGATIVE_INFINITY);
				state.setBoardDepth(0);
				state.setChosenColVal(-1);
				state.setChosenRowVal(-1);
				int[][] board = new int[dimension][dimension];
				for (int i = 0; i < dimension; i++) {
					for (int j = 0; j < dimension; j++) {
						board[i][j] = input[i][j];
					}
				}
				state.setInitBoard(board);
				state.setOppAgentScore(0);
				state.setOurAgentScore(0);
				BoardState outputState = maxVal(state, state.getAlpha(), state.getBeta());

				if (immediateReturn) {
					generateOutput(finalBoard.getChildRow(), finalBoard.getChildCol());
					return;
				}

				for (int i = 0; i < dimension; i++) {
					for (int j = 0; j < dimension; j++) {
						if (outputState.getChildBoard()[i][j] == -1)
							output[i][j] = '*';
						else
							output[i][j] = (char) (outputState.getChildBoard()[i][j] + '0');
					}
				}

				finalBoard = fr.new BoardState(outputState);

			}
			generateOutput(finalBoard.getChildRow(), finalBoard.getChildCol());
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
	}
}
