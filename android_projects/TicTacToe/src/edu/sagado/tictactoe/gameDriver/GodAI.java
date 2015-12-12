package edu.sagado.tictactoe.gameDriver;

import java.util.ArrayList;

import edu.sagado.tictactoe.utils.Constants;

public class GodAI implements GameAI<Character>{
	
	@Override
	public int playPiece(GameManager<Character> gameManager) {
		ArrayList<Character> list = gameManager.getTileList();
		if (gameManager.getAvailableTilesPosition().size() < 7){
			int index = 0;
			for (Character c : list){
				if (c.charValue() == Constants.PLAYER_SYMBOL){
					return index;
				}
				index++;
			}
		}
		return WeakAI.randomPositionFrom(gameManager.getAvailableTilesPosition());
	}

}
