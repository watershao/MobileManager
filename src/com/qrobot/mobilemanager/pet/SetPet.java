package com.qrobot.mobilemanager.pet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.qrobot.mobilemanager.R;
import com.qrobot.mobilemanager.pet.login.QRClientManager;

public class SetPet extends Activity {
	
    private EditText mNicknameEText;
    private TextView mPortraitIdView;
    private ImageView mPortraitImageView;
	
    private int     mId;
    
    private Pet mOriginalPet;
    
    private static final String SHAREPREFERENCE_NAME = "set_pet";
    
    List<Map<String ,Object>> portraitList = new ArrayList<Map<String,Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

        // Override the default content view.
        setContentView(R.layout.pet_set_information);

        mNicknameEText = (EditText)findViewById(R.id.pet_nickname);
        mPortraitIdView = (TextView)findViewById(R.id.pet_portrait_id);
        mPortraitImageView = (ImageView)findViewById(R.id.pet_portrait_image);
        
        mPortraitImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				initAdapter();
				createDialog();
				
//				Toast.makeText(getApplicationContext(), "nihao", Toast.LENGTH_LONG).show();
			}
		});
        
        Intent i = getIntent();
        
        Pet pet = i.getParcelableExtra("pet_info");
        
//        mId = i.getIntExtra("pet_info", -1);

//        Pet pet = null;
//        if (mId == -1) {
//            // No alarm id means create a new reminder.
//        	pet = new Pet();
//        } else {
//            if (pet == null) {
//                finish();
//                return;
//            }
//        }
        mOriginalPet = pet;

        updatePrefs(mOriginalPet);

        // Attach actions to each button.
        Button save = (Button) findViewById(R.id.pet_save);
        save.setOnClickListener(new View.OnClickListener() {
                @Override
				public void onClick(View v) {
                    savePetSet();
                    finish();
                }
        });

        Button cancel = (Button) findViewById(R.id.pet_cancel);
        
    	cancel.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                cancelSet();
            }
        });
    
	}


	private void updatePrefs(Pet pet) {
	    mId = pet.id;
	    mNicknameEText.setText(pet.nickname);
	    Bitmap bm = getPortraitBitmap(pet.portrait);
	    if (bm != null) {
	    	mPortraitImageView.setImageBitmap(bm);
		} else {
			mPortraitImageView.setImageResource(R.drawable.ic_launcher);
		}
	}
	
	private Bitmap getPortraitBitmap(String fileName){
		AssetManager am = getResources().getAssets();
		InputStream is;
		try {
			is = am.open("portrait/"+fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
    private void savePetSet() {
        Pet pet = new Pet();
        pet.id = mId;
        pet.nickname = mNicknameEText.getText().toString();
        pet.portrait = mPortraitIdView.getText().toString();

        SharedPreferences petSP = getSharedPreferences(SHAREPREFERENCE_NAME, MODE_WORLD_READABLE);
        Editor petEditor = petSP.edit();
        petEditor.putString("nickname", pet.nickname);
        petEditor.putString("portrait", pet.portrait);

        petEditor.commit();
        
        final Pet mPet = pet;
		Thread  updateThread = new Thread(){
			public void run(){
				Looper.prepare();
				boolean bUpdate = false;
				bUpdate = updatePetInfo("", mPet.nickname, mPet.portrait);
				Log.w("update", mPet.nickname+mPet.portrait+bUpdate);
				if (bUpdate) {
					Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "修改失败，请重试!", Toast.LENGTH_LONG).show();
				}
			}
		};
		try {
			updateThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateThread.start();		
        
    }
    
	private boolean updatePetInfo(String loginInfo,String nickname, String portrait){
		QRClientManager clientManager = new QRClientManager();
		boolean check = clientManager.checkToLogin(loginInfo);
		if (!check) {
			Toast.makeText(getApplicationContext(), "对不起，宠物信息更新失败，请重试！", Toast.LENGTH_LONG).show();
		} else {

			boolean bUpdate = clientManager.updatePetInfo(nickname, portrait);
	        return bUpdate;
		}
		
		return false;
		
	}
    
    private void cancelSet() {
    	finish();
    }
    
    int mSingleChoiceID = -1;  
    private ListAdapter portraitAdapter;

	private void createDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(SetPet.this);
		mSingleChoiceID = -1;
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("选择头像");
		
/*		LinearLayout pLayout = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.pet_portrait_selector, null);
		ListView pListView = (ListView)pLayout.findViewById(R.id.pet_portrait_listviw);
		pListView.setAdapter(new PortraitAdapter(getApplicationContext(),getData()));
		pListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "click:"+getData().get(position).get("id"), Toast.LENGTH_LONG).show();
			}
		
		});
		builder.setView(pLayout);*/
		
		builder.setSingleChoiceItems(portraitAdapter, mSingleChoiceID,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						mSingleChoiceID = whichButton;
						mPortraitIdView.setText(portraitList.get(whichButton).get("id").toString());
//						mPortraitIdView.setText(String.valueOf((Integer)portraitList.get(whichButton).get("image")));
//						mPortraitImageView.setImageResource((Integer)portraitList.get(whichButton).get("image"));
						mPortraitImageView.setImageBitmap((Bitmap)portraitList.get(whichButton).get("image"));
						Toast.makeText(getApplicationContext(), "nihao"+mSingleChoiceID, Toast.LENGTH_LONG).show();
						dialog.dismiss();
						
					}

				});

/*		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (mSingleChoiceID > 0) {
//					showDialog("你选择的是" + mSingleChoiceID);
				}
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});*/

		builder.create().show();
	}
	
	private void initAdapter(){
//		portraitAdapter = new SimpleAdapter(getApplicationContext(), 
//						getData(), R.layout.pet_portrait_item, 
//						new String[]{"id","image"}, 
//						new int[]{R.id.pet_portrait_id,R.id.pet_portrait_image});
		portraitAdapter = new PortraitAdapter(getApplicationContext(),getData());
	}
	
	private List<Map<String ,Object>> getData(){
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		AssetManager am = getResources().getAssets();
		InputStream is;
		try{
			String[] fileNames = am.list("portrait");
//			Log.w("pet", am.list("")[0]+"#"+fileNames.length);
			if (fileNames != null && fileNames.length > 0) {
				for (int i = 0; i < fileNames.length; i++) {
//					Log.w("pet", fileNames[i]);
					is = am.open("portrait/"+fileNames[i]);
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", fileNames[i]);
					map.put("image", bitmap);
//					map.put("image", R.raw.robot_female);
					list.add(map);
				}
				
			} else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", "default");
				map.put("image", R.drawable.ic_launcher);
				list.add(map);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			
		}
/*        map = new HashMap<String, Object>();
        map.put("id", "p1");
        map.put("image", R.drawable.ic_clock_alarm_on);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("id", "p1");
        map.put("image", R.drawable.ic_clock_refresh_alarm);
        list.add(map);*/
         
        portraitList = list;
        return list;
	}
	
}
