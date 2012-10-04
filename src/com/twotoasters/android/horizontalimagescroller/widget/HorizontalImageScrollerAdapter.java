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
package com.twotoasters.android.horizontalimagescroller.widget;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.twotoasters.android.horizontalimagescroller.R;
import com.twotoasters.android.horizontalimagescroller.image.BitmapHelper;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoad;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadDrawableResource;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrl;
import com.twotoasters.android.horizontalimagescroller.io.ImageCacheManager;
import com.twotoasters.android.horizontalimagescroller.io.ImageUrlRequest;

public class HorizontalImageScrollerAdapter extends BaseAdapter {
	protected Context _context;
	protected int _imageSize;
	protected int _frameColor;
	protected int _frameOffColor;
	protected int _transparentColor;
	protected int _imageLayoutResourceId;
	protected int _loadingImageResourceId;
	protected LayoutInflater _inflater;
	protected int _currentImageIndex = -1;
	protected boolean _highlightActive = true;
	protected boolean _showImageFrame = true;
	protected ImageCacheManager _imageCacheManager;
	protected OnClickListener _imageOnClickListener;
	protected int _defaultImageFailedToLoadResourceId;
	protected List<ImageToLoad> _images;
	protected int _imageIdInLayout;
	protected int _innerWrapperIdInLayout;

	public HorizontalImageScrollerAdapter(final Context context, final List<ImageToLoad> images, final int imageSize, final int frameColorResourceId, final int frameOffColorResourceId,
			final int transparentColorResourceId, final int imageLayoutResourceId, final int loadingImageResourceId) {
		_context = context;
		_inflater = LayoutInflater.from(context);
		_images = images;
		Resources res = context.getResources();
		_imageSize = imageSize;
		_frameColor = res.getColor(frameColorResourceId);
		_frameOffColor = res.getColor(frameOffColorResourceId);
		_transparentColor = res.getColor(transparentColorResourceId);
		_imageLayoutResourceId = imageLayoutResourceId;
		_loadingImageResourceId = loadingImageResourceId;
		_imageCacheManager = ImageCacheManager.getInstance(context);
	}

	public HorizontalImageScrollerAdapter(final Context context, final List<ImageToLoad> images) {
		_context = context;
		_inflater = LayoutInflater.from(context);
		_images = images;
		_setDefaultValues();
	}
	
	private void _setDefaultValues() {
		Resources res = _context.getResources();
		_imageSize = (int) res.getDimension(R.dimen.default_image_size);
		_frameColor = res.getColor(R.color.default_frame_color);
		_frameOffColor = res.getColor(R.color.default_frame_off_color);
		_transparentColor = res.getColor(R.color.default_transparent_color);
		_imageLayoutResourceId = R.layout.horizontal_image_scroller_item;
		_imageCacheManager = ImageCacheManager.getInstance(_context);
		_imageIdInLayout = R.id.image;
		_innerWrapperIdInLayout = R.id.image_frame;
	}

	public int getImageSize() {
		return _imageSize;
	}

	public void setImageSize(int imageSize) {
		_imageSize = imageSize;
		notifyDataSetChanged();
	}

	public int getFrameColor() {
		return _frameColor;
	}

	public void setFrameColor(int frameColor) {
		_frameColor = frameColor;
		notifyDataSetChanged();
	}

	public int getFrameOffColor() {
		return _frameOffColor;
	}

	public void setFrameOffColor(int frameOffColor) {
		_frameOffColor = frameOffColor;
		notifyDataSetChanged();
	}

	public int getTransparentColor() {
		return _transparentColor;
	}

	public void setTransparentColor(int transparentColor) {
		_transparentColor = transparentColor;
		notifyDataSetChanged();
	}

	public int getImageLayoutResourceId() {
		return _imageLayoutResourceId;
	}

	public void setImageLayoutResourceId(int imageLayoutResourceId) {
		_imageLayoutResourceId = imageLayoutResourceId;
		notifyDataSetChanged();
	}

	public int getLoadingImageResourceId() {
		return _loadingImageResourceId;
	}

	public void setLoadingImageResourceId(int loadingImageResourceId) {
		_loadingImageResourceId = loadingImageResourceId;
		notifyDataSetChanged();
	}

	public boolean isShowImageFrame() {
		return _showImageFrame;
	}

	public void setCurrentIndex(int index) {
		_currentImageIndex = index;
		notifyDataSetChanged();
	}
	
	public int getCurrentIndex() {
		return _currentImageIndex;
	}
	
	public boolean hasCurrentIndex() {
		return _currentImageIndex >= 0;
	}

	public void setHighlightActiveImage(boolean highlight) {
		_highlightActive = highlight;
	}

	public void setShowImageFrame(boolean b) {
		_showImageFrame = b;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(getCount() > 0) {
			if(view == null) {
				view = _inflater.inflate(_imageLayoutResourceId, null);
			}
			ImageToLoad imageToLoad = getItem(position);
			ImageView imageView = (ImageView)view.findViewById(_imageIdInLayout);
			_imageCacheManager.unbindImage(imageView);
			imageToLoad.setImageView(imageView);
			if (_imageOnClickListener != null) imageView.setOnClickListener(_imageOnClickListener);
			_setupImageViewLayout(view, imageToLoad, position);
			_setupInnerWrapper(view, imageToLoad, position);
			if (imageToLoad instanceof ImageToLoadUrl) {
				ImageToLoadUrl imageToLoadUrl = (ImageToLoadUrl) imageToLoad;
				ImageUrlRequest imageUrlRequest = new ImageUrlRequest(imageToLoadUrl, _imageSize, _imageSize);
				if (imageToLoadUrl.getOnImageLoadFailureResourceId() == 0 && _defaultImageFailedToLoadResourceId != 0) {
					imageUrlRequest.setImageFailedToLoadResourceId(_defaultImageFailedToLoadResourceId);
				} else if (imageToLoadUrl.getOnImageLoadFailureResourceId() != 0) {
					imageUrlRequest.setImageFailedToLoadResourceId(imageToLoadUrl.getOnImageLoadFailureResourceId());
				}
				BitmapHelper.applySampledResourceToImageView(_context.getResources(), _loadingImageResourceId, _imageSize, _imageSize, imageView);
				_imageCacheManager.bindDrawable(imageUrlRequest);
			} else if (imageToLoad instanceof ImageToLoadDrawableResource) {
				ImageToLoadDrawableResource imageToLoadDrawableResource = (ImageToLoadDrawableResource) imageToLoad;
				BitmapHelper.applySampledResourceToImageView(_context.getResources(), imageToLoadDrawableResource.getDrawableResourceId(), _imageSize, _imageSize, imageView);
			}
		}
		return view;
	}
	
	protected void _setupImageViewLayout(View view, ImageToLoad imageToLoad, int position) {
		ImageView imageView = (ImageView)view.findViewById(_imageIdInLayout);
		LayoutParams params = imageView.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = _imageSize;
		imageView.setLayoutParams(params);
	}
	
	protected void _setupInnerWrapper(View view, ImageToLoad imageToLoad, int position) {
		View frame = view.findViewById(_innerWrapperIdInLayout);
		LayoutParams frameParams = frame.getLayoutParams();
		frameParams.width = LayoutParams.WRAP_CONTENT;
		frameParams.height = LayoutParams.WRAP_CONTENT;
		frame.setLayoutParams(frameParams);
		if (_showImageFrame == false) {
			frame.setBackgroundColor(_transparentColor);
		} else if (_highlightActive && _currentImageIndex == position) {
			frame.setBackgroundColor(_frameColor);
		} else {
			frame.setBackgroundColor(_frameOffColor);
		}
	}
	
	@Override
	public ImageToLoad getItem(int position) {
		return _images.get(position);
	}

	@Override
	public int getCount() {
		return _images.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void unbindImageViews() {
		if (_images != null) {
			ImageCacheManager icm = ImageCacheManager.getInstance(_context);
			for (ImageToLoad image : _images) {
				if (image instanceof ImageToLoadUrl) {
					icm.unbindImage(((ImageToLoadUrl) image).getImageView());
				}
			}
		}
	}

	public int getDefaultImageFailedToLoadResourceId() {
		return _defaultImageFailedToLoadResourceId;
	}

	public void setDefaultImageFailedToLoadResourceId(int defaultImageFailedToLoadResourceId) {
		_defaultImageFailedToLoadResourceId = defaultImageFailedToLoadResourceId;
	}
	
	
}