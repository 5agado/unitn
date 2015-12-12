package edu.unitn.pbam.androidproject.model.dao.db;

import android.content.ContentValues;
import android.database.Cursor;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.Category.Type;
import edu.unitn.pbam.androidproject.model.dao.CategoryDao;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class CategoryDaoDb extends ModelDaoDb implements CategoryDao {
	public static final String CATEGORY_TABLE = "category";

	public static final String NAME_COL = "name";
	public static final String DESCR_COL = "description";
	public static final String TYPE_COL = "type";
	public static final String ID_COL = "_id";

	private static final String TYPE_BOOK = "l";
	private static final String TYPE_MOVIE = "f";
	private static final String TYPE_BOTH = "e";

	@Override
	public Category getById(long id) {
		String[] args = { id + "" };
		Cursor c = db.query(CATEGORY_TABLE, null, ID_COL + "=?", args, null,
				null, null);
		c.moveToFirst();
		Category cat = generateCategory(c);
		c.close();
		return cat;

	}

	@Override
	public Cursor getAll() {
		Cursor c = db.query(CATEGORY_TABLE, null, null, null, null, null, null);
		return c;
	}

	@Override
	public long save(Category element) {
		ContentValues values = generateContentValues(element);
		long cat_id = 0;

		try {
			// tolgo gli spazi prima e dopo il nome della categoria
			element.setName(element.getName().trim());

			db.beginTransaction();
			// nuova categoria non ancora presente nel db
			if (element.getId() == 0) {
				Category old = getCategoryByName(element.getName());
				if (old == null
						|| (old.getType() != element.getType() && old.getType() != Type.BOTH)) {
					cat_id = db.insert(CATEGORY_TABLE, null, values);
				} else {
					cat_id = old.getId();
				}
				element.setId(cat_id);
			} else {
				String[] args = { element.getId() + "" };
				db.update(CATEGORY_TABLE, values, ID_COL + "=?", args);
				cat_id = element.getId();
			}

			db.setTransactionSuccessful();
			App.getAppContext().getContentResolver()
					.notifyChange(Constants.URI_GENRES, null);
			return cat_id;

		} finally {
			db.endTransaction();
		}

	}

	@Override
	public Cursor getCategoriesMatching(String pattern) {
		String[] cols = { NAME_COL };
		String where = NAME_COL + " like ?";
		String[] args = { "%" + pattern + "%" };
		Cursor c = db.query(CATEGORY_TABLE, cols, where, args, null, null,
				NAME_COL);
		return c;
	}

	private Category getCategoryByName(String name) {
		Category cat = null;

		Cursor c = db.query(CATEGORY_TABLE, null, NAME_COL + " like ?",
				new String[] { name }, null, null, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			cat = generateCategory(c);
		}

		return cat;
	}

	@Override
	public void delete(Category element) {
		String[] args = { element.getId() + "" };

		db.delete(CATEGORY_TABLE, ID_COL + "=?", args);
		App.getAppContext().getContentResolver()
				.notifyChange(Constants.URI_GENRES, null);
	}

	@Override
	public Cursor getByType(Type t) {
		String where = TYPE_COL + " in (?, ?)";
		String[] whereArgs = null;
		switch (t) {
		case BOOK:
			whereArgs = new String[] { TYPE_BOOK, TYPE_BOTH };
			break;
		case MOVIE:
			whereArgs = new String[] { TYPE_MOVIE, TYPE_BOTH };
			break;
		default:
			where = null;
		}

		Cursor c = db.query(CATEGORY_TABLE, null, where, whereArgs, null, null,
				null);
		return c;
	}

	public static Category generateCategory(Cursor c) {
		Category cat = new Category();

		cat.setId(c.getLong(c.getColumnIndex(ID_COL)));
		cat.setName(c.getString(c.getColumnIndex(NAME_COL)));
		cat.setDescription(c.getString(c.getColumnIndex(DESCR_COL)));
		String type = c.getString(c.getColumnIndex(TYPE_COL));
		Type t;
		if (TYPE_BOOK.equals(type)) {
			t = Type.BOOK;
		} else if (TYPE_MOVIE.equals(type)) {
			t = Type.MOVIE;
		} else {
			t = Type.BOTH;
		}
		cat.setType(t);
		return cat;
	}

	private ContentValues generateContentValues(Category element) {
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
		default:
			type = TYPE_BOTH;
		}
		values.put(TYPE_COL, type);
		return values;
	}

}
