package edu.unitn.pbam.androidproject.fragments;

import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

public abstract class CustomFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	protected CursorAdapter adapter;

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// caricamento dei dati completato. Posso aggiornare il CursorAdapter
		// con il nuovo cursore
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// tutte le risorse (in questo caso il cursore) devono essere
		// rilasciate.
		adapter.swapCursor(null);
	}

	public void initLoader() {
		getLoaderManager().initLoader(0, null, this);
	}

}
