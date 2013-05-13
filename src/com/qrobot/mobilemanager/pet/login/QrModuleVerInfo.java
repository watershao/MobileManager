package com.qrobot.mobilemanager.pet.login;

public class QrModuleVerInfo
{
	public int getModuleID() {
		return moduleID;
	}
	public void setModuleID(int moduleID) {
		this.moduleID = moduleID;
	}
	public int getMinVersion() {
		return minVersion;
	}
	public void setMinVersion(int minVersion) {
		this.minVersion = minVersion;
	}
	public int getNewestVersion() {
		return newestVersion;
	}
	public void setNewestVersion(int newestVersion) {
		this.newestVersion = newestVersion;
	}
	public boolean isTest() {
		return isTest;
	}
	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getVersionDesc() {
		return versionDesc;
	}
	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}
	private int moduleID;
	private int minVersion;
	private int newestVersion;
	private boolean isTest;
	private String releaseDate;
	private String versionDesc;
}
