package edu.unitn.pbam.androidproject.utilities;

import static android.content.Intent.ACTION_VIEW;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BarcodeScanner {
	public static final String PACKAGE = "com.visionsmarts.pic2shop";
	public static final String ACTION = PACKAGE + ".SCAN";
	public static final String BARCODE = "BARCODE";
	public static final int REQUEST_CODE_SCAN = 1;

	private BarcodeScanner() {
	}

	public static boolean isBarcodeScannerAvailable(Context context) {
		Intent test = new Intent(ACTION);
		return context.getPackageManager().resolveActivity(test, 0) != null;
	}

	public static void launchMarketToInstallScanner(Context context) {
		Intent intent = new Intent(ACTION_VIEW,
				Uri.parse("market://details?id=" + PACKAGE));
		context.startActivity(intent);
	}

}
