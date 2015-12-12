package edu.unitn.pbam.androidproject.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class FileExplorerActivity extends Activity {
	private ListView lst;
	public static final String FILE_PATH_PARAM = "file_path";
	public static final int CHOOSE_FILE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_explorer);
		lst = (ListView) findViewById(R.id.list);
		Intent intent = getIntent();
		String filePath = intent.getStringExtra(FILE_PATH_PARAM);
		FileExplorerAdapter adapter;
		if (filePath != null)
			adapter = new FileExplorerAdapter(this, lst, false, new File(
					filePath));
		else
			adapter = new FileExplorerAdapter(this, lst, false);
		lst.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_FILE) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
			}
		}
	}

}

class FileExplorerAdapter extends ArrayAdapter<File> {
	private List<File> mList;
	private boolean mDirsOnly;
	private boolean mIsRoot;
	private ListView mLv;
	private Activity mActivity;

	public FileExplorerAdapter(Activity activity, ListView lv, boolean dirsOnly) {
		this(activity, lv, dirsOnly, Environment.getExternalStorageDirectory());
	}

	public FileExplorerAdapter(Activity activity, ListView lv,
			boolean dirsOnly, File currDir) {
		super(activity, R.layout.file_item);
		mDirsOnly = dirsOnly;
		mLv = lv;
		mList = new ArrayList<File>();
		mActivity = activity;

		File[] files = currDir.listFiles();

		if (files != null) {
			for (File f : files) {
				if (!mDirsOnly || f.isDirectory()) {
					mList.add(f);
				}
			}

			Comparator<File> comp = new Comparator<File>() {
				@Override
				public int compare(File lhs, File rhs) {
					boolean isDir1 = lhs.isDirectory();
					boolean isDir2 = rhs.isDirectory();
					if (isDir1 && !isDir2)
						return -1;
					if (isDir2 && !isDir1)
						return 1;
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			};

			Collections.sort(mList, comp);
		}

		if (currDir.getParent() != null) {
			mList.add(0, currDir.getParentFile());
			mIsRoot = false;
		} else {
			mIsRoot = true;
		}

		mLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File file = getItem(position);
				if (file.isDirectory()) {
					/*if (position == 0 && !mIsRoot) {
						mActivity.finish();
					}*/
					Intent intent = new Intent(mActivity,
							FileExplorerActivity.class);
					intent.putExtra(FileExplorerActivity.FILE_PATH_PARAM,
							file.getAbsolutePath());
					mActivity.startActivityForResult(intent,
							FileExplorerActivity.CHOOSE_FILE);
				} else {
					Intent data = new Intent();
					data.putExtra(Constants.INTENT_FILEPATH_PARAMNAME,
							file.getAbsolutePath());
					mActivity.setResult(Activity.RESULT_OK, data);
					mActivity.finish();
				}
			}
		});

	}

	@Override
	public File getItem(int position) {
		return mList.get(position);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.file_item, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.text);

		File file = getItem(position);

		if (position == 0 && !mIsRoot)
			tv.setText("..");
		else
			tv.setText(file.getName());

		ImageView iv = (ImageView) convertView.findViewById(R.id.image);
		if (file.isDirectory())
			iv.setImageResource(R.drawable.ic_menu_archive);
		else
			iv.setImageDrawable(null);

		return convertView;
	}

}