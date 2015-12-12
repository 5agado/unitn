package edu.unitn.pbam.androidproject.adapters;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_LIST_TYPE_PARAMNAME;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_AUTHORS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_DLISTS;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_GENRES;
import static edu.unitn.pbam.androidproject.utilities.Constants.LISTTYPE_RATING;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.fragments.SimpleListFragment;
import edu.unitn.pbam.androidproject.fragments.TitlesFragment;
import edu.unitn.pbam.androidproject.utilities.App;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	private final static String TAG = "SectionsPagerAdapter";
	private Context c;
	private int docType;

	public SectionsPagerAdapter(FragmentManager fm, int docType) {
		super(fm);
		c = App.getAppContext();
		this.docType = docType;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		Bundle bundle = new Bundle();
		bundle.putInt(INTENT_DOC_TYPE_PARAMNAME, docType);
		switch (position) {
		case 0: // Dlists
			fragment = new SimpleListFragment();
			bundle.putInt(INTENT_LIST_TYPE_PARAMNAME, LISTTYPE_DLISTS);
			fragment.setArguments(bundle);
			return fragment;
		case 1: // Titles
			fragment = new TitlesFragment();
			fragment.setArguments(bundle);
			return fragment;
		case 2: // Authors
			fragment = new SimpleListFragment();
			bundle.putInt(INTENT_LIST_TYPE_PARAMNAME, LISTTYPE_AUTHORS);
			fragment.setArguments(bundle);
			return fragment;
		case 3: // Genres
			fragment = new SimpleListFragment();
			bundle.putInt(INTENT_LIST_TYPE_PARAMNAME, LISTTYPE_GENRES);
			fragment.setArguments(bundle);
			return fragment;
		case 4: // Rating
			fragment = new SimpleListFragment();
			bundle.putInt(INTENT_LIST_TYPE_PARAMNAME, LISTTYPE_RATING);
			fragment.setArguments(bundle);
			return fragment;
		default:
			Log.e(TAG, "nonDefault getItem position");
			throw new RuntimeException("nonDefault getItem position");
		}
	}

	@Override
	public int getCount() {
		// Dlists, Titles, Authors, Genres, Rating
		return 5;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return c.getString(R.string.dlists).toUpperCase(l);
		case 1:
			return c.getString(R.string.titles).toUpperCase(l);
		case 2:
			if (docType == DOCTYPE_BOOK)
				return c.getString(R.string.authors).toUpperCase(l);
			else
				return c.getString(R.string.directors).toUpperCase(l);
		case 3:
			return c.getString(R.string.genres).toUpperCase(l);
		case 4:
			return c.getString(R.string.rating).toUpperCase(l);
		default:
			Log.e(TAG, "nonDefault getPageTitle position");
			throw new RuntimeException("nonDefault getPageTitle position");
		}
	}
}
