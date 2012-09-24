package com.twotoasters.android.horizontalimagescroller.listener;

public interface OnImageLoadedListener {
	public void onImageLoaded(String imageUrl);

	public void onLoadFailure(String imageUrl);
}