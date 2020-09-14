package com.xiaobai.viewimage;

import android.os.Bundle;
import android.renderscript.Float2;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.app.Dialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EdtDialog extends Dialog implements View.OnClickListener{
	private EditText edtName;
	private EditText edtPhone;
	
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;
 
 
    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    public String infoString;
 
 
    public EdtDialog(Context context) {
        super(context);
        this.mContext = context;
    }
 
 
    public EdtDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }
 
 
    public EdtDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }
 
 
    protected EdtDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }
 
 
    public EdtDialog setTitle(String title){
        this.title = title;
        return this;
    }
 
 
    public EdtDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }
 
 
    public EdtDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }
 
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edt_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }
 
 
    private void initView(){
//        contentTxt = (TextView)findViewById(R.id.edt_name);
//        titleTxt = (TextView)findViewById(R.id.);
    	edtName = (EditText) findViewById(R.id.edt_name);
    	edtPhone = (EditText) findViewById(R.id.edt_phone);
    	
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);
    }
 
 
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(listener != null){
                    listener.onClick(this, false);
                }
                this.dismiss();
                break;
            case R.id.submit:
                if(listener != null){
                	String nameString = edtName.getText().toString();
                	String phoString = edtPhone.getText().toString();
                	if (nameString.equals("姓名") || phoString.equals("手机号") || phoString.length()<=0 
                			||nameString.length()<0 ){
                		Toast.makeText(getContext(), "请录入姓名以及手机号", Toast.LENGTH_LONG).show();
                	}else{
                		infoString = nameString+'_'+phoString;
                		listener.onClick(this, true);
                	}
                }
                break;
        }
    }
 
 
    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}