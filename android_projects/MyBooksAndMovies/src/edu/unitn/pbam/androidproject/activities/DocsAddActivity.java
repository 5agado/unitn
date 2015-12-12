package edu.unitn.pbam.androidproject.activities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOC_ID_NO_DOC;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_BARCODE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_OBJ_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_STRING_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.fragments.CheckboxesFragment;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Category;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Movie;
import edu.unitn.pbam.androidproject.model.dao.db.DListDaoDb;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.BarcodeScanner;
import edu.unitn.pbam.androidproject.utilities.Constants;
import edu.unitn.pbam.androidproject.utilities.RottenTomatoes;
import edu.unitn.pbam.androidproject.utilities.Utils;

public class DocsAddActivity extends SherlockFragmentActivity {
	private final static String TAG = "DocsAdd";
	private int docType;
	private long docId;
	private Document doc;
	private CheckboxesFragment dialog;
	private CheckBox box1;
	private Long box1ID;
	private CheckBox box2;
	private Long box2ID;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		docType = i.getIntExtra(INTENT_DOC_TYPE_PARAMNAME, -1);
		docId = i.getLongExtra(INTENT_DOC_ID_PARAMNAME, 0);

		if (docType == DOCTYPE_BOOK) {
			setContentView(R.layout.activity_book_add);
		} else {
			setContentView(R.layout.activity_movie_add);
		}

		// Action bar cutomization
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.action_bar_add_doc);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		View customView = actionBar.getCustomView();

		// Confirm/Cancel buttons default actions
		LinearLayout layout = (LinearLayout) customView
				.findViewById(R.id.confirm);
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirm(v);
			}
		});

		layout = (LinearLayout) customView.findViewById(R.id.cancel);
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel(v);
			}
		});

		// Gestione checkBox
		box1 = (CheckBox) findViewById(R.id.checkBox_1);
		box2 = (CheckBox) findViewById(R.id.checkBox_2);

		Cursor c = null;
		Cursor dlists;
		if (docType == Constants.DOCTYPE_BOOK) {
			c = App.bDao.getAllAuthors();
			dlists = App.dlDao.getByType(DList.Type.BOOK);

		} else {
			c = App.mDao.getAllDirectors();
			dlists = App.dlDao.getByType(DList.Type.MOVIE);
		}
		dlists.moveToFirst();
		box1.setText(dlists.getString(dlists
				.getColumnIndex(DListDaoDb.NAME_COL)));
		box1ID = dlists.getLong(dlists.getColumnIndex(DListDaoDb.ID_COL));
		dlists.moveToNext();
		box2.setText(dlists.getString(dlists
				.getColumnIndex(DListDaoDb.NAME_COL)));
		box2ID = dlists.getLong(dlists.getColumnIndex(DListDaoDb.ID_COL));

		ArrayList<String> names = new ArrayList<String>(c.getCount());

		c.moveToFirst();
		while (!c.isAfterLast()) {
			names.add(c.getString(0));
			c.moveToNext();
		}

		// Gestione Autocomplete textviews
		AutoCompleteTextView tv = (AutoCompleteTextView) findViewById(R.id.author);
		tv.setAdapter(new AutoCompleteAdapter(this,
				android.R.layout.simple_dropdown_item_1line, android.R.id.text1));
		tv.setThreshold(3);
		AutoCompleteTextView tvGenres = (AutoCompleteTextView) findViewById(R.id.genre);
		AutoCompleteAdapter autoGenresAdapter = new AutoCompleteAdapter(this,
				android.R.layout.simple_dropdown_item_1line, android.R.id.text1);
		autoGenresAdapter.isGenres = true;
		tvGenres.setAdapter(autoGenresAdapter);
		tvGenres.setThreshold(3);

		if (docId != DOC_ID_NO_DOC) {
			doc = (Document) i.getSerializableExtra(INTENT_DOC_OBJ_PARAMNAME);
			fillForm();
			if (docType == DOCTYPE_BOOK) {
				findViewById(R.id.isbn_layout).setVisibility(View.GONE);
				findViewById(R.id.barcode_layout).setVisibility(View.GONE);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BarcodeScanner.REQUEST_CODE_SCAN
				&& resultCode == RESULT_OK) {
			searchByISBN(data.getStringExtra(BarcodeScanner.BARCODE));
		}
	}

	public List<DList> getCurrentDocDlists() {
		if (doc == null)
			return null;
		else
			return doc.getLists();
	}

	/*
	 * Fill the form layout from the doc variables of the Activity Called when
	 * we get the DOC_ID_NO_DOC and the Documents as a serialized object
	 */
	private void fillForm() {
		// Isbn
		if (docType == DOCTYPE_BOOK) {
			TextView isbn = (TextView) findViewById(R.id.isbn_upc);
			isbn.setText(((Book) doc).getCode());
		}

		// Author
		TextView author = (TextView) findViewById(R.id.author);
		if (docType == DOCTYPE_BOOK) {
			author.setText(((Book) doc).getAuthor());
		} else {
			author.setText(((Movie) doc).getDirector());
		}

		// Year
		TextView year = (TextView) findViewById(R.id.year);
		year.setText(String.valueOf(doc.getYear()));
		// Title
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(doc.getTitle());

		// Genres
		TextView genre = (TextView) findViewById(R.id.genre);

		List<Category> cat = doc.getCategories();
		String catName = "";
		for (int i = 0; i < cat.size(); i++) {
			Category c = cat.get(i);
			if (catName.length() == 0)
				catName += c.getName();
			else
				catName += ", " + c.getName();
		}
		genre.setText(catName);

		// Lists
		List<DList> dlists = doc.getLists();
		ArrayList<Long> listIDs = new ArrayList<Long>();
		for (DList l : dlists) {
			listIDs.add(l.getId());
		}
		if (listIDs.contains(box1ID)) {
			box1.setChecked(true);
		}
		if (listIDs.contains(box2ID)) {
			box2.setChecked(true);
		}

		// Rating
		EditText rating = (EditText) findViewById(R.id.rating_box);
		Double dRating = Double.valueOf(doc.getRating());
		if (dRating != null)
			rating.setText(String.valueOf(dRating.intValue()));

		// Notes
		EditText notes = (EditText) findViewById(R.id.notes);
		notes.setText(doc.getNotes());
	}

	private Document getDocFromForm() {
		String errorMessage = App.getAppContext().getResources()
				.getString(R.string.field_required);
		Document res;
		if (doc == null) {
			if (docType == DOCTYPE_BOOK) {
				res = new Book();
			} else {
				res = new Movie();
			}
		} else
			res = doc;

		// Isbn
		if (docType == DOCTYPE_BOOK) {
			TextView isbn = (TextView) findViewById(R.id.isbn_upc);
			res.setCode(isbn.getText().toString());
		}

		// Title
		TextView title = (TextView) findViewById(R.id.title);
		if (title.getText().toString().length() == 0) {
			title.setError(errorMessage);
			return null;
		} else
			res.setTitle(title.getText().toString());

		// Author
		TextView author = (TextView) findViewById(R.id.author);
		if (!(author.getText().toString().length() == 0)) {
			if (docType == DOCTYPE_BOOK) {
				((Book) res).setAuthor(author.getText().toString());
			} else {
				((Movie) res).setDirector(author.getText().toString());
			}
		}

		// Genres
		TextView genre = (TextView) findViewById(R.id.genre);
		ArrayList<Category> categories = new ArrayList<Category>();
		String text = genre.getText().toString();
		String cats[] = text.split(",");
		for (String cat : cats) {
			cat = cat.trim();
			if (cat.length() != 0) {
				Category c = new Category();
				c.setType(docType == DOCTYPE_BOOK ? Category.Type.BOOK
						: Category.Type.MOVIE);
				c.setName(cat);
				categories.add(c);
			}
		}
		res.setCategories(categories);

		// Year
		TextView year = (TextView) findViewById(R.id.year);
		if (!(year.getText().toString().length() == 0)) {
			res.setYear(Integer.valueOf(year.getText().toString()));
		}

		// Rating
		EditText rating = (EditText) findViewById(R.id.rating_box);
		Double val;

		if (rating.getText().toString().length() != 0) {
			val = Double.valueOf(rating.getText().toString());
			if (val < 0 || val > 100) {
				rating.setError(errorMessage);
				return null;
			} else
				res.setRating(Double.valueOf(rating.getText().toString()));
		}
		// RatingBar rating = (RatingBar) findViewById(R.id.rating);
		// res.setRating(rating.getProgress());

		// Notes
		EditText notes = (EditText) findViewById(R.id.notes);
		res.setNotes(notes.getText().toString());

		// Lists
		ArrayList<DList> dlists = new ArrayList<DList>();
		if (dialog != null) {
			List<Long> selectedDlistsID = dialog.getSelectedItemsID();
			for (Long l : selectedDlistsID) {
				DList d = App.dlDao.getById(l);
				dlists.add(d);
			}
		}
		if (box1.isChecked()) {
			DList d = App.dlDao.getById(box1ID);
			dlists.add(d);
		}
		if (box2.isChecked()) {
			DList d = App.dlDao.getById(box2ID);
			dlists.add(d);
		}
		res.setLists(dlists);

		return res;
	}

	// Follow all the onClick methods for the action bar options
	public void confirm(View v) {
		Document doc = getDocFromForm();

		if (doc == null) {
			Log.i(TAG, "null doc");
			return;
		}

		if (doc.getId() == 0 && Utils.isNetworkAvailable()
				&& doc.getCover() != null
				&& doc.getCover().getRemoteUrl().length() > 0) {

			new AsyncTask<Document, Void, Document>() {
				@Override
				protected Document doInBackground(Document... params) {
					Document doc = params[0];
					doc.setCover(RottenTomatoes.getCoverAt(doc.getCover()
							.getRemoteUrl()));
					return doc;
				}

				protected void onPostExecute(Document result) {
					saveDocument(result);
				};
			}.execute(doc);
		}

		else {
			saveDocument(doc);
		}
	}

	private void saveDocument(Document doc) {
		String docTypeName;
		if (docType == Constants.DOCTYPE_BOOK) {
			docId = App.bDao.save((Book) doc);
			docTypeName = App.getAppContext().getResources()
					.getString(R.string.book);
		} else {
			docId = App.mDao.save((Movie) doc);
			docTypeName = App.getAppContext().getResources()
					.getString(R.string.movie);
		}
		Toast.makeText(
				this,
				docTypeName
						+ App.getAppContext().getResources()
								.getString(R.string.doc_saved),
				Toast.LENGTH_SHORT).show();
		Intent returnIntent = new Intent();
		returnIntent.putExtra(INTENT_DOC_ID_PARAMNAME, docId);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	public void cancel(View v) {
		finish();
	}

	public void searchByTitle(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					this,
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this, WebSearchActivity.class);
		intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
		EditText title = (EditText) findViewById(R.id.title);
		String text = title.getText().toString();
		if (text.length() != 0) {
			intent.putExtra(INTENT_DOC_STRING_PARAMNAME, text);
			startActivity(intent);
		} else {
			title.setError(App.getAppContext().getResources()
					.getString(R.string.field_required));
		}
	}

	public void showLists(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.INTENT_DOC_TYPE_PARAMNAME, docType);
		dialog = new CheckboxesFragment();
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), TAG);
	}

	public void scanBarcode(View v) {
		if (!BarcodeScanner.isBarcodeScannerAvailable(this)) {
			BarcodeScanner.launchMarketToInstallScanner(this);
		} else {
			Intent intent = new Intent(BarcodeScanner.ACTION);
			startActivityForResult(intent, BarcodeScanner.REQUEST_CODE_SCAN);
		}
	}

	public void searchByISBN(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					this,
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		EditText txt = (EditText) findViewById(R.id.isbn_upc);
		String isbn = txt.getText().toString();
		if (isbn.length() != 0)
			searchByISBN(isbn);
		else {
			txt.setError(App.getAppContext().getResources()
					.getString(R.string.field_required));
		}
	}

	private void searchByISBN(String isbn) {
		if (!Utils.isNetworkAvailable()) {
			Toast.makeText(
					this,
					App.getAppContext().getResources()
							.getString(R.string.no_connetions),
					Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i = new Intent(this, DocDetailsActivity.class);
		i.putExtra(INTENT_DOC_ID_PARAMNAME, 0L);
		i.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
		i.putExtra(INTENT_DOC_BARCODE_PARAMNAME, isbn);
		startActivity(i);
	}

	// Inner class for the autocompletetextviews present in the form layout
	private class AutoCompleteAdapter extends ArrayAdapter<String> implements
			Filterable {
		private ArrayList<String> mList;
		public boolean isGenres = false;

		public AutoCompleteAdapter(Context context, int layoutId, int textId) {
			super(context, layoutId, textId);
			mList = new ArrayList<String>();
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public String getItem(int position) {
			return mList.get(position);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					Cursor c;
					FilterResults results = null;
					ArrayList<String> names = null;

					if (constraint != null) {
						results = new FilterResults();
						if (isGenres) {
							c = App.cDao.getCategoriesMatching(constraint
									.toString());
						} else {
							if (docType == Constants.DOCTYPE_BOOK) {
								c = App.bDao.getAuthorsMatching(constraint
										.toString());
							} else {
								c = App.mDao.getDirectorsMatching(constraint
										.toString());
							}
						}

						names = new ArrayList<String>(c.getCount());
						c.moveToFirst();
						while (!c.isAfterLast()) {
							names.add(c.getString(0));
							c.moveToNext();
						}
						c.close();

						results.values = names;
						results.count = names.size();
					}
					mList = names;

					return results;
				}
			};

			return filter;
		}

	}
}
