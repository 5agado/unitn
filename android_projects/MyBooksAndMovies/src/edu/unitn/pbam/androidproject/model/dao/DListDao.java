package edu.unitn.pbam.androidproject.model.dao;

import android.database.Cursor;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.DList.Type;

public interface DListDao extends ModelDao<DList> {
	public Cursor getByType(Type t);
}
