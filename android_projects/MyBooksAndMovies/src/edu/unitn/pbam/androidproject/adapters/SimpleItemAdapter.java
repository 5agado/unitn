package edu.unitn.pbam.androidproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class SimpleItemAdapter extends GenericListAdapter implements Filterable {
	private String mFrom;
	private int mListType;
	private int mDocType;

	public SimpleItemAdapter(Context context, int resId, int docType,
			Cursor objects, String from, int listType) {
		super(context, resId, objects);
		mFrom = from;
		mListType = listType;
		mDocType = docType;
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		TextView text = (TextView) v.findViewById(R.id.text1);
		text.setText(cursor.getString(cursor.getColumnIndex(mFrom)));

		final TextView counter = (TextView) v.findViewById(R.id.cardinality);

		AsyncTask<Long, Void, Integer> asyncTask = new AsyncTask<Long, Void, Integer>() {
			@Override
			protected void onPreExecute() {
				counter.setVisibility(View.GONE);
			}

			@Override
			protected Integer doInBackground(Long... params) {
				Cursor c;
				long id = params[0];
				if (mListType == Constants.LISTTYPE_DLISTS) {
					if (mDocType == Constants.DOCTYPE_BOOK)
						c = App.bDao.getByDList(id);
					else
						c = App.mDao.getByDList(id);
				} else {
					if (mDocType == Constants.DOCTYPE_BOOK)
						c = App.bDao.getByCategory(id);
					else
						c = App.mDao.getByCategory(id);
				}
				int result = c.getCount();
				c.close();
				return result;
			}

			@Override
			protected void onPostExecute(Integer result) {
				counter.setText("(" + String.valueOf(result) + ")");
				counter.setVisibility(View.VISIBLE);
			}
		};

		if (mListType == Constants.LISTTYPE_DLISTS
				|| mListType == Constants.LISTTYPE_GENRES) {
			long listId = getItemId(cursor.getPosition());
			asyncTask.execute(listId);
		} else {
			RatingBar bar = (RatingBar) v.findViewById(R.id.rating);
			bar.setNumStars((int) getItemId(cursor.getPosition()));
			bar.setProgress(100);
		}
	}
}
