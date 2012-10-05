HorizontalImageScroller-Android
===============================

HorizontalImageScroller-Android is a UI widget library for Android that will let your users scroll through a horizontal list of images, and let you implement it without breaking a sweat.

Features:

- Specify images by drawable resource IDs and/or URL strings
- Optionally cache URL images
- Image sampling for efficient memory use on older devices
- Supports Android API level 8 (version 2.2 Froyo) and higher

Quick Start:

1. Add the HorizontalImageScroller-Android library to your Eclipse workspace as a new Android project from existing source.
2. On your Android project, add the HorizontalImageScroller-Android library to your project's list of libraries. (Project -> Properties -> Android -> Library -> Add)
3. In a layout XML where you'd like to place a HorizontalImageScroller, add a HorizontalImageScroller element like so:
```xml
    <com.twotoasters.android.horizontalimagescroller.widget.HorizontalImageScroller 
        android:id="@+id/my_horizontal_image_scroller"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

4. Set up the HorizontalImageScroller with a HorizontalImageScrollerAdapter and give it a list of ImageToLoad objects like so: 
```java
    import com.twotoasters.android.horizontalimagescroller.image.*;
    import com.twotoasters.android.horizontalimagescroller.widget.*;
    public class MyActivity extends Activity {
        public void onCreate(Bundle savedInstanceState) {
            // do all your activity setup stuff, blah blah
            super.onCreate(savedInstanceState);
            setContentView(R.layout.my_activity);
    
            // make a list of ImageToLoad objects
            ArrayList<ImageToLoad> images = new ArrayList<ImageToLoad>();
            for (int i=0; i<20; i++) {
                images.add(new ImageToLoadUrl("http://link.to/some-awesome-image.jpg")); // substitute some pretty picture you can stand to see 20 times in a list
                images.add(new ImageToLoadDrawableResource(R.drawable.some_drawable)); // plug in some of your own drawables
            }
    
            // set up the scroller with an adapter populated with the list of ImageToLoad objects
            HorizontalImageScroller scroller = (HorizontalImageScroller) findViewById(R.id.my_horizontal_image_scroller);
            scroller.setAdapter(new HorizontalImageScrollerAdapter(images));
        }
    }
```

Tips:


If you need to place the horizontal image scroller in a vertical scroll view, use a ```<com.twotoasters.android.horizontalimagescroller.widget.VerticalScrollView />```.

If you have several HorizontalImageScroller views that are being driven by subsets of a master list of ImageToLoad objects, and you want to set an OnItemClickListener on each of them, you won't be able to get the item by its position in the master list, because its position in the subset list might differ. You can subclass the ImageToLoadUrl and/or ImageToLoadDrawableResource classes with extra properties to hold whatever data you need. In your OnItemClickListener, you can get the item by its position from the adapter, and then refer to the extra properties in the subclass you passed in. See the ToasterToLoadUrl, ToasterToLoadDrawableResource, and ToasterToLoad inner classes/interface in the included sample app's MainActivity.

For the best user experience, don't size your images such that they are wider than the viewport of the scroller.

If you are going to use the ImageToLoadUrl class, add the following uses-permission to your ```AndroidManifest.xml```
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

If you want to enable caching (drastically reduces load times at the expense of some disk space), add the following uses-permission to your ```AndroidManifest.xml```
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
On any ImageToLoadUrl objects that you want cached, call 
```java
myImageToLoadUrl.setCanCacheFile(true);
```