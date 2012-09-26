package com.twotoasters.android.horizontalimagescroller.io;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrl;

public class ImageUrlRequest {
	private ImageToLoadUrl _imageToLoadUrl;
	private int _reqWidth;
	private int _reqHeight;
	
	public ImageUrlRequest(ImageToLoadUrl imageToLoadUrl, int reqWidth, int reqHeight) {
		_imageToLoadUrl = imageToLoadUrl;
		_reqWidth = reqWidth;
		_reqHeight = reqHeight;
	}
	
	public ImageToLoadUrl getImageToLoadUrl() {
		return _imageToLoadUrl;
	}
	
	public int getReqWidth() {
		return _reqWidth;
	}
	
	public int getReqHeight() {
		return _reqHeight;
	}
}
