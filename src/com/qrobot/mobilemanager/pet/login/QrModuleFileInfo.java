package com.qrobot.mobilemanager.pet.login;

public class QrModuleFileInfo {
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getVersionDesc() {
		return versionDesc;
	}
	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isForce() {
		return isForce;
	}
	public void setForce(boolean isForce) {
		this.isForce = isForce;
	}
	private int version;
	private String fileName;
	private String versionDesc;
	private int size;
	private boolean isForce;
}
