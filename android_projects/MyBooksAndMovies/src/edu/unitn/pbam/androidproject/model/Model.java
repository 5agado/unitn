package edu.unitn.pbam.androidproject.model;

import java.io.Serializable;

public abstract class Model implements Serializable {
	// entity identifier
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
