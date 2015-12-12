package edu.sagado.tictactoe.utils;

/**
 * Defines general constants for project
 * @author 5agado
 *
 */
public class Constants {
	 
	//SYMBOLS
	public static final char TILE_STATE_EMPTY = '_';
	 public static final char TILE_STATE_X = 'X';
	 public static final char TILE_STATE_O = 'O';
	 
	 public static char COMPUTER_SYMBOL;
	 public static char PLAYER_SYMBOL;

	 //GAME STATE
	 public static final int GAME_STATE_READY = 0;
	 public static final int GAME_STATE_PLAYING = 1;
	 public static final int GAME_STATE_COMPUTER_TURN = 2;
	 public static final int GAME_STATE_PLAYER_TURN = 3;
	 public static final int GAME_STATE_PLAYER_WINS = 4;
	 public static final int GAME_STATE_COMPUTER_WINS = 5;
	 public static final int GAME_STATE_TIE = 6;
	 
	 //AI RELATED
	 public static final String AI_PARAM_NAME = "gameAI";
	 public static final int WEAK_AI = 0;
	 public static final int NOTSOSTRONG_AI = 2;
	 public static final int STRONG_AI = 4;
	 public static final int GOD_AI = 5;
	 
	 //GRID VALUES
	 public static final int NUM_TILES = 9;
	 public static final int NUM_TILES_PER_ROW = 3;
	 public static final int NUM_TILES_PER_COL = NUM_TILES/NUM_TILES_PER_ROW;
}