package com.twotoasters.android.horizontalimagescroller;

public class ImageToLoadUrlCacheKey {

	final private String _url;
	final private String _username;
	final private String _password;
	
	public ImageToLoadUrlCacheKey(final String url, final String username, final String password) {
		_url = url;
		_username = username;
		_password = password;
	}

	public String getUrl() {
		return _url;
	}

	public String getUsername() {
		return _username;
	}

	public String getPassword() {
		return _password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_password == null) ? 0 : _password.hashCode());
		result = prime * result + ((_url == null) ? 0 : _url.hashCode());
		result = prime * result + ((_username == null) ? 0 : _username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ImageToLoadUrlCacheKey other = (ImageToLoadUrlCacheKey)obj;
		if(_password == null) {
			if(other._password != null)
				return false;
		} else if(!_password.equals(other._password))
			return false;
		if(_url == null) {
			if(other._url != null)
				return false;
		} else if(!_url.equals(other._url))
			return false;
		if(_username == null) {
			if(other._username != null)
				return false;
		} else if(!_username.equals(other._username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ImageToLoadUrlCacheKey [_url=" + _url + ", _username=" + _username + ", _password=" + _password + "]";
	}
	
	
}