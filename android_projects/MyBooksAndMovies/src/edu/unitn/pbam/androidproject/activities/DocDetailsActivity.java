package edu.unitn.pbam.androidproject.activities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOC_ID_NO_DOC;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_BARCODE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_INTERNETID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_OBJ_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_STRING_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.google.android.gms.plus.PlusShare;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.fragments.CheckboxesFragment;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Document.SyncType;
import edu.unitn.pbam.androidproject.model.Movie;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;
import edu.unitn.pbam.androidproject.utilities.Facebook;
import edu.unitn.pbam.androidproject.utilities.GoogleBooks;
import edu.unitn.pbam.androidproject.utilities.RottenTomatoes;
import edu.unitn.pbam.androidproject.utilities.Utils;

public class DocDetailsActivity extends SherlockFragmentActivity {
	private final static String TAG = "DocDetailsActivity";
	private final static int NUM_CATEGORIES = 2; // Showed in
	private final static int NUM_ACTORS = 5; // the layout
	private final static int SYNC_REQUEST = 123;
	private final static int EDIT_REQUEST = 99;
	private int docType;
	private long docId;
	private String docCode;
	private Document currentDoc;
	private ProgressDialog progressDialog;
	private ImageView wait;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		Intent i = getIntent();
		docId = i.getLongExtra(INTENT_DOC_ID_PARAMNAME, 0);
		docType = i.getIntExtra(INTENT_DOC_TYPE_PARAMNAME, -1);
		docCode = i.getStringExtra(INTENT_DOC_INTERNETID_PARAMNAME);

		setContentView(R.layout.activity_doc_details);
		wait = (ImageView) findViewById(R.id.wait_image);
		wait.setVisibility(View.VISIBLE);

		if (docId != 0 && docCode == null) {
			if (docType == DOCTYPE_BOOK) {
				currentDoc = App.bDao.getById(docId);
			} else {
				currentDoc = App.mDao.getById(docId);
			}
			fillLayoutFrom();
		} else {
			if (docType == DOCTYPE_BOOK) {
				if (docCode == null)
					findBook(i.getStringExtra(INTENT_DOC_BARCODE_PARAMNAME),
							true);
				else
					findBook(docCode, false);
			} else {
				findFilm(docCode);
			}
		}

		// Action bar customization
		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(App.getAppContext().getString(R.string.details));
		actionBar.setIcon(docType == DOCTYPE_BOOK ? R.drawable.ic_notebook
				: R.drawable.ic_movie);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		if (docId == 0) {
			inflater.inflate(R.menu.action_bar_internet_details, menu);
		} else {
			inflater.inflate(R.menu.action_bar_details, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_delete:
			new AlertDialog.Builder(this)
					.setMessage(
							App.getAppContext().getResources()
									.getString(R.string.confirmation_msg))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int whichButton) {
									String docTypeName;
									if (docType == Constants.DOCTYPE_BOOK) {
										App.bDao.delete(App.bDao.getById(docId));
										docTypeName = App.getAppContext()
												.getResources()
												.getString(R.string.book);
									} else {
										App.mDao.delete(App.mDao.getById(docId));
										docTypeName = App.getAppContext()
												.getResources()
												.getString(R.string.movie);
									}
									Toast.makeText(
											DocDetailsActivity.this,
											docTypeName
													+ App.getAppContext()
															.getResources()
															.getString(
																	R.string.doc_deleted),
											Toast.LENGTH_SHORT).show();
									finish();
								}
							}).setNegativeButton(android.R.string.no, null)
					.show();
			return true;
		case R.id.action_edit:
			intent = new Intent(this, DocsAddActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			intent.putExtra(INTENT_DOC_ID_PARAMNAME, docId);
			intent.putExtra(INTENT_DOC_OBJ_PARAMNAME, currentDoc);
			startActivityForResult(intent, EDIT_REQUEST);
			return true;
		case R.id.share_facebook:
			Facebook.shareDocument(currentDoc, this);
			return true;
		case R.id.share_google:
			Intent shareIntent;
			if (currentDoc.getUrlinfo() != null){
				shareIntent = new PlusShare.Builder(this)
					.setType("text/plain").setText(currentDoc.getTitle())
					.setContentUrl(Uri.parse(currentDoc.getUrlinfo()))
					.getIntent();
			}
			else{
				shareIntent = new PlusShare.Builder(this)
				.setType("text/plain").setText(currentDoc.getTitle())
				.getIntent();
			}

			startActivityForResult(shareIntent, 0);
			return true;
		case R.id.share_astext:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			/*
			 * intent.setType("image/*"); intent.putExtra(Intent.EXTRA_STREAM,
			 * Uri.parse(currentDoc.getCover().getRemoteUrl()));
			 */
			intent.putExtra(Intent.EXTRA_SUBJECT, currentDoc.getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, Utils.toString(currentDoc));
			startActivity(Intent.createChooser(intent, App.getAppContext()
					.getResources().getString(R.string.progress_share)));
			return true;
		case R.id.action_confirm:
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.INTENT_DOC_TYPE_PARAMNAME, docType);
			bundle.putLong(Constants.INTENT_DOC_ID_PARAMNAME, DOC_ID_NO_DOC);
			CheckboxesFragment dialog = new CheckboxesFragment();
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), TAG);
			return true;
		case R.id.action_search_web:
			intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, currentDoc.getTitle());
			startActivity(intent);
			return true;
		case android.R.id.home:
			intent = new Intent(this, DocsHomeActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			startActivity(intent);
			return true;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_sync_doc:
			syncDoc();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		if (session != null) {
			session.onActivityResult(this, requestCode, resultCode, data);
		}

		if (requestCode == EDIT_REQUEST) {
			if (resultCode == RESULT_OK) {
				if (docId == 0) {
					Intent intent = new Intent(this, DocsHomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
					startActivity(intent);
					return;
				}

				docId = data.getLongExtra(INTENT_DOC_ID_PARAMNAME, 0);
				if (docType == DOCTYPE_BOOK) {
					currentDoc = App.bDao.getById(docId);
				} else {
					currentDoc = App.mDao.getById(docId);
				}
				fillLayoutFrom();
			}
		} else if (requestCode == SYNC_REQUEST) {
			if (resultCode == RESULT_OK) {
				docCode = data.getStringExtra(INTENT_DOC_INTERNETID_PARAMNAME);
				if (docType == DOCTYPE_BOOK) {
					findBook(docCode, false);
				} else {
					findFilm(docCode);
				}
			}
		}
	}

	/*
	 * Called by the Checkboxes fragment when the user confirms to save the
	 * document.
	 */
	public void saveDocWithDlist(ArrayList<DList> dlists) {
		currentDoc.setLists(dlists);
		String docTypeName;
		if (docType == DOCTYPE_BOOK) {
			App.bDao.save((Book) currentDoc);
			docTypeName = App.getAppContext().getResources()
					.getString(R.string.book);
		} else {
			App.mDao.save((Movie) currentDoc);
			docTypeName = App.getAppContext().getResources()
					.getString(R.string.movie);
		}
		Toast.makeText(
				this,
				docTypeName
						+ App.getAppContext().getResources()
								.getString(R.string.doc_saved),
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, DocsHomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
		startActivity(intent);
		finish();
	}

	private void fillLayoutFrom() {
		if (currentDoc == null) {
			Log.i(TAG, "nullDOc");
			if (progressDialog != null)
				progressDialog.dismiss();
			Toast.makeText(
					this,
					App.getAppContext().getResources()
							.getString(R.string.empty), Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		Document result = currentDoc;
		// Differenze book movie (author, cast layout)
		TextView author = (TextView) findViewById(R.id.author);
		LinearLayout castLayout = (LinearLayout) findViewById(R.id.cast_layout);
		if (docType == DOCTYPE_BOOK) {
			author.setText(((Book) result).getAuthor());
			castLayout.setVisibility(LinearLayout.GONE);
			TextView authorText = (TextView) findViewById(R.id.author_text);
			authorText.setText(App.getAppContext().getResources()
					.getString(R.string.author)
					+ ": ");
			if (((Book) result).getPages() != 0) {
				findViewById(R.id.lenght_layout).setVisibility(View.VISIBLE);
				TextView lengthText = (TextView) findViewById(R.id.lenght);
				lengthText.setText(String.valueOf(((Book) result).getPages())
						+ App.getAppContext().getResources()
								.getString(R.string.pages));
			}
		} else {
			author.setText(((Movie) result).getDirector());
			TextView authorText = (TextView) findViewById(R.id.author_text);
			authorText.setText(App.getAppContext().getResources()
					.getString(R.string.director)
					+ ": ");
			TextView actors = (TextView) findViewById(R.id.actors);
			List<String> acts = ((Movie) result).getActors();
			if (acts != null) {
				String res_acts = "";
				for (int i = 0; i < NUM_ACTORS && i < acts.size(); i++) {
					String act = acts.get(i);
					if (res_acts.length() == 0)
						res_acts += act;
					else
						res_acts += ", " + act;
				}
				actors.setText(res_acts);
				actors.setVisibility(View.VISIBLE);
				castLayout.setVisibility(View.VISIBLE);
			} else
				castLayout.setVisibility(LinearLayout.GONE);
			if (((Movie) result).getDuration() != 0) {
				findViewById(R.id.lenght_layout).setVisibility(View.VISIBLE);
				TextView lengthText = (TextView) findViewById(R.id.lenght);
				lengthText.setText(String.valueOf(((Movie) result)
						.getDuration())
						+ App.getAppContext().getResources()
								.getString(R.string.min));
			}
		}

		// Year
		TextView year = (TextView) findViewById(R.id.year);
		if (result.getYear() == 0)
			year.setVisibility(View.GONE);
		else {
			year.setText("(" + result.getYear() + ")");
			year.setVisibility(View.VISIBLE);
		}

		// Genres
		TextView genre = (TextView) findViewById(R.id.genre);
		List<Category> cat = result.getCategories();

		String catName = "";
		for (int i = 0; i < NUM_CATEGORIES && i < cat.size(); i++) {
			Category c = cat.get(i);
			if (catName.length() == 0)
				catName += c.getName();
			else
				catName += ", " + c.getName();
		}
		LinearLayout genreLayout = (LinearLayout) findViewById(R.id.genre_layout);
		if (!catName.isEmpty()) {
			genre.setText(catName);
			genreLayout.setVisibility(View.VISIBLE);
		}

		else {
			genreLayout.setVisibility(View.GONE);
		}

		// Title
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(result.getTitle());
		title.setSelected(true);
		// Description
		
		/*WebView description = (WebView) findViewById(R.id.description);
		if (result.getDescription() == null
				|| result.getDescription().length() == 0)
			description.setVisibility(View.GONE);
		else {
			WebSettings webSettings = description.getSettings();
			webSettings.setTextSize(WebSettings.TextSize.SMALLER);
			description.loadDataWithBaseURL(null, result.getDescription(),
					"text/html", "utf-8", null);
			description.setVisibility(View.VISIBLE);
			description.setBackgroundColor(0x00000000);
		}*/
		TextView description = (TextView) findViewById(R.id.description);
		if (result.getDescription() == null
				|| result.getDescription().length() == 0)
			description.setVisibility(View.GONE);
		else {
			description.setText(result.getDescription());
			description.setVisibility(View.VISIBLE);
		}
		
		// Rating
		RatingBar rating;
		TextView rat_num;
		Double rat_value = Double.valueOf(result.getRating());
		
		if (rat_value == 0) {
			findViewById(R.id.layout_user_rating).setVisibility(View.GONE);
		}
		else {
			rating = (RatingBar) findViewById(R.id.rating);
			rat_num = (TextView) findViewById(R.id.rating_num);
			rating.setProgress(rat_value.intValue());
			rat_num.setText(String.valueOf(rat_value.intValue()));
			findViewById(R.id.layout_user_rating).setVisibility(View.VISIBLE);
		}
		
		// Web Rating
		Double rat_value_web = Double.valueOf(result.getWebrating());
		if (rat_value_web == 0) {
			findViewById(R.id.layout_web_rating).setVisibility(View.GONE);
		}
		else {
			rating = (RatingBar) findViewById(R.id.web_rating);
			rat_num = (TextView) findViewById(R.id.web_rating_num);
			rat_num.setText(String.valueOf(rat_value_web.intValue()));
			rating.setProgress(rat_value_web.intValue());
			findViewById(R.id.layout_web_rating).setVisibility(View.VISIBLE);
		}
		
		if (rat_value == 0 && rat_value_web == 0) {
			findViewById(R.id.rating_text).setVisibility(View.GONE);
		} else {
			findViewById(R.id.rating_text).setVisibility(View.GONE);
		}
		
		// link
		TextView link = (TextView) findViewById(R.id.link);
		link.setText(result.getUrlinfo());
		// Notes
		TextView notes = (TextView) findViewById(R.id.notes);
		notes.setText(result.getNotes());
		// Thumbnail
		ImageView thumbnail = (ImageView) findViewById(R.id.imageView1);
		if (result.getCover() != null) {
			thumbnail.setImageDrawable(result.getCover().getImage());
		}

		if (progressDialog != null)
			progressDialog.dismiss();
		wait.setVisibility(View.GONE);
	}

	private void findFilm(String id) {
		showProgressDialog();
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					this,
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		new AsyncTask<String, Void, Movie>() {
			@Override
			protected Movie doInBackground(String... params) {
				Movie movie = RottenTomatoes.getMovieByID(params[0]);
				if (movie!=null)
					movie.setSync(SyncType.SYNCUSER);
				if (docId != 0 && movie!=null) {
					Movie oldVersion = App.mDao.getById(docId);
					movie.setId(docId);
					movie.setLists(oldVersion.getLists());
					movie.setRating(oldVersion.getRating());
					movie.setNotes(oldVersion.getNotes());
					App.mDao.save(movie);
				}

				return movie;
			}

			protected void onPostExecute(Movie result) {
				currentDoc = result;
				fillLayoutFrom();
			};
		}.execute(id);
	}

	private void findBook(String id, final Boolean isBarcode) {
		showProgressDialog();
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					this,
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		new AsyncTask<String, Void, Book>() {

			@Override
			protected Book doInBackground(String... params) {
				Book book;
				if (isBarcode){
					book = GoogleBooks.getBookByIsbn(params[0]);
					if (book!=null)
						book.setSync(SyncType.SYNCUSER);
				}
				else {
					book = GoogleBooks.getBookById(params[0]);
					if (book!=null)
						book.setSync(SyncType.SYNCUSER);
					if (docId != 0 && book!=null) {
						Book oldVersion = App.bDao.getById(docId);
						book.setId(docId);
						book.setLists(oldVersion.getLists());
						book.setRating(oldVersion.getRating());
						book.setNotes(oldVersion.getNotes());
						App.bDao.save(book);
					}
				}
				return book;
			}

			protected void onPostExecute(Book result) {
				currentDoc = result;
				fillLayoutFrom();
			};
		}.execute(id);

	}

	private void syncDoc() {
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					this,
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// se ho il codice per aggiornare direttamente da internet lo aggiorno
		// riavviando l'activity
		// e settando il codice come parametro
		if (currentDoc.getSync() == Document.SyncType.SYNCUSER) {
			Intent i = getIntent();
			i.putExtra(INTENT_DOC_INTERNETID_PARAMNAME, currentDoc.getCode());
			finish();
			startActivity(i);
			// nascondo l'animazione
			overridePendingTransition(0, 0);
		}
		// altrimenti creo una nuova websearchactivity per mostrare i titoli
		// corrispondenti fra cui scegliere
		else {
			Intent intent = new Intent(DocDetailsActivity.this,
					WebSearchActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			intent.putExtra(INTENT_DOC_STRING_PARAMNAME, currentDoc.getTitle());
			intent.putExtra(INTENT_DOC_ID_PARAMNAME, currentDoc.getId());
			startActivityForResult(intent, SYNC_REQUEST);
		}
	}

	private void showProgressDialog() {
		progressDialog = ProgressDialog.show(
				DocDetailsActivity.this,
				"",
				App.getAppContext().getResources()
						.getString(R.string.progress_wait), true);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
	}

}
