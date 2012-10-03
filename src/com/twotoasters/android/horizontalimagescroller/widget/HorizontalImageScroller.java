/*
Copyright 2012 Two Toasters, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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
	
	public int getCurrentImageIndex() {
		if (mAdapter != null) {
			return ((HorizontalImageScrollerAdapter)mAdapter).getCurrentIndex();
		}
		return -1;
	}
	
	public boolean hasCurrentImageIndex() {
		if (mAdapter != null) {
			return ((HorizontalImageScrollerAdapter)mAdapter).hasCurrentIndex();
		}
		return false;
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
