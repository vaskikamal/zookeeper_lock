package org.demo.model;

public class LockModel {
	
	private String applicationName;
	private String appIdentifier;
	private long lastUpdateTime;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appIdentifier == null) ? 0 : appIdentifier.hashCode());
		result = prime * result
				+ ((applicationName == null) ? 0 : applicationName.hashCode());
		result = prime * result
				+ (int) (lastUpdateTime ^ (lastUpdateTime >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LockModel other = (LockModel) obj;
		if (appIdentifier == null) {
			if (other.appIdentifier != null)
				return false;
		} else if (!appIdentifier.equals(other.appIdentifier))
			return false;
		if (applicationName == null) {
			if (other.applicationName != null)
				return false;
		} else if (!applicationName.equals(other.applicationName))
			return false;
		if (lastUpdateTime != other.lastUpdateTime)
			return false;
		return true;
	}
	
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getAppIdentifier() {
		return appIdentifier;
	}
	public void setAppIdentifier(String appIdentifier) {
		this.appIdentifier = appIdentifier;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	

}
