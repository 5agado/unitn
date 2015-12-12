package edu.unitn.pbam.androidproject.fragments;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_OBJ_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_STRING_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_LIST_TYPE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_AUTHORS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_FILTER;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.plus.PlusShare;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.activities.DocDetailsActivity;
import edu.unitn.pbam.androidproject.activities.DocsAddActivity;
import edu.unitn.pbam.androidproject.activities.DocsHomeActivity;
import edu.unitn.pbam.androidproject.adapters.DocAdapter;
import edu.unitn.pbam.androidproject.loaders.DocLoader;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Movie;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;
import edu.unitn.pbam.androidproject.utilities.Facebook;
import edu.unitn.pbam.androidproject.utilities.Utils;

public class TitlesFragment extends CustomFragment implements
		ActionMode.Callback {
	private final static String TAG = "TitlesFragment";
	private int docType;
	private int filterType;
	private long filterId;
	private String paramName;
	private ActionMode mActionMode;
	private Document currentDoc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		docType = bundle.getInt(INTENT_DOC_TYPE_PARAMNAME);
		filterType = bundle.getInt(INTENT_LIST_TYPE_PARAMNAME, -1);
		filterId = bundle.getLong(INTENT_DOC_ID_PARAMNAME, -1);

		if (filterType == LISTTYPE_AUTHORS) {
			paramName = bundle.getString(INTENT_DOC_STRING_PARAMNAME);
		}

		if (docType == DOCTYPE_BOOK) {
			adapter = new DocAdapter<Book>(this.getActivity(),
					R.layout.list_item_document, docType, null);
		} else {
			adapter = new DocAdapter<Movie>(this.getActivity(),
					R.layout.list_item_document, docType, null);
		}

		View parentView = inflater.inflate(R.layout.fragment_docs_listview,
				container, false);
		ListView view = (ListView) parentView.findViewById(android.R.id.list);
		view.setAdapter(adapter);
		view.setFastScrollEnabled(true);
		// view.setFastScrollAlwaysVisible(true);
		view.setLongClickable(true);
		view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long id) {
				SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
				mActionMode = activity.startActionMode(TitlesFragment.this);
				mActionMode.setTag(id);
				if (DocsHomeActivity.class.isInstance(getActivity()))
					((DocsHomeActivity) getActivity())
							.setActionMode(mActionMode);
				return true;
			}
		});

		initLoader();

		return parentView;
	}

	@Override
	public void onPause() {
		if (mActionMode != null) {
			mActionMode.finish();
		}
		super.onPause();
	}

	public void setFilterText(String text) {
		if (!text.equals("")) {
			paramName = text;
			filterType = LISTTYPE_FILTER;
		} else {
			filterType = -1;
		}
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this.getActivity(), DocDetailsActivity.class);
		i.putExtra(INTENT_DOC_ID_PARAMNAME, id);
		i.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
		startActivity(i);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// questo metodo si occupa di creare il loader apposito
		return new DocLoader(getActivity(), docType, filterType, filterId,
				paramName);
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
		MenuInflater inflater = activity.getSupportMenuInflater();
		inflater.inflate(R.menu.action_mode_doc, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		final Long docId = (Long) mode.getTag();
		if (docType == DOCTYPE_BOOK) {
			currentDoc = App.bDao.getById(docId);
		} else {
			currentDoc = App.mDao.getById(docId);
		}
		switch (item.getItemId()) {
		case R.id.action_delete:
			new AlertDialog.Builder(getActivity())
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
										App.bDao.delete((Book) currentDoc);
										docTypeName = App.getAppContext()
												.getResources()
												.getString(R.string.book);
									} else {
										App.mDao.delete((Movie) currentDoc);
										docTypeName = App.getAppContext()
												.getResources()
												.getString(R.string.movie);
									}
									Toast.makeText(
											getActivity(),
											docTypeName
													+ App.getAppContext()
															.getResources()
															.getString(
																	R.string.doc_deleted),
											Toast.LENGTH_SHORT).show();
								}
							}).setNegativeButton(android.R.string.no, null)
					.show();
			mode.finish();
			return true;
		case R.id.action_edit:
			Intent intent = new Intent(getActivity(), DocsAddActivity.class);
			intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, docType);
			intent.putExtra(INTENT_DOC_ID_PARAMNAME, docId);
			intent.putExtra(INTENT_DOC_OBJ_PARAMNAME, currentDoc);
			startActivityForResult(intent, 0);
			return true;
		case R.id.share_facebook:
			Facebook.shareDocument(currentDoc, getActivity());
			mode.finish();
			return true;
		case R.id.share_google:
			Intent shareIntent;
			if (currentDoc.getUrlinfo() != null){
				shareIntent = new PlusShare.Builder(this.getActivity())
					.setType("text/plain").setText(currentDoc.getTitle())
					.setContentUrl(Uri.parse(currentDoc.getUrlinfo()))
					.getIntent();
			}
			else{
				shareIntent = new PlusShare.Builder(this.getActivity())
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
		default:
			return false;
		}
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mActionMode = null;
	}
}
