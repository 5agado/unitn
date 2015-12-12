package edu.unitn.pbam.androidproject.model;

import java.io.Serializable;

import android.graphics.drawable.BitmapDrawable;

public class Cover extends Model implements Serializable {
	private String path;
	private String remoteUrl;
	transient private BitmapDrawable image;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public BitmapDrawable getImage() {
		return image;
	}

	public void setImage(BitmapDrawable image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Path: " + path + "\nremoteUrl: " + remoteUrl + "\n";
	}

}
