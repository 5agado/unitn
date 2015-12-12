package edu.unitn.pbam.androidproject.fragments;

import static edu.unitn.pbam.androidproject.utilities.Constants.DLIST_WATCHED;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_STRING_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_LIST_TYPE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_AUTHORS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_DLISTS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_GENRES;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_RATING;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.activities.DocsHomeActivity;
import edu.unitn.pbam.androidproject.activities.GenericListActivity;
import edu.unitn.pbam.androidproject.adapters.SimpleItemAdapter;
import edu.unitn.pbam.androidproject.loaders.SimpleListLoader;
import edu.unitn.pbam.androidproject.model.dao.db.BookDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.CategoryDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.DListDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.MovieDaoDb;
import edu.unitn.pbam.androidproject.utilities.App;

public class SimpleListFragment extends CustomFragment implements
		ActionMode.Callback {
	private int docType;
	private int listType;
	private ActionMode mActionMode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		docType = bundle.getInt(INTENT_DOC_TYPE_PARAMNAME);
		listType = bundle.getInt(INTENT_LIST_TYPE_PARAMNAME);

		int[] to = { R.id.text1 };
		String[] from = null;

		if (listType == LISTTYPE_AUTHORS) {
			if (docType == DOCTYPE_BOOK) {
				from = new String[] { BookDaoDb.AUTH_COL };
			} else {
				from = new String[] { MovieDaoDb.DIREC_COL };
			}
			adapter = new SimpleCursorAdapter(this.getActivity(),
					R.layout.list_item_simple, null, from, to, 0);
		} else if (listType == LISTTYPE_GENRES) {
			from = new String[] { CategoryDaoDb.NAME_COL };
			adapter = new SimpleItemAdapter(this.getActivity(),
					R.layout.list_item_simple, docType, null, from[0], listType);
			// adapter = new SimpleCursorAdapter(this.getActivity(),
			// R.layout.list_item_simple, null, from, to, 0);
		} else if (listType == LISTTYPE_RATING) {
			from = new String[] { "_id" };
			adapter = new SimpleItemAdapter(this.getActivity(),
					R.layout.list_item_rating, docType, null, from[0], listType);
			// adapter = new SimpleCursorAdapter(this.getActivity(),
			// R.layout.list_item_rating, null, from, to, 0);
		} else if (listType == LISTTYPE_DLISTS) {
			from = new String[] { DListDaoDb.NAME_COL };
			adapter = new SimpleItemAdapter(this.getActivity(),
					R.layout.list_item_simple, docType, null, from[0], listType);
			// adapter = new SimpleCursorAdapter(this.getActivity(),
			// R.layout.list_item_simple, null, from, to, 0);
		}

		View parentView = inflater.inflate(R.layout.fragment_docs_listview,
				container, false);
		ListView view = (ListView) parentView.findViewById(android.R.id.list);
		view.setAdapter(adapter);

		if (listType != LISTTYPE_AUTHORS & listType != LISTTYPE_RATING) {
			view.setLongClickable(true);
			view.setFastScrollEnabled(true);
			// view.setFastScrollAlwaysVisible(true);
			view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long id) {
					if (listType == LISTTYPE_GENRES || id > DLIST_WATCHED) {
						SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
						mActionMode = activity
								.startActionMode(SimpleListFragment.this);
						mActionMode.setTag(id);
						((DocsHomeActivity) getActivity())
								.setActionMode(mActionMode);
					}
					return true;
				}
			});
		}

		initLoader();

		return parentView;
	}

	@Override
	public void onPause() {
		if (mActionMode != null) {
			mActionMode.finish();
		}
		super.onPause();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this.getActivity(), GenericListActivity.class);
		i.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
		i.putExtra(INTENT_LIST_TYPE_PARAMNAME, listType);
		i.putExtra(INTENT_DOC_ID_PARAMNAME, id);
		TextView tv = (TextView) (v.findViewById(R.id.text1));
		i.putExtra(INTENT_DOC_STRING_PARAMNAME, tv.getText());
		startActivity(i);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// questo metodo si occupa di creare il loader apposito
		return new SimpleListLoader(getActivity(), docType, listType);
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
		MenuInflater inflater = activity.getSupportMenuInflater();
		inflater.inflate(R.menu.action_mode_delete, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:
			if (listType == LISTTYPE_GENRES) {
				App.cDao.delete(App.cDao.getById((Long) mode.getTag()));
			} else if (listType == LISTTYPE_DLISTS) {
				App.dlDao.delete(App.dlDao.getById((Long) mode.getTag()));
			}
			mode.finish();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mActionMode = null;
	}

}
