package edu.unitn.pbam.androidproject.model.dao.db;

import android.database.sqlite.SQLiteDatabase;

public abstract class ModelDaoDb {
	protected SQLiteHelper helper = SQLiteHelper.getInstance();
	protected SQLiteDatabase db;

	public ModelDaoDb() {
		open();
	}

	@Override
	public void finalize() {
		close();
	}

	public void open() {
		if (db == null || !db.isOpen()) {
			db = helper.getWritableDatabase();
		}
	}

	public void close() {
		helper.close();
	}

}
