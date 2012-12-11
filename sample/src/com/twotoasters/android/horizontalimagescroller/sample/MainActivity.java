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
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoad;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadDrawableResource;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrl;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScroller;
import com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScrollerAdapter;
import com.twotoasters.android.horizontalimagescroller.widget.SelectionToggleOnItemClickListener;

public class MainActivity extends Activity {

	/*
	 *  there are multiple HorizontalImageScroller widgets in this activity, and there are some
	 *  cases where we want to perform the same operations on all of them. if your activity only
	 *  has one instance, keeping a reference is probably not necessary (but may be convenient)
	 */
	private List<HorizontalImageScroller> _horizontalImageScrollers;
	
	// String key for persisting the scrollX position in Bundle objects 
	private static final String KEY_SCROLL_XES = "scrollXes";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_horizontalImageScrollers = new ArrayList<HorizontalImageScroller>();
		
		/*
		 * this is a convenience to map our ImageToLoad objects by name of toaster, making it easy
		 * to reference toasters by name. your app probably doesn't need to use this pattern,
		 * but since this app is "programmer art" with no formal design process, this lessened the
		 * impact on my quality of life when changing the design.
		 */
		ToasterImageToLoadHolder toasters = new ToasterImageToLoadHolder();
		
		/*
		 * demonstrate onItemClick handling. Use the adapter's getItem() method to find the backing
		 * object for the clicked item. Tap a toaster where this listener is set to see a Toast
		 * with the name of the Toaster.
		 */
		OnItemClickListener onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ToasterToLoad toaster = (ToasterToLoad)((HorizontalImageScroller)parent).getAdapter().getItem(position);
				Toast.makeText(MainActivity.this, toaster.getName(), Toast.LENGTH_SHORT).show();
			}
		};

		// android toasters
		ArrayList<ImageToLoad> androidToasters = new ArrayList<ImageToLoad>();
		androidToasters.add(toasters.FRED);
		androidToasters.add(toasters.CARLTON);
		androidToasters.add(toasters.JEREMY);
		androidToasters.add(toasters.PAT);
		_setupToasterScroller(androidToasters, R.id.scroller_androids, onItemClickListener); // more on this method later

		// ios toasters
		ArrayList<ImageToLoad> iosToasters = new ArrayList<ImageToLoad>();
		iosToasters.add(toasters.DIRK);
		iosToasters.add(toasters.DUNCAN);
		iosToasters.add(toasters.GEOFF);
		iosToasters.add(toasters.KEVIN);
		iosToasters.add(toasters.JOSH);
		iosToasters.add(toasters.SCOTT);
		_setupToasterScroller(iosToasters, R.id.scroller_ioesses, onItemClickListener); // isn't it nice when code is reusable?

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
		HorizontalImageScroller scroller = (HorizontalImageScroller)findViewById(R.id.scroller_all_toasters);
		// AllToasters...Adapter provides some additional functionality... keep reading
		HorizontalImageScrollerAdapter adapter = new AllToastersHorizontalImageScrollerAdapter(this, allToasters);
		scroller.setAdapter(adapter);
		/*
		 *  use our handy dandy SelectionToggleOnItemClickListener. It toggles the selection state
		 *  of the clicked item
		 */
		scroller.setOnItemClickListener(new SelectionToggleOnItemClickListener());
		_horizontalImageScrollers.add(scroller);
		
		/*
		 * if onCreate() has been called with a non-null savedInstanceState, then we're recreating
		 * the activity following a device configuration change (such as the user rotated the
		 * device). here we restore the scroll position of each HorizontalImageScroller widget.
		 * if you don't mind losing the scroll position, you don't *have* to do this, but don't
		 * blame me if your users rage-quit over it.
		 */
		if(savedInstanceState != null) {
			// restore the scroll position of each scroller
			int[] scrollXes = savedInstanceState.getIntArray(KEY_SCROLL_XES);
			for(int i = 0; i < scrollXes.length; i++) {
				_horizontalImageScrollers.get(i).scrollTo(scrollXes[i]);
			}
		}
	}
	
	/*
	 * since most of the toaster scrollers have the exact same behavior and appearance (except
	 * for the toasters on display), here we make the setup process reusable.
	 */
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
		/*
		 * when your activity pauses, your activity will release its layout, and in turn the
		 * ImageView objects. however, the HorizontalImageScrollerAdapter may have passed 
		 * references to some of those objects into the ImageCacheManager, which might still
		 * be hanging onto them. if you don't unbind them, they won't be garbage collected.
		 * if you only have one HorizontalImageScroller (and no need for a list), you can
		 * get its adapter, and call its unbindImageViews() directly.
		 * HorizontalImageScroller.unbindImageViews(List<HorizontalImageScroller> scrollers) is
		 * just for convenience if you have multiple scrollers.  
		 */
		HorizontalImageScroller.unbindImageViews(_horizontalImageScrollers);
	}
	
	/*
	 * map the ToasterToLoad* objects to the name of the toaster for easy reference. again, you
	 * probably don't need to use this pattern in your app. i figure multiple hard-coded lists 
	 * sharing the same images is a bit of a contrived use-case.
	 */
	private class ToasterImageToLoadHolder {
		ImageToLoad ADIT = new ToasterToLoadDrawableResource(R.drawable.adit, "Adit Shukla");
		ImageToLoad FRED = new ToasterToLoadUrl(getGravatarUrl("7305841a1b14e179a54a7fd74b808297"), "Fred Medlin");
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
	
	/*
	 * make it easy to get the toaster's name on the Toaster...Url/Drawable subclasses 
	 */
	private interface ToasterToLoad {
		public String getName();
	}
	
	/*
	 * extend the ImageToLoadUrl class with the name of a toaster 
	 */
	private class ToasterToLoadUrl extends ImageToLoadUrl implements ToasterToLoad {
		private String _name;

		public ToasterToLoadUrl(String url, String name) {
			super(url);
			_name = name;
			/*
			 *  ImageToLoadUrl objects won't have their images cached by default. in this case,
			 *  we always want ToasterToLoadUrl to have their images cached, so we set that here.
			 *  there is also a public setter you can use if you want more nuanced control in your
			 *  app. one more piece is required for this to work, though. in your manifest, make 
			 *  sure you have the following uses-permission tags: 
			 *  INTERNET
			 *  WRITE_EXTERNAL_STORAGE 
			 */
			_canCacheFile = true;
		}

		public String getName() {
			return _name;
		}
	}
	
	/*
	 * extend the ImageToLoadDrawableResource with the name of a toaster 
	 */
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
			// deters the wrath of your users 
			scrollXes[_horizontalImageScrollers.indexOf(scroller)] = scroller.getCurrentX();
		}
		outState.putIntArray(KEY_SCROLL_XES, scrollXes);
	}
	
	/*
	 * custom adapter with a custom layout that shows the name of the toaster as a caption, and
	 * provides selection state
	 */
	private class AllToastersHorizontalImageScrollerAdapter extends HorizontalImageScrollerAdapter {

		public AllToastersHorizontalImageScrollerAdapter(Context context, List<ImageToLoad> images) {
			super(context, images);
			_showImageFrame = true;
			_highlightActive = true;
			// substitute our custom layout
			_imageLayoutResourceId = R.layout.alltoasters_horizontal_image_scroller_item;
			_imageSize = (int) getResources().getDimension(R.dimen.image_size);
			_loadingImageResourceId = R.drawable.generic_toaster;
			_defaultImageFailedToLoadResourceId = R.drawable.generic_toaster;
			_frameColor = getResources().getColor(R.color.light_grey);
			_frameOffColor = getResources().getColor(android.R.color.transparent);
			
			/*
			 * if you specify a custom layout, you MUST also specify the _imageIdInLayout and
			 *  _innerWrapperIdInLayout. since we're using a custom layout, keep in mind that 
			 * although the id of the image appears to be the same as the one in the stock layout
			 * at first glance, the sample app is a different "project", so its R class is a 
			 * different package, and the underlying integer id will almost surely differ. 
			 */
			_imageIdInLayout = R.id.image;
			_innerWrapperIdInLayout = R.id.inner_wrapper;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// the parent getView() method does most of the hard work 
			View view = super.getView(position, convertView, parent);
			
			// set the caption to the name of the toaster
			TextView textView = (TextView) view.findViewById(R.id.name);
			textView.setText(((ToasterToLoad) getItem(position)).getName()); 
			
			return view;
		}
	}
}
