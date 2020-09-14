package com.xiaobai.viewimage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class PictureLoader extends AsyncTaskLoader<List<String>> {
	private List<String> dataResult;
	private boolean dataIsReady;
	private static final String PICTURE = "pics";

	public PictureLoader(Context context) {
		super(context);
		Log.v("debug","-----11-------"+dataIsReady);
		/*if (dataIsReady) {
			//Log.debug()
		//	deliverResult(dataResult);
		} else {
			forceLoad();
		}*/
		forceLoad();
		Log.v("debug","-----22-------"+dataIsReady);
	}

	@Override
	public List<String> loadInBackground() {
		Log.v("debug","-----33-------"+dataIsReady);
		List<String> list = new ArrayList<String>();
		try {
			
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				Log.v("debug","-----dir1-------有sd卡");
			}
			else {
				Log.v("debug","-----dir1-------没有sd卡");
			}
			
			//String dir = Environment.getExternalStorageDirectory().toString() +"/phone";//Environment.getExternalStorageDirectory().toString()+"/gbphone";//"/storage/sdcard1/gbphone";//
			//String dir = "/storage/emulated/0/DCIM/Sgame";
			String dir = Environment.getExternalStorageDirectory().toString() +"/phonebook";
			Log.v("debug","-----dir-------"+dir);
			File mfile = new File(dir);
			if (!mfile.exists()){
				mfile.mkdirs();
				Log.v("debug", "创建成功 文件夹");
			}
			File[] files = mfile.listFiles();
			Log.v("debug","-----dir-------"+dir+","+files.length);
			//String[] flLists =  //getContext().getAssets().list(PICTURE);
			for(File f:files){
				String file = f.getPath();
				Log.i("debug",file);
				if (file.endsWith(".jpg") || file.endsWith(".png")) {
					list.add(file);
				}
			}
			/*
			String[] flLists =  getContext().getAssets().list(PICTURE);
			for (String file : flLists) {
				if (file.endsWith(".jpg") || file.endsWith(".png")) {
					list.add(ImageCacheManager.ASSETS_PATH_PREFIX + PICTURE
							+ "/" + file);
				}
			}*/
  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("debug",e.toString());
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
	}

	@Override
	protected void onStartLoading() {
		// 显示加载条
		Logger.LOG(this, "onStartLoading");
		super.onStartLoading();
	}

	@Override
	protected void onStopLoading() {
		// 隐藏加载条
		Logger.LOG(this, "onStopLoading");
		super.onStopLoading();
	}

	@Override
	public boolean takeContentChanged() {

		return super.takeContentChanged();
	}

}
