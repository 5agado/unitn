package edu.unitn.pbam.androidproject.model.dao.db;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import edu.unitn.pbam.androidproject.model.Cover;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.utilities.App;

public class CoverDaoDb {

	private static final String PATH_COL = "cover_path";
	private static final String REM_URL_COL = "cover_remoteurl";
	private static final String ID_COL = "_id";
	public static String COVER_PATH = "";

	static {
		try {
			COVER_PATH = App.getAppContext().getPackageManager()
					.getPackageInfo(App.getAppContext().getPackageName(), 0).applicationInfo.dataDir
					+ "/imgs";
		} catch (NameNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	private CoverDaoDb() {
	};

	public static String getCoverPath(Document doc) {
		return COVER_PATH + "/" + doc.getId();
	}

	/* aggiunge le informazioni sulla cover all'oggetto ContentValues passato */
	public static void updateContentValues(Cover element, ContentValues values) {
		if (element != null) {
			values.put(REM_URL_COL, element.getRemoteUrl());
		}
	}

	public static Cover generateCover(Cursor c) {
		Cover cov = new Cover();
		cov.setPath(COVER_PATH + "/" + c.getString(c.getColumnIndex(ID_COL)));
		cov.setRemoteUrl(c.getString(c.getColumnIndex(REM_URL_COL)));

		if (coverNotEmpty(cov, true)) {
			Bitmap img = null;
			try {
				img = BitmapFactory.decodeStream(new BufferedInputStream(
						new FileInputStream(cov.getPath())));
			} catch (IOException e) {
				e.printStackTrace();
			}
			cov.setImage(new BitmapDrawable(App.getAppContext().getResources(),
					img));
		} else {
			cov = null;
		}
		return cov;
	}

	public static void saveCover(Document doc) {
		Cover c = doc.getCover();
		if (coverNotEmpty(c, false)) {
			c.setPath(getCoverPath(doc));
			try {
				File file = new File(c.getPath());
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(c.getPath()));
				Bitmap bmp = c.getImage().getBitmap();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.close();
				Log.d("Created", "File saved: " + c.getPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void deleteCover(Cover c) {
		if (coverNotEmpty(c, true)) {
			File file = new File(c.getPath());
			if (!file.delete()) {
				Log.e("Delete error", "Can not delete file " + c.getPath());
			}
		}
	}

	private static boolean coverNotEmpty(Cover c, boolean exists) {
		if (c == null) {
			return false;
		}
		if (exists) {
			boolean isNotNull = c.getPath() != null && !c.getPath().equals("");
			if (isNotNull) {
				File f = new File(c.getPath());
				isNotNull = f.exists();
			}
			return isNotNull;
		} else {
			return c.getImage() != null;
		}
	}

}
