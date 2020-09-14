package com.xiaobai.viewimage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;

public class ImageCacheManager extends KVCache<String, Bitmap> {
	private static final String TAG = "ImageCacheManager";
	private static final boolean DEBUG = false;

	public static final String ASSETS_PATH_PREFIX = "file:///android_asset/";

	private static final int THREAD_POOL_SIZE = 5;
	
	public Context context1;

	public static enum ImageShape {
		NORMAL, CIRCLE, ROUND_CORNER, RECT_CORNER
	};

	private static ImageCacheManager sImageCache;

	private final byte[] lock = new byte[0];

	/**
	 * Record the downloading queue, in order to avoid downloading several same
	 * images at the same time.
	 */
	private final Set<String> downloadingQueue = new HashSet<String>(
			THREAD_POOL_SIZE);

	/**
	 * CompressFormat of Bitmap, ie jpeg or png file format. Default is jpeg.
	 */
	private CompressFormat mCompressFormat = CompressFormat.JPEG;

	/**
	 * Quality of bitmap when compress. Default is 100;
	 */
	private int mQuality = 100;

    public Bitmap bmm = null;
    public Bitmap bmm2 = null;
    public Bitmap bmm3 = null;
    
	private ImageCacheManager(Context context) {
		super(context);
		context1 = context;
	}

	public synchronized static ImageCacheManager getInstance(Context context) {
		if (sImageCache == null) {
			sImageCache = new ImageCacheManager(context);
		}

		return sImageCache;
	}

	/**
	 * Some bitmap should be saved as png format. Same as using
	 * {@link #setCompressFormat(CompressFormat)} to set format, then using
	 * {@link #put(String, Bitmap)} to put bitmap into cache, then set
	 * compressFormat back to default
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void putAsPng(String key, Bitmap bitmap) {
		putAsPng(key, bitmap, null);
	}

	public void putAsPng(String key, Bitmap bitmap, ImageShape shape) {
		try {
			if (key == null || bitmap == null) {
				return;
			}

			final String hashKey = getHash(encodeUrlWidthShape(key, shape));
			
			// put the object in memory cache.
			mMemCache.put(hashKey, bitmap);

			// put the object in local disk cache.
			File f = new File(mContext.getCacheDir(), hashKey);
			if (f.exists() || f.createNewFile()) {
				FileOutputStream fos = new FileOutputStream(f);
				toLocalDiskWithPng(hashKey, bitmap, fos);
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			LOGD("put bitmap with png format into image cache error: "
					+ e.getMessage());
		}
	}

	public Bitmap getFromMem(String url) {
		return getFromMem(url, null);
	}

	public Bitmap getFromMem(String url, ImageShape shape) {
		return fromMemCache(encodeUrlWidthShape(url, shape));
	}

	@Override
	public boolean containsInMem(String key) {
		return containsInMem(key, null);
	}

	public boolean containsInMem(String key, ImageShape shape) {
		return super.containsInMem(encodeUrlWidthShape(key, shape));
	}

	@Override
	public boolean contains(String key) {
		return contains(key, null);
	}

	public boolean contains(String key, ImageShape shape) {
		return super.contains(encodeUrlWidthShape(key, shape));
	}

	@Override
	public boolean clearMemCache(String key) {
		return clearMemCache(key, null);
	}

	public boolean clearMemCache(String key, ImageShape shape) {
		return super.clearMemCache(encodeUrlWidthShape(key, shape));
	}

	@Override
	public void put(String key, Bitmap value) throws IOException {
		put(key, value, null);
	}

	public void put(String key, Bitmap value, ImageShape shape)
			throws IOException {
		super.put(encodeUrlWidthShape(key, shape), value);
	}

	@Override
	public Bitmap get(String url) throws IOException {
		return getFromLocalOrNetwork(url, true, null, null);
	}

	public Bitmap get(String url, ImageShape shape) throws IOException {
		return getFromLocalOrNetwork(url, true, shape, null);
	}

	public Bitmap get(String url, boolean isAutoSave) throws IOException {
		return getFromLocalOrNetwork(url, isAutoSave, null, null);
	}

	public Bitmap getGbFile(String path)
	{
//		if(bmm!=null){
//			bmm.recycle();
//			bmm = null;
//		}
		Bitmap tmp = mMemCache.get(path);
		if(tmp == null){
			tmp = getBitMapFromPath(path);
			mMemCache.put(path, tmp);
		}
		return tmp;
//		mMemCache.put(path, bitmap);
//		bmm = getBitMapFromPath(path);//BitmapFactory.decodeFile(path);
//		return bmm;
	}
	
	 private Bitmap getCompressImageFromPath(String filepath){
		 BitmapFactory.Options options = new BitmapFactory.Options();
		 Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
		 //首先先解码图片文件，这里 options 的属性都是默认的
		 ByteArrayOutputStream outStream = new ByteArrayOutputStream();//准备用来存放压缩图片的位数组
		 bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);//这里就是压缩关键，第一个入参是选择以哪种图片格式进行压缩，jpeg 是不带透明度的，所以如果以 jpeg 格式进行压缩，那 options.inPreferredConfig 就可以设置为 RGB_565，如果是 png，那质量不会损失，第二个入参是无效的。第二个入参就是质量数 0~100 之间，值越大质量越好。
		 ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		 Bitmap result = BitmapFactory.decodeStream(inStream, null, null);//解码数据流的方式，将图片解码
		 return result;
	 }

	 private Bitmap getBitMapFromPath(String imageFilePath) {
         Display currentDisplay = ((Activity)context1).getWindowManager().getDefaultDisplay();

         int dw = currentDisplay.getWidth();

         int dh = currentDisplay.getHeight();

         // Load up the image's dimensions not the image itself

         BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

         bmpFactoryOptions.inJustDecodeBounds = true;

         Bitmap bmp = BitmapFactory.decodeFile(imageFilePath,

                 bmpFactoryOptions);

         int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight

                 / (float) dh);

         int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth

                 / (float) dw);



         // If both of the ratios are greater than 1,

         // one of the sides of the image is greater than the screen

         if (heightRatio > 1 && widthRatio > 1) {

             if (heightRatio > widthRatio) {

                 // Height ratio is larger, scale according to it

                 bmpFactoryOptions.inSampleSize = heightRatio;

             } else {

                 // Width ratio is larger, scale according to it

                 bmpFactoryOptions.inSampleSize = widthRatio;

             }

         }

         // Decode it for real

         bmpFactoryOptions.inJustDecodeBounds = false;

         bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

         return bmp;

     }
	 
	public void delImage(){
		
	}
		
	public Bitmap getFromLocalOrNetwork(String url, boolean isAutoSave,
			ImageShape shape, ImageLoadCallback callback) throws IOException {
		if (url == null) {
			return null;
		}
		//Bitmap bitmap = BitmapFactory.decodeFile(url);
		Bitmap bitmap = super.get(encodeUrlWidthShape(url, shape));
		boolean fromCache = true;

		if (bitmap == null) {
			fromCache = false;
			// url starts with http or https generally, ftp or else not
			// supported yet.
			if (url.startsWith(ASSETS_PATH_PREFIX)) {
				// from assets dir
				bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(
						url.substring(ASSETS_PATH_PREFIX.length())));
			} else {
				// from local file path
				// There maybe OOM for large image(low possibility). But for
				// better performance, we don't consider this yet.
				bitmap = BitmapFactory.decodeFile(url);
			}

			if (bitmap != null && isAutoSave) {
				put(url, bitmap, null);
			}
		}

		if (callback != null) {
			callback.onImageLoaded(url, bitmap, fromCache);
		}

		return bitmap;
	}


	/**
	 * (non-javadoc)
	 * 
	 * @see KVCache#fromLocalDisk(Object, InputStream)
	 */
	@Override
	protected Bitmap fromLocalDisk(String key, InputStream is)
			throws IOException {
		try {
			return BitmapFactory.decodeStream(is);
		} catch (OutOfMemoryError error) {
			throw new IOException("bitmap not loaded");
		}
	}

	/**
	 * (non-javadoc)
	 * 
	 * @see KVCache#toLocalDisk(Object, Object, OutputStream)
	 */
	@Override
	protected void toLocalDisk(String key, Bitmap value, OutputStream os) {
		value.compress(CompressFormat.JPEG, mQuality, os);
	}

	protected void toLocalDiskWithPng(String key, Bitmap value, OutputStream os) {
		value.compress(CompressFormat.PNG, mQuality, os);
	}

	/**
	 * Callback when finished loading image.
	 */
	public static interface ImageLoadCallback {
		/**
		 * Called when loading success.
		 * 
		 * @param url
		 * @param bitmap
		 */
		public void onImageLoaded(String url, Bitmap bitmap, boolean fromCache);

		/**
		 * Called when error happened.
		 * 
		 * @param message
		 */
		public void onImageLoadError(String message);
	}

	public CompressFormat getCompressFormat() {
		return mCompressFormat;
	}

	public void setCompressFormat(CompressFormat mCompressFormat) {
		this.mCompressFormat = mCompressFormat;
	}

	public int getQuality() {
		return mQuality;
	}

	public void setQuality(int mQuality) {
		this.mQuality = mQuality;
	}

	private String encodeUrlWidthShape(String url, ImageShape shape) {
		if (url == null) {
			return null;
		}

		if (shape == null) {
			return url;
		}

		StringBuilder sb = new StringBuilder(url);

		switch (shape) {
		case CIRCLE:
			sb.append("-circle");
			break;
		case ROUND_CORNER:
			sb.append("-round");
			break;
		case RECT_CORNER:
			sb.append("--rect");
			break;
		default:
			break;
		}

		return sb.toString();
	}

	private void LOGD(String message) {
		if (DEBUG) {
			Log.i(TAG, message);
		}
	}

}
