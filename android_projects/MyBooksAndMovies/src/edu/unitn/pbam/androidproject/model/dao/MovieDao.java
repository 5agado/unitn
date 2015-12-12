package edu.unitn.pbam.androidproject.model.dao;

import android.database.Cursor;
import edu.unitn.pbam.androidproject.model.Movie;

public interface MovieDao extends ModelDao<Movie> {
	public Cursor getByCategory(long catId);

	public Cursor getByDList(long listId);

	public Cursor getByDirector(String dir);

	public Cursor getNotSync();

	public Cursor getAllDirectors();

	public Cursor getFiltered(String pattern);

	public Cursor getDirectorsMatching(String pattern);

	/**
	 * 
	 * @param rating
	 *            intero fra 1 e 10
	 * @return tutti i libri aventi rating nel range (rating-1, rating]
	 */
	public Cursor getByRating(int rating);

	public int getNumberOfMovies();

	public int getToWatchMovies();

	public int getWatchedMovies();

	public double getAverageRating();
}
