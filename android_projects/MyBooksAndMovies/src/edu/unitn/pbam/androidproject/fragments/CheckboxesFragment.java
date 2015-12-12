package edu.unitn.pbam.androidproject.fragments;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOC_ID_NO_DOC;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_ID_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.activities.DocDetailsActivity;
import edu.unitn.pbam.androidproject.activities.DocsAddActivity;
import edu.unitn.pbam.androidproject.model.DList;
import edu.unitn.pbam.androidproject.model.DList.Type;
import edu.unitn.pbam.androidproject.model.dao.db.DListDaoDb;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class CheckboxesFragment extends DialogFragment {
	private final static String TAG = "CHECKBOX_FRAGMENT";
	private List<Long> mSelectedItems;
	private List<Long> mItemsID;
	private List<String> mItemsNames;
	private boolean[] mIsItemChecked;
	private CharSequence[] charSeqOfNames;
	private int mDocType;
	private Long mDocID;

	private void initData() {
		// Prima preparo i dati delle Dlist a partire dal cursor
		Cursor dlists = App.dlDao
				.getByType(mDocType == Constants.DOCTYPE_BOOK ? Type.BOOK
						: Type.MOVIE);

		mSelectedItems = new ArrayList<Long>();
		mIsItemChecked = new boolean[dlists.getCount()];
		// Settaggio delle liste già selezionate nel caso
		// abbia un documento non proveniente da internet
		Set<Long> docDlistsID = null;
		if (mDocID != DOC_ID_NO_DOC) {
			List<DList> docDlists = ((DocsAddActivity) getActivity())
					.getCurrentDocDlists();
			if (docDlists != null) {
				docDlistsID = new HashSet<Long>();
				for (DList d : docDlists) {
					docDlistsID.add(d.getId());
				}
			}
		}
		mItemsID = new ArrayList<Long>();
		mItemsNames = new ArrayList<String>();

		dlists.moveToFirst();
		// se è il caso di addActivity non mostro le due principali Dlists
		if (mDocID != DOC_ID_NO_DOC) {
			dlists.moveToNext();
			dlists.moveToNext();
		}
		int count = 0;
		while (!dlists.isAfterLast()) {
			Long id = dlists.getLong(dlists.getColumnIndex(DListDaoDb.ID_COL));
			mItemsID.add(id);
			mItemsNames.add(dlists.getString(dlists
					.getColumnIndex(DListDaoDb.NAME_COL)));
			if (docDlistsID != null) {
				mIsItemChecked[count] = docDlistsID.contains(id);
			} else {
				mIsItemChecked[count] = false;
			}
			dlists.moveToNext();
			count++;
		}
		charSeqOfNames = mItemsNames.toArray(new CharSequence[mItemsNames
				.size()]);
		try {
			dlists.close();
		} catch (Throwable t) {
			Log.e("CheckboxesFragment",
					"Error closing myCursorFromSqLite Cursor " + t);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mDocType = bundle.getInt(INTENT_DOC_TYPE_PARAMNAME);
		mDocID = bundle.getLong(INTENT_DOC_ID_PARAMNAME, 0);
		initData();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.select_lists)
				.setMultiChoiceItems(charSeqOfNames, mIsItemChecked,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									mSelectedItems.add(mItemsID.get(which));
									mIsItemChecked[which] = true;
								} else if (mSelectedItems.contains(which)) {
									mSelectedItems.remove(mItemsID.get(which));
									mIsItemChecked[which] = false;
								}
							}
						})
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// Se caso internet chiamo il metodo
								// di salvataggio dell'acticity e passo
								// le Dlists
								if (mDocID == DOC_ID_NO_DOC) {
									ArrayList<DList> dlists = new ArrayList<DList>();
									for (Long l : mSelectedItems) {
										DList d = App.dlDao.getById(l);
										dlists.add(d);
									}
									((DocDetailsActivity) getActivity())
											.saveDocWithDlist(dlists);
								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
							}
						});

		return builder.create();
	}

	public List<Long> getSelectedItemsID() {
		return mSelectedItems;
	}
}
