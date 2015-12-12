package edu.unitn.pbam.androidproject.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.activities.HomeActivity;
import edu.unitn.pbam.androidproject.model.dao.db.BookDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.CategoryDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.CoverDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.DListDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.MovieDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.SQLiteHelper;

public class Backup {
	private static final String BACKUP_DB_NAME = "booksmovies.db";

	private Backup() {
	}

	public static void backupDatabase(final Context context) {
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
			private ProgressDialog dialog;

			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(context);
				dialog.setIndeterminate(true);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setMessage(context.getString(R.string.progress_wait));
				dialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				File backupFile = null;
				try {
					File dbFile = App.getAppContext().getDatabasePath(
							SQLiteHelper.DB_NAME);
					backupFile = new File(
							Environment.getExternalStorageDirectory()
									+ Constants.BACKUP_PATH);
					backupFile.getParentFile().mkdirs();

					BufferedInputStream src = null;
					ZipOutputStream dest = null;

					try {
						src = new BufferedInputStream(new FileInputStream(
								dbFile));
						dest = new ZipOutputStream(new BufferedOutputStream(
								new FileOutputStream(backupFile)));

						byte[] buffer = new byte[1024];
						dest.putNextEntry(new ZipEntry(BACKUP_DB_NAME));
						int len;
						while ((len = src.read(buffer)) > 0)
							dest.write(buffer, 0, len);
						src.close();
						dest.closeEntry();

						File coverDirectory = new File(CoverDaoDb.COVER_PATH);
						if (coverDirectory.exists()) {
							for (File f : coverDirectory.listFiles()) {
								src = new BufferedInputStream(
										new FileInputStream(f));
								dest.putNextEntry(new ZipEntry(f.getName()));
								while ((len = src.read(buffer)) > 0)
									dest.write(buffer, 0, len);
								src.close();
								dest.closeEntry();
							}
						}
					} finally {
						if (src != null) {
							src.close();
						}
						if (dest != null) {
							dest.close();
						}
					}
					return true;
				} catch (IOException ex) {
					ex.printStackTrace();
					if (backupFile.exists())
						backupFile.delete();
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				dialog.dismiss();
				if (result)
					Toast.makeText(context, App.getAppContext().getResources().getString(R.string.backup_success),
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(context, App.getAppContext().getResources().getString(R.string.backup_error),
							Toast.LENGTH_SHORT).show();
			}

		};
		task.execute();
	}

	public static void importBackup(final Context context) {
		final AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
			private ProgressDialog dialog;

			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(context);
				dialog.setIndeterminate(true);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setMessage(context.getString(R.string.progress_wait));
				dialog.show();
				SQLiteHelper.getInstance().close();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				File dbFile = App.getAppContext().getDatabasePath(
						SQLiteHelper.DB_NAME);
				try {
					ZipInputStream src = null;
					BufferedOutputStream dest = null;
					File backupFile = new File(
							Environment.getExternalStorageDirectory()
									+ Constants.BACKUP_PATH);
					if (!backupFile.exists())
						return false;

					// elimino le vecchie cover
					String coverPath = CoverDaoDb.COVER_PATH;
					File imgsDir = new File(coverPath);
					if (imgsDir.exists()) {
						for (File f : imgsDir.listFiles()) {
							f.delete();
						}
					} else {
						imgsDir.mkdirs();
					}

					try {
						src = new ZipInputStream(new BufferedInputStream(
								new FileInputStream(backupFile)));

						byte[] buffer = new byte[1024];
						int len;
						ZipEntry entry;

						while ((entry = src.getNextEntry()) != null) {
							// database
							if (entry.getName().equals(BACKUP_DB_NAME)) {
								dest = new BufferedOutputStream(
										new FileOutputStream(dbFile));
							}
							// cover
							else {
								dest = new BufferedOutputStream(
										new FileOutputStream(coverPath + "/"
												+ entry.getName()));
							}
							while ((len = src.read(buffer)) != -1)
								dest.write(buffer, 0, len);
							src.closeEntry();
							dest.close();
						}

					} finally {
						if (src != null) {
							src.close();
						}
						if (dest != null) {
							dest.close();
						}
					}

					return true;
				} catch (IOException ex) {
					ex.printStackTrace();
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				App.mDao = new MovieDaoDb();
				App.bDao = new BookDaoDb();
				App.cDao = new CategoryDaoDb();
				App.dlDao = new DListDaoDb();
				dialog.dismiss();
				if (result)
					Toast.makeText(context, App.getAppContext().getResources().getString(R.string.import_b_success),
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(context, App.getAppContext().getResources().getString(R.string.import_b_error),
							Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(context, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}

		};

		new AlertDialog.Builder(context)
				.setMessage(context.getString(R.string.import_confirmation))
				.setTitle(context.getString(R.string.import_title))
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								task.execute();
							}
						}).setNegativeButton(android.R.string.no, null).show();

	}
}
