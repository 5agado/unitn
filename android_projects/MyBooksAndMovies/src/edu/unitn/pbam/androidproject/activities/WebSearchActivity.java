package edu.unitn.pbam.androidproject.activities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_INTERNETID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_STRING_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Movie;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;
import edu.unitn.pbam.androidproject.utilities.GoogleBooks;
import edu.unitn.pbam.androidproject.utilities.RottenTomatoes;
import edu.unitn.pbam.androidproject.utilities.Utils;

public class WebSearchActivity extends SherlockFragmentActivity {
	private int docType;
	private ListView listView;
	private ProgressDialog dialog;
	private boolean isPerformingSearch = false;
	private String queryText;
	private long docId; // utilizzato se impiego l'activity per sincronizzare un
						// documento già salvato

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		docType = i.getIntExtra(INTENT_DOC_TYPE_PARAMNAME, -1);
		queryText = i.getStringExtra(INTENT_DOC_STRING_PARAMNAME);
		docId = i.getLongExtra(INTENT_DOC_ID_PARAMNAME, 0);

		setContentView(R.layout.activity_web_search);

		// Action bar customization
		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(App.getAppContext().getString(
				R.string.action_search_web));
		actionBar.setIcon(docType == DOCTYPE_BOOK ? R.drawable.ic_notebook
				: R.drawable.ic_movie);

		listView = (ListView) findViewById(R.id.list1);
		listView.setAdapter(new DocWebAdapter(this, docType));
		listView.setEmptyView(findViewById(R.id.empty));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Document doc = (Document) listView.getItemAtPosition(position);
				// nuova ricerca
				if (docId == 0) {
					Intent i = new Intent(WebSearchActivity.this,
							DocDetailsActivity.class);
					i.putExtra(INTENT_DOC_INTERNETID_PARAMNAME, doc.getCode());
					i.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
					startActivity(i);
				}
				// sincronizzazione di un documento
				else {
					Intent intent = new Intent();
					intent.putExtra(INTENT_DOC_INTERNETID_PARAMNAME,
							doc.getCode());
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});

		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setMessage(App.getAppContext().getResources()
				.getString(R.string.progress_wait));
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
	}

	@Override
	protected void onPause() {
		dialog.dismiss();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isPerformingSearch) {
			dialog.show();
		}
	}

	private void hideKeyboardPerformSearch(IBinder token, CharSequence text) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(token, 0);
		if (!text.toString().equals("")) {
			((Filterable) listView.getAdapter()).getFilter().filter(text);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_web_search, menu);

		MenuItem item = menu.findItem(R.id.menu_search_web);
		View v = item.getActionView();
		final EditText text = (EditText) v.findViewById(R.id.search_box);
		text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
					return false;
				} else if (event == null
						|| actionId == EditorInfo.IME_ACTION_SEARCH) {
					hideKeyboardPerformSearch(text.getWindowToken(),
							text.getText());
					return false;
				}
				return true;
			}
		});

		final ImageButton b = (ImageButton) v.findViewById(R.id.button_search);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboardPerformSearch(b.getWindowToken(), text.getText());
			}
		});

		if (queryText != null && !queryText.equals("")) {
			text.setText(queryText);
			hideKeyboardPerformSearch(text.getWindowToken(), queryText);
		} else {
			text.requestFocus();
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, DocsHomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class DocWebAdapter extends ArrayAdapter<Document> implements Filterable {
		private ArrayList<? extends Document> mList;
		private int docType;

		public DocWebAdapter(Context context, int docType) {
			super(context, 0, 0);
			mList = new ArrayList<Document>();
			this.docType = docType;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				v = ((LayoutInflater) getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.list_item_document, null);
			}

			Document currentDoc = getItem(position);

			TextView author = (TextView) v.findViewById(R.id.extraDocText);
			if (docType == Constants.DOCTYPE_MOVIE) {
				int year = ((Movie) currentDoc).getYear();
				if (year != 0)
					author.setText("(" + String.valueOf(year) + ")");
				// author.setText(((Movie)currentDoc).getDirector());
			} else {
				author.setText(((Book) currentDoc).getAuthor());
			}

			TextView title = (TextView) v.findViewById(R.id.mainDocText);
			title.setText(currentDoc.getTitle());
			ImageView thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
			if (currentDoc.getCover() != null) {
				thumbnail.setImageDrawable(currentDoc.getCover().getImage());
			} else {
				thumbnail.setImageResource(R.drawable.poster_default);
			}

			return v;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Document getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mList.get(position).getId();
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();

						// mostra la lista dall'inizio
						listView.setSelectionFromTop(0, 0);
					} else {
						notifyDataSetInvalidated();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							isPerformingSearch = false;
							dialog.dismiss();
						}
					});
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							isPerformingSearch = true;
							dialog.show();
						}
					});

					FilterResults results = null;
					ArrayList<? extends Document> docs = null;

					if (constraint != null) {
						results = new FilterResults();
						
						if (!Utils.isNetworkAvailable()) {
							Toast.makeText(
									App.getAppContext(),
									App.getAppContext().getResources()
											.getString(R.string.no_connetions),
									Toast.LENGTH_SHORT).show();
							return results;
						}

						if (docType == Constants.DOCTYPE_BOOK) {
							docs = GoogleBooks.getBooksByTitle(constraint
									.toString());
						} else {
							docs = RottenTomatoes.getMoviesByTitle(constraint
									.toString());
						}

						results.values = docs;
						results.count = docs.size();
					}
					mList = docs;
					return results;
				}
			};

			return filter;
		}

	}

}
