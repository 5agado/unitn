package edu.unitn.pbam.androidproject.utilities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_MOVIE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Movie;

public class Facebook {
	private final static String TAG = "Facebook";
	private static final boolean UPLOAD_IMAGE = true;
	private static final String URL_SITE = "http://androidbooksandmovies.appspot.com/";
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static Activity activity;
	private static boolean pendingPublishReauthorization = false;
	private static Document pendingDoc;
	private static int docType;
	private static ProgressDialog progressDialog;

	private static Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.i(TAG, "callback");
			Log.i("Session state= ", state.toString());
			if (exception == null) {
				if (pendingPublishReauthorization && session.isOpened()){
						//&& state == SessionState.OPENED)
					shareDocument(pendingDoc, activity);
				}
			} else {
				Log.i(TAG, "call of Session.StatusCallback with exception "
						+ exception.getMessage());
			}
		}
	};

	private static Request.Callback reqCallback = new Request.Callback() {
		public void onCompleted(Response response) {
			if (response != null) {
				FacebookRequestError error = response.getError();
				if (error != null) {
					Log.e("onCompleted", error.getErrorMessage());
					Toast.makeText(
							activity.getApplicationContext(),
							App.getAppContext().getResources()
									.getString(R.string.error),
							Toast.LENGTH_LONG).show();

				} else {
					Log.i("onCompleted", "OK");
					Toast.makeText(
							activity.getApplicationContext(),
							App.getAppContext().getResources()
									.getString(R.string.doc_shared),
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(
						activity.getApplicationContext(),
						App.getAppContext().getResources()
								.getString(R.string.error), Toast.LENGTH_LONG)
						.show();
				Log.e("onCompleted", "null response");
			}
		}
	};

	public static void shareDocument(Document doc, Activity activity) {
		Facebook.activity = activity;

		/*
		 * if (pendingPublishReauthorization){ Toast.makeText(
		 * activity.getApplicationContext(), App.getAppContext().getResources()
		 * .getString(R.string.pending_share), Toast.LENGTH_SHORT).show();
		 * return; }
		 */

		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					activity.getApplicationContext(),
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}

		Session session = Session.getActiveSession();

		if (session != null && session.getState().isOpened()) {
			List<String> permissions = session.getPermissions();
			if (!Utils.isSubsetOf(PERMISSIONS, permissions)) {
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						activity, PERMISSIONS);
				pendingPublishReauthorization = true;
				pendingDoc = doc;
				session.requestNewPublishPermissions(newPermissionsRequest);
				Log.i("sessionCheck", "requestNewPublishPermissions");
				return;
			}
			Log.i("sessionCheck", "cofirm OPENED e PERMISSIONS");
			// publishDocAsMessage(doc);
			publishDoc(doc);
		} else {
			Log.i("sessionCheck", "NULL or notOpened session");
			pendingPublishReauthorization = true;
			pendingDoc = doc;
			Session.openActiveSession(activity, true, callback);
			/*
			 * if (session == null) { Log.i("sessionCheck",
			 * "NULL session, openActiveSession"); pendingPublishReauthorization
			 * = true; pendingDoc = doc; Session.openActiveSession(activity,
			 * true, callback); } else { Log.i("sessionCheck",
			 * "notOpenedSession, openForRead"); pendingPublishReauthorization =
			 * true; pendingDoc = doc; session.openForRead(new
			 * Session.OpenRequest(activity) .setCallback(callback)); //
			 * session.openForPublish(new Session.OpenRequest(activity). //
			 * setCallback(callback).setRequestCode(100)); }
			 */
		}
	}

	public static void publishDocAsMessage(Document doc) {
		Session session = Session.getActiveSession();

		Bundle postParams = new Bundle();
		postParams.putString("message", doc.toString());

		Request request = new Request(session, "me/feed", postParams,
				HttpMethod.POST, reqCallback);
		RequestAsyncTask task = new RequestAsyncTask(request);
		task.execute();
		pendingPublishReauthorization = false;
	}

	/*
	 * Request: Staging image upload request If uploading an image, set up the
	 * first batch request to do this.
	 */
	private static Request getImageRequest(Document doc) {
		Bundle imageParams = new Bundle();
		Bitmap image;
		if (doc.getCover() == null) {
			image = BitmapFactory.decodeResource(activity.getResources(),
					R.drawable.poster_default);
		} else
			image = doc.getCover().getImage().getBitmap();
		imageParams.putParcelable("file", image);

		Request.Callback imageCallback = new Request.Callback() {

			@Override
			public void onCompleted(Response response) {
				FacebookRequestError error = response.getError();
				if (error != null) {
					progressDialog.dismiss();
					Log.i(TAG, error.getErrorMessage());
					Toast.makeText(
							activity.getApplicationContext(),
							App.getAppContext().getResources()
									.getString(R.string.error),
							Toast.LENGTH_LONG).show();
				}
			}
		};

		Request imageRequest = new Request(Session.getActiveSession(),
				"me/staging_resources", imageParams, HttpMethod.POST,
				imageCallback);

		imageRequest.setBatchEntryName("imageUpload");
		return imageRequest;
	}

	/*
	 * Request: Object request
	 */
	private static Request getObjectRequest(Document doc) {
		String graphPath;
		String url;
		if (docType == DOCTYPE_MOVIE) {
			String urlInfo = doc.getUrlinfo();
			String title = urlInfo.substring(0, urlInfo.length() - 1);
			int slashIndex = title.lastIndexOf("/");
			url = URL_SITE + "movies/"
					+ title.subSequence(slashIndex + 1, title.length());
		} else {
			url = URL_SITE + "books/?id=" + doc.getCode();
		}
		try {
			// Set up the JSON representing the document
			JSONObject document = new JSONObject();
			if (UPLOAD_IMAGE) {
				// "uri" result reefer to the previous batch request
				document.put("image", "{result=imageUpload:$.uri}");

			} else {
				document.put("image", doc.getCover().getRemoteUrl());
			}
			document.put("title", doc.getTitle());
			document.put("url", url);
			document.put("description", doc.getDescription());
			JSONObject data = new JSONObject();
			data.put("isbn", "0-553-57340-3");
			document.put("data", data);

			if (docType == DOCTYPE_MOVIE) {
				graphPath = "me/objects/video.movie";
			} else {
				graphPath = "me/objects/books.book";
			}

			// Set up object request parameters
			Bundle objectParams = new Bundle();
			objectParams.putString("object", document.toString());

			Request.Callback objectCallback = new Request.Callback() {

				@Override
				public void onCompleted(Response response) {
					if (response != null) {
						Log.d(TAG, response.toString());

					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						progressDialog.dismiss();// dismissProgressDialog();
						Log.i(TAG, error.getErrorMessage());
						Toast.makeText(
								activity.getApplicationContext(),
								App.getAppContext().getResources()
										.getString(R.string.error),
								Toast.LENGTH_LONG).show();
					}
				}
			};

			Request objectRequest = new Request(Session.getActiveSession(),
					graphPath, objectParams, HttpMethod.POST, objectCallback);

			// Set the batch name so you can refer to the result
			// in the follow-on publish action request
			objectRequest.setBatchEntryName("objectCreate");
			return objectRequest;
		} catch (JSONException e) {
			Log.i(TAG, "JSON error " + e.getMessage());
			progressDialog.dismiss();
			Toast.makeText(
					activity.getApplicationContext(),
					App.getAppContext().getResources()
							.getString(R.string.error), Toast.LENGTH_LONG)
					.show();
		}
		return null;
	}

	private static Request getActionRequest(Document doc) {
		String graphPath;

		// Refer to the "id" in the result from the previous batch request
		Bundle actionParams = new Bundle();
		if (docType == DOCTYPE_MOVIE) {
			actionParams.putString("movie", "{result=objectCreate:$.id}");
		} else {
			actionParams.putString("book", "{result=objectCreate:$.id}");
		}

		actionParams.putString("fb:explicitly_shared", "true");

		Request.Callback actionCallback = new Request.Callback() {

			@Override
			public void onCompleted(Response response) {
				FacebookRequestError error = response.getError();
				if (error != null) {
					progressDialog.dismiss();
					Log.e(TAG, error.getErrorMessage());
					Toast.makeText(
							activity.getApplicationContext(),
							App.getAppContext().getResources()
									.getString(R.string.error),
							Toast.LENGTH_LONG).show();
				} else {
					String actionId = null;
					try {
						JSONObject graphResponse = response.getGraphObject()
								.getInnerJSONObject();
						actionId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i(TAG, "JSON error " + e.getMessage());
					}
					Log.i(TAG, "id=" + actionId);
					progressDialog.dismiss();
					Toast.makeText(
							activity.getApplicationContext(),
							App.getAppContext().getResources()
									.getString(R.string.doc_shared),
							Toast.LENGTH_LONG).show();
				}
			}
		};

		List<DList> lists = doc.getLists();
		Set<Integer> listsID = new HashSet<Integer>();
		for (DList l : lists) {
			listsID.add((int)l.getId());
		}
		if (docType == DOCTYPE_MOVIE) {
			Log.i("isInstanceof", "movie.class");
			if (listsID.contains(Constants.DLIST_TOWATCH)) {
				graphPath = "me/video.wants_to_watch";
				Log.d(TAG, "toWatch");
			} else {
				double normValue = (doc.getRating() / 100.);
				actionParams.putDouble("value", doc.getRating());
				actionParams.putInt("scale", 100);
				actionParams.putDouble("normalized_value", normValue);
				graphPath = "me/video.rates";
				// graphPath = "me/video.watches";
			}
		} else {
			Log.i("isInstanceof", "book.class");
			if (listsID.contains(Constants.DLIST_TOREAD)) {
				Log.d(TAG, "toRead");
				graphPath = "me/books.wants_to_read";
			} else {
				double normValue = (doc.getRating() - 1) / (100 - 1);
				// graphPath = "me/books.reads";
				// SimpleDateFormat s = new
				// SimpleDateFormat("yyyy-MM-dd-hh:mm:ss", Locale.ENGLISH);
				// String format = s.format(new Date());
				// actionParams.putString("timestamp", format);
				// actionParams.putDouble("percent_complete", 100.0);
				actionParams.putDouble("value", doc.getRating());
				actionParams.putInt("scale", 100);
				actionParams.putDouble("normalized_value", normValue);
				graphPath = "me/books.rates";
			}
		}

		Request actionRequest = new Request(Session.getActiveSession(),
				graphPath, actionParams, HttpMethod.POST, actionCallback);
		return actionRequest;
	}

	public static void publishDoc(Document doc) {
		Log.i("publishDoc", "start publishing");
		String docTypeName;

		if (Movie.class.isInstance(doc)) {
			docType = DOCTYPE_MOVIE;
			docTypeName = App.getAppContext().getResources()
					.getString(R.string.movie);
		} else {
			docType = DOCTYPE_BOOK;
			docTypeName = App.getAppContext().getResources()
					.getString(R.string.book);
		}

		progressDialog = ProgressDialog.show(activity, "", App.getAppContext()
				.getResources().getString(R.string.progress_share)
				+ docTypeName, true);

		RequestBatch requestBatch = new RequestBatch();

		if (UPLOAD_IMAGE) {
			requestBatch.add(getImageRequest(doc));
		}
		requestBatch.add(getObjectRequest(doc));
		requestBatch.add(getActionRequest(doc));

		requestBatch.executeAsync();

	}
}
