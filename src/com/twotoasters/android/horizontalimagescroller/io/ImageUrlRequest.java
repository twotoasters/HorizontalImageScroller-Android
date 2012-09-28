package com.twotoasters.android.horizontalimagescroller.io;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrl;

public class ImageUrlRequest {
	final private ImageToLoadUrl _imageToLoadUrl;
	final private int _reqWidth;
	final private int _reqHeight;
	private ImageUrlRequestCacheKey _cacheKey;
	
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
	
	public ImageUrlRequestCacheKey getCacheKey() {
		if (_cacheKey == null) {
			_cacheKey = new ImageUrlRequestCacheKey(_imageToLoadUrl.getUrl(), _imageToLoadUrl.getUsername(), _imageToLoadUrl.getPassword(), _reqWidth, _reqHeight);
		}
		return _cacheKey;
	}
}
