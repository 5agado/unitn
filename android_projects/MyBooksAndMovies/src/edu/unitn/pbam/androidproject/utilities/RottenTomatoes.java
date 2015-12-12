package edu.unitn.pbam.androidproject.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.Cover;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Movie;

public class RottenTomatoes {
	private final static int I_RESULTS_PER_PAGE = 9;
	private final static String RESULTS_PER_PAGE = String
			.valueOf(I_RESULTS_PER_PAGE);

	private final static String TAG = "RottenTomatoes";
	private final static String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies";
	private final static String JSON = ".json?";
	private final static String APIKEY = "apikey=zh3dna8459zttbt996yqjfqf";
	private final static String SEARCH = "q=";
	private final static String PAGE_LIMIT = "page_limit=";
	private final static String PAGE_NUMBER = "page=";

	private RottenTomatoes() {
	}

	/*
	 * 
	 * {"link_template": "http://api.rottentomatoes.com/api/public/v1.0/
	 * movies.json
	 * ?q={search-term}&page_limit={results-per-page}&page={page-number}"}
	 */

	public static String[] getIdsByTitle(String title) {
		String[] ids = new String[I_RESULTS_PER_PAGE];
		String url = URL + JSON + APIKEY + "&" + SEARCH + Uri.encode(title)
				+ "&" + PAGE_LIMIT + RESULTS_PER_PAGE;
		JSONObject res = executeAndParseRequest(url);
		try {
			int numResult = Math.min(res.getInt("total"), I_RESULTS_PER_PAGE);
			JSONArray movies = res.getJSONArray("movies");
			for (int i = 0; i < numResult; i++) {
				ids[i] = movies.getJSONObject(i).getString("id");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ids;
	}

	public static ArrayList<Movie> getMoviesByTitle(String title) {
		ArrayList<Movie> movies = new ArrayList<Movie>(I_RESULTS_PER_PAGE);
		String url = URL + JSON + APIKEY + "&" + SEARCH + Uri.encode(title)
				+ "&" + PAGE_LIMIT + RESULTS_PER_PAGE;
		JSONObject res = executeAndParseRequest(url);
		try {
			int numResult = Math.min(res.getInt("total"), I_RESULTS_PER_PAGE);
			JSONArray array = res.getJSONArray("movies");
			for (int i = 0; i < numResult; i++) {
				movies.add(getMovieFrom(array.getJSONObject(i), false));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return movies;
	}

	/*
	 * http://api.rottentomatoes.com/api/public/v1.0/movies/770672122.json?apikey
	 * =[your_api_key]
	 */
	public static Movie getMovieByID(String id) {
		String url = URL + "/" + id + JSON + APIKEY;
		Movie res = getMovieFrom(executeAndParseRequest(url), true);
		return res;
	}

	public static Movie getMovieFrom(JSONObject json, boolean isComplete) {
		Movie res = new Movie();
		JSONArray array;
		JSONObject obj;

		try {

			array = json.getJSONArray("abridged_cast");
			List<String> act = new ArrayList<String>();
			for (int i = 0; i < array.length(); i++) {
				obj = array.getJSONObject(i);
				act.add(obj.getString("name"));
			}
			res.setActors(act);

			res.setCode(json.getString("id"));

			obj = json.getJSONObject("posters");
			res.setCover(getCoverAt(obj.getString("profile")));
			res.setDescription(json.getString("synopsis"));
			if (res.getDescription() == null
					|| res.getDescription().length() == 0) {
				if (json.has("critics_consensus"))
					res.setDescription(json.getString("critics_consensus"));
			}

			res.setDuration(json.getInt("runtime"));

			res.setTitle(json.getString("title"));

			obj = json.getJSONObject("links");
			res.setUrlinfo(obj.getString("alternate"));

			obj = json.getJSONObject("ratings");
			res.setWebrating(obj.getDouble("critics_score"));
			if (res.getWebrating() < 0 || res.getWebrating() > 100)
				res.setWebrating(0);
			res.setYear(json.getInt("year"));

			if (isComplete) {
				array = json.getJSONArray("genres");
				ArrayList<Category> cat = new ArrayList<Category>();
				for (int i = 0; i < array.length(); i++) {
					Category c = new Category();
					c.setType(Category.Type.MOVIE);
					c.setName(array.getString(i));
					cat.add(c);
				}
				res.setCategories(cat);

				array = json.getJSONArray("abridged_directors");
				obj = array.getJSONObject(0);
				res.setDirector(obj.getString("name"));
				res.setStudio(json.getString("studio"));
			}
			res.setSync(Document.SyncType.SYNCAUTO);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public static Cover getCoverAt(String url) {
		Bitmap pic = null;
		Cover res = new Cover();
		try {
			pic = BitmapFactory
					.decodeStream((InputStream) new java.net.URL(url)
							.getContent());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		res.setImage(new BitmapDrawable(App.getAppContext().getResources(), pic));
		res.setRemoteUrl(url);
		return res;
	}

	private static JSONObject executeAndParseRequest(String url) {
		Log.d("url = ", url);
		HttpGet request = new HttpGet(url);
		Log.d("request = ", request.getURI().getQuery());
		HttpClient client = new DefaultHttpClient();

		try {
			HttpResponse response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			JSONTokener tokener = new JSONTokener(builder.toString());
			JSONObject json = new JSONObject(tokener);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}

}
