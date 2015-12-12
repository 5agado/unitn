package edu.unitn.pbam.androidproject.model.dao.db;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.DList.Type;
import edu.unitn.pbam.androidproject.utilities.App;

public class SQLiteHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "booksmovies.db";
	private static final String DB_NAME_ASSET = DB_NAME;
	private static final int DB_VERSION = 1;
	private static boolean _closed = true;

	private static SQLiteHelper _selfInstance;

	private Context _context;
	private boolean _copyDatabase = false;

	public synchronized static SQLiteHelper getInstance() {
		if (_selfInstance == null || _closed) {
			_selfInstance = new SQLiteHelper(App.getAppContext());
			_closed = false;
		}
		return _selfInstance;
	}

	@Override
	public synchronized void close() {
		super.close();
		_closed = true;
	}

	private SQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this._context = context;
		SQLiteDatabase db = null;

		try {
			db = getReadableDatabase();
			if (db != null) {
				db.close();
			}
			if (_copyDatabase) {
				copyDatabase();
				_copyDatabase = false;
			}

		} catch (Exception ex) {
			Log.d("DB", ex.getLocalizedMessage());
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	private void copyDatabase() throws IOException {
		Log.d("DB", "Copy database!!!");

		// ottieni il file contenente il database vuoto
		File output_file = _context.getDatabasePath(DB_NAME);
		if (!output_file.exists()) {
			output_file.createNewFile();
		}

		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			// crea un input buffer attraverso cui leggere il db memorizzato
			// negli asset
			input = new BufferedInputStream(_context.getAssets().open(
					DB_NAME_ASSET));

			// crea un output buffer attraverso cui copiare il db nel path
			// richiesto
			output = new BufferedOutputStream(new FileOutputStream(output_file));
			int nextByte;
			while ((nextByte = input.read()) != -1) {
				output.write(nextByte);
			}
		} finally {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
		}
		setDatabaseVersion();
		updateLists();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		_copyDatabase = true;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("DB", "onUpgrade");
	}

	@Override
	public synchronized void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// serve a permettere l'utilizzo di chiavi esterne e relative
			// constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	private void setDatabaseVersion() {
		SQLiteDatabase db = null;
		try {
			String dbPath = _context.getDatabasePath(DB_NAME).getAbsolutePath();
			db = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.OPEN_READWRITE);
			db.execSQL("PRAGMA user_version = " + DB_VERSION);
		} catch (SQLException ex) {
			Log.d("DB", ex.getLocalizedMessage());
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void updateLists() {
		DListDaoDb dld = null;
		try {
			dld = new DListDaoDb();
			Context context = App.getAppContext();

			String[] lists = { context.getString(R.string.toread),
					context.getString(R.string.read),
					context.getString(R.string.towatch),
					context.getString(R.string.watched) };

			Type[] types = { Type.BOOK, Type.BOOK, Type.MOVIE, Type.MOVIE };

			DList lst;
			for (int i = 0; i < lists.length; i++) {
				lst = new DList();
				lst.setName(lists[i]);
				lst.setType(types[i]);
				dld.save(lst);
			}
		} catch (SQLException ex) {
			Log.d("DB", ex.getLocalizedMessage());
		} finally {
			if (dld != null) {
				dld.close();
			}
		}
	}

}
