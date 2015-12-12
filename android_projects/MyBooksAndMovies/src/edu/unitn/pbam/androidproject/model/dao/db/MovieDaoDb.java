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
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteQueryBuilder;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Movie;
import edu.unitn.pbam.androidproject.model.dao.MovieDao;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class MovieDaoDb extends ModelDaoDb implements MovieDao {
	private static final String MOVIES_TABLE = "movie";
	public static final String DOC_TABLE = "document";
	public static final String DOC_MOVIE_TABLE = "doc_movie";
	public static final String MOVIES_TO_WATCH_TABLE = "movies_to_watch";
	public static final String MOVIES_WATCHED_TABLE = "movies_watched";

	public static final String ID_COL = "_id";
	public static final String DIREC_COL = "director";
	private static final String STUDIO_COL = "studio";
	private static final String DURATION_COL = "duration";
	private static final String ACTORS_COL = "actors";

	@Override
	public Movie getById(long id) {
		String[] args = { id + "" };
		try {
			db.beginTransaction();
			Cursor c = db.query(DOC_MOVIE_TABLE, null, ID_COL + "=?", args,
					null, null, null);
			c.moveToFirst();
			Movie m = generateMovie(c);
			updateCategories(m);
			updateDLists(m);
			c.close();
			db.setTransactionSuccessful();
			return m;
		} finally {
			db.endTransaction();
		}

	}

	@Override
	public Cursor getAll() {
		Cursor c = db.query(DOC_MOVIE_TABLE, null, null, null, null, null,
				TITLE_COL);

		return c;

	}

	@Override
	public long save(Movie element) {
		if (element.getTitle()!=null)
			element.setTitle(element.getTitle().trim());
		if (element.getDirector()!=null && element.getDirector().length() == 0)
			element.setDirector(null);
		ContentValues valuesMovie = generateContentValuesMovie(element);
		ContentValues valuesDoc = generateContentValuesDoc(element);
		long movie_id = 0;
		try {
			db.beginTransaction();
			// nuovo film non ancora presente nel db
			if (element.getId() == 0) {
				movie_id = db.insert(DOC_TABLE, null, valuesDoc);
				element.setId(movie_id);
				valuesMovie.put(ID_COL, movie_id);
				db.insert(MOVIES_TABLE, null, valuesMovie);
			} else {
				String[] args = { element.getId() + "" };
				db.update(MOVIES_TABLE, valuesMovie, ID_COL + "=?", args);
				db.update(DOC_TABLE, valuesDoc, ID_COL + "=?", args);
				movie_id = element.getId();

				// elimino tutte le precedenti associazioni film/categoria
				db.delete(DOCS_CATS_TABLE, DC_DOC_ID + "=?", args);

				db.delete(DOCS_DLISTS_TABLE, DD_DOC_ID + "=?", args);

			}
			// creo tutte le associazioni film/categoria
			for (Category c : element.getCategories()) {
				if (!c.getName().equals("")) {
					// se la categoria è nuova la salvo nel db
					if (c.getId() == 0) {
						App.cDao.save(c);
					}
					ContentValues v = generateDocCatCV(element, c);
					db.insert(DOCS_CATS_TABLE, null, v);
				}
			}

			for (DList l : element.getLists()) {
				ContentValues v = generateDocListCV(element, l);
				db.insert(DOCS_DLISTS_TABLE, null, v);
			}

			db.setTransactionSuccessful();

			App.getAppContext().getContentResolver()
					.notifyChange(Constants.URI_MOVIES, null);

			CoverDaoDb.saveCover(element);

			return movie_id;
		} finally {
			db.endTransaction();
		}

	}

	@Override
	public void delete(Movie element) {
		CoverDaoDb.deleteCover(element.getCover());
		String[] args = { element.getId() + "" };
		db.delete(DOC_TABLE, ID_COL + "=?", args);
		App.getAppContext().getContentResolver()
				.notifyChange(Constants.URI_MOVIES, null);
	}

	@Override
	public Cursor getByCategory(long catId) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_CATS_TABLE + " \"m_c\" JOIN " + DOC_MOVIE_TABLE
				+ " \"movies\" ON m_c." + DC_DOC_ID + "=movies." + ID_COL);

		String where = "m_c." + DC_CAT_ID + "=?";
		String[] whereArgs = { "" + catId };

		String query = sb.buildQuery(null, where, null, null, TITLE_COL, null);

		Cursor c = db.rawQuery(query, whereArgs);
		return c;

	}

	@Override
	public Cursor getByDirector(String dir) {
		String where = DIREC_COL + "=?";
		String[] whereArgs = { dir };

		Cursor c = db.query(DOC_MOVIE_TABLE, null, where, whereArgs, null,
				null, TITLE_COL);
		return c;
	}

	@Override
	public Cursor getByRating(int rating) {
		double lowerBound = (rating - 1) * 10;
		double upperBound = rating * 10;
		String where = RATING_COL + " > ? and " + RATING_COL + " <= ?";
		String[] args = { "" + lowerBound, "" + upperBound };
		Cursor c = db.query(DOC_MOVIE_TABLE, null, where, args, null, null,
				TITLE_COL);
		return c;
	}

	@Override
	public Cursor getNotSync() {
		String where = SYNC_COL + "= ?";
		String[] args = { Document.SyncType.NOSYNC.ordinal() + "" };
		Cursor c = db.query(DOC_MOVIE_TABLE, null, where, args, null, null,
				TITLE_COL);
		return c;
	}

	@Override
	public Cursor getAllDirectors() {
		Cursor c = db.rawQuery("select " + DIREC_COL + ", min(" + ID_COL
				+ ") as _id  from " + MOVIES_TABLE + " group by " + DIREC_COL
				+ " order by " + DIREC_COL, null);
		return c;
	}

	@Override
	public Cursor getByDList(long listId) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_DLISTS_TABLE + " \"m_d\" JOIN " + DOC_MOVIE_TABLE
				+ " \"movies\" ON m_d." + DD_DOC_ID + "=movies." + ID_COL);

		String where = "m_d." + DD_DLIST_ID + "=?";
		String[] whereArgs = { "" + listId };

		String query = sb.buildQuery(null, where, null, null, TITLE_COL, null);

		Cursor c = db.rawQuery(query, whereArgs);

		return c;

	}

	public Cursor getDirectorsMatching(String pattern) {
		String[] cols = { DIREC_COL, "min(" + ID_COL + ") as _id" };

		// poichè la condizione è verificata all'interno della clausola
		// having, non posso usare i placeholder e devo effettuare
		// manualmente l'escape della stringa
		pattern = DatabaseUtils.sqlEscapeString("%" + pattern + "%");
		String query = SQLiteQueryBuilder.buildQueryString(false, MOVIES_TABLE,
				cols, null, DIREC_COL, DIREC_COL + " like " + pattern,
				DIREC_COL, null);

		Cursor c = db.rawQuery(query, null);

		// Cursor c = db.rawQuery("select " + AUTH_COL + ", min(" + ID_COL +
		// ") as _id  from " + BOOKS_TABLE + " group by " + AUTH_COL +
		// " having " + AUTH_COL + " like '" + pattern + "%' order by " +
		// AUTH_COL, null);
		return c;
	}

	@Override
	public Cursor getFiltered(String pattern) {
		String where = DIREC_COL + " LIKE ? or " + TITLE_COL + " LIKE ?";
		pattern = "%" + pattern + "%";
		String[] whereArgs = { pattern, pattern };

		Cursor c = db.query(DOC_MOVIE_TABLE, null, where, whereArgs, null,
				null, TITLE_COL);

		return c;

	}

	@Override
	public int getNumberOfMovies() {
		int ris = 0;
		String[] cols = { "count(*)" };
		Cursor c = db
				.query(DOC_MOVIE_TABLE, cols, null, null, null, null, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			ris = c.getInt(0);
		}
		c.close();
		return ris;
	}

	@Override
	public int getToWatchMovies() {
		int ris = 0;
		Cursor c = db.query(MOVIES_TO_WATCH_TABLE, null, null, null, null,
				null, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			ris = c.getInt(0);
		}
		c.close();
		return ris;
	}

	@Override
	public int getWatchedMovies() {
		int ris = 0;
		Cursor c = db.query(MOVIES_WATCHED_TABLE, null, null, null, null, null,
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
		Cursor c = db
				.query(DOC_MOVIE_TABLE, cols, null, null, null, null, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			ris = c.getDouble(0);
		}
		c.close();
		return ris;
	}

	private ContentValues generateContentValuesMovie(Movie element) {
		ContentValues values = new ContentValues();
		if (element.getDirector() != null && element.getDirector().equals(""))
			element.setDirector(null);
		values.put(DIREC_COL, element.getDirector());
		values.put(DURATION_COL, element.getDuration());
		values.put(STUDIO_COL, element.getStudio());

		List<String> actors = element.getActors();
		StringBuilder strBuild = new StringBuilder();
		if (actors != null) {
			for (int i = 0; i < actors.size(); i++) {
				if (i > 0) {
					strBuild.append(",");
				}
				strBuild.append(actors.get(i).trim());
			}
		}
		values.put(ACTORS_COL, strBuild.toString());

		return values;
	}

	public static Movie generateMovie(Cursor c) {
		Movie m = new Movie();

		DaoDbUtils.updateDocument(c, m);
		m.setDirector(c.getString(c.getColumnIndex(DIREC_COL)));
		m.setStudio(c.getString(c.getColumnIndex(STUDIO_COL)));
		m.setDuration(c.getInt(c.getColumnIndex(DURATION_COL)));

		String actorsStr = c.getString(c.getColumnIndex(ACTORS_COL));
		if (actorsStr != null) {
			String actorsArr[] = actorsStr.split(",");
			List<String> actors = new ArrayList<String>();
			Collections.addAll(actors, actorsArr);
			m.setActors(actors);
		}

		return m;
	}

	private List<Category> getCategoriesByMovieId(long id) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_CATS_TABLE + " \"m_c\" JOIN "
				+ CategoryDaoDb.CATEGORY_TABLE + " \"cat\" ON m_c." + DC_CAT_ID
				+ "=cat." + CategoryDaoDb.ID_COL);
		String[] columns = { "cat.*" };
		String where = "m_c." + DC_DOC_ID + "=?";
		String[] whereArgs = { "" + id };

		String query = sb.buildQuery(columns, where, null, null, null, null);

		Cursor c = db.rawQuery(query, whereArgs);
		c.moveToFirst();
		List<Category> cats = new ArrayList<Category>();
		Category cat;
		while (!c.isAfterLast()) {
			cat = CategoryDaoDb.generateCategory(c);
			cats.add(cat);
			c.moveToNext();
		}
		return cats;
	}

	private List<DList> getDListsByMovieId(long id) {
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		sb.setTables(DOCS_DLISTS_TABLE + " \"m_d\" JOIN "
				+ DListDaoDb.DLIST_TABLE + " \"list\" ON m_d." + DD_DLIST_ID
				+ "=list." + DListDaoDb.ID_COL);
		String[] columns = { "list.*" };
		String where = "m_d." + DD_DOC_ID + "=?";
		String[] whereArgs = { "" + id };

		String query = sb.buildQuery(columns, where, null, null, null, null);

		Cursor c = db.rawQuery(query, whereArgs);
		c.moveToFirst();
		List<DList> lists = new ArrayList<DList>();
		DList lst;
		while (!c.isAfterLast()) {
			lst = DListDaoDb.generateDList(c);
			lists.add(lst);
			c.moveToNext();
		}
		return lists;
	}

	private void updateCategories(Movie m) {
		List<Category> cats = getCategoriesByMovieId(m.getId());
		m.setCategories(cats);
	}

	private void updateDLists(Movie m) {
		List<DList> lists = getDListsByMovieId(m.getId());
		m.setLists(lists);
	}

}
