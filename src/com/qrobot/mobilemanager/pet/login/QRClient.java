package com.qrobot.mobilemanager.pet.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simplistic QR client.
 */
public class QRClient {

	private final String _url;
	private int _robotCode;
	private int _versionType;
	private byte[] _loginProof;
	
	
	public QRClient(String url) {
		_url = url;
		_robotCode = 0;
		_versionType = 0;
		_loginProof = new byte[40];
	}
	
	
	public HashMap activeCode(int versionType, String robotId, String sellId)
	{
		try {
			JSONObject param = new JSONObject();  
			param.put("robotId", robotId);  
			param.put("activeNo", sellId);
			param.put("versionType", new Integer(versionType));
			String activeUrl = _url + "/activeMT.action";
			String result = QRClientUtil.getContent(activeUrl, param);
			JSONObject jsonObj = new JSONObject(result).getJSONObject("data"); 
			int robotCode = jsonObj.getInt("robotCode"); 
			String robotKey = jsonObj.getString("robotKey"); 
			String expiredDate = jsonObj.getString("expiredDate"); 
			HashMap mapResult = new HashMap();
			mapResult.put("robotCode", new Integer(robotCode));
			mapResult.put("robotKey", robotKey);
			mapResult.put("expiredDate", expiredDate);
			return mapResult;
		} catch (Exception ex) {
//			throw new RuntimeException(ex);  
			ex.printStackTrace();
			return null;
		}
	}
	
	public void setLoginData(int versionType, byte[] bData)
	{
		if (bData.length>=25)
		{
			_versionType = versionType;
			_robotCode = QRUtil.byteArrayToInt(bData, 0);
			System.arraycopy(bData, 8+1, _loginProof, 0, 16);
		}
	}
	
	public boolean login()
	{
		if (_robotCode==0)
			return false;
		
		byte[] random = new byte[8];
		System.arraycopy(_loginProof, 0, random, 0, 8);
		String base64Random = Base64.encode(random);
		
		byte[] desCode = new byte[8];
		System.arraycopy(_loginProof, 8, desCode, 0, 8);
		String base64DesCode = Base64.encode(desCode);
		
		try {
			JSONObject param = new JSONObject();  
			param.put("robotCode", new Integer(_robotCode));  
			param.put("result", base64DesCode);
			param.put("random", base64Random);
			param.put("versionType", new Integer(_versionType));
			
			String loginUrl = _url + "/loginMT.action";
			String result = QRClientUtil.getContent(loginUrl, param);
			boolean ret = new JSONObject(result).getBoolean("success"); 
			return ret;
		} catch (Exception ex) {
//			throw new RuntimeException(ex);
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public List<QrModuleVerInfo> querySpecifiedSysModuleVers(int versionType, int[] arModuleId) 
	{
		try {
			JSONObject param = new JSONObject();  
			param.put("versionType", new Integer(versionType));
			JSONArray arrModuleId = new JSONArray(); 
			for (int i=0; i<arModuleId.length; i++)
			{
				arrModuleId.put(i, arModuleId[i]);
			}
			
			param.put("moduleId", arrModuleId);
			String activeUrl = _url + "/querySpecifiedPlatformVersion.action";
			String result = QRClientUtil.getContent(activeUrl, param);
			JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
			List<QrModuleVerInfo> list = new ArrayList<QrModuleVerInfo>();
			for (int i=0; i<jsonArray.length(); i++)
			{
				QrModuleVerInfo verInfo = new QrModuleVerInfo();
				
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				verInfo.setModuleID(jsonObj.getInt("moduleId"));
				verInfo.setMinVersion(jsonObj.getInt("minVersion"));
				verInfo.setNewestVersion(jsonObj.getInt("newestVersion"));
				verInfo.setTest(jsonObj.getBoolean("isTest"));
				verInfo.setReleaseDate(jsonObj.getString("releaseDate"));
				verInfo.setVersionDesc(jsonObj.getString("versionDesc"));
				list.add(verInfo);
			}
			
			return list;
		} catch (Exception ex) {
//			throw new RuntimeException(ex); 
			ex.printStackTrace();
			return null;
		}
	}
	
	public List<QrModuleFileInfo> querySpecifiedModuleUpdateInfo(int versionType, int moduleId, int oldVer, int newVer)
	{
		try {
			JSONObject param = new JSONObject();  
			param.put("versionType", new Integer(versionType));
			param.put("moduleId", new Integer(moduleId));
			param.put("localVersion", new Integer(oldVer));
			param.put("upgradeVersion", new Integer(newVer));
			
			String activeUrl = _url + "/querySpecifiedModuleUpgrade.action";
			String result = QRClientUtil.getContent(activeUrl, param);
			JSONArray jsonArray = new JSONObject(result).getJSONObject("data").getJSONArray("versions");
			List<QrModuleFileInfo> list = new ArrayList<QrModuleFileInfo>();
			for (int i=0; i<jsonArray.length(); i++)
			{
				QrModuleFileInfo fileInfo = new QrModuleFileInfo();
				
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				fileInfo.setVersion(jsonObj.getInt("version"));
				fileInfo.setFileName(jsonObj.getString("fileName"));
				fileInfo.setSize(jsonObj.getInt("size"));
				fileInfo.setForce(jsonObj.getBoolean("isForce"));
				fileInfo.setVersionDesc(jsonObj.getString("versionDesc"));
				
				list.add(fileInfo);
			}
			
			return list;
		} catch (Exception ex) {
//			throw new RuntimeException(ex);  
			ex.printStackTrace();
			return null;
		}
	}
	
	public QrResServerInfo queryResServer()
	{
		try {
			JSONObject param = new JSONObject();  
			
			String loginUrl = _url + "/queryResServer.action";
			String result = QRClientUtil.getContent(loginUrl, param);
			JSONObject jsonObj = new JSONObject(result).getJSONObject("data");
			String server = jsonObj.getString("server"); 
			String token = jsonObj.getString("token"); 
			QrResServerInfo serverInfo = new QrResServerInfo();
			serverInfo.setServer(server);
			serverInfo.setToken(token);
			return serverInfo; 
			
		} catch (Exception ex) {
//			throw new RuntimeException(ex);
			ex.printStackTrace();
			return null;
		}
	}
	
	public int DownloadSpecifiedModuleFile(String ResServer, String token, int versionType, int moduleId, 
			int fileVer, String fileName, int startOffset, String destFile)
	{
		try {
			JSONObject param = new JSONObject();  
			param.put("versionType", new Integer(versionType));
			param.put("moduleId", new Integer(moduleId));
			if (null!=token)
				param.put("token", token);
			param.put("version", new Integer(fileVer));
			param.put("fileName", fileName);
			param.put("startPos", new Integer(startOffset));
			String loginUrl = ResServer + "/downloadSpecifiedPkg.action";
			int num = QRClientUtil.downloadContent(loginUrl, param, destFile, startOffset);
			
			return num;
			
		} catch (Exception ex) {
//			throw new RuntimeException(ex);
			ex.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 获取小Q信息
	 * @return
	 */
	public QrRobotInfo QueryRobotInfo() {
		try {
			JSONObject param = new JSONObject();  
			
			String url = _url + "/queryRobotInfo.action";
			String result = QRClientUtil.getContent(url, param);
			JSONObject jsonObj = new JSONObject(result).getJSONObject("data");
			String nickName = jsonObj.getString("nickName"); 
			String image = jsonObj.getString("image"); 
			int level = jsonObj.getInt("level");
			QrRobotInfo robotInfo = new QrRobotInfo();
			robotInfo.setNickName(nickName);
			robotInfo.setImage(image);
			robotInfo.setLevel(level);
			return robotInfo; 
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 更新小Q信息
	 * @param nickName
	 * @param image
	 * @return
	 */
	public boolean UpdateRobotInfo(String nickName, String image) {
		try {
			JSONObject param = new JSONObject();  
			param.put("nickName", nickName);
			param.put("image", image);
			String url = _url + "/updateRobotInfo.action";
			String result = QRClientUtil.getContent(url, param);
			boolean ret = new JSONObject(result).getBoolean("success"); 
			return ret;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
}