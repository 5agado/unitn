package edu.unitn.pbam.androidproject.model.dao.db;

import android.content.ContentValues;
import android.database.Cursor;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.DList.Type;
import edu.unitn.pbam.androidproject.model.dao.DListDao;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class DListDaoDb extends ModelDaoDb implements DListDao {
	public static final String DLIST_TABLE = "dlist";
	public static final String ID_COL = "_id";
	public static final String NAME_COL = "name";

	private static final String DESCR_COL = "description";
	private static final String TYPE_COL = "type";

	private static final String TYPE_BOOK = "l";
	private static final String TYPE_MOVIE = "f";

	@Override
	public DList getById(long id) {
		String[] args = { id + "" };
		Cursor c = db.query(DLIST_TABLE, null, ID_COL + "=?", args, null, null,
				null);
		c.moveToFirst();
		DList l = generateDList(c);
		c.close();
		return l;
	}

	@Override
	public Cursor getAll() {
		Cursor c = db.query(DLIST_TABLE, null, null, null, null, null, null);
		return c;
	}

	@Override
	public long save(DList element) {
		ContentValues values = generateContentValues(element);

		long l_id = 0;

		// nuova lista non ancora presente nel db
		if (element.getId() == 0) {
			l_id = db.insert(DLIST_TABLE, null, values);
			element.setId(l_id);
		} else {
			String[] args = { element.getId() + "" };
			db.update(DLIST_TABLE, values, ID_COL + "=?", args);
			l_id = element.getId();
		}
		App.getAppContext().getContentResolver()
				.notifyChange(Constants.URI_DLISTS, null);
		return l_id;
	}

	@Override
	public void delete(DList element) {

		String[] args = { element.getId() + "" };

		db.delete(DLIST_TABLE, ID_COL + "=?", args);
		App.getAppContext().getContentResolver()
				.notifyChange(Constants.URI_DLISTS, null);
	}

	@Override
	public Cursor getByType(Type t) {
		String where = TYPE_COL + "=?";
		String[] whereArgs = null;
		switch (t) {
		case BOOK:
			whereArgs = new String[] { TYPE_BOOK };
			break;
		case MOVIE:
			whereArgs = new String[] { TYPE_MOVIE };
		}

		Cursor c = db.query(DLIST_TABLE, null, where, whereArgs, null, null,
				null);
		return c;
	}

	private ContentValues generateContentValues(DList element) {
		ContentValues values = new ContentValues();
		values.put(NAME_COL, element.getName());
		values.put(DESCR_COL, element.getDescription());
		String type = null;
		switch (element.getType()) {
		case BOOK:
			type = TYPE_BOOK;
			break;
		case MOVIE:
			type = TYPE_MOVIE;
			break;
		}
		values.put(TYPE_COL, type);
		return values;
	}

	public static DList generateDList(Cursor c) {
		DList l = new DList();

		l.setId(c.getLong(c.getColumnIndex(ID_COL)));
		l.setName(c.getString(c.getColumnIndex(NAME_COL)));
		l.setDescription(c.getString(c.getColumnIndex(DESCR_COL)));
		String type = c.getString(c.getColumnIndex(TYPE_COL));

		// valore di default
		Type t = Type.BOOK;
		if (TYPE_MOVIE.equals(type)) {
			t = Type.MOVIE;
		}
		l.setType(t);
		return l;
	}

}
