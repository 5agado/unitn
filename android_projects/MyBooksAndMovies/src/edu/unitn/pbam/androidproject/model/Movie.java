package edu.unitn.pbam.androidproject.model;

import java.io.Serializable;
import java.util.List;

public class Movie extends Document implements Serializable {
	private String director;
	private int duration;
	private String studio;
	private List<String> actors;

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	public List<String> getActors() {
		return actors;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

}
