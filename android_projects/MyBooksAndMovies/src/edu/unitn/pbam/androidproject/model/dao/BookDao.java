package edu.unitn.pbam.androidproject.model.dao;

import android.database.Cursor;
import edu.unitn.pbam.androidproject.model.Book;

public interface BookDao extends ModelDao<Book> {
	public Cursor getByCategory(long catId);

	public Cursor getByDList(long listId);

	public Cursor getByAuthor(String author);

	public Cursor getNotSync();

	public Cursor getAllAuthors();

	public Cursor getFiltered(String pattern);

	public Cursor getAuthorsMatching(String pattern);

	/**
	 * 
	 * @param rating
	 *            intero fra 1 e 10
	 * @return tutti i libri aventi rating nel range (rating-1, rating]
	 */
	public Cursor getByRating(int rating);

	public int getNumberOfBooks();

	public int getToReadBooks();

	public int getReadBooks();

	public double getAverageRating();
}
