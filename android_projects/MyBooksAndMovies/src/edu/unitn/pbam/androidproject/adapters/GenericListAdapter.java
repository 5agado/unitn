package edu.unitn.pbam.androidproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class GenericListAdapter extends CursorAdapter {
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected Cursor mCursor;
	protected int mViewResId;

	public GenericListAdapter(Context context, int res, Cursor cursor) {
		super(context, cursor, false);
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCursor = cursor;
		mViewResId = res;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = mInflater.inflate(mViewResId, null);
		return v;
	}

	@Override
	public long getItemId(int position) {
		long id = 0;
		Cursor cursor = getCursor();
		if (cursor != null) {
			cursor.moveToPosition(position);
			id = cursor.getLong(cursor.getColumnIndex("_id"));
		}
		return id;
	}

}
