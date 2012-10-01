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
package com.twotoasters.android.horizontalimagescroller.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoad;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadDrawableResource;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrl;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScroller;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScrollerAdapter;

public class MainActivity extends Activity {

	private List<HorizontalImageScroller> _horizontalImageScrollers;
	private static final String KEY_SCROLL_XES = "scrollXes";
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_horizontalImageScrollers = new ArrayList<HorizontalImageScroller>();

		ToasterImageToLoadHolder toasters = new ToasterImageToLoadHolder();

		OnItemClickListener onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ToasterToLoad toaster = (ToasterToLoad)((HorizontalImageScroller)parent).getAdapter().getItem(position);
				Toast.makeText(MainActivity.this, toaster.getName(), Toast.LENGTH_SHORT).show();
			}
		};

		// android toasters
		ArrayList<ImageToLoad> androidToasters = new ArrayList<ImageToLoad>();
		androidToasters.add(toasters.BRIAN);
		androidToasters.add(toasters.CARLTON);
		androidToasters.add(toasters.JEREMY);
		androidToasters.add(toasters.PAT);
		_setupToasterScroller(androidToasters, R.id.scroller_androids, onItemClickListener);

		// ios toasters
		ArrayList<ImageToLoad> iosToasters = new ArrayList<ImageToLoad>();
		iosToasters.add(toasters.DIRK);
		iosToasters.add(toasters.DUNCAN);
		iosToasters.add(toasters.GEOFF);
		iosToasters.add(toasters.KEVIN);
		iosToasters.add(toasters.JOSH);
		iosToasters.add(toasters.SCOTT);
		_setupToasterScroller(iosToasters, R.id.scroller_ioesses, onItemClickListener);

		// biz dev toasters
		ArrayList<ImageToLoad> bizDevToasters = new ArrayList<ImageToLoad>();
		bizDevToasters.add(toasters.RACHIT);
		bizDevToasters.add(toasters.SIMON);
		_setupToasterScroller(bizDevToasters, R.id.scroller_bizdevs, onItemClickListener);

		// pm toasters
		ArrayList<ImageToLoad> pmToasters = new ArrayList<ImageToLoad>();
		pmToasters.add(toasters.KAYLA);
		pmToasters.add(toasters.MATT);
		_setupToasterScroller(pmToasters, R.id.scroller_pms, onItemClickListener);

		// designer toasters
		ArrayList<ImageToLoad> designerToasters = new ArrayList<ImageToLoad>();
		designerToasters.add(toasters.ADIT);
		designerToasters.add(toasters.DUSTIN);
		_setupToasterScroller(designerToasters, R.id.scroller_designers, onItemClickListener);

		// all toasters
		ArrayList<ImageToLoad> allToasters = new ArrayList<ImageToLoad>();
		allToasters.addAll(androidToasters);
		allToasters.addAll(iosToasters);
		allToasters.addAll(bizDevToasters);
		allToasters.addAll(pmToasters);
		allToasters.addAll(designerToasters);
		_setupToasterScroller(allToasters, R.id.scroller_all_toasters, onItemClickListener);

		if(savedInstanceState != null) {
			// restore the scroll position of each scroller
			int[] scrollXes = savedInstanceState.getIntArray(KEY_SCROLL_XES);
			for(int i = 0; i < scrollXes.length; i++) {
				_horizontalImageScrollers.get(i).scrollTo(scrollXes[i]);
			}
		}
	}

	private void _setupToasterScroller(ArrayList<ImageToLoad> imagesToLoad, int scrollerResourceId, OnItemClickListener onItemClickListener) {
		HorizontalImageScroller scroller = (HorizontalImageScroller)findViewById(scrollerResourceId);
		HorizontalImageScrollerAdapter adapter = new HorizontalImageScrollerAdapter(MainActivity.this, imagesToLoad);
		adapter.setLoadingImageResourceId(R.drawable.generic_toaster);
		adapter.setImageSize((int) getResources().getDimension(R.dimen.image_size));
		adapter.setDefaultImageFailedToLoadResourceId(R.drawable.generic_toaster);
		scroller.setAdapter(adapter);
		scroller.setOnItemClickListener(onItemClickListener);
		_horizontalImageScrollers.add(scroller);
	}

	private String getGravatarUrl(String hash) {
		StringBuilder builder = new StringBuilder("http://www.gravatar.com/avatar/");
		builder.append(hash)
			.append(".jpg?size=200")
			.append("&rating=g")
			.append("&default=404");
		return builder.toString();
	}

	@Override
	protected void onPause() {
		super.onPause();
		HorizontalImageScroller.unbindImageViews(_horizontalImageScrollers);
	}

	private class ToasterImageToLoadHolder {
		ImageToLoad ADIT = new ToasterToLoadDrawableResource(R.drawable.adit, "Adit Shukla");
		ImageToLoad BRIAN = new ToasterToLoadDrawableResource(R.drawable.brian, "Brian Dupuis");
		ImageToLoad CARLTON = new ToasterToLoadDrawableResource(R.drawable.carlton, "Carlton Whitehead");
		ImageToLoad JEREMY = new ToasterToLoadDrawableResource(R.drawable.jeremy, "Jeremy Ellison");
		ImageToLoad PAT = new ToasterToLoadDrawableResource(R.drawable.pat, "Pat Fives");
		ImageToLoad DIRK = new ToasterToLoadDrawableResource(R.drawable.dirk, "Dirk Smith");
		ImageToLoad DUNCAN = new ToasterToLoadDrawableResource(R.drawable.duncan, "Duncan Lewis");
		ImageToLoad GEOFF = new ToasterToLoadDrawableResource(R.drawable.geoff, "Geoff Mackey");
		ImageToLoad KEVIN = new ToasterToLoadUrl(getGravatarUrl("070948597b3f5b4f1a98cc01e3e3da8a"), "Kevin Conner");
		ImageToLoad JOSH = new ToasterToLoadDrawableResource(R.drawable.josh, "Josh Johnson");
		ImageToLoad SCOTT = new ToasterToLoadDrawableResource(R.drawable.scott, "Scott Penrose");
		ImageToLoad RACHIT = new ToasterToLoadDrawableResource(R.drawable.rachit, "Rachit Shukla");
		ImageToLoad SIMON = new ToasterToLoadUrl(getGravatarUrl("86752d9f14430042c1aad0054081249c"), "Simon Kirk");
		ImageToLoad MATT = new ToasterToLoadUrl(getGravatarUrl("efb9d67132359d3a8cbc3cefd1169eb4"), "Matt Raimundo");
		ImageToLoad KAYLA = new ToasterToLoadUrl(getGravatarUrl("20580e80650dcf07167e9c8779371e16"), "Kayla Bourgeois");
		ImageToLoad DUSTIN = new ToasterToLoadUrl(getGravatarUrl("266f809f5baf5421098c93e73520ad42"), "Dustin Rhodes");
	}

	private interface ToasterToLoad {
		public String getName();
	}

	private class ToasterToLoadUrl extends ImageToLoadUrl implements ToasterToLoad {
		private String _name;

		public ToasterToLoadUrl(String url, String name) {
			super(url);
			_name = name;
			_canCacheFile = true;
		}

		public String getName() {
			return _name;
		}
	}

	private class ToasterToLoadDrawableResource extends ImageToLoadDrawableResource implements ToasterToLoad {
		private String _name;

		public ToasterToLoadDrawableResource(int drawableResourceId, String name) {
			super(drawableResourceId);
			_name = name;
		}

		public String getName() {
			return _name;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// store the scroll position of each scroller
		// (so they can be restored after device configuration change)
		int[] scrollXes = new int[_horizontalImageScrollers.size()];
		for(HorizontalImageScroller scroller : _horizontalImageScrollers) {
			scrollXes[_horizontalImageScrollers.indexOf(scroller)] = scroller.getCurrentX();
		}
		outState.putIntArray(KEY_SCROLL_XES, scrollXes);
	}
}
