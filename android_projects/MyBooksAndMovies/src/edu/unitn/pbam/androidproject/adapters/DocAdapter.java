package edu.unitn.pbam.androidproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.model.Book;
import edu.unitn.pbam.androidproject.model.Document;
import edu.unitn.pbam.androidproject.model.Movie;
import edu.unitn.pbam.androidproject.model.dao.db.BookDaoDb;
import edu.unitn.pbam.androidproject.model.dao.db.MovieDaoDb;
import edu.unitn.pbam.androidproject.utilities.Constants;

public class DocAdapter<T extends Document> extends GenericListAdapter
		implements Filterable {
	int docType;

	public DocAdapter(Context context, int resId, int docType, Cursor objects) {
		super(context, resId, objects);
		this.docType = docType;
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		Document currentDoc;

		TextView author = (TextView) v.findViewById(R.id.extraDocText);
		if (docType == Constants.DOCTYPE_MOVIE) {
			currentDoc = MovieDaoDb.generateMovie(cursor);
			author.setText(((Movie) currentDoc).getDirector());
		} else {
			currentDoc = BookDaoDb.generateBook(cursor);
			author.setText(((Book) currentDoc).getAuthor());
		}

		TextView title = (TextView) v.findViewById(R.id.mainDocText);
		title.setText(currentDoc.getTitle());
		ImageView thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
		if (currentDoc.getCover() != null
				&& currentDoc.getCover().getImage() != null) {
			thumbnail.setImageDrawable(currentDoc.getCover().getImage());
		} else
			thumbnail.setImageResource(R.drawable.poster_default);

		if (currentDoc.getRating() != 0) {
			v.findViewById(R.id.layout_item_rating).setVisibility(View.VISIBLE);
			Double rat_value = Double.valueOf(currentDoc.getRating());
			TextView rat_num = (TextView) v.findViewById(R.id.rating_num);
			rat_num.setText(String.valueOf(rat_value.intValue()));
		} else
			v.findViewById(R.id.layout_item_rating).setVisibility(View.GONE);
	}
}
