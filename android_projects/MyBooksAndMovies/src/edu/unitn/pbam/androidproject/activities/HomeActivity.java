package edu.unitn.pbam.androidproject.activities;

import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_BOOK;
import static edu.unitn.pbam.androidproject.utilities.Constants.DOCTYPE_MOVIE;
import static edu.unitn.pbam.androidproject.utilities.Constants.INTENT_DOC_TYPE_PARAMNAME;

import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Utils;

public class HomeActivity extends SherlockActivity implements
		View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
	private final static String TAG = "HomeActivity";

	// Variables related to the Facebook login
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");

	private UiLifecycleHelper uiHelper;
	private LoginButton authButton;

	// Variables related to the Google+ login
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private SignInButton signInButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		// Facebook related
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		Session session = Session.getActiveSession();
		authButton = (LoginButton) findViewById(R.id.authButton);
		
		if (session == null || session.isClosed()) {
			authButton.setReadPermissions(Arrays.asList("basic_info"));
		}

		// Google+ related
		mPlusClient = new PlusClient.Builder(this, this, this)
				.setVisibleActivities("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity").build();
		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage(App.getAppContext().getResources()
				.getString(R.string.progress_sign_in));
		signInButton = (SignInButton) findViewById(R.id.sign_in_button);
		signInButton.setOnClickListener(this);

		// Hiding actionBar
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		uiHelper.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_stats:
			intent = new Intent(this, StatisticsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// We put an extra in the intent in order to differentiate between
	// movies and books, this int is then reused and passed between
	// fragment and activities, when necessary

	// the onClick method for the MovieButton
	public void startMoviesActivity(View view) {
		Intent intent = new Intent(this, DocsHomeActivity.class);
		intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, DOCTYPE_MOVIE);
		startActivity(intent);
	}

	// the onClick method for the BookButton
	public void startBooksActivity(View view) {
		Intent intent = new Intent(this, DocsHomeActivity.class);
		intent.putExtra(INTENT_DOC_TYPE_PARAMNAME, DOCTYPE_BOOK);
		startActivity(intent);
	}

	// Google+ related methods
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle b) {
		Log.i(TAG, "connected");
		// String accountName = mPlusClient.getAccountName();
		// Toast.makeText(this, accountName + " is connected.",
		// Toast.LENGTH_LONG).show();
		signInButton.setVisibility(View.GONE);
	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "disconnected");
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mConnectionResult = null;
					mPlusClient.connect();
				}
			}
		}
		/*
		 * if (view.getId() == R.id.sign_out_button &&
		 * mPlusClient.isConnected()) { mPlusClient.clearDefaultAccount();
		 * mPlusClient.disconnect(); mPlusClient.connect(); onDisconnected(); }
		 */
	}

	// Facebook related methods
	// Listener to be passed to the UiLifecycleHelper
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.i(TAG, "callback");
			if (exception == null) {
				onSessionStateChange(session, state, exception);
			} else {
				Log.i(TAG, "call of Session.StatusCallback with exception "
						+ exception.getMessage());
			}
		}
	};

	// Debug purpose
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
			List<String> permissions = session.getPermissions();
			try {
				if (!Utils.isSubsetOf(PERMISSIONS, permissions)) {
					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
							this, PERMISSIONS);
					session.requestNewPublishPermissions(newPermissionsRequest);
					Log.i("onSessionStateChange", "requestNewPublishPermissions");
					return;
				}
			} catch (Exception ex){
				Log.i(TAG, "catched exception" + ex.getMessage());
				return;
			} 
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		} else {
			Log.i(TAG, "Session state = " + state.name());
		}
	}
}
