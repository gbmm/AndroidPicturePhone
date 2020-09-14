package com.xiaobai.viewimage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

public class PictureViewActivity extends FragmentActivity {

	// 屏幕宽度
	public static int screenWidth;
	// 屏幕高度
	public static int screenHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.picture_view_activity);
		initViews();
		closeAndroidPDialog();
	}

	private void initViews() {

		screenWidth = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();

	}
	
	 private void closeAndroidPDialog(){
	        try {
	            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
	            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
	            declaredConstructor.setAccessible(true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
	            Class cls = Class.forName("android.app.ActivityThread");
	            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
	            declaredMethod.setAccessible(true);
	            Object activityThread = declaredMethod.invoke(null);
	            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
	            mHiddenApiWarningShown.setAccessible(true);
	            mHiddenApiWarningShown.setBoolean(activityThread, true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}