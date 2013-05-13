package com.qrobot.mobilemanager.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.qrobot.mobilemanager.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;
import android.widget.ViewSwitcher;

/**
 * ���ͼƬ
 * @author water
 *
 */
public class ViewPhoto extends Activity implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private ImageSwitcher mSwitcher;
	
	private Gallery mGallery;
	
	private Integer[] mThumbIds = { R.drawable.ic_launcher,
				R.drawable.ic_clock_refresh_alarm_selected, R.drawable.ic_clock_refresh_alarm,
				R.drawable.ic_clock_alarm_selected, R.drawable.ic_clock_alarm_on };
	private Integer[] mImageIds = { R.drawable.ic_launcher,
				R.drawable.ic_clock_refresh_alarm_selected, R.drawable.ic_clock_refresh_alarm,
				R.drawable.ic_clock_alarm_selected, R.drawable.ic_clock_alarm_on };
	
	 // ���һ���ʱ��ָ���µ�X����
	 private float touchDownX;
	 // ���һ���ʱ��ָ�ɿ���X����
	 private float touchUpX;
	 
	 private int pictureIndex;
	 
	 private List<String> photoList;
	
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view);
		mSwitcher = (ImageSwitcher) findViewById(R.id.photo_switcher);
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		
		mSwitcher.setOnTouchListener(switcherTouchListener);
		
//		photoList = getImagesFromSD();
		
		mGallery = (Gallery) findViewById(R.id.photo_gallery);
		mGallery.setAdapter(new ImageAdapter(this));
		mGallery.setOnItemSelectedListener(this);
		
	}

	private OnTouchListener switcherTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// ȡ�����һ���ʱ��ָ���µ�X����
				touchDownX = event.getX();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// ȡ�����һ���ʱ��ָ�ɿ���X����
				touchUpX = event.getX();
				// �������ң���ǰһ��
				if (touchUpX - touchDownX > 100) {
					// ȡ�õ�ǰҪ����ͼƬ��index
					pictureIndex = pictureIndex == 0 ? mImageIds.length - 1
							: pictureIndex - 1;
					// ����ͼƬ�л��Ķ���
					mSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), android.R.anim.slide_in_left));
					mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), android.R.anim.slide_out_right));
					// ���õ�ǰҪ����ͼƬ
					mSwitcher.setImageResource(mImageIds[pictureIndex]);
//					mSwitcher.setImageURI(Uri.parse(photoList.get(pictureIndex)));
					mGallery.setSelection(pictureIndex, true);
					// �������󣬿���һ��
				} else if (touchDownX - touchUpX > 100) {
					// ȡ�õ�ǰҪ����ͼƬ��index
					pictureIndex = pictureIndex == mImageIds.length - 1 ? 0
							: pictureIndex + 1;
					// ����ͼƬ�л��Ķ���
					// ����Androidû���ṩslide_out_left��slide_in_right�����Է���slide_in_left��slide_out_right��д��slide_out_left��slide_in_right
					mSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.photo_slide_out_left));
					mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.photo_slide_in_right));
					// ���õ�ǰҪ����ͼƬ
					mSwitcher.setImageResource(mImageIds[pictureIndex]);
//					mSwitcher.setImageURI(Uri.parse(photoList.get(pictureIndex)));
					mGallery.setSelection(pictureIndex, true);
				}
				return true;
			}
			return false;
		}
	};
	
	/*
	 * override for ViewSwitcher.ViewFactory#makeView()
	 */
	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

	/*
	 * override for AdapterView.OnItemSelectedListener#onItemSelected()
	 */
	public void onItemSelected(AdapterView parent, View v, int position, long id) {
		pictureIndex = position;
		mSwitcher.setImageResource(mImageIds[position]);
//		mSwitcher.setImageURI(Uri.parse(photoList.get(position)));
	}

	/*
	 * override for AdapterView.OnItemSelectedListener #onNothingSelected()
	 */
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	private List<String> getImagesFromSD() {
		List<String> imageList = new ArrayList<String>();

//		File f = new File("/sdcard/");
		File f = new File("/storage/sdcard0/wandoujia/image/");
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
//			f = new File(Environment.getExternalStorageDirectory().toString());
		}
		else
		{
			Toast.makeText(ViewPhoto.this, "sdcard error", 1).show();
			return imageList;
		}

		File[] files = f.listFiles();

		if(files == null || files.length == 0)
			return imageList;
		/**
		 * ������ͼ���ļ���·������ArrayList�б�
		 */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (isImageFile(file.getPath()))
				imageList.add(file.getPath());
		}
		return imageList;
	}
	
	private boolean isImageFile(String fName) {
		boolean re;
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/**
		 * �����ļ���չ���ж��Ƿ�Ϊͼ���ļ�
		 */
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}
		return re;
	}
	
	class ImageAdapter2 extends BaseAdapter {

		private int mGalleryItemBackground;
		private Context mContext;
		/**
		 * ͼ��·���б�
		 */
		private List<String> imageList;

		public ImageAdapter2(Context context){
			mContext = context;
			
			TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);

			mGalleryItemBackground = typedArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			typedArray.recycle();
		}
		
		public ImageAdapter2(Context c,  List<String> li) {
			mContext = c;
			imageList = li;

			TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);

			mGalleryItemBackground = typedArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			typedArray.recycle();
		}
		
		public ImageView createReflectedImages(String filePath) {

			final int reflectionGap = 4;

			Bitmap originalImage = BitmapFactory.decodeFile(filePath);
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();

			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);

			Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
					height / 2, width, height / 2, matrix, false);

			Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
					(height + height / 2), Config.ARGB_8888);

			/**
			 * ����һ������bitmapWithReflection�Ļ���������������bitmapWithReflection������ͬ
			 * Ҳ���൱�ڰ�bitmapWithReflection��Ϊ����ʹ�� �û����߶�Ϊ ԭͼ + ��� + ��Ӱ
			 */
			Canvas canvas = new Canvas(bitmapWithReflection);

			/**
			 * �ڻ������Ͻǣ�0,0������ԭʼͼ
			 */
			canvas.drawBitmap(originalImage, 0, 0, null);

			Paint deafaultPaint = new Paint();

			canvas.drawRect(0, height, width, height + reflectionGap,
					deafaultPaint);

			canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

			Paint paint = new Paint();

			LinearGradient shader = new LinearGradient(0,
					originalImage.getHeight(), 0,
					bitmapWithReflection.getHeight() + reflectionGap,
					0x70ffffff, 0x00ffffff, TileMode.CLAMP);

			paint.setShader(shader);

			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

			/**
			 * �ڵ�Ӱͼ���ô���Ӱ�Ļ��ʻ��ƾ���
			 */
			canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
					+ reflectionGap, paint);

			ImageView imageView = new ImageView(mContext);

			/**
			 * BitmapDrawable bd = new BitmapDrawable(bitmapWithReflection);
			 * bd.setAntiAlias=true; imageView.setImageDrawable(bd); ������棺
			 * imageView.setImageBitmap(bitmapWithReflection); ��ʵ��������ݵ�Ч��
			 */
			imageView.setImageBitmap(bitmapWithReflection);
			imageView.setLayoutParams(new GalleryFlow.LayoutParams(180, 240));
			imageView.setBackgroundResource(mGalleryItemBackground);
			
			/**
			 * ����ͼ������ģʽ����ӦImageView��С��ScaleType.MATRIX������ʱʹ��ͼ��任�������š�
			 * 
			 * ע�⣺���ִ�д��д��룬��ԭʼͼʢ������ImageView���޵�ӰЧ��
			 */
			// imageView.setScaleType(ScaleType.MATRIX);
			return imageView;
		}
		@Override
		public int getCount() {
			return imageList.size();
		}
		@Override
		public Object getItem(int position) {
			return position;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return createReflectedImages(imageList.get(position));
		}
//		public float getScale(boolean focused, int offset) {
//			/* Formula: 1 / (2 ^ offset) */
//			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
//		}
	}
	
	
	
	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			i.setImageResource(mThumbIds[position]);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			i.setBackgroundResource(R.drawable.gallery_bg);
			return i;
		}

		private Context mContext;
	}
}