package edu.unitn.pbam.androidproject.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Movie;

public class Sync {
	private static final int BOOKS_NOTIFICATION = 1;
	private static final int MOVIES_NOTIFICATION = 2;

	private Sync() {
	}

	public static void syncAll() {
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					App.getAppContext(),
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		syncBooks();
		syncMovies();
	}

	public static void syncBooks() {
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					App.getAppContext(),
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		Cursor books = App.bDao.getNotSync();
		books.moveToFirst();
		Book[] booksList = new Book[books.getCount()];
		int i = 0;
		while (!books.isAfterLast()) {
			booksList[i] = App.bDao.getById(books.getLong(0));
			books.moveToNext();
			i++;
		}
		books.close();

		new AsyncTask<Book, Void, Void>() {
			NotificationManager mNotifyManager;
			Builder mBuilder;

			protected void onPreExecute() {
				Context context = App.getAppContext();
				mNotifyManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mBuilder = new NotificationCompat.Builder(context);
				mBuilder.setContentTitle(context.getString(R.string.books_sync))
						.setSmallIcon(R.drawable.ic_notebook)
						.setContentText(
								context.getString(R.string.sync_progress))
						.setOngoing(true).setWhen(System.currentTimeMillis());
			};

			@Override
			protected Void doInBackground(Book... books) {
				int counter = 0;
				int size = books.length;

				for (Book b : books) {
					try {
						mBuilder.setProgress(size, counter, false);
						counter++;
						Notification notification = mBuilder.build();
						notification.flags = Notification.FLAG_NO_CLEAR
								| Notification.FLAG_ONGOING_EVENT;
						mNotifyManager.notify(BOOKS_NOTIFICATION, notification);

						Book book = GoogleBooks.getBooksByTitle(b.getTitle())
								.get(0);

						if (book != null) {
							book = GoogleBooks.getBookById(book.getCode());

							book.setId(b.getId());
							book.setLists(b.getLists());
							book.setRating(b.getRating());
							book.setNotes(b.getNotes());
							App.bDao.save(book);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				return null;
			}

			protected void onPostExecute(Void id) {
				mBuilder.setContentText(
						App.getAppContext().getString(R.string.sync_end))
						.setProgress(0, 0, false);
				Notification notification = mBuilder.build();
				notification.flags &= ~Notification.FLAG_ONGOING_EVENT;
				notification.flags &= ~Notification.FLAG_NO_CLEAR;
				mNotifyManager.notify(BOOKS_NOTIFICATION, notification);
			}
		}.execute(booksList);
	}

	public static void syncMovies() {
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					App.getAppContext(),
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		Cursor movies = App.mDao.getNotSync();
		movies.moveToFirst();
		Movie[] moviesList = new Movie[movies.getCount()];
		int i = 0;
		while (!movies.isAfterLast()) {
			moviesList[i] = App.mDao.getById(movies.getLong(0));
			movies.moveToNext();
			i++;
		}
		movies.close();

		new AsyncTask<Movie, Void, Void>() {
			NotificationManager mNotifyManager;
			Builder mBuilder;

			protected void onPreExecute() {
				Context context = App.getAppContext();
				mNotifyManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mBuilder = new NotificationCompat.Builder(context);
				mBuilder.setContentTitle(
						context.getString(R.string.movies_sync))
						.setSmallIcon(R.drawable.ic_movie)
						.setContentText(
								context.getString(R.string.sync_progress))
						.setOngoing(true).setWhen(System.currentTimeMillis());
			};

			@Override
			protected Void doInBackground(Movie... movies) {
				int counter = 0;
				int size = movies.length;

				for (Movie m : movies) {
					try {
						mBuilder.setProgress(size, counter, false);
						counter++;
						Notification notification = mBuilder.build();
						notification.flags |= Notification.FLAG_NO_CLEAR;
						mNotifyManager
								.notify(MOVIES_NOTIFICATION, notification);

						String movie_id = RottenTomatoes.getIdsByTitle(m
								.getTitle())[0];
						Movie movie;
						if (movie_id != null) {
							movie = RottenTomatoes.getMovieByID(movie_id);

							movie.setId(m.getId());
							movie.setLists(m.getLists());
							movie.setRating(m.getRating());
							movie.setNotes(m.getNotes());
							App.mDao.save(movie);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				return null;
			}

			protected void onPostExecute(Void id) {
				mBuilder.setContentText(
						App.getAppContext().getString(R.string.sync_end))
						.setProgress(0, 0, false);
				Notification notification = mBuilder.build();
				notification.flags &= ~Notification.FLAG_ONGOING_EVENT;
				notification.flags &= ~Notification.FLAG_NO_CLEAR;
				mNotifyManager.notify(MOVIES_NOTIFICATION, notification);
			};
		}.execute(moviesList);
	}
}
