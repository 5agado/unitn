package edu.unitn.pbam.androidproject.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.views.PieChartView;

public class StatisticsActivity extends SherlockActivity {
	// private int docType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// docType =
		// getIntent().getIntExtra(Constants.INTENT_DOC_TYPE_PARAMNAME,
		// Constants.DOCTYPE_BOOK);

		setContentView(R.layout.activity_statistics);

		// Action bar customization
		ActionBar actionBar = getSupportActionBar();
		actionBar.setIcon(R.drawable.statistics);
		actionBar.setTitle(App.getAppContext().getString(R.string.stats));
		actionBar.setHomeButtonEnabled(true);

		View moviesPie = findViewById(R.id.movies_pie);
		View booksPie = findViewById(R.id.books_pie);

		int val1;
		int val2;
		String[] labels;

		// Books
		val1 = App.bDao.getToReadBooks();
		val2 = App.bDao.getReadBooks();
		labels = new String[] { getString(R.string.toread),
				getString(R.string.read) };
		int[] values = new int[] { val1, val2 };

		PieChartView chart = (PieChartView) booksPie.findViewById(R.id.chart);
		chart.setValues(values, labels, new int[] {
				Color.rgb(0x33, 0xB5, 0xE5), Color.rgb(0xFF, 0x44, 0x44) });
		chart.invalidate();
		WebView legend = (WebView) booksPie.findViewById(R.id.legend);
		legend.loadData(chart.getLegend(), "text/html", "utf-8");

		// setto lo sfondo a trasparente
		legend.setBackgroundColor(0x00000000);
		legend.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

		// Movies
		val1 = App.mDao.getToWatchMovies();
		val2 = App.mDao.getWatchedMovies();
		labels = new String[] { getString(R.string.towatch),
				getString(R.string.watched) };
		values = new int[] { val1, val2 };
		chart = (PieChartView) moviesPie.findViewById(R.id.chart);
		chart.setValues(values, labels, new int[] {
				Color.rgb(0x33, 0xB5, 0xE5), Color.rgb(0xFF, 0x44, 0x44) });
		chart.invalidate();
		legend = (WebView) moviesPie.findViewById(R.id.legend);
		legend.loadData(chart.getLegend(), "text/html", "utf-8");

		// setto lo sfondo a trasparente
		legend.setBackgroundColor(0x00000000);
		legend.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

		TextView view;
		int numBooks = App.bDao.getNumberOfBooks();
		int numMovies = App.mDao.getNumberOfMovies();
		double avgMovies = App.mDao.getAverageRating();
		double avgBooks = App.bDao.getAverageRating();
		view = (TextView) findViewById(R.id.num_doc);
		view.setText(String.valueOf(numBooks + numMovies));
		view = (TextView) findViewById(R.id.num_books);
		view.setText(String.valueOf(numBooks));
		view = (TextView) findViewById(R.id.num_movies);
		view.setText(String.valueOf(numMovies));
		view = (TextView) findViewById(R.id.num_cats);
		view.setText("6");
		view = (TextView) findViewById(R.id.avg_rating_books);
		view.setText(String.valueOf(avgBooks));
		view = (TextView) findViewById(R.id.avg_rating_movies);
		view.setText(String.valueOf(avgMovies));

	}

	/*
	 * StringBuilder sb = new StringBuilder(); TextView viewInfos =
	 * (TextView)findViewById(R.id.statistics_info);
	 * 
	 * if (docType==Constants.DOCTYPE_BOOK) { sb.append("Total: " +
	 * App.bDao.getNumberOfBooks() + "\n"); sb.append("To read: " + val1 +
	 * "\n"); sb.append("Read: " + val2 + "\n"); sb.append("Average rating: " +
	 * App.bDao.getAverageRating() + "\n"); } else { sb.append("Total: " +
	 * App.mDao.getNumberOfMovies() + "\n"); sb.append("To watch: " + val1 +
	 * "\n"); sb.append("Watched: " + val2 + "\n"); sb.append("Average rating: "
	 * + App.mDao.getAverageRating() + "\n"); }
	 * viewInfos.setText(sb.toString());
	 * 
	 * }
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.statistics, menu); return true; }
	 */

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

}
