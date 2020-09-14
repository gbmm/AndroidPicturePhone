package com.xiaobai.viewimage;

import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class button 
{
	private final static float[] BUTTON_PRESSED = new float[] {       
	    0.0f, 0, 0, 0, 2,       
	       0, 124.0f, 0, 0, 2,       
	       0, 0, 171.0f, 0, 2,       
	       0, 0, 0, 5, 0 };     
	         
	   /**   
	    * °´Å¥»Ö¸´Ô­×´   
	    */     
	private final static float[] BUTTON_RELEASED = new float[] {       
	       1, 0, 0, 0, 0,       
	       0, 1, 0, 0, 0,       
	       0, 0, 1, 0, 0,       
	       0, 0, 0, 1, 0 };   
	
	public static final OnTouchListener touchListener = new OnTouchListener() { 
		   
		  @Override 
		  public boolean onTouch(View v, MotionEvent event) { 
		   if(event.getAction() == MotionEvent.ACTION_DOWN) { 
		    v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_PRESSED)); 
		    v.setBackgroundDrawable(v.getBackground()); 
		   }else if(event.getAction() == MotionEvent.ACTION_UP) { 
		    v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_RELEASED)); 
		    v.setBackgroundDrawable(v.getBackground()); 
		   } 
		   return false; 
		  } 
		}; 
		public static void setButtonStateChangeListener(View v) { 
		  v.setOnTouchListener(touchListener); 
		}

}
