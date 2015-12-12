package edu.sagado.tictactoe.gameDriver;

public interface GameAI<E>{
	
	/**
	  * AI choice for the next move
	  * @param chosen tile position in the linear array
	  * that describes the game grid
	  */
	public int playPiece(GameManager<E> gameManager);

}
