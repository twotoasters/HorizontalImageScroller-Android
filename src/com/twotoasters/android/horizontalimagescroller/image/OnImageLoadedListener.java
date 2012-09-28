package com.twotoasters.android.horizontalimagescroller.image;


public interface OnImageLoadedListener {
	public void onImageLoaded(ImageToLoad imageToLoad);

	public void onLoadFailure(ImageToLoad imageToLoad);
}