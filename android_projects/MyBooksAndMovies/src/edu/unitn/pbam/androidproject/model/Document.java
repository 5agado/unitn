package edu.unitn.pbam.androidproject.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Document extends Model implements Serializable {
	public enum SyncType {NOSYNC, SYNCAUTO, SYNCUSER};
	
	// codice identificativo: ISBN oppure UPC
	private String code;

	// liste a cui l'oggetto è associato
	private List<DList> lists = new ArrayList<DList>();

	// categorie a cui l'oggetto è associato
	private List<Category> categories = new ArrayList<Category>();

	private Cover cover;
	private String title;
	private int year;
	private String description;
	private double rating;

	private String urlinfo;
	private String notes;
	private double webrating;
	private SyncType sync = SyncType.NOSYNC;

	public SyncType getSync() {
		return sync;
	}

	public void setSync(SyncType sync) {
		this.sync = sync;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Cover getCover() {
		return cover;
	}

	public void setCover(Cover cover) {
		this.cover = cover;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getUrlinfo() {
		return urlinfo;
	}

	public void setUrlinfo(String urlinfo) {
		this.urlinfo = urlinfo;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public double getWebrating() {
		return webrating;
	}

	public void setWebrating(double webrating) {
		this.webrating = webrating;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> cat) {
		categories = cat;
	}

	public void setLists(List<DList> lst) {
		lists = lst;
	}

	public List<DList> getLists() {
		return lists;
	}

}
