package com.qrobot.mobilemanager;

import com.qrobot.mobilemanager.datalistener.LoginDataListener;
import com.qrobot.mobilemanager.netty.NettyClientManager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeFragment extends Fragment {
	
	private RelativeLayout welcomeLayout;

	private Context mContext;
	
	private TextView helloView;
	
	private LinearLayout loginLayout;
	
	private EditText idText;
	
	private Button loginButton;
	private Button logoutButton;
	private static NettyClientManager nClientManager;
	
	public WelcomeFragment(Context context,NettyClientManager clientManager){
		mContext = context;
		nClientManager = clientManager;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		welcomeLayout = (RelativeLayout)inflater.inflate(R.layout.activity_main, null);
		
		loginLayout = (LinearLayout)welcomeLayout.findViewById(R.id.login);
		helloView = (TextView)welcomeLayout.findViewById(R.id.hello_world);
		idText = (EditText)welcomeLayout.findViewById(R.id.edit_login);
		loginButton = (Button)welcomeLayout.findViewById(R.id.btn_login);
		logoutButton = (Button)welcomeLayout.findViewById(R.id.btn_logout);
		
		loginButton.setOnClickListener(loginListener);
		logoutButton.setOnClickListener(logoutListener);
		
		return welcomeLayout;
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
	}
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				loginLayout.setVisibility(View.GONE);
				helloView.setText(R.string.login_success);
				logoutButton.setVisibility(View.VISIBLE);
				MainActivity.setLogin(true);
				
				break;
			case 2:
				logoutButton.setVisibility(View.GONE);
				loginLayout.setVisibility(View.VISIBLE);
				helloView.setText(R.string.hello_world);
				MainActivity.setLogin(false);
				break;
			case -1:
				toastShow("登陆失败，请重试！");
				MainActivity.setLogin(true);
				break;
			default:
				break;
			}
		}
    	
    };
	
	private OnClickListener loginListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final String id = idText.getText().toString();
			if (id == null || id.trim().length() == 0 ) {
				toastShow("您的输入为空或不合法，请重新输入！");
				return;
			}
			
			Thread loginThread = new Thread(){
					public void run(){
						Looper.prepare();
						try {
							int loginId = Integer.parseInt(id.trim());
							print("login id:"+loginId);
							int bLogin = nClientManager.login(loginId);
							nClientManager.setLoginDataListener(loginDataListener);
							
							print("login id:"+bLogin);
							
						} catch (Exception e) {
							e.printStackTrace();
							toastShow("您的输入为空或不合法，请重新输入！");
						}
						
					}
				};
			
				loginThread.start();
			
		}
	};
	
	private OnClickListener logoutListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (nClientManager != null) {
				int ret = nClientManager.logout();
				if (ret == 1) {
					mHandler.obtainMessage(2).sendToTarget();
				}
			}
			
		}
	};
	
	private LoginDataListener loginDataListener = new LoginDataListener() {
		
		@Override
		public void OnLoginDataListener(int flag) {
			// TODO Auto-generated method stub
			if (flag == -1) {
				mHandler.obtainMessage(-1).sendToTarget();
			} else {
				mHandler.obtainMessage(1).sendToTarget();
			}
		}
	};
	
	private void toastShow(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
	
	private void print(String msg){
		Log.w(getTag(), msg);
	}
}
