package edu.sagado.tictactoe.gameDriver;

import static edu.sagado.tictactoe.utils.Constants.*;

import java.util.ArrayList;

public class StrongAI implements GameAI<Character> {	
	//FIXME
	//merge methods & remove the pair
	
	@Override
	public int playPiece(GameManager<Character> manager) {
		Pair res = maximazedMove(manager);
		return res.move;	
	}
	
	/*
	 * Chose the move that leads to the maximum score for the computer
	 */
	private Pair maximazedMove(GameManager<Character> manager){
		int score = -1;
		int bestScore = -10;
		int bestMovePos = -10;
		
		ArrayList<Integer> available = manager.getAvailableTilesPosition();
		for (int i=0; i<available.size(); i++){
			int pos = available.get(i);
			manager.changeTileValue(pos, COMPUTER_SYMBOL);
			char winner = manager.checkForAWin().charValue();
			if (winner != TILE_STATE_EMPTY){
				score = (winner == COMPUTER_SYMBOL)? 1:-1;
			}
			else {
				Pair result = minimazedMove(manager);
				score = result.score;
			}
			
			manager.changeTileValue(pos, TILE_STATE_EMPTY);
			if (score > bestScore){
				if (score == 1){
					return new Pair(pos, score);
				}
				bestScore = score;
				bestMovePos = pos;
			}
		}
		
		if (available.isEmpty()){
			bestScore = 0;
		}
		
		Pair res = new Pair(bestMovePos, bestScore);
		return res;
		
	}
	
	/*
	 * Chose the move that leads to the minimum score for the player
	 */
	private Pair minimazedMove(GameManager<Character> manager){
		int score = 1;
		int bestScore = 10;
		int bestMovePos = 10;
		
		ArrayList<Integer> available = manager.getAvailableTilesPosition();
		for (int i=0; i<available.size(); i++){
			int pos = available.get(i);
			manager.changeTileValue(pos, PLAYER_SYMBOL);
			char winner = manager.checkForAWin().charValue();
			if (winner != TILE_STATE_EMPTY){
				score = (winner == COMPUTER_SYMBOL)? 1:-1;
			}
			else {
				Pair result = maximazedMove(manager);
				score = result.score;
			}
			
			manager.changeTileValue(pos, TILE_STATE_EMPTY);
			if (score < bestScore){
				if (score == -1){
					return new Pair(pos, score);
				}
				bestScore = score;
				bestMovePos = pos;
			}
		}
		
		if (available.isEmpty()){
			bestScore = 0;
		}
		
		Pair res = new Pair(bestMovePos, bestScore);
		return res;		
	}
	
	//Used for the return type of the private methods
	private class Pair{
		public int move;
		public int score;
		
		public Pair(int move, int score){
			this.move = move;
			this.score = score;
		}
	}
	
}
