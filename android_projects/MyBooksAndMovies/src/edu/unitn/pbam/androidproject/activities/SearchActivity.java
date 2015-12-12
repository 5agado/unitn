package edu.unitn.pbam.androidproject.activities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_STRING_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.fragments.TitlesFragment;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;
import edu.unitn.pbam.androidproject.utilities.Utils;

public class SearchActivity extends SherlockFragmentActivity {
	private final static String TAG = "SearchActivity";
	private int docType;
	private ListFragment fragment;
	private EditText mEditText;
	private final int THRESHOLD = 3;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		docType = i.getIntExtra(INTENT_DOC_TYPE_PARAMNAME, 0);

		setContentView(R.layout.activity_generic_list);
		findViewById(R.id.title_strip).setVisibility(View.GONE);
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.INTENT_DOC_TYPE_PARAMNAME, docType);
		fragment = new TitlesFragment();
		fragment.setArguments(bundle);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.mainFragment, fragment).commit();

		findViewById(R.id.title_strip).setVisibility(View.GONE);
		Button b = (Button) findViewById(R.id.button_websearch);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchOnline(v);
			}
		});

		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");
		actionBar.setIcon(docType == DOCTYPE_BOOK ? R.drawable.ic_notebook
				: R.drawable.ic_movie);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		if (session != null) {
			session.onActivityResult(this, requestCode, resultCode, data);
		}
	}

	private void hideKeyboardPerformSearch(IBinder token, CharSequence text) {
		if (token != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(token, 0);
		}
		String txt = text.toString();
		if (txt.length() < THRESHOLD) {
			txt = "";
		}
		TitlesFragment f = (TitlesFragment) fragment;
		f.setFilterText(txt);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * MenuInflater inflater = getSupportMenuInflater();
		 * inflater.inflate(R.menu.action_bar_search, menu);
		 * 
		 * SearchManager searchManager =
		 * (SearchManager)getSystemService(Context.SEARCH_SERVICE); SearchView
		 * searchView = (SearchView)
		 * menu.findItem(R.id.menu_search).getActionView();
		 * searchView.setSearchableInfo
		 * (searchManager.getSearchableInfo(getComponentName()));
		 * searchView.setIconifiedByDefault(false);
		 * searchView.setQueryHint(App.getAppContext
		 * ().getText(R.string.search_hint));
		 * 
		 * SearchView.OnQueryTextListener queryTextListener = new
		 * SearchView.OnQueryTextListener() { public boolean
		 * onQueryTextChange(String newText) { TitlesFragment f =
		 * (TitlesFragment)fragment; f.setFilterText(newText); return true; }
		 * 
		 * public boolean onQueryTextSubmit(String query) { TitlesFragment f =
		 * (TitlesFragment)fragment; f.setFilterText(query); return true; } };
		 * searchView.setOnQueryTextListener(queryTextListener);
		 * 
		 * 
		 * mSearchView = searchView;
		 * 
		 * return true;
		 */
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_web_search, menu);

		MenuItem item = menu.findItem(R.id.menu_search_web);
		View v = item.getActionView();
		final EditText text = (EditText) v.findViewById(R.id.search_box);
		text.setHint(R.string.search_hint);

		text.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				hideKeyboardPerformSearch(null, text.getText());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

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

		text.requestFocus();
		mEditText = text;

		final ImageButton b = (ImageButton) v.findViewById(R.id.button_search);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboardPerformSearch(b.getWindowToken(), text.getText());
			}
		});

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void searchOnline(View v) {
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
		if (mEditText != null) {
			String text = mEditText.getText().toString();
			intent.putExtra(INTENT_DOC_STRING_PARAMNAME, text);
		}
		startActivity(intent);
	}

}
