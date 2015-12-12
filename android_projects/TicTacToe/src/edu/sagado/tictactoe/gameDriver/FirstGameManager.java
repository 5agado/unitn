package edu.sagado.tictactoe.gameDriver;

import static edu.sagado.tictactoe.utils.Constants.COMPUTER_SYMBOL;
import static edu.sagado.tictactoe.utils.Constants.GAME_STATE_COMPUTER_TURN;
import static edu.sagado.tictactoe.utils.Constants.GAME_STATE_COMPUTER_WINS;
import static edu.sagado.tictactoe.utils.Constants.GAME_STATE_PLAYER_TURN;
import static edu.sagado.tictactoe.utils.Constants.GAME_STATE_PLAYER_WINS;
import static edu.sagado.tictactoe.utils.Constants.GAME_STATE_READY;
import static edu.sagado.tictactoe.utils.Constants.GAME_STATE_TIE;
import static edu.sagado.tictactoe.utils.Constants.NUM_TILES;
import static edu.sagado.tictactoe.utils.Constants.NUM_TILES_PER_COL;
import static edu.sagado.tictactoe.utils.Constants.NUM_TILES_PER_ROW;
import static edu.sagado.tictactoe.utils.Constants.PLAYER_SYMBOL;
import static edu.sagado.tictactoe.utils.Constants.TILE_STATE_EMPTY;

import java.util.ArrayList;

public class FirstGameManager implements GameManager<Character> {	
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Character> tileList;
	transient private GameAI<Character> gameAI;
	private int gameState;
	
	/**
	 * New FirstGameManager with gameState=GAME_STATE_READY
	 * waiting for the assignment of the player to start 
	 */
	public FirstGameManager() {
		newEmptyTileList();
		gameState = GAME_STATE_READY;
	}
	
	/**
	 * 
	 * @param gameState 
	 */
	public FirstGameManager(int gameState) {
		newEmptyTileList();
		this.gameState = gameState;
	}
	
	/**
	 * Create a new tileList for this manager with all tiles
	 * in empty state
	 */
	private void newEmptyTileList() {
		tileList = new ArrayList<Character>();
		for (int i=0; i<NUM_TILES; i++){
			tileList.add(Character.valueOf(TILE_STATE_EMPTY));
		}
	}
	
	@Override
	public void setTileList(ArrayList<Character> tileList) {
		this.tileList = tileList;
	}
	
	@Override
	public ArrayList<Character> getTileList() {
		return tileList;
	}
	
	@Override
	@Deprecated
	public ArrayList<Character> getAvailableTiles() {
		//not used
		return null;
	}

	@Override
	public ArrayList<Integer> getAvailableTilesPosition() {
		int pos = 0;
		ArrayList<Integer> avaiableTiles = new ArrayList<Integer>();
		for (Character t : tileList){
			if (t.equals(TILE_STATE_EMPTY)){
				avaiableTiles.add(pos);
			}
			pos++;
		}
		
		return avaiableTiles;		
	}

	@Override
	public void setGameAI(GameAI<Character> gameAI) {
		this.gameAI = gameAI;		
	}	
	
	@Override
	public int playAIMove(){
		int pos = gameAI.playPiece(this);
		changeTileValue(pos, COMPUTER_SYMBOL);
		if (checkWin(COMPUTER_SYMBOL, pos/NUM_TILES_PER_ROW, pos%NUM_TILES_PER_ROW, tileList))
			gameState = GAME_STATE_COMPUTER_WINS;
		else{
			if (getAvailableTilesPosition().isEmpty())
				gameState = GAME_STATE_TIE;
			else
				gameState = GAME_STATE_PLAYER_TURN;
		}
		return pos;
	}

	@Override
	public void playerMove(int pos){
		changeTileValue(pos, PLAYER_SYMBOL);
		if (checkWin(PLAYER_SYMBOL, pos/NUM_TILES_PER_ROW, pos%NUM_TILES_PER_ROW, tileList))
			gameState = GAME_STATE_PLAYER_WINS;
		else{
			if (getAvailableTilesPosition().isEmpty())
				gameState = GAME_STATE_TIE;
			else
				gameState = GAME_STATE_COMPUTER_TURN;
		}
	}
	
	@Override
	public void changeTileValue(int pos, Character tileVal) {
		tileList.set(pos, tileVal);	
	}

	@Override
	//FIXME
	public boolean checkWin(Character tilePiece, int row, int col, ArrayList<Character> tileList) {
		boolean resR = true;
		boolean resC = true;
		boolean resD = false;
		boolean resAD = false;
		
		tileList.set(row*NUM_TILES_PER_ROW + col, tilePiece);
		
		//check rows
		for (int i=0; i<NUM_TILES_PER_ROW; i++){
			if (!tileList.get((NUM_TILES_PER_ROW*row) + i).equals(tilePiece)){
				resR = false;
			}			
		}
		
		//check columns
		for (int i=0; i<NUM_TILES_PER_COL; i++){
			if (!tileList.get((NUM_TILES_PER_ROW*i) + col).equals(tilePiece)){
				resC = false;
			}			
		}
		
		if (row==col){
			//check diagonal
			resD = true;
			for(int i=0; i<NUM_TILES_PER_COL; i++){
				if (!tileList.get((NUM_TILES_PER_ROW+1) * i).equals(tilePiece)){
						resD = false;
				}
			}
		}
		
		if ((row+col) == 2){
			//check anti diagonal
			resAD = true;
			for(int i=0; i<NUM_TILES_PER_COL; i++){
				if (!tileList.get((2*i) + 2).equals(tilePiece)){
					resAD = false;
				}
	    	}
		}

		return (resR || resC || resD || resAD);
	}
	
	@Override
	//FIXME
	public Character checkForAWin(){
		boolean win = false;
		Character c,c1,c2;
		Character empty = Character.valueOf(TILE_STATE_EMPTY);
		
		//Rows
		for (int i=0; i<NUM_TILES_PER_COL; i++){
			c = tileList.get(i*NUM_TILES_PER_ROW);
			c1 = tileList.get((i*NUM_TILES_PER_ROW)+1);
			c2 = tileList.get((i*NUM_TILES_PER_ROW)+2);
			win = (!c.equals(empty) && c.equals(c1) && c1.equals(c2));
			if (win)
				return c;
		}
		
		//Columns
		for (int i=0; i<NUM_TILES_PER_ROW; i++){
			c = tileList.get(i);
			c1 = tileList.get((1*NUM_TILES_PER_ROW)+i);
			c2 = tileList.get((2*NUM_TILES_PER_ROW)+i);
			win = (!c.equals(empty) && c.equals(c1) && c1.equals(c2));
			if (win)
				return c;
		}
		
		//Diagonal
		c = tileList.get(0);
		c1 = tileList.get(4);
		c2 = tileList.get(8);
		win = (!c.equals(empty) && c.equals(c1) && c1.equals(c2));
		if (win)
			return c;
		
		//AntiDiagonal
		c = tileList.get(2);
		c1 = tileList.get(4);
		c2 = tileList.get(6);
		win = (!c.equals(empty) && c.equals(c1) && c1.equals(c2));
		if (win)
			return c;
		
		return empty;
	}

	@Override
	public int getGameState() {
		return gameState;
	}
	
	@Override
	public void setGameState(int gameState) {
		this.gameState = gameState;
	}
}
