package com.qrobot.mobilemanager.pet;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qrobot.mobilemanager.MainActivity;
import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.pet.login.QRClientManager;
import com.qrobot.mobilemanager.pet.login.QrRobotInfo;

public class PetFragment extends Fragment {
	
	private static final String SHAREPREFERENCE_NAME = "set_pet";
	private LinearLayout petLayout;

	private Context mContext;
	
	private TextView setInfoView;
	
	private TextView nicknamView;
	
	private ImageView portraitView;
	
	private String loginInfo;
	
	private Pet pet;
	
	public PetFragment(Context context,String loginInfo){
		mContext = context;
		this.loginInfo = loginInfo;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		petLayout = (LinearLayout)inflater.inflate(R.layout.pet_main, null);
		setInfoView = (TextView)petLayout.findViewById(R.id.pet_information_set);
		nicknamView = (TextView)petLayout.findViewById(R.id.pet_nickname);
		portraitView = (ImageView)petLayout.findViewById(R.id.pet_portrait);
		
		return petLayout;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Thread  getThread = new Thread(){
			public void run(){
				Looper.prepare();
				getServerPetInfo();
			}
		};
		
		getThread.start();
//		initSet();
	}
    
	private void initSet(){
        SharedPreferences petSP = mContext.getSharedPreferences(SHAREPREFERENCE_NAME, 1);
        String nickname = petSP.getString("nickname", "xiao Q");
        String portrait = petSP.getString("portrait", "portrait/robot_male.png");
        pet = new Pet();
        pet.nickname = nickname;
        pet.portrait = portrait;
        
        nicknamView.setText(nickname);
        
//        portraitView.setImageResource(drawableID);
        Bitmap bitmap = getPortraitBitmap("portrait/"+portrait);
        if (bitmap != null) {
        	portraitView.setImageBitmap(bitmap);
		}else {
			portraitView.setImageResource(R.drawable.ic_launcher);
			
		}
        
		setInfoView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SetPet.class);
				intent.putExtra("pet_info", pet);
				startActivity(intent);
			}
		});
	}
	
	
	private Bitmap getPortraitBitmap(String fileName){
		AssetManager am = mContext.getResources().getAssets();
		InputStream is;
		try {
			is = am.open(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean getServerPetInfo(){
		QRClientManager clientManager = new QRClientManager();
		boolean check = clientManager.checkToLogin(loginInfo);
		if (!check) {
			mHandler.obtainMessage(-1).sendToTarget();
		} else {
			QrRobotInfo petInfo = clientManager.getPetInfo();
			if (petInfo == null) {
				return false;
			}
	        SharedPreferences petSP = mContext.getSharedPreferences(SHAREPREFERENCE_NAME, 1);
	        Editor petEditor = petSP.edit();
	        petEditor.putString("nickname", petInfo.getNickName());
	        petEditor.putString("portrait", petInfo.getImage());
	        Log.w("get", petInfo.getNickName()+"*"+petInfo.getImage()+"*"+petInfo.getLevel());
	        petEditor.commit();
	        mHandler.obtainMessage(1).sendToTarget();
	        return true;
		}
		
		return false;
		
	}
	
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				initSet();
				
				break;
			case 2:
				
				break;
			case -1:
				Toast.makeText(mContext, "对不起，宠物信息更新失败，请重试！", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
    	
    };
}
