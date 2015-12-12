package edu.unitn.pbam.androidproject.activities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_MOVIE;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOC_ID_NO_DOC;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.adapters.SectionsPagerAdapter;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;
import edu.unitn.pbam.androidproject.utilities.Import;

public class DocsHomeActivity extends SherlockFragmentActivity implements
		OnItemSelectedListener {
	private final static String TAG = "DocsHome";
	private final static int REQUEST_IMPORT_CSV = 1;
	private int docType;

	private ViewPager mViewPager;
	private ActionMode mActionMode;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private Spinner mSpinner;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		docType = i.getIntExtra(Constants.INTENT_DOC_TYPE_PARAMNAME, -1);

		// Swipey Tabs init
		setContentView(R.layout.swipey_tabs);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), docType);

		mViewPager.setAdapter(mSectionsPagerAdapter);
		// onPageListener in order to dismiss the ActionMode
		// when another page of the viewpager is selected
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						if (mActionMode != null)
							mActionMode.finish();
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
				});

		// Action bar customization
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.action_bar_custom);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setIcon(docType == DOCTYPE_BOOK ? R.drawable.ic_notebook
				: R.drawable.ic_movie);
		actionBar.setTitle("");
		actionBar.setHomeButtonEnabled(true);
		View customView = actionBar.getCustomView();
		mSpinner = (Spinner) customView.findViewById(R.id.spinner);
		mSpinner.setSelection(docType);
		mSpinner.setOnItemSelectedListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSpinner.setSelection(docType);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		if (session != null) {
			session.onActivityResult(this, requestCode, resultCode, data);
		}
		if (requestCode == REQUEST_IMPORT_CSV) {
			if (resultCode == RESULT_OK) {
				String path = data
						.getStringExtra(Constants.INTENT_FILEPATH_PARAMNAME);
				if (docType == DOCTYPE_MOVIE)
					Import.importMoviesFromList(path);
				else
					Import.importBooksFromList(path);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_add:
			intent = new Intent(this, DocsAddActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			intent.putExtra(INTENT_DOC_ID_PARAMNAME, DOC_ID_NO_DOC);
			startActivityForResult(intent, 0);
			return true;
		case R.id.action_search:
			intent = new Intent(this, SearchActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			startActivity(intent);
			return true;
		case android.R.id.home:
			intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.action_newdlist:
			final EditText dlistName = new EditText(this);
			dlistName.setHint(App.getAppContext().getResources()
					.getString(R.string.newdlist_hint));
			dlistName.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			new AlertDialog.Builder(this)
					.setView(dlistName)
					.setPositiveButton(R.string.action_create,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (dlistName.getText().toString().length() == 0) {
										dlistName
												.setError(App
														.getAppContext()
														.getResources()
														.getString(
																R.string.field_required));
										Toast.makeText(
												DocsHomeActivity.this,
												App.getAppContext()
														.getResources()
														.getString(
																R.string.dlist_void_name),
												Toast.LENGTH_SHORT).show();
									} else {
										DList dlist = new DList();
										dlist.setName(dlistName.getText()
												.toString());
										if (docType == Constants.DOCTYPE_BOOK) {
											dlist.setType(DList.Type.BOOK);
											App.dlDao.save(dlist);
										} else {
											dlist.setType(DList.Type.MOVIE);
											App.dlDao.save(dlist);
										}

										Toast.makeText(
												DocsHomeActivity.this,
												App.getAppContext()
														.getResources()
														.getString(
																R.string.dlist_added),
												Toast.LENGTH_SHORT).show();
									}
								}
							}).setNegativeButton(android.R.string.cancel, null)
					.show();
			return true;
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_stats:
			intent = new Intent(this, StatisticsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_import_csv:
			intent = new Intent(this, FileExplorerActivity.class);
			startActivityForResult(intent, REQUEST_IMPORT_CSV);
			return true;
		case R.id.action_export_csv:
			String path = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			if (docType == DOCTYPE_BOOK)
				Import.exportBooksCSV(path + "/books.csv");
			else
				Import.exportMoviesCSV(path + "/movies.csv");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (position != docType) {
			Intent intent = new Intent(this, DocsHomeActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, position);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	// Called by the fragments in order to set the given ActionMode
	// in the activity actionbar
	public void setActionMode(ActionMode aMode) {
		mActionMode = aMode;
	}

}
