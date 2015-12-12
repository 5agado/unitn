package edu.sagado.tictactoe.gameDriver;

import java.io.Serializable;
import java.util.ArrayList;

public interface GameManager<E> extends Serializable{
 
	 /**
	  * Set the list of tiles for the board
	  * @param tileList
	  */
	 public void setTileList(ArrayList<E> tileList);
	 
	 /**
	  * Get the list of tiles of the board
	  * @param tileList
	  */
	 public ArrayList<E> getTileList();
	 
	 /**
	  * Get a list of the available tiles for playing
	  * @return An ArrayList of available tiles on the board
	  */
	 public ArrayList<E> getAvailableTiles();
	 
	 /**
	  * Get a list of the available tiles position
	  * @return An ArrayList of positions of the available tiles
	  */
	 public ArrayList<Integer> getAvailableTilesPosition();
	 
	 /**
	  * Set the GameAI to be used in the game
	  * @param gameAI the AI to use
	  */
	 public void setGameAI(GameAI<E> gameAI);
	 
	 /**
	  * Called on computer turn
	  * @param the position of the tile in the array
	  * @return the position of the chosen tile
	  */
	 public int playAIMove();
	 
	 /**
	  * Called when the player chooses a tile
	  * @param the position of the chosen tile
	  */
	 public void playerMove(int tilePosition);
	 
	 /**
	  * Called when a player clicks a tile
	  * @param the position of the tile in the array
	  * @param the new value for the tile
	  */
	 public void changeTileValue(int tilePosition, E tileValue); 
	 
	 /**
	  * Check if with the new tilePiece in the position row/col the game goes in a win state
	  * The method will assign the given tilePiece to the chosen position
	  * if this is not already set accordingly.  
	  * @param tilePiece
	  * @param row y coordinate for the position in the grid
	  * @param col x coordinate for the position in the grid
	  * @param tileList the tileList where to check for a win
	  * @return
	  */
	 public boolean checkWin(E tilePiece, int row, int col, ArrayList<E> tileList);
	 
	 /**
	  * Check if someone has won
	  * @return the tilePiece of the winner, on the empty one if nobody has won
	  */
	 public E checkForAWin();
	 
	 /**
	  * Get the current state of the game
	  * @return An integer value representing the current state of the game
	  */
	 public int getGameState();
	 
	 /**
	  * Set the current state of the game to the given value
	  * @param gameState
	  */
	 public void setGameState(int gameState);
}
