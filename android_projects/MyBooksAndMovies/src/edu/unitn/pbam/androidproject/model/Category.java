package edu.unitn.pbam.androidproject.model;

import java.io.Serializable;

public class Category extends Model implements Serializable {
	private String name;
	private String description;
	private Type type;

	public static enum Type {
		BOOK, MOVIE, BOTH
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	};

}
