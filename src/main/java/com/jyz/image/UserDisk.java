package com.eighth.airrent.test;

import java.util.List;

public class UserDisk {
	private String diskID;
	private String diskPWD;
	private String userCounts;
	public String getUserCounts() {
		return userCounts;
	}
	public void setUserCounts(String userCounts) {
		this.userCounts = userCounts;
	}
	private List<UserData> userDataList;
	
	public List<UserData> getUserDataList() {
		return userDataList;
	}
	public void setUserDataList(List<UserData> userDataList) {
		this.userDataList = userDataList;
	}
	public String getDiskID() {
		return diskID;
	}
	public void setDiskID(String diskID) {
		this.diskID = diskID;
	}
	public String getDiskPWD() {
		return diskPWD;
	}
	public void setDiskPWD(String diskPWD) {
		this.diskPWD = diskPWD;
	}
}
