package edu.unitn.pbam.androidproject.utilities;

import android.net.Uri;

public class Constants {
	// Serve riferirsi a qualche parametro del model?
	public static final String INTENT_DOC_TYPE_PARAMNAME = "docType";
	public static final String INTENT_DOC_ID_PARAMNAME = "docId";
	public static final String INTENT_LIST_TYPE_PARAMNAME = "listType";
	public static final String INTENT_DOC_STRING_PARAMNAME = "docString";
	public static final String INTENT_DOC_OBJ_PARAMNAME = "docObj";
	public static final String INTENT_DOC_BARCODE_PARAMNAME = "barcode";
	public static final String INTENT_DOC_INTERNETID_PARAMNAME = "internetId";
	public static final String INTENT_FILEPATH_PARAMNAME = "filepath";

	public static final int DOCTYPE_MOVIE = 0;
	public static final int DOCTYPE_BOOK = 1;

	public static final long DOC_ID_NO_DOC = -2;

	public static final int LISTTYPE_DLISTS = 0;
	public static final int LISTTYPE_GENRES = 1;
	public static final int LISTTYPE_AUTHORS = 2;
	public static final int LISTTYPE_RATING = 3;
	public static final int LISTTYPE_FILTER = 4;

	public static final int DLIST_TOREAD = 1;
	public static final int DLIST_READ = 2;
	public static final int DLIST_TOWATCH = 3;
	public static final int DLIST_WATCHED = 4;

	public static final Uri URI_BOOKS = Uri
			.parse("sqlite://edu.unitn.pbam.androidproject.utilities/books");
	public static final Uri URI_MOVIES = Uri
			.parse("sqlite://edu.unitn.pbam.androidproject.utilities/movies");
	public static final Uri URI_DLISTS = Uri
			.parse("sqlite://edu.unitn.pbam.androidproject.utilities/dlists");
	public static final Uri URI_GENRES = Uri
			.parse("sqlite://edu.unitn.pbam.androidproject.utilities/genres");
	public static final String BACKUP_PATH = "/mbam/db.bak";
}
