package com.twotoasters.android.horizontalimagescroller;


import android.content.Context;
import android.util.AttributeSet;

public class HorizontalImageScroller extends HorizontalListView {

	private HorizontalImageScrollerAdapter _adapter = null;

	public HorizontalImageScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSolidColor(getResources().getColor(android.R.color.black));
	}

	public void setCurrentImageIndex(int index) {
		if(_adapter != null) {
			_adapter.setCurrentIndex(index);
			setSelection(index);
		}
	}

	public void setImageSize(int size) {
		if(_adapter != null) {
			_adapter.setImageSize(size);
		}
	}

	public void setHighlightActiveImage(boolean b) {
		if(_adapter != null) {
			_adapter.setHighlightActiveImage(b);
		}
	}

	public void setShowImageFrame(boolean b) {
		if(_adapter != null) {
			_adapter.setShowImageFrame(b);
		}
	}
}
