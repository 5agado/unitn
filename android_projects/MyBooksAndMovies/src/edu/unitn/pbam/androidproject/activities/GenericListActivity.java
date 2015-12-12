package edu.unitn.pbam.androidproject.activities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_MOVIE;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOC_ID_NO_DOC;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_STRING_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_LIST_TYPE_PARAMNAME;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.fragments.TitlesFragment;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;
import edu.unitn.pbam.androidproject.utilities.Import;

public class GenericListActivity extends SherlockFragmentActivity {
	private final static String TAG = "GenericListActivity";
	private final static int REQUEST_IMPORT_CSV = 1;
	private int docType;
	private int listType;
	private long docId;
	private ListFragment fragment;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		docType = i.getIntExtra(INTENT_DOC_TYPE_PARAMNAME, 0);
		listType = i.getIntExtra(INTENT_LIST_TYPE_PARAMNAME, 0);
		docId = i.getLongExtra(INTENT_DOC_ID_PARAMNAME, 0);
		String filterString = i.getStringExtra(INTENT_DOC_STRING_PARAMNAME);
		if (filterString == null) {
			filterString = "Details";
		}

		setContentView(R.layout.activity_generic_list);
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.INTENT_DOC_TYPE_PARAMNAME, docType);
		bundle.putInt(Constants.INTENT_LIST_TYPE_PARAMNAME, listType);
		bundle.putLong(Constants.INTENT_DOC_ID_PARAMNAME, docId);
		bundle.putString(Constants.INTENT_DOC_STRING_PARAMNAME, filterString);
		fragment = new TitlesFragment();
		fragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.mainFragment, fragment).commit();

		TextView title = (TextView) findViewById(R.id.title_strip);
		title.setText(filterString);

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
			Log.d(TAG, "add");
			intent = new Intent(this, DocsAddActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			intent.putExtra(INTENT_DOC_ID_PARAMNAME, DOC_ID_NO_DOC);
			startActivityForResult(intent, 0);
			return true;
		case R.id.action_search:
			Log.d(TAG, "search");
			intent = new Intent(this, SearchActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
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
												GenericListActivity.this,
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
												GenericListActivity.this,
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
		case android.R.id.home:
			intent = new Intent(this, DocsHomeActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			startActivity(intent);
			return true;
		case R.id.action_settings:
			Log.d(TAG, "settings");
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
}
