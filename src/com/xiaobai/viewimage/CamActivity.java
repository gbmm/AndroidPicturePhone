package com.xiaobai.viewimage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.Manifest;
import android.R.string;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CamActivity extends Activity {

	private static final int REQUEST_SCAN_BARCODE_CODE = 1;
    private static final int REQUEST_CAMERA_CODE = 2;
    public static final int SELECT_PHOTO = 3;
    
    private String picFileFullName;
    private Uri uri;
	private ImageView imgView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cam);
		
		imgView = (ImageView) findViewById(R.id.imageIV);
		Button btnCam = (Button) findViewById(R.id.btnCam);
		btnCam.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				clickCam();
			}});
		
		//录入
		Button btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				clickPhoto();
				Toast.makeText(CamActivity.this, "btn cam", Toast.LENGTH_LONG).show();
			}});
		
		//录入
		Button btnSysPic = (Button) findViewById(R.id.btnSysPic);
		btnSysPic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				selectPhoto();
			}});
		
	}
	
	public void selectPhoto(){
	
		 Intent intent = new Intent("android.intent.action.GET_CONTENT");
	     intent.setType("image/*");
	     startActivityForResult(intent, SELECT_PHOTO);
	}
	
	public void clickPhoto(){
		if (picFileFullName == null){
			Toast.makeText(CamActivity.this, "请先拍照或导入图片", Toast.LENGTH_LONG).show();
			return;
		}
		EdtDialog commomDialog = new EdtDialog(CamActivity.this, R.style.dialog, "我是谁，我在哪？ --  VollegeTargetActivity", new EdtDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
            	if (confirm && ((EdtDialog)dialog).infoString.length() > 0){
            		// 复制图片
            		if (copyFile(picFileFullName,((EdtDialog)dialog).infoString +".jpg")){
            			Toast.makeText(CamActivity.this,"录入成功", Toast.LENGTH_LONG).show();
            		}else{
            			Toast.makeText(CamActivity.this, "录入失败", Toast.LENGTH_LONG).show();
            		}
            		 
            	}
            	
                dialog.dismiss();
            }
        });
		commomDialog.setTitle("提示").show();
	}
	
	
	public boolean copyFile(String oldPathName, String newName) {
	    try {
	        File oldFile = new File(oldPathName);
	        /* 如果不需要打log，可以使用下面的语句*/
	        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
	            return false;
	        }
	        
	        String dir = Environment.getExternalStorageDirectory().toString() +"/phonebook";
	        String newPathName = dir + File.separator + newName;
	        
	        File newImage=new File(newPathName);
             if(newImage.exists()){
            	 newImage.delete();
             }
             
	        FileInputStream fileInputStream = new FileInputStream(oldPathName);    //读入原文件
	        FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
	        byte[] buffer = new byte[1024];
	        int byteRead;
	        while ((byteRead = fileInputStream.read(buffer)) != -1) {
	            fileOutputStream.write(buffer, 0, byteRead);
	        }
	        fileInputStream.close();
	        fileOutputStream.flush();
	        fileOutputStream.close();
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public void clickCam(){
		 File outImage=new File(Environment.getExternalStorageDirectory(),"/output_image.jpg");
         try{
             if(outImage.exists())
             {
                 outImage.delete();
             }
             outImage.createNewFile();
         }
         catch (IOException e)
         {
             e.printStackTrace();
         }
         picFileFullName = outImage.getAbsolutePath();
         //uri = getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//         Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		 startActivityForResult(intent, REQUEST_CAMERA_CODE);
         Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
         intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outImage));
         startActivityForResult(intent,REQUEST_CAMERA_CODE);
         Log.v("debug", "======"+picFileFullName);
		 Toast.makeText(CamActivity.this, "btn cam", Toast.LENGTH_LONG).show();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imgView.setImageBitmap(photo);
            //获得图片
        	Log.v("debug", "======"+picFileFullName);
            Bitmap bp = getBitMapFromPath(picFileFullName);
            imgView.setImageBitmap(bp);
            Toast.makeText(CamActivity.this, picFileFullName, Toast.LENGTH_LONG).show();
        }else if(requestCode == SELECT_PHOTO){
        	 if (resultCode == RESULT_OK) {
                 //判断手机系统版本号
                 if (Build.VERSION.SDK_INT > 19) {
                     //4.4及以上系统使用这个方法处理图片
                	 picFileFullName = handleImgeOnKitKat(data);
                	 Log.e("debug", "===photo==="+picFileFullName);
                	 Toast.makeText(CamActivity.this, picFileFullName, Toast.LENGTH_LONG).show();
                     Bitmap bp = getBitMapFromPath(picFileFullName);
                     imgView.setImageBitmap(bp);
                 }
             }
        }
    }
	
	 @TargetApi(Build.VERSION_CODES.KITKAT)
	 private String handleImgeOnKitKat(Intent data) {
	        String imagePath = null;
	        Uri uri = data.getData();
	        imagePath = getImagePath(uri,null);
	        Log.e("debug", "uri.imagePath() = "+imagePath);
	        Log.e("debug", "uri.getAuthority() = "+uri.getAuthority());
        	Log.e("debug", "uri.getScheme() = "+uri.getScheme());
        	
        	if (DocumentsContract.isDocumentUri(this,uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID+"="+id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                    imagePath = getImagePath(contentUri,null);
                }
            }else if ("content".equalsIgnoreCase(uri.getScheme())){
            	imagePath = getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
            	imagePath = uri.getPath();
            }

			return imagePath;
	    }

	 
	 /**
	     * 通过uri和selection来获取真实的图片路径
	     * */
	 private String getImagePath(Uri uri,String selection) {
	        String path = null;
	        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
	        if (cursor != null) {
	            if (cursor.moveToFirst()) {
	                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
	            }
	            cursor.close();
	        }
	        return path;
	    }

	/* 获得图片，并进行适当的 缩放。 图片太大的话，是无法展示的。 */
    private Bitmap getBitMapFromPath(String imageFilePath) {
         Display currentDisplay = getWindowManager().getDefaultDisplay();

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
}
