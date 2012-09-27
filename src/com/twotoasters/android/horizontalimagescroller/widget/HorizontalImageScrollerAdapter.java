package com.twotoasters.android.horizontalimagescroller.widget;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
	protected int _currentImageIndex;
	protected boolean _highlightActive = true;
	protected boolean _showImageFrame = true;
	protected ImageCacheManager _imageCacheManager;
	protected OnClickListener _imageOnClickListener;
	protected List<ImageToLoad> _images;

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
		Resources res = context.getResources();
		_imageSize = (int) res.getDimension(R.dimen.default_image_size);
		_frameColor = res.getColor(R.color.default_frame_color);
		_frameOffColor = res.getColor(R.color.default_frame_off_color);
		_transparentColor = res.getColor(R.color.default_transparent_color);
		_imageLayoutResourceId = R.layout.horizontal_image_scroller_item;
		_imageCacheManager = ImageCacheManager.getInstance(context);
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

	public void setHighlightActiveImage(boolean highlight) {
		_highlightActive = highlight;
	}

	public void setShowImageFrame(boolean b) {
		_showImageFrame = b;
	}

	protected int _getImageIdInLayout() {
		return R.id.image;
	}

	protected int _getImageFrameIdInLayout() {
		return R.id.image_frame;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(getCount() > 0) {
			if(view == null) {
				view = _inflater.inflate(_imageLayoutResourceId, null);
			}
			ImageToLoad imageToLoad = getItem(position);
			ImageView imageView = (ImageView)view.findViewById(_getImageIdInLayout());
			_imageCacheManager.unbindImage(imageView);
			imageToLoad.setImageView(imageView);
			if (_imageOnClickListener != null) imageView.setOnClickListener(_imageOnClickListener);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)imageView.getLayoutParams();
			params.width = _imageSize;
			params.height = _imageSize;
			imageView.setLayoutParams(params);
			View frame = view.findViewById(_getImageFrameIdInLayout());
			if (imageToLoad instanceof ImageToLoadUrl) {
				ImageToLoadUrl imageToLoadUrl = (ImageToLoadUrl) imageToLoad;
				ImageUrlRequest imageUrlRequest = new ImageUrlRequest(imageToLoadUrl, _imageSize, _imageSize);
				_setImageViewWithDrawableResourceId(imageView, _loadingImageResourceId);
				_imageCacheManager.bindDrawable(imageUrlRequest);
			} else if (imageToLoad instanceof ImageToLoadDrawableResource) {
				_setImageViewWithDrawableResourceId(imageView, ((ImageToLoadDrawableResource) imageToLoad).getDrawableResourceId());
			}
			if(!_showImageFrame) {
				frame.setBackgroundColor(_transparentColor);
			}
		}
		return view;
	}
	
	protected void _setImageViewWithDrawableResourceId(ImageView imageView, int drawableResourceId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			imageView.setImageDrawable(_context.getResources().getDrawable(drawableResourceId));
		} else {
			// workaround for old versions of android
			imageView.setImageBitmap(BitmapHelper.decodeSampledBitmapFromResource(_context.getResources(), drawableResourceId, _imageSize, _imageSize));
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
	
	
}