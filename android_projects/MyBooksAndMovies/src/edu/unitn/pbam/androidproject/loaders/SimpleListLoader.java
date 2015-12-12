package edu.unitn.pbam.androidproject.loaders;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_AUTHORS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_DLISTS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_GENRES;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_RATING;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.content.AsyncTaskLoader;
import edu.unitn.pbam.androidproject.model.DList.Type;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class SimpleListLoader extends AsyncTaskLoader<Cursor> {
	private int _docType;
	private int _listType;

	private Cursor _cursor = null;
	private final ForceLoadContentObserver _observer;

	public SimpleListLoader(Context context, int docType, int listType) {
		super(context);
		_docType = docType;
		_listType = listType;
		_observer = new ForceLoadContentObserver();
	}

	@Override
	public Cursor loadInBackground() {
		// qui viene effettuata la query al db in base al tipo di lista

		Cursor list = null;

		if (_docType == DOCTYPE_BOOK) {
			switch (_listType) {
			case LISTTYPE_AUTHORS:
				list = App.bDao.getAllAuthors();

				// così il loader potrà essere notificato quando verrà
				// inviato un notifyChange alla URI_BOOKS
				list.setNotificationUri(App.getAppContext()
						.getContentResolver(), Constants.URI_BOOKS);
				break;
			case LISTTYPE_DLISTS:
				list = App.dlDao.getByType(Type.BOOK);

				// così il loader potrà essere notificato quando verrà
				// inviato un notifyChange alla URI_DLISTS
				list.setNotificationUri(App.getAppContext()
						.getContentResolver(), Constants.URI_DLISTS);
				break;
			case LISTTYPE_GENRES:
				list = App.cDao
						.getByType(edu.unitn.pbam.androidproject.model.Category.Type.BOOK);

				// così il loader potrà essere notificato quando verrà
				// inviato un notifyChange alla URI_GENRES
				list.setNotificationUri(App.getAppContext()
						.getContentResolver(), Constants.URI_GENRES);
				break;
			}
		} else {
			switch (_listType) {
			case LISTTYPE_AUTHORS:
				list = App.mDao.getAllDirectors();

				// così il loader potrà essere notificato quando verrà
				// inviato un notifyChange alla URI_MOVIES
				list.setNotificationUri(App.getAppContext()
						.getContentResolver(), Constants.URI_MOVIES);
				break;
			case LISTTYPE_DLISTS:
				list = App.dlDao.getByType(Type.MOVIE);

				// così il loader potrà essere notificato quando verrà
				// inviato un notifyChange alla URI_DLISTS
				list.setNotificationUri(App.getAppContext()
						.getContentResolver(), Constants.URI_DLISTS);
				break;
			case LISTTYPE_GENRES:
				list = App.cDao
						.getByType(edu.unitn.pbam.androidproject.model.Category.Type.MOVIE);

				// così il loader potrà essere notificato quando verrà
				// inviato un notifyChange alla URI_GENRES
				list.setNotificationUri(App.getAppContext()
						.getContentResolver(), Constants.URI_GENRES);
				break;
			}
		}

		if (_listType == LISTTYPE_RATING) {
			MatrixCursor curs = new MatrixCursor(new String[] { "_id" });
			for (int i = 1; i <= 10; i++) {
				curs.addRow(new Object[] { i });
			}
			list = curs;
		} else {
			// aggiunge al cursore l'observer che notifica il loader nel caso in
			// cui i dati vengano modificati
			list.registerContentObserver(_observer);
		}

		return list;
	}

	@Override
	public void onCanceled(Cursor data) {
		// cerca di annullare l'operazione asincrona in corso
		super.onCanceled(data);

		// rilascia tutte le risorse allocate
		release(data);

	}

	@Override
	public void deliverResult(Cursor data) {
		if (isReset()) {
			release(data);
			return;
		}

		Cursor old = _cursor;
		_cursor = data;
		if (isStarted()) {
			super.deliverResult(data);
		}
		if (old != null && old != data) {
			release(old);
		}
	}

	@Override
	protected void onReset() {
		// blocca eventualmente l'operazione in corso del loader
		onStopLoading();

		// rilascia tutte le risorse allocate
		if (_cursor != null) {
			release(_cursor);
			_cursor = null;
		}

	}

	@Override
	protected void onStartLoading() {
		if (_cursor != null && !_cursor.isClosed()) {
			// restituisce immediatamente i dati se sono già caricati
			deliverResult(_cursor);
		}
		// se i dati non sono ancora stati caricati, oppure hanno subito
		// modifiche,
		// li carico nuovamente
		if (takeContentChanged() || _cursor == null || _cursor.isClosed()) {
			forceLoad();
		}

	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	private void release(Cursor data) {
		if (data != null && !data.isClosed()) {
			data.close();
		}
	}

}
