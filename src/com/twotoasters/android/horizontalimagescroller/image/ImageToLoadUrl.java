package com.twotoasters.android.horizontalimagescroller.image;

import com.google.common.base.Strings;
import com.twotoasters.android.horizontalimagescroller.io.ImageUrlRequestCacheKey;
import com.twotoasters.android.horizontalimagescroller.listener.OnImageLoadedListener;

public class ImageToLoadUrl extends ImageToLoad {
	private String _url;
	private String _username = "";
	private String _password = "";
	protected boolean _priority = false;
	protected boolean _canCacheFile = false;
	protected OnImageLoadedListener _onImageLoadedListener;
	
	protected ImageUrlRequestCacheKey _cacheKey;

	public ImageToLoadUrl(String url) {
		_url = url;
	}

	public ImageToLoadUrl(String url, String username, String password) {
		_url = url;
		_username = Strings.isNullOrEmpty(username) ? "" : username;
		_password = Strings.isNullOrEmpty(password) ? "" : password;
	}

	public ImageToLoadUrl(String url, OnImageLoadedListener onImageLoadedListener) {
		_url = url;
		_onImageLoadedListener = onImageLoadedListener;
	}

	public ImageToLoadUrl(String url, String username, String password, OnImageLoadedListener onImageLoadedListener) {
		_url = url;
		setUsername(username);
		setPassword(password);
		_onImageLoadedListener = onImageLoadedListener;
	}

	public String getUrl() {
		return _url;
	}

	public void setUrl(String url) {
		_url = url;
	}

	public String getUsername() {
		return _username;
	}

	public void setUsername(String username) {
		_username = Strings.isNullOrEmpty(username) ? "" : username;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		_password = Strings.isNullOrEmpty(password) ? "" : password;
	}

	public boolean isPriority() {
		return _priority;
	}

	public void setPriority(boolean priority) {
		_priority = priority;
	}

	public boolean isCanCacheFile() {
		return _canCacheFile;
	}

	public void setCanCacheFile(boolean canCacheFile) {
		_canCacheFile = canCacheFile;
	}

	public OnImageLoadedListener getOnImageLoadedListener() {
		return _onImageLoadedListener;
	}

	public void setOnImageLoadedListener(OnImageLoadedListener onImageLoadedListener) {
		_onImageLoadedListener = onImageLoadedListener;
	}
}