/*
Copyright 2012 Two Toasters, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.twotoasters.android.horizontalimagescroller.image;

import com.google.common.base.Strings;
import com.twotoasters.android.horizontalimagescroller.io.ImageUrlRequestCacheKey;

public class ImageToLoadUrl extends ImageToLoad {
	private String _url;
	private String _username = "";
	private String _password = "";
	protected boolean _priority = false;
	protected boolean _canCacheFile = false;
	protected int _onImageLoadFailureResourceId;
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

	public int getOnImageLoadFailureResourceId() {
		return _onImageLoadFailureResourceId;
	}

	public void setOnImageLoadFailureResourceId(int onImageLoadFailureResourceId) {
		_onImageLoadFailureResourceId = onImageLoadFailureResourceId;
	}

}