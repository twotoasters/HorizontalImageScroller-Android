package com.twotoasters.android.horizontalimagescroller.image;

import android.widget.ImageView;

public abstract class ImageToLoad {
	protected ImageView _imageView;

	public ImageView getImageView() {
		return _imageView;
	}

	public void setImageView(ImageView imageView) {
		_imageView = imageView;
	}
	
}
