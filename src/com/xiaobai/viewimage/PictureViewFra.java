package com.xiaobai.viewimage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.R.string;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


	
public class PictureViewFra extends Fragment implements
		LoaderCallbacks<List<String>> {
	private static final String TAG = "PictureViewFra";
	private PicGallery gallery;
	private Button btn_phone,btn_msg;
	private TextView tName,tPhone;
	// private ViewGroup tweetLayout; // 弹层
	private boolean mTweetShow = false; // 弹层是否显示

	private GalleryAdapter mAdapter;

	// private ProgressDialog mProgress;

	public GalleryAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		    View view = inflater.inflate(R.layout.picture_view, null);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tName = (TextView) view.findViewById(R.id.textViewName);
		tPhone = (TextView) view.findViewById(R.id.textViewPhone);
		btn_phone = (Button)view.findViewById(R.id.btn_phone);
		btn_msg = (Button)view.findViewById(R.id.btn_sendmsg);
		
		btn_phone.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ClickBtnPhone();
			}
		});
		
		btn_msg.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ClickBtnMsg();
			}
		});
		
		Button btn_cam = (Button)view.findViewById(R.id.btn_cam);
		btn_cam.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(); 
				intent.setClass(getActivity(), CamActivity.class);
				startActivityForResult(intent, 1);
				
			}
		});
		
		
		Button btn_wifi = (Button)view.findViewById(R.id.btn_wifi);
		btn_wifi.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				WifiManager wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(true);
				Toast.makeText(getActivity(), "已经打开wifi",Toast.LENGTH_LONG).show();
			}
		});
		
		Button btn_ring = (Button)view.findViewById(R.id.btn_ring);
		btn_ring.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
//				PowerManager pManager=(PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);    
//				pManager.reboot(null);//重启
				
				AudioManager mAudioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
				int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_RING );
				int current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING );
				mAudioManager.setStreamVolume(AudioManager.STREAM_RING , max, 1);
				
				max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL );
				mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL , max, 1);
				
				try {
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
		                AudioManager.VIBRATE_SETTING_ON);
				mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
		                AudioManager.VIBRATE_SETTING_ON);}
				catch(Exception e) {
					Toast.makeText(getActivity(), e.toString(),Toast.LENGTH_LONG).show();
				}
		        
				MediaPlayer mp = new MediaPlayer();
				try {
					mp.setDataSource(getActivity(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
					mp.prepare();
					mp.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// Toast.makeText(getActivity(), " "+max+','+ current,Toast.LENGTH_SHORT).show();
			}
		});
		
		//btn_phone.setOnTouchListener(button.touchListener);
		//btn_msg.setOnTouchListener(button.touchListener);
		gallery = (PicGallery) view.findViewById(R.id.pic_gallery);
		gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
		gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
		gallery.setPadding(10, 0, 10, 0);
		gallery.setDetector(new GestureDetector(getActivity(),
				new MySimpleGesture()));
		mAdapter = new GalleryAdapter(getActivity());
		gallery.setAdapter(mAdapter);
		gallery.setOnItemSelectedListener(new ProvOnItemSelectedListener());

//		gallery.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				Toast.makeText(getActivity(), "LongClick唤起复制、保存操作",
//						Toast.LENGTH_SHORT).show();
//				return false;
//			}
//		});
		getLoaderManager().initLoader(0, null, this);

		// mProgress = ProgressDialog.show(getActivity(),
		// null,getActivity().getString(R.string.loading));
	}
	
	//OnItemSelected监听器
	private class  ProvOnItemSelectedListener implements OnItemSelectedListener{		
		@Override
		public void onItemSelected(AdapterView<?> adapter,View view1,int position,long id) {
			//获取选择的项的值
			try {
				View view = gallery.getSelectedView();
				if (view instanceof MyImageView) {
					MyImageView imageView = (MyImageView) view;
					
					String str = imageView.textimg;
					String tmp[] = str.split(File.separator);
					String ph[] = tmp[tmp.length -1].split("_"); 
					tName.setText(ph[0]);
					tPhone.setText(ph[1].replace(".jpg", ""));
				}

			} catch (Exception e2) {
				// TODO: handle exception
			}		
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			String sInfo="什么也没选！";
			Toast.makeText(getActivity(),sInfo, Toast.LENGTH_LONG).show();
			
		}
	}
	
	 @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        Log.e("debug", "执行onActivityResult");
        getLoaderManager().restartLoader(0, null, this);
    }

	 
	void ClickBtnPhone()
	{
		//String name = getContactNameFromPhoneBook(this.getActivity(),"张红梅");
		//Log.i("debug", "name+-"+name);
		View view = gallery.getSelectedView();
		if (view instanceof MyImageView) {
			MyImageView imageView = (MyImageView) view;
			
//			String str = imageView.textimg;
//			String list[] = str.split("/");
//			String tmp1 = list[list.length-1].split("\\.")[0];
//			String phone = getContactNameFromPhoneBook(this.getActivity(),tmp1);
//			Log.v("debug", phone);
			String phone = tPhone.getText().toString(); 
			if(phone.length()>0){
				 //用intent启动拨打电话  
	            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));  
	            startActivity(intent);  
			}
		}
	}
	
	void ClickBtnMsg()
	{
		try
		{
			View view = gallery.getSelectedView();
			if (view instanceof MyImageView) {
				MyImageView imageView = (MyImageView) view;
				
				String str = imageView.textimg;
				String list[] = str.split("/");
				String tmp1 = list[list.length-1].split("\\.")[0];
				String phone = getContactNameFromPhoneBook(this.getActivity(),tmp1);
				Log.v("debug", phone);
				if(phone.length()>0){
					 //用intent启动拨打电话  
		            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));  
		            intent.putExtra("sms_body", "给我打一个电话！13896882792"); 
		            startActivity(intent);  
				}
				
			}
		}
		catch(Exception ex)
		{
			Toast.makeText(this.getActivity(), "出错！",Toast.LENGTH_SHORT).show();
		}
		
	}

	public String getContactNameFromPhoneBook(Context context, String phoneNum) {
		String contactName = "";
		ContentResolver cr = context.getContentResolver();
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
				new String[] { phoneNum }, null);
		if (pCur.moveToFirst()) {
			contactName = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			pCur.close();
		}
		return contactName;
	}
	
	private class MySimpleGesture extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {

			View view = gallery.getSelectedView();
			if (view instanceof MyImageView) {
				MyImageView imageView = (MyImageView) view;
				if (imageView.getScale() > imageView.getMiniZoom()) {
					imageView.zoomTo(imageView.getMiniZoom());
				} else {
					imageView.zoomTo(imageView.getMaxZoom());
				}

			} else {

			}
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// Logger.LOG("onSingleTapConfirmed",
			// "onSingleTapConfirmed excute");
			// mTweetShow = !mTweetShow;
			// tweetLayout.setVisibility(mTweetShow ? View.VISIBLE
			// : View.INVISIBLE);
			try {
				View view = gallery.getSelectedView();
				if (view instanceof MyImageView) {
					MyImageView imageView = (MyImageView) view;
					
					String str = imageView.textimg;
					String tmp[] = str.split(File.separator);
					String ph[] = tmp[tmp.length -1].split("_"); 
					tName.setText(ph[0]);
					tPhone.setText(ph[1].replace(".jpg", ""));
				}
				return true;
			} catch (Exception e2) {
				// TODO: handle exception
				return true;
			}
			
		}
	}

	@Override
	public Loader<List<String>> onCreateLoader(int arg0, Bundle arg1) {
		Logger.LOG("this", "onCreateLoader");
		return new PictureLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<String>> arg0, List<String> arg1) {
		Logger.LOG("debug", "-------onLoadFinished");
		mAdapter.setData(arg1);
		// mProgress.dismiss();
	}

	@Override
	public void onLoaderReset(Loader<List<String>> arg0) {
		Logger.LOG(this, "onLoaderReset");
		mAdapter.setData(null);
	}


}
