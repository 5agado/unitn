package edu.unitn.pbam.androidproject.utilities;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Movie;
import edu.unitn.pbam.androidproject.model.dao.db.BookDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.MovieDaoDb;

public class Import {
	private Import() {
	}

	public static void importMoviesFromList(String filePath) {
		// String fileText = loadFile(filePath, false);
		String fileText = Utils.loadFileFromFS(filePath);
		if (fileText == null) {
			return;
		}
		String titles[] = fileText.split(";");
		if (!Utils.isNetworkAvailable()) {
			return;
		}
		new AsyncTask<String, Void, Void>() {
			@Override
			protected void onPreExecute() {
			}

			@Override
			protected Void doInBackground(String... params) {
				for (String title : params) {
					if (!title.equals("")){
						try {
							Movie movie = new Movie();
							movie.setTitle(title);
							App.mDao.save(movie);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				return null;
			}

			protected void onPostExecute(Void id) {
				Sync.syncMovies();
			}
		}.execute(titles);

	}

	public static void importBooksFromList(String filePath) {
		String fileText = Utils.loadFileFromFS(filePath);
		if (fileText == null) {
			return;
		}
		String titles[] = fileText.split(";");
		new AsyncTask<String, Void, Void>() {
			protected void onPreExecute() {
			}

			@Override
			protected Void doInBackground(String... params) {
				for (String title : params) {
					if (!title.equals("")){
						try {
							Book book = new Book();
							book.setTitle(title);
							App.bDao.save(book);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				Sync.syncBooks();
			}

		}.execute(titles);

	}

	public static void exportMoviesCSV(String filePath) {
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				Cursor cursMovies = App.mDao.getAll();
				List<Movie> movies = new ArrayList<Movie>();
				cursMovies.moveToFirst();
				while (!cursMovies.isAfterLast()) {
					movies.add(MovieDaoDb.generateMovie(cursMovies));
					cursMovies.moveToNext();
				}
				try {
					BufferedOutputStream buffOutput = new BufferedOutputStream(
							new FileOutputStream(params[0]));
					PrintWriter output = new PrintWriter(buffOutput);
					for (Movie m : movies) {
						output.write(m.getTitle() + ";\n");
					}
					output.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
				return true;
			}

			protected void onPostExecute(Boolean result) {
				Context context = App.getAppContext();
				if (result) {
					Toast.makeText(context,
							context.getString(R.string.export_success),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context,
							context.getString(R.string.export_error),
							Toast.LENGTH_SHORT).show();
				}

			};

		}.execute(filePath);
	}

	public static void exportBooksCSV(String filePath) {
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				Cursor cursBooks = App.bDao.getAll();
				List<Book> books = new ArrayList<Book>();
				cursBooks.moveToFirst();
				while (!cursBooks.isAfterLast()) {
					books.add(BookDaoDb.generateBook(cursBooks));
					cursBooks.moveToNext();
				}
				try {
					BufferedOutputStream buffOutput = new BufferedOutputStream(
							new FileOutputStream(params[0]));
					PrintWriter output = new PrintWriter(buffOutput);
					for (Book b : books) {
						output.write(b.getTitle() + ";\n");
					}
					output.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
				return true;
			}

			protected void onPostExecute(Boolean result) {
				Context context = App.getAppContext();
				if (result) {
					Toast.makeText(context,
							context.getString(R.string.export_success),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context,
							context.getString(R.string.export_error),
							Toast.LENGTH_SHORT).show();
				}

			};

		}.execute(filePath);
	}
}
