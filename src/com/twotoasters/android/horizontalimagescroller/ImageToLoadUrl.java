package com.twotoasters.android.horizontalimagescroller;

import com.google.common.base.Strings;


public class ImageToLoadUrl extends ImageToLoad {
	private String _url;
	private String _username;
	private String _password;
	private boolean _priority;
	private boolean _canCacheFile;
	private OnImageLoadedListener _onImageLoadedListener;

	public ImageToLoadUrl(String url, String username, String password, OnImageLoadedListener onImageLoadedListener) {
		_url = url;
		_username = Strings.isNullOrEmpty(username) ? null : username;
		_password = Strings.isNullOrEmpty(password) ? null : password;
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
		_username = username;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		_password = password;
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

	public ImageToLoadUrlCacheKey toCacheKey() {
		return new ImageToLoadUrlCacheKey(_url, _username, _password);
	}

	public OnImageLoadedListener getOnImageLoadedListener() {
		return _onImageLoadedListener;
	}

	public void setOnImageLoadedListener(OnImageLoadedListener onImageLoadedListener) {
		_onImageLoadedListener = onImageLoadedListener;
	}
}