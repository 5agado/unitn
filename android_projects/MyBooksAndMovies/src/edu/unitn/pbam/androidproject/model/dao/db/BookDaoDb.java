package edu.unitn.pbam.androidproject.model.dao.db;

import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.DC_CAT_ID;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.DC_DOC_ID;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.DD_DLIST_ID;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.DD_DOC_ID;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.DOCS_CATS_TABLE;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.DOCS_DLISTS_TABLE;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.RATING_COL;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.SYNC_COL;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.TITLE_COL;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.generateContentValuesDoc;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.generateDocCatCV;
import static edu.unitn.pbam.androidproject.model.dao.db.DaoDbUtils.generateDocListCV;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteQueryBuilder;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.dao.BookDao;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class BookDaoDb extends ModelDaoDb implements BookDao {
	public static final String BOOKS_TABLE = "book";
	public static final String DOC_TABLE = "document";
	public static final String DOC_BOOK_TABLE = "doc_book";
	public static final String BOOKS_TO_READ_TABLE = "books_to_read";
	public static final String BOOKS_READ_TABLE = "books_read";

	public static final String ID_COL = "_id";
	public static final String AUTH_COL = "author";
	public static final String PUBL_COL = "publishing";
	public static final String LANG_COL = "language";
	public static final String PAGES_COL = "pages";

	@Override
	public Book getById(long id) {
		/*
		 * Book b1; String chars = "abcdefghijklnopqrstuvwxyz"; Random r = new
		 * Random(); char text[] = new char[8]; List<Category> cats = new
		 * ArrayList<Category>(); cats.add(App.cDao.getById(1)); List<DList>
		 * lists = new ArrayList<DList>(); lists.add(App.dlDao.getById(1)); for
		 * (int i=0; i<5000; i++) { b1 = new Book(); b1.setCode(i+"up"); for
		 * (int j=0; j<8; j++) { text[j] =
		 * chars.charAt(r.nextInt(chars.length())); } b1.setAuthor("aaa" + new
		 * String(text)); b1.setTitle("Asdq"+i); b1.setCategories(cats);
		 * b1.setLists(lists); App.bDao.save(b1); }
		 */

		try {
			db.beginTransaction();
			String[] args = { id + "" };
			Cursor c = db.query(DOC_BOOK_TABLE, null, ID_COL + "=?", args,
					null, null, null);
			c.moveToFirst();
			Book b = generateBook(c);
			updateCategories(b);
			updateDLists(b);
			c.close();

			db.setTransactionSuccessful();
			return b;
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public Cursor getAll() {
		Cursor c = db.query(DOC_BOOK_TABLE, null, null, null, null, null,
				TITLE_COL);
		return c;
	}

	@Override
	public long save(Book element) {
		if (element.getTitle()!=null)
			element.setTitle(element.getTitle().trim());
		if (element.getAuthor()!=null && element.getAuthor().length() == 0)
			element.setAuthor(null);
		ContentValues valuesDoc = generateContentValuesDoc(element);
		ContentValues valuesBook = generateContentValuesBook(element);
		long book_id = 0;
		try {
			db.beginTransaction();
			// nuovo libro non ancora presente nel db
			if (element.getId() == 0) {
				book_id = db.insert(DOC_TABLE, null, valuesDoc);
				element.setId(book_id);
				valuesBook.put(ID_COL, book_id);
				db.insert(BOOKS_TABLE, null, valuesBook);
			} else {
				String[] args = { element.getId() + "" };
				db.update(DOC_TABLE, valuesDoc, ID_COL + "=?", args);
				db.update(BOOKS_TABLE, valuesBook, ID_COL + "=?", args);
				book_id = element.getId();

				// elimino tutte le precedenti associazioni libro/categoria
				db.delete(DOCS_CATS_TABLE, DC_DOC_ID + "=?", args);

				db.delete(DOCS_DLISTS_TABLE, DD_DOC_ID + "=?", args);
			}

			// creo tutte le associazioni libro/categoria
			for (Category c : element.getCategories()) {
				// se la categoria è nuova la salvo nel db
				if (c.getId() == 0) {
					App.cDao.save(c);
				}
				ContentValues v = generateDocCatCV(element, c);
				db.insert(DOCS_CATS_TABLE, null, v);
			}

			for (DList l : element.getLists()) {
				ContentValues v = generateDocListCV(element, l);
				db.insert(DOCS_DLISTS_TABLE, null, v);
			}

			db.setTransactionSuccessful();
			App.getAppContext().getContentResolver()
					.notifyChange(Constants.URI_BOOKS, null);

			CoverDaoDb.saveCover(element);

			return book_id;
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void delete(Book element) {
		CoverDaoDb.deleteCover(element.getCover());
		String[] args = { element.getId() + "" };
		db.delete(DOC_TABLE, ID_COL + "=?", args);
		App.getAppContext().getContentResolver()
				.notifyChange(Constants.URI_BOOKS, null);
	}

	@Override
	public Cursor getByCategory(long catId) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_CATS_TABLE + " \"b_c\" JOIN " + DOC_BOOK_TABLE
				+ " \"books\" ON b_c." + DC_DOC_ID + "=books." + ID_COL);

		String where = "b_c." + DC_CAT_ID + "=?";
		String[] whereArgs = { "" + catId };

		String query = sb.buildQuery(null, where, null, null, TITLE_COL, null);

		Cursor c = db.rawQuery(query, whereArgs);

		return c;

	}

	@Override
	public Cursor getByDList(long listId) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_DLISTS_TABLE + " \"b_d\" JOIN " + DOC_BOOK_TABLE
				+ " \"books\" ON b_d." + DD_DOC_ID + "=books." + ID_COL);

		String where = "b_d." + DD_DLIST_ID + "=?";
		String[] whereArgs = { "" + listId };

		String query = sb.buildQuery(null, where, null, null, TITLE_COL, null);

		Cursor c = db.rawQuery(query, whereArgs);

		return c;

	}

	@Override
	public Cursor getByAuthor(String author) {
		String where = AUTH_COL + "=?";
		String[] whereArgs = { author };

		Cursor c = db.query(DOC_BOOK_TABLE, null, where, whereArgs, null, null,
				TITLE_COL);

		return c;

	}

	@Override
	public Cursor getByRating(int rating) {
		double lowerBound = (rating - 1) * 10;
		double upperBound = rating * 10;
		String where = RATING_COL + " > ? and " + RATING_COL + " <= ?";
		String[] args = { "" + lowerBound, "" + upperBound };
		Cursor c = db.query(DOC_BOOK_TABLE, null, where, args, null, null,
				TITLE_COL);
		return c;
	}

	@Override
	public Cursor getNotSync() {
		String where = SYNC_COL + "= ?";
		String[] args = { Document.SyncType.NOSYNC.ordinal() + "" };
		Cursor c = db.query(DOC_BOOK_TABLE, null, where, args, null, null,
				TITLE_COL);
		return c;
	}

	@Override
	public Cursor getAllAuthors() {
		// Cursor c = db.rawQuery("select " + AUTH_COL + ", min(" + ID_COL +
		// ") as _id  from " + BOOKS_TABLE + " group by " + AUTH_COL +
		// " order by " + AUTH_COL, null);

		String[] cols = { AUTH_COL, "min(" + ID_COL + ") as _id" };

		Cursor c = db.query(BOOKS_TABLE, cols, null, null, AUTH_COL, null,
				AUTH_COL);

		return c;
	}

	@Override
	public Cursor getAuthorsMatching(String pattern) {
		String[] cols = { AUTH_COL, "min(" + ID_COL + ") as _id" };

		// poichè la condizione è verificata all'interno della clausola
		// having, non posso usare i placeholder e devo effettuare
		// manualmente l'escape della stringa
		pattern = DatabaseUtils.sqlEscapeString("%" + pattern + "%");
		String query = SQLiteQueryBuilder.buildQueryString(false, BOOKS_TABLE,
				cols, null, AUTH_COL, AUTH_COL + " like " + pattern, AUTH_COL,
				null);

		Cursor c = db.rawQuery(query, null);

		// Cursor c = db.rawQuery("select " + AUTH_COL + ", min(" + ID_COL +
		// ") as _id  from " + BOOKS_TABLE + " group by " + AUTH_COL +
		// " having " + AUTH_COL + " like '" + pattern + "%' order by " +
		// AUTH_COL, null);
		return c;
	}

	@Override
	public Cursor getFiltered(String pattern) {
		String where = AUTH_COL + " LIKE ? or " + TITLE_COL + " LIKE ?";
		pattern = "%" + pattern + "%";
		String[] whereArgs = { pattern, pattern };

		Cursor c = db.query(DOC_BOOK_TABLE, null, where, whereArgs, null, null,
				TITLE_COL);

		return c;

	}

	@Override
	public int getNumberOfBooks() {
		int ris = 0;
		String[] cols = { "count(*)" };
		Cursor c = db.query(DOC_BOOK_TABLE, cols, null, null, null, null, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			ris = c.getInt(0);
		}
		c.close();
		return ris;
	}

	@Override
	public int getToReadBooks() {
		int ris = 0;
		Cursor c = db.query(BOOKS_TO_READ_TABLE, null, null, null, null, null,
				null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			ris = c.getInt(0);
		}
		c.close();
		return ris;
	}

	@Override
	public int getReadBooks() {
		int ris = 0;
		Cursor c = db.query(BOOKS_READ_TABLE, null, null, null, null, null,
				null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			ris = c.getInt(0);
		}
		c.close();
		return ris;
	}

	@Override
	public double getAverageRating() {
		double ris = 0;
		String[] cols = { "avg(rating)" };
		Cursor c = db.query(DOC_BOOK_TABLE, cols, null, null, null, null, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			ris = c.getDouble(0);
		}
		c.close();
		return ris;
	}

	public static Book generateBook(Cursor c) {
		Book book = new Book();

		DaoDbUtils.updateDocument(c, book);

		book.setAuthor(c.getString(c.getColumnIndex(AUTH_COL)));
		book.setPublishing(c.getString(c.getColumnIndex(PUBL_COL)));
		book.setLanguage(c.getString(c.getColumnIndex(LANG_COL)));
		book.setPages(c.getInt(c.getColumnIndex(PAGES_COL)));

		return book;
	}

	private List<Category> getCategoriesByBookId(long id) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_CATS_TABLE + " \"b_c\" JOIN "
				+ CategoryDaoDb.CATEGORY_TABLE + " \"cat\" ON b_c." + DC_CAT_ID
				+ "=cat." + CategoryDaoDb.ID_COL);
		String[] columns = { "cat.*" };
		String where = "b_c." + DC_DOC_ID + "=?";
		String[] whereArgs = { "" + id };

		String query = sb.buildQuery(columns, where, null, null, null, null);

		Cursor c = db.rawQuery(query, whereArgs);
		List<Category> cats = new ArrayList<Category>();
		Category cat;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			cat = CategoryDaoDb.generateCategory(c);
			cats.add(cat);
			c.moveToNext();
		}
		return cats;
	}

	private List<DList> getDListsByBookId(long id) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_DLISTS_TABLE + " \"b_d\" JOIN "
				+ DListDaoDb.DLIST_TABLE + " \"list\" ON b_d." + DD_DLIST_ID
				+ "=list." + DListDaoDb.ID_COL);
		String[] columns = { "list.*" };
		String where = "b_d." + DD_DOC_ID + "=?";
		String[] whereArgs = { "" + id };

		String query = sb.buildQuery(columns, where, null, null, null, null);

		Cursor c = db.rawQuery(query, whereArgs);
		List<DList> lists = new ArrayList<DList>();
		DList lst;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			lst = DListDaoDb.generateDList(c);
			lists.add(lst);
			c.moveToNext();
		}
		return lists;
	}

	private void updateCategories(Book b) {
		List<Category> cats = getCategoriesByBookId(b.getId());
		b.setCategories(cats);
	}

	private void updateDLists(Book b) {
		List<DList> lists = getDListsByBookId(b.getId());
		b.setLists(lists);
	}

	private ContentValues generateContentValuesBook(Book element) {
		ContentValues values = new ContentValues();
		if (element.getAuthor() != null && element.getAuthor().equals(""))
			element.setAuthor(null);
		values.put(AUTH_COL, element.getAuthor());
		values.put(PUBL_COL, element.getPublishing());
		values.put(LANG_COL, element.getLanguage());
		values.put(PAGES_COL, element.getPages());

		return values;
	}

}
