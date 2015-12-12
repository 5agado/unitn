package edu.unitn.pbam.androidproject.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.Uri;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Category.Type;
import edu.unitn.pbam.androidproject.model.Cover;

public class GoogleBooks {
	private final static int PAGE_SIZE = 10;
	private final static String BASE_URL = "https://www.googleapis.com/books/v1";
	private final static String VOLUME_DETAILS = "/volumes/";
	private final static String ISBN_SEARCH = "isbn:";
	private final static String VOLUME_SEARCH = "/volumes?q=";
	private final static String API_KEY = "key=AIzaSyB3hAZzpYG6ybO5s25eoLXej2nW2YneyUQ";
	private final static String MAX_RESULTS = "maxResults=" + PAGE_SIZE;

	private GoogleBooks() {
	}

	/*
	 * link format: https://www.googleapis.com/books/v1/volumes?q={search terms}
	 */
	public static ArrayList<Book> getBooksByTitle(String title) {
		String url = BASE_URL + VOLUME_SEARCH + Uri.encode(title) + "&"
				+ MAX_RESULTS + "&" + API_KEY;
		ArrayList<Book> books = new ArrayList<Book>();
		JSONObject jsonBooks = getJsonObject(url);

		try {
			JSONArray array = jsonBooks.getJSONArray("items");
			for (int i = 0; i < array.length(); i++) {
				books.add(getBookFromJson(array.getJSONObject(i), true));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return books;
	}

	/*
	 * link format: https://www.googleapis.com/books/v1/volumes?q=isbn:<isbn>
	 * Poichè il libro restituito dalla ricerca tramite isbn non è completo di
	 * tutte le informazioni, estraggo l'identificativo del libro e faccio una
	 * ricerca utilizzando quest'ultimo.
	 */
	public static Book getBookByIsbn(String isbn) {
		String url = BASE_URL + VOLUME_SEARCH + ISBN_SEARCH + Uri.encode(isbn)
				+ "&" + API_KEY;

		JSONObject jsonBooks = getJsonObject(url);
		Book book = null;

		try {
			if (jsonBooks.getInt("totalItems") > 0) {
				JSONArray array = jsonBooks.getJSONArray("items");
				JSONObject jsonBook = array.getJSONObject(0);
				String id = jsonBook.getString("id");
				return getBookById(id);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return book;
	}

	/*
	 * link format: https://www.googleapis.com/books/v1/volumes/<id>
	 */
	public static Book getBookById(String id) {
		String url = BASE_URL + VOLUME_DETAILS + id + "?" + API_KEY;
		JSONObject jsonBooks = getJsonObject(url);
		Book book = null;
		book = getBookFromJson(jsonBooks, true);
		return book;
	}

	/*
	 * se il parametro complete è true, viene caricata anche la cover
	 */
	private static Book getBookFromJson(JSONObject jsonBook, boolean complete) {
		Book book = new Book();

		// final String ISBN_TYPE = "ISBN_13";

		try {

			JSONObject volumeInfo = jsonBook.getJSONObject("volumeInfo");

			/*
			 * JSONArray industryIdentifiers =
			 * volumeInfo.getJSONArray("industryIdentifiers"); JSONObject
			 * identifier;
			 * 
			 * boolean found = false; for (int i=0;
			 * i<industryIdentifiers.length() && !found; i++) { identifier =
			 * industryIdentifiers.getJSONObject(i); if
			 * (identifier.getString("type").equals(ISBN_TYPE)) {
			 * book.setCode(identifier.getString("identifier")); found = true; }
			 * }
			 */

			book.setCode(jsonBook.getString("id"));

			book.setTitle(volumeInfo.getString("title"));

			book.setUrlinfo(volumeInfo.getString("infoLink"));

			final String PUBLISHER = "publisher";
			if (volumeInfo.has(PUBLISHER)) {
				book.setPublishing(volumeInfo.getString(PUBLISHER));
			}

			final String DATE = "publishedDate";
			if (volumeInfo.has(DATE)) {
				// publishedDate: yyyy-mm-dd
				int year = Integer.valueOf(volumeInfo
						.getString("publishedDate").split("-")[0]);
				book.setYear(year);
			}

			final String DESCRIPTION = "description";
			if (volumeInfo.has(DESCRIPTION)) {
				book.setDescription(volumeInfo.getString(DESCRIPTION));
			}

			final String AUTHORS = "authors";
			if (volumeInfo.has(AUTHORS)) {
				book.setAuthor((String) volumeInfo.getJSONArray("authors").get(
						0));
			}

			final String LANGUAGE = "language";
			if (volumeInfo.has(LANGUAGE)) {
				book.setLanguage(volumeInfo.getString(LANGUAGE));
			}

			ArrayList<Category> categories = new ArrayList<Category>();

			final String CATEGORIES = "categories";
			if (volumeInfo.has(CATEGORIES)) {
				JSONArray jsonCats = volumeInfo.getJSONArray(CATEGORIES);

				for (int i = 0; i < jsonCats.length(); i++) {
					String catString = jsonCats.getString(i);
					Category cat = new Category();
					cat.setName(catString);
					cat.setType(Type.BOOK);
					categories.add(cat);
				}
			}

			book.setCategories(categories);

			final String PAGES = "pageCount";
			if (volumeInfo.has(PAGES)) {
				book.setPages(volumeInfo.getInt(PAGES));
			}

			final String RATING = "averageRating";
			if (volumeInfo.has(RATING)) {
				book.setWebrating(volumeInfo.getDouble(RATING) * 20);
			}

			if (complete) {
				final String IMAGES = "imageLinks";
				if (volumeInfo.has(IMAGES)) {
					JSONObject jsonImgs = volumeInfo.getJSONObject(IMAGES);
					Cover c = RottenTomatoes.getCoverAt(jsonImgs
							.getString("thumbnail"));
					book.setCover(c);
				}
			}
			book.setSync(Document.SyncType.SYNCAUTO);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return book;
	}

	private static JSONObject getJsonObject(String url) {
		HttpGet request = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();

		try {
			HttpResponse response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}
			JSONTokener tokener = new JSONTokener(builder.toString());
			JSONObject json = new JSONObject(tokener);
			return json;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
