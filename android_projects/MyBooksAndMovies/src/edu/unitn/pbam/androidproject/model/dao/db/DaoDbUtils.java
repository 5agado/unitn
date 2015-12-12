package edu.unitn.pbam.androidproject.model.dao.db;

import android.content.ContentValues;
import android.database.Cursor;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Document.SyncType;

public class DaoDbUtils {
	public static final String DC_DOC_ID = "idd";
	public static final String DC_CAT_ID = "idc";

	public static final String DD_DOC_ID = "idd";
	public static final String DD_DLIST_ID = "idl";

	public static final String ID_COL = "_id";
	public static final String TITLE_COL = "title";
	public static final String CODE_COL = "code";
	public static final String YEAR_COL = "year";
	public static final String DESCR_COL = "description";
	public static final String RATING_COL = "rating";
	public static final String WEBRATING_COL = "webrating";
	public static final String URLINFO_COL = "urlinfo";
	public static final String NOTES_COL = "notes";
	public static final String SYNC_COL = "sync";

	public static final String DOCS_CATS_TABLE = "doc_category";
	public static final String DOCS_DLISTS_TABLE = "doc_list";

	public static void updateDocument(Cursor c, Document doc) {
		doc.setId(c.getLong(c.getColumnIndex(ID_COL)));
		doc.setTitle(c.getString(c.getColumnIndex(TITLE_COL)));
		doc.setCode(c.getString(c.getColumnIndex(CODE_COL)));
		doc.setYear(c.getInt(c.getColumnIndex(YEAR_COL)));
		doc.setDescription(c.getString(c.getColumnIndex(DESCR_COL)));
		doc.setRating(c.getDouble(c.getColumnIndex(RATING_COL)));
		doc.setWebrating(c.getDouble(c.getColumnIndex(WEBRATING_COL)));
		doc.setUrlinfo(c.getString(c.getColumnIndex(URLINFO_COL)));
		doc.setNotes(c.getString(c.getColumnIndex(NOTES_COL)));
		doc.setSync(SyncType.values()[c.getInt(c.getColumnIndex(SYNC_COL))]);
		doc.setCover(CoverDaoDb.generateCover(c));
	}

	public static ContentValues generateDocCatCV(Document element, Category c) {
		ContentValues values = new ContentValues();
		values.put(DC_DOC_ID, element.getId());
		values.put(DC_CAT_ID, c.getId());
		return values;
	}

	public static ContentValues generateDocListCV(Document element, DList l) {
		ContentValues values = new ContentValues();
		values.put(DD_DOC_ID, element.getId());
		values.put(DD_DLIST_ID, l.getId());
		return values;
	}

	public static ContentValues generateContentValuesDoc(Document element) {
		ContentValues values = new ContentValues();
		values.put(TITLE_COL, element.getTitle());
		values.put(CODE_COL, element.getCode());
		values.put(YEAR_COL, element.getYear());
		values.put(DESCR_COL, element.getDescription());

		// se il rating vale 0, significa che non è stato assegnato, quindi lo
		// setto a null
		// nel db per non calcolarlo nella media per le statistiche
		double rating = element.getRating();
		values.put(RATING_COL, rating > 0 ? rating : null);
		values.put(WEBRATING_COL, element.getWebrating());
		values.put(URLINFO_COL, element.getUrlinfo());
		values.put(NOTES_COL, element.getNotes());
		values.put(SYNC_COL, element.getSync().ordinal());
		CoverDaoDb.updateContentValues(element.getCover(), values);

		return values;
	}

}
