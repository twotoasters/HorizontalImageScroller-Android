package com.twotoasters.android.horizontalimagescroller;

public interface OnImageLoadedListener {
	public void onImageLoaded(String imageUrl);

	public void onLoadFailure(String imageUrl);
}