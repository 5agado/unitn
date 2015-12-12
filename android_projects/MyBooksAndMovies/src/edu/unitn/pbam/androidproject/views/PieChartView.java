package edu.unitn.pbam.androidproject.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {
	private float[] mValues;
	private String[] mLabels;
	private float mSum;
	private RectF mRect;
	private Paint paints[];
	private Paint borderPaint;
	private int[] mColors;
	private final int PADDING = 10;

	public PieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}
		borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setColor(Color.LTGRAY);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(0);
	}

	public String getLegend() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><ul>");
		for (int i = 0; i < mLabels.length; i++) {
			String hexColor = String.format("#%06X", (0xFFFFFF & mColors[i]));
			sb.append("<li style='color: " + hexColor + ";'>" + mLabels[i]
					+ "</li>");
		}
		sb.append("</ul></body></html>");
		return sb.toString();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != 0 && h != 0) {
			int minSide = Math.min(w, h);
			int radius = minSide / 2 - PADDING;
			int centerX = w / 2;
			int centerY = h / 2;
			mRect = new RectF(centerX - radius, centerY - radius, centerX
					+ radius, centerY + radius);
		}
	}

	public void setValues(int[] values, String[] labels, int[] colors) {
		mSum = 0;
		mValues = new float[values.length];
		for (int i : values) {
			mSum += i;
		}
		for (int i = 0; i < values.length; i++) {
			mValues[i] = (values[i] / mSum) * 360f;
		}
		mLabels = labels;
		mColors = colors;
		paints = new Paint[mColors.length];
		for (int i = 0; i < colors.length; i++) {
			Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
			p.setColor(colors[i]);
			paints[i] = p;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		float startAngle = -30f;

		for (int i = 0; i < mValues.length; i++) {
			if (mValues[i] > 0) {
				canvas.drawArc(mRect, startAngle, mValues[i], true, paints[i]);
				canvas.drawArc(mRect, startAngle, mValues[i], true, borderPaint);
				startAngle += mValues[i];
			}
		}
	}

}
