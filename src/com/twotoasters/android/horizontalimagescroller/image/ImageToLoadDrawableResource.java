package com.twotoasters.android.horizontalimagescroller.image;

public class ImageToLoadDrawableResource extends ImageToLoad {
	
	private int _drawableResourceId;
	
	public ImageToLoadDrawableResource(int drawableResourceId) {
		setDrawableResourceId(drawableResourceId);
	}

	public int getDrawableResourceId() {
		return _drawableResourceId;
	}

	public void setDrawableResourceId(int drawableResourceId) {
		this._drawableResourceId = drawableResourceId;
	}
}
