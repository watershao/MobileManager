package com.qrobot.mobilemanager.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {

	private static final String TAG = "ImageLoader";
	
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 20;
	
	private static final int MB = 1024 * 1024;
	
	private static final int CACHE_SIZE= 10;
	
	private static final String WHOLESALE_CONV = "";
	
	private int mTimeDiff = 60*60*24*7;
	
	private static final int HARD_CACHE_CAPACITY = 30;
	
	/**
	 * 保存图片到sd卡
	 * @param bm
	 * @param url
	 */
	private void saveBmpToSd(Bitmap bm, String url) { 
        if (bm == null) { 
            Log.w(TAG, " trying to save null bitmap"); 
            return; 
        } 
         //判断sdcard上的空间 
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) { 
            Log.w(TAG, "Low free space onsd, do not cache"); 
            return; 
        } 
        String filename =convertUrlToFileName(url); 
        String dir = getDirectory(filename); 
        File file = new File(dir +"/" + filename); 
        try { 
            file.createNewFile(); 
            OutputStream outStream = new FileOutputStream(file); 
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream); 
            outStream.flush(); 
            outStream.close(); 
            Log.i(TAG, "Image saved tosd"); 
        } catch (FileNotFoundException e) { 
            Log.w(TAG,"FileNotFoundException"); 
            return;
        } catch (IOException e) { 
            Log.w(TAG,"IOException"); 
            return;
        } 
    }
	
	private String convertUrlToFileName(String url){
		
		return null;
	}
	
	private String getDirectory(String fileName){
		
		return null;
	}
	
	/** 
	 * 计算sdcard上的剩余空间 
	 * @return 
	 */ 
	private int freeSpaceOnSd() { 
	    StatFs stat = new StatFs(Environment.getExternalStorageDirectory() .getPath()); 
	    double sdFreeMB = ((double)stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB; 
	    return (int) sdFreeMB; 
	} 
	
	/** 
	 * 修改文件的最后修改时间 
	 * @param dir 
	 * @param fileName 
	 */ 
	private void updateFileTime(String dir,String fileName) { 
	    File file = new File(dir,fileName);        
	    long newModifiedTime =System.currentTimeMillis(); 
	    file.setLastModified(newModifiedTime); 
	} 
	
	/** 
	 *计算存储目录下的文件大小，当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定 
	 * 那么删除40%最近没有被使用的文件 
	 * @param dirPath 
	 * @param filename 
	 */ 
	private void removeCache(String dirPath) { 
	    File dir = new File(dirPath); 
	    File[] files = dir.listFiles(); 
	    if (files == null) { 
	        return; 
	    } 
	    int dirSize = 0; 
	    for (int i = 0; i < files.length;i++) { 
	        if(files[i].getName().contains(WHOLESALE_CONV)) { 
	            dirSize += files[i].length(); 
	        } 
	    } 
	    if (dirSize > CACHE_SIZE * MB ||FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) { 
	        int removeFactor = (int) ((0.4 *files.length) + 1); 
	 
	        Arrays.sort(files, new FileLastModifSort()); 
	 
	        Log.i(TAG, "Clear some expiredcache files "); 
	 
	        for (int i = 0; i <removeFactor; i++) { 
	 
	            if(files[i].getName().contains(WHOLESALE_CONV)) { 
	 
	                files[i].delete();              
	 
	            } 
	        } 
	    } 
	} 
	
	/** 
	 * 删除过期文件 
	 * @param dirPath 
	 * @param filename 
	 */ 
	private void removeExpiredCache(String dirPath, String filename) { 
	 
	    File file = new File(dirPath,filename); 
	 
	    if (System.currentTimeMillis() -file.lastModified() > mTimeDiff) { 
	 
	        Log.i(TAG, "Clear some expiredcache files "); 
	 
	        file.delete(); 
	 
	    } 
	 
	} 
	
	/** 
	 * 根据文件的最后修改时间进行排序 * 
	 */ 
	class FileLastModifSort implements Comparator<File>{ 
		
	    public int compare(File arg0, File arg1) {
	    	
	        if (arg0.lastModified() >arg1.lastModified()) { 
	            return 1; 
	        } else if (arg0.lastModified() ==arg1.lastModified()) { 
	            return 0; 
	        } else { 
	            return -1; 
	        } 
	    }
	}
	
	private final HashMap<String, Bitmap> mHardBitmapCache = 
			new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY/ 2, 0.75f, true) { 
		
        @Override 
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) { 
            if (size() > HARD_CACHE_CAPACITY) { 
               //当map的size大于30时，把最近不常用的key放到mSoftBitmapCache中，从而保证mHardBitmapCache的效率 
               mSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue())); 
                return true; 
            } else {
                return false; 
            } 
        }
    }; 
    
    
    /** 
     *当mHardBitmapCache的key大于30的时候，会根据LRU算法把最近没有被使用的key放入到这个缓存中。 
     *Bitmap使用了SoftReference，当内存空间不足时，此cache中的bitmap会被垃圾回收掉 
     */ 
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> mSoftBitmapCache =
    		new ConcurrentHashMap<String,SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2); 
    
    /** 
     * 从缓存中获取图片 
     */ 
    private Bitmap getBitmapFromCache(String url) { 
        // 先从mHardBitmapCache缓存中获取 
        synchronized (mHardBitmapCache) { 
            final Bitmap bitmap =mHardBitmapCache.get(url); 
            if (bitmap != null) { 
                //如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除 
                mHardBitmapCache.remove(url); 
                mHardBitmapCache.put(url,bitmap); 
                return bitmap; 
            } 
        } 
        //如果mHardBitmapCache中找不到，到mSoftBitmapCache中找 
        SoftReference<Bitmap>bitmapReference = mSoftBitmapCache.get(url); 
        if (bitmapReference != null) { 
            final Bitmap bitmap =bitmapReference.get(); 
            if (bitmap != null) { 
                return bitmap; 
            } else { 
                mSoftBitmapCache.remove(url); 
            } 
        } 
        return null; 
    } 
	 
    /** 
     * 异步下载图片 
     */ 
    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> { 
        private static final int IO_BUFFER_SIZE= 4 * 1024; 
        private String url; 
        private final WeakReference<ImageView> imageViewReference; 
        
        public ImageDownloaderTask(ImageView imageView) { 
            imageViewReference = new WeakReference<ImageView>(imageView); 
        } 
  
       @Override 
        protected Bitmap doInBackground(String... params) { 
            final AndroidHttpClient client =AndroidHttpClient.newInstance("Android"); 
            url = params[0]; 
            final HttpGet getRequest = new HttpGet(url); 
            try { 
                HttpResponse response =client.execute(getRequest); 
                final int statusCode =response.getStatusLine().getStatusCode(); 
                if (statusCode !=HttpStatus.SC_OK) { 
                    Log.w(TAG, "从" +url + "中下载图片时出错!,错误码:" + statusCode); 
                    return null; 
                } 
                final HttpEntity entity =response.getEntity(); 
                if (entity != null) { 
                    InputStream inputStream =null; 
                    OutputStream outputStream =null; 
                    try { 
                        inputStream =entity.getContent(); 
                        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream(); 
                        outputStream = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE); 
                        copy(inputStream,outputStream); 
                        outputStream.flush(); 
                        final byte[] data =dataStream.toByteArray(); 
                        final Bitmap bitmap =BitmapFactory.decodeByteArray(data, 0, data.length); 
                        return bitmap; 
                    } finally { 
                        if (inputStream !=null) { 
                           inputStream.close(); 
                        } 
                        if (outputStream !=null) { 
                           outputStream.close(); 
                        } 
                       entity.consumeContent(); 
                    } 
                } 
            } catch (IOException e) { 
                getRequest.abort(); 
                Log.w(TAG, "I/O errorwhile retrieving bitmap from " + url, e); 
            } catch (IllegalStateException e) { 
                getRequest.abort(); 
                Log.w(TAG, "Incorrect URL:" + url); 
            } catch (Exception e) { 
                getRequest.abort(); 
                Log.w(TAG, "Error whileretrieving bitmap from " + url, e); 
            } finally { 
                if (client != null) { 
                    client.close(); 
                } 
            } 
            return null; 
        } 
       
		private void copy(InputStream is, OutputStream os) {
			if (is != null && os != null) {
				try {

					byte[] buffer = new byte[1024];
					int count = 0;
					while ((count = is.read(buffer, 0, 1024)) > 0) {
						os.write(buffer, 0, count);
					}

				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		}
    }

}