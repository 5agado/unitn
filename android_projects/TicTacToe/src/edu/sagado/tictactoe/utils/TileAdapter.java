package edu.sagado.tictactoe.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import edu.sagado.tictactoe.R;

/**
 * The adapter of the tiles that compose the 
 * game, for the Android GridLayout used for 
 * the rendering. 
 * For the moment each tile is represented with
 * an Android Button
 * @author 5agado
 *
 */
public class TileAdapter extends BaseAdapter {
	private Context mContext;
	ArrayList<Button> btnByPos;

	public TileAdapter(Context c) {
		btnByPos = new ArrayList<Button>();
		mContext = c;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		for (int i = 0; i < Constants.NUM_TILES; i++) {
			Button btn = (Button)inflater.inflate(R.layout.game_tile, null);  
			btnByPos.add(i, btn);
			btn.setId(i);
			btn.setText(R.string.empty_tile);
		}
	}

	// Total number of things contained within the adapter
	public int getCount() {
		return Constants.NUM_TILES;
	}

	// Require for structure, not really used in my code.
	public Object getItem(int position) {
		return btnByPos.get(position);
	}

	// Require for structure, not really used in my code. Can
	// be used to get the id of an item in the adapter for
	// manual control.
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return btnByPos.get(position);
	}
}
