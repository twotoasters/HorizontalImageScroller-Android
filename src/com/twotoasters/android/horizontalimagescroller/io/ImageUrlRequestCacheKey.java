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
package com.twotoasters.android.horizontalimagescroller.io;

public class ImageUrlRequestCacheKey {

	final private String _url;
	final private String _username;
	final private String _password;
	final private int _width;
	final private int _height;
	
	public ImageUrlRequestCacheKey(final String url, final String username, final String password, final int width, final int height) {
		_url = url;
		_username = username;
		_password = password;
		_width = width;
		_height = height;
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
	
	public int getWidth() {
		return _width;
	}
	
	public int getHeight() {
		return _height;
	}

	@Override
	public String toString() {
		return "ImageUrlRequestCacheKey [_url=" + _url + ", _username=" + _username + ", _password=" + _password + ", _width=" + _width + ", _height="
				+ _height + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _height;
		result = prime * result + ((_password == null) ? 0 : _password.hashCode());
		result = prime * result + ((_url == null) ? 0 : _url.hashCode());
		result = prime * result + ((_username == null) ? 0 : _username.hashCode());
		result = prime * result + _width;
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
		ImageUrlRequestCacheKey other = (ImageUrlRequestCacheKey)obj;
		if(_height != other._height)
			return false;
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
		if(_width != other._width)
			return false;
		return true;
	}
	
}