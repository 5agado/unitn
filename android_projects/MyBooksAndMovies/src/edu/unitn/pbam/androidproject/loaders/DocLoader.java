package edu.unitn.pbam.androidproject.loaders;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_AUTHORS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_DLISTS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_FILTER;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_GENRES;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_RATING;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class DocLoader extends AsyncTaskLoader<Cursor> {
	private int _docType;
	private int _filterType;
	private long _filterId;
	private String _paramName;

	private final ForceLoadContentObserver _observer;
	private Cursor _cursor = null;

	public DocLoader(Context context, int docType, int filterType,
			long filterId, String paramName) {
		super(context);
		_docType = docType;
		_filterType = filterType;
		_filterId = filterId;
		_paramName = paramName;
		_observer = new ForceLoadContentObserver();
	}

	@Override
	public Cursor loadInBackground() {
		// qui viene effettuata la query al db in base al tipo di
		// documento/filtro

		Cursor docs = null;

		if (_docType == DOCTYPE_BOOK) {
			switch (_filterType) {
			case LISTTYPE_AUTHORS:
				docs = App.bDao.getByAuthor(_paramName);
				break;
			case LISTTYPE_DLISTS:
				docs = App.bDao.getByDList(_filterId);
				Log.d("DocLoader", "Get by dlist: " + _filterId);
				break;
			case LISTTYPE_GENRES:
				docs = App.bDao.getByCategory(_filterId);
				break;
			case LISTTYPE_RATING:
				docs = App.bDao.getByRating((int) _filterId);
				break;
			case LISTTYPE_FILTER:
				docs = App.bDao.getFiltered(_paramName);
				break;
			default:
				docs = App.bDao.getAll();
			}

			// così il loader potrà essere notificato quando verrà inviato un
			// notifyChange alla URI_BOOKS
			docs.setNotificationUri(App.getAppContext().getContentResolver(),
					Constants.URI_BOOKS);
		} else {
			switch (_filterType) {
			case LISTTYPE_AUTHORS:
				docs = App.mDao.getByDirector(_paramName);
				break;
			case LISTTYPE_DLISTS:
				docs = App.mDao.getByDList(_filterId);
				break;
			case LISTTYPE_GENRES:
				docs = App.mDao.getByCategory(_filterId);
				break;
			case LISTTYPE_RATING:
				docs = App.mDao.getByRating((int) _filterId);
				break;
			case LISTTYPE_FILTER:
				docs = App.mDao.getFiltered(_paramName);
				break;
			default:
				docs = App.mDao.getAll();
			}

			// così il loader potrà essere notificato quando verrà inviato un
			// notifyChange alla URI_MOVIES
			docs.setNotificationUri(App.getAppContext().getContentResolver(),
					Constants.URI_MOVIES);

		}
		// forza il ricaricamento dei dati quando vengono modificati
		docs.registerContentObserver(_observer);

		return docs;
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
