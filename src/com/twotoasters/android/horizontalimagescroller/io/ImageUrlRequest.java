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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrl;

public class ImageUrlRequest {
	final private ImageToLoadUrl _imageToLoadUrl;
	final private int _reqWidth;
	final private int _reqHeight;
	private ImageUrlRequestCacheKey _cacheKey;
	private String _cacheFileName;
	private int _imageFailedToLoadResourceId;
	
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
	
	public String getCacheFileName() {
		if (_cacheFileName == null) {
			String url = _imageToLoadUrl.getUrl();
			String username = _imageToLoadUrl.getUsername();
			String password = _imageToLoadUrl.getPassword();
			String toHash = String.format("url_%1$s_creds_%2$s%3$s_size_%4$dx%5$d", url, username, password, _reqWidth, _reqHeight);
			_cacheFileName = new String(Hex.encodeHex(DigestUtils.sha256(toHash)));
		}
		return _cacheFileName;
	}

	public int getImageFailedToLoadResourceId() {
		return _imageFailedToLoadResourceId;
	}

	public void setImageFailedToLoadResourceId(int imageFailedToLoadResourceId) {
		_imageFailedToLoadResourceId = imageFailedToLoadResourceId;
	}
}
