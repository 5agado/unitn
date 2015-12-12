package edu.unitn.pbam.androidproject.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import edu.unitn.pbam.androidproject.activities.SettingsActivity;
import edu.unitn.pbam.androidproject.model.Document;

public class Utils {
	private Utils() {
	}

	public static String append(final String baseUrl, final String endpoint,
			final String... args) {
		String url = baseUrl + "/" + endpoint + "?";
		if (args.length > 0) {
			int i = 0;
			for (String arg : args) {
				url += arg + "{arg" + i + "}&";
			}
		}
		return url;
	}

	public static String join(String separator, String... string) {
		int len = string.length;
		if (len == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(string[0]);
		for (int i = 1; i < len; i++) {
			sb.append(separator).append(string[i]);
		}
		return sb.toString();
	}

	public static boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNetworkAvailable() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(App.getAppContext());
		boolean wifiPref = sharedPref.getBoolean(
				SettingsActivity.KEY_PREF_WIFI, false);

		Context context = App.getAppContext();
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (wifiPref) {
			NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (!wifi.isAvailable())
				return false;
		}

		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static String toString(Document doc) {
		String res = "";
		res += doc.getTitle() + "\n" + String.valueOf(doc.getYear()) + "\n"
				+ "My Rating =" + String.valueOf(doc.getRating()) + "/100\n"
				+ doc.getDescription() + "\n" + doc.getUrlinfo();
		return res;
	}

	public static String loadFileFromFS(String path) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader input = new BufferedReader(new FileReader(path));
			String line;
			try {
				while ((line = input.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}

	// load file from apps res/raw folder or Assets folder
	public static String loadFile(String fileName, boolean loadFromRawFolder) {
		InputStream iS;
		Resources resources = App.getAppContext().getResources();

		try {
			if (loadFromRawFolder) {
				int rID = resources.getIdentifier(
						"edu.unitn.pbam.androidproject:raw/" + fileName, null,
						null);
				iS = resources.openRawResource(rID);
			} else {

				iS = resources.getAssets().open(fileName);
			}

			byte[] buffer = new byte[iS.available()];
			iS.read(buffer);
			ByteArrayOutputStream oS = new ByteArrayOutputStream();
			oS.write(buffer);
			oS.close();
			iS.close();

			return oS.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
