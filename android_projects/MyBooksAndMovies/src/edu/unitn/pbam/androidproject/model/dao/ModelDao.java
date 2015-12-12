package edu.unitn.pbam.androidproject.model.dao;

import android.database.Cursor;

public interface ModelDao<T> {
	T getById(long id);

	Cursor getAll();

	/**
	 * 
	 * @param element
	 * @return the id assigned to this element
	 */
	long save(T element);

	void delete(T element);

	public void open();

	public void close();
}
