package com.twotoasters.android.horizontalimagescroller;

public class ImageToLoadAsset extends ImageToLoad {
	
	public String _path;
	
	public ImageToLoadAsset(String path) {
		_path = path;
	}

	public String getPath() {
		return _path;
	}

	public void setPath(String path) {
		_path = path;
	}

}
