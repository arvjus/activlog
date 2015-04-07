package org.zv.activlog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class RatingBar extends android.widget.RatingBar {
	private static final int NUM_STARS = 5;
	private Drawable drawable;
	
	public RatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		drawable = this.getResources().getDrawable(R.drawable.ratingstars);
		this.setIsIndicator(false);
		this.setNumStars(NUM_STARS);
		this.setStepSize(1.0f);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.setMeasuredDimension(drawable.getIntrinsicWidth() * NUM_STARS, drawable.getIntrinsicHeight());
	}
}
