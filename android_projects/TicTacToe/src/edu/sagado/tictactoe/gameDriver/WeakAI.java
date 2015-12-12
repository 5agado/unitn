package edu.sagado.tictactoe.gameDriver;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

public class WeakAI implements GameAI<Character> {

	@Override
	public int playPiece(GameManager<Character> gameManager) {
		return randomPositionFrom(gameManager.getAvailableTilesPosition());
	}
	
	static int randomPositionFrom(ArrayList<Integer> available){
		if (available == null || available.isEmpty()){
			Log.e("randomPositionFrom", "empty available list");
			return -1;
		}
		
		Random rand;
		rand = new Random();
		int randInt = rand.nextInt(available.size()-1);
		int randTilePosition = available.get(randInt).intValue();
		return randTilePosition;
	}

}
