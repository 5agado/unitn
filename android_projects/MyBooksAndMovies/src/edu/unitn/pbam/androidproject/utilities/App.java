package edu.unitn.pbam.androidproject.utilities;

import android.app.Application;
import android.content.Context;
import edu.unitn.pbam.androidproject.model.dao.BookDao;
import edu.unitn.pbam.androidproject.model.dao.CategoryDao;
import edu.unitn.pbam.androidproject.model.dao.DListDao;
import edu.unitn.pbam.androidproject.model.dao.MovieDao;
import edu.unitn.pbam.androidproject.model.dao.db.BookDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.CategoryDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.DListDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.MovieDaoDb;

public class App extends Application {
	private static Context context;
	public static MovieDao mDao;
	public static BookDao bDao;
	public static CategoryDao cDao;
	public static DListDao dlDao;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		mDao = new MovieDaoDb();
		bDao = new BookDaoDb();
		cDao = new CategoryDaoDb();
		dlDao = new DListDaoDb();
	}

	public static Context getAppContext() {
		return context;
	}

}
