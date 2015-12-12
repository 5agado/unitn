package edu.sagado.tictactoe.gameDriver;

import static edu.sagado.tictactoe.utils.Constants.NUM_TILES_PER_ROW;

import java.util.ArrayList;

import edu.sagado.tictactoe.utils.Constants;

public class NotSoStrongAI implements GameAI<Character> {	
	@Override
	public int playPiece(GameManager<Character> manager) {		
		int res; 
		res = findMoveForAWin(manager, Constants.COMPUTER_SYMBOL);
		if (res != -1){
			return res;
		}
		res = findMoveForAWin(manager, Constants.PLAYER_SYMBOL);
		if (res != -1){
			return res;
		}
		
		return (WeakAI.randomPositionFrom(manager.getAvailableTilesPosition()));
		
	}
	
	static int findMoveForAWin (GameManager<Character> manager, Character symbol){
		ArrayList<Integer> available = manager.getAvailableTilesPosition();
		for (int i=0; i<available.size(); i++){
			int pos = available.get(i);
			int row = pos/NUM_TILES_PER_ROW;
			int col = pos%NUM_TILES_PER_ROW;
			ArrayList<Character> nextMoveTileList = new ArrayList<Character>(manager.getTileList());
			if (manager.checkWin(symbol, row, col, nextMoveTileList)){
				return pos;
			}
		}
		
		return -1;
	}

}
