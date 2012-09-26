package com.twotoasters.android.horizontalimagescroller.widget;


import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

public class HorizontalImageScroller extends HorizontalListView {

	public HorizontalImageScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSolidColor(getResources().getColor(android.R.color.black));
	}

	public void setCurrentImageIndex(int index) {
		if (mAdapter != null) {
			((HorizontalImageScrollerAdapter)mAdapter).setCurrentIndex(index);
			setSelection(index);
		}
	}

	public void setImageSize(int size) {
		if(mAdapter != null) {
			((HorizontalImageScrollerAdapter)mAdapter).setImageSize(size);
		}
	}

	public void setHighlightActiveImage(boolean b) {
		if(mAdapter != null) {
			((HorizontalImageScrollerAdapter)mAdapter).setHighlightActiveImage(b);
		}
	}

	public void setShowImageFrame(boolean b) {
		if(mAdapter != null) {
			((HorizontalImageScrollerAdapter)mAdapter).setShowImageFrame(b);
		}
	}
	
	public static void unbindImageViews(List<HorizontalImageScroller> scrollers) {
		for (HorizontalImageScroller scroller : scrollers) {
			((HorizontalImageScrollerAdapter)scroller.mAdapter).unbindImageViews();
		}
	}
	
	public int getCurrentX() {
		return mCurrentX;
	}
}
