package edu.sagado.tictactoe.activities;

import static edu.sagado.tictactoe.utils.Constants.*;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import edu.sagado.tictactoe.R;
import edu.sagado.tictactoe.gameDriver.FirstGameManager;
import edu.sagado.tictactoe.gameDriver.GodAI;
import edu.sagado.tictactoe.gameDriver.NotSoStrongAI;
import edu.sagado.tictactoe.gameDriver.StrongAI;
import edu.sagado.tictactoe.gameDriver.WeakAI;
import edu.sagado.tictactoe.utils.TileAdapter;

public class PlayActivity extends Activity {
	private final String managerKey = "manager";
	FirstGameManager manager = null;
	TextView statusView;
	GridView gridview;
	TileAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null){
			//WEAK POINT
			//definition of the players related symbols
			//present in the Constants class
			COMPUTER_SYMBOL = 'X';
			PLAYER_SYMBOL = 'O';
			
			manager = new FirstGameManager(GAME_STATE_PLAYER_TURN);
			
			int aiLevel = getIntent().getIntExtra(AI_PARAM_NAME, WEAK_AI);
			switch (aiLevel) {
			case WEAK_AI:
				manager.setGameAI(new WeakAI());
				break;
			case NOTSOSTRONG_AI:
				manager.setGameAI(new NotSoStrongAI());
				break;
			case STRONG_AI:
				manager.setGameAI(new StrongAI());
				break;
			case GOD_AI:
				manager.setGameAI(new GodAI());
				break;
			default:
				manager.setGameAI(new WeakAI());
				break;
			}
			
			setContentView(R.layout.play_activity);
	
			adapter = new TileAdapter(this);
			gridview = (GridView) findViewById(R.id.gridview);
			gridview.setAdapter(adapter);
			
			statusView = (TextView) findViewById(R.id.statusView);
		}	
		else {	
			manager = (FirstGameManager)savedInstanceState.get(managerKey);
			setContentView(R.layout.play_activity);
			
			adapter = new TileAdapter(this);
			gridview = (GridView) findViewById(R.id.gridview);
			gridview.setAdapter(adapter);
			
			statusView = (TextView) findViewById(R.id.statusView);
		}
		
		setGameStateText();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setGameStateText();
		ArrayList<Character> tileList = manager.getTileList();
		for (int i=0; i<tileList.size(); i++){
			Button btn = (Button)adapter.getItem(i);
			if (tileList.get(i).charValue() == TILE_STATE_O){
				btn.setText(R.string.o_tile);
				btn.setClickable(false);
			}
			if (tileList.get(i).equals(TILE_STATE_X)){
				btn.setText(R.string.x_tile);
				btn.setClickable(false);
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putSerializable(managerKey, manager);
	}
	
	/*
	 * Manage the turn played by the AI
	 * Called after each user's action
	 */
	private void aiCall(){
		if (manager.getGameState() == GAME_STATE_COMPUTER_TURN){
			int position = manager.playAIMove();
			Button btn = (Button)gridview.getAdapter().getItem(position);
			btn.setClickable(false);
			btn.setText(COMPUTER_SYMBOL == TILE_STATE_X? R.string.x_tile : R.string.o_tile);
		}
		setGameStateText();
	}
	
	/**
	 * Executed when a tile of the grid is clicked
	 * @param v
	 */
	public void onTileClicked(View v){
		Button btn = (Button)v;
		btn.setClickable(false);
		btn.setText(PLAYER_SYMBOL == TILE_STATE_O? R.string.o_tile : R.string.x_tile);
		manager.playerMove(btn.getId());
		setGameStateText(); //??Unneeded
		aiCall();
	}
	
	/**
	 * Executed when the New Game button is clicked
	 * @param v
	 */
	public void newGame(View v){
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
	
	/*
	 * Set the text of the TextView of the activity
	 * accordingly to the gameState of the gameManager
	 */
	private void setGameStateText(){
		int gameState = manager.getGameState();
		switch (gameState) {
		case GAME_STATE_COMPUTER_TURN:
			statusView.setText(R.string.gm_computer_turn);
			break;
		case GAME_STATE_PLAYER_TURN:
			statusView.setText(R.string.gm_player_turn);
			break;
		case GAME_STATE_COMPUTER_WINS:
			statusView.setText(R.string.gm_computer_win);
			invalidateAll();
			break;
		case GAME_STATE_PLAYER_WINS:
			statusView.setText(R.string.gm_player_win);
			invalidateAll();
			break;
		case GAME_STATE_TIE:
			statusView.setText(R.string.gm_tie);
			break;
		default:
			statusView.setText(String.valueOf(gameState));
			break;
		}
	}
	
	/*
	 * Invalidate all the buttons of the grid
	 */
	private void invalidateAll() {
		for (int i=0; i<adapter.getCount(); i++){
			Button btn = (Button)adapter.getItem(i);
			btn.setClickable(false);
		}
	}
}
