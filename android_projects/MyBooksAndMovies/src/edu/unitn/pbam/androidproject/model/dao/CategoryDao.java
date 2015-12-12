package edu.unitn.pbam.androidproject.model.dao;

import android.database.Cursor;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.Category.Type;

public interface CategoryDao extends ModelDao<Category> {
	public Cursor getByType(Type t);

	public Cursor getCategoriesMatching(String pattern);
}
