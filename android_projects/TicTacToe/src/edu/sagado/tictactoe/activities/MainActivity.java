package edu.sagado.tictactoe.activities;

import edu.sagado.tictactoe.R;
import edu.sagado.tictactoe.utils.Constants;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RadioGroup;

/**
 * First view to be opened.
 * Contains the button for starting a new game
 * and the settings menu. For now the only
 * setting available is the one related to
 * the GameAI level.
 * @author 5agado
 *
 */
public class MainActivity extends Activity {
	private RadioGroup radioGameAI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		radioGameAI = (RadioGroup)findViewById(R.id.radioGameAI);
	}
	
	/**
	 * Action executed when the Play button is clicked.
	 * Here we create a new intent with an extra that 
	 * defines the GameAI level. 
	 * Default case is equal to a weak AI
	 * @param view
	 */
	public void startGame(View view){
		int gameAI = radioGameAI.getCheckedRadioButtonId();
		Intent intent = new Intent(this, PlayActivity.class);
		switch (gameAI) {
		case R.id.weakAI:
			intent.putExtra(Constants.AI_PARAM_NAME, Constants.WEAK_AI);
			break;
		case R.id.notSoStrongAI:
			intent.putExtra(Constants.AI_PARAM_NAME, Constants.NOTSOSTRONG_AI);
			break;
		case R.id.strongAI:
			intent.putExtra(Constants.AI_PARAM_NAME, Constants.STRONG_AI);
			break;
		case R.id.godAI:
			intent.putExtra(Constants.AI_PARAM_NAME, Constants.GOD_AI);
			break;
		default:
			intent.putExtra(Constants.AI_PARAM_NAME, Constants.WEAK_AI);
			break;
		}
    	startActivity(intent);
	}
	
	/**
	 * Action executed when the Settings button is clicked
	 * @param view
	 */
	public void settings(View view){
		radioGameAI.setVisibility((radioGameAI.getVisibility() == View.VISIBLE)? 
				View.INVISIBLE : View.VISIBLE);
	}

}
