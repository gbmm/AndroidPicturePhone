package com.xiaobai.viewimage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<MyImageView> imageViews = new ArrayList<MyImageView>();

	private ImageCacheManager imageCache;

	private List<String> mItems;

//	private Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			Log.v("debug","msg--"+msg.what);
//			Bitmap bitmap = (Bitmap) msg.obj;
//			Bundle bundle = msg.getData();
//			String url = bundle.getString("url");
//			for (int i = 0; i < imageViews.size(); i++) {
//				if (imageViews.get(i).getTag().equals(url)) {
//					imageViews.get(i).setImageBitmap(bitmap);
//				}
//			}
//		}
//	};

	public void setData(List<String> data) {
		this.mItems = data;
		notifyDataSetChanged();
	}

	public GalleryAdapter(Context context) {
		this.context = context;
		imageCache = ImageCacheManager.getInstance(context);
	}

	@Override
	public int getCount() {
		return mItems != null ? mItems.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
//		//---------------------------------------------------------------------
		MyImageView view = null;
		try {
			view = imageViews.get(position);
		} catch (Exception e) {
			view = null;
		}
		 //new MyImageView(context);
		
		if(view ==null){
			view = new MyImageView(context);
			view.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			String item = mItems.get(position);
			Log.d("debug", "滑动---滑动1");
			if (item != null) {
				Bitmap bmp;
				try {
					//bmp = imageCache.get(item);
					Log.d("debug", "-----1--- "+item);
					bmp = imageCache.getGbFile(item);
					
					view.setTag(item);
					if (bmp != null) {
						view.setImageBitmap(bmp);
						view.setImageText(item);
					} 
					if (!this.imageViews.contains(view)) {
						imageViews.add(view);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else{
			String item = mItems.get(position);
			Log.d("debug", "滑动---滑动2");	
			if (item != null) {
				Log.d("debug", "-----2--- "+item);
				Bitmap bmp;
				try {
					bmp = imageCache.getGbFile(item);
					view.setTag(item);
					if (bmp != null) {
						view.setImageBitmap(bmp);
						view.setImageText(item);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return view;
	}

    static class ViewHolder { 
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public boolean isComMsg = true;
        public boolean BOT = true;
    }

}
