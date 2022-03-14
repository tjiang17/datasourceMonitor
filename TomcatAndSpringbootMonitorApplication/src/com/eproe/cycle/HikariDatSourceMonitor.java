package com.eproe.cycle;

public class HikariDatSourceMonitor {

	private String poolName;
	private String maximumPoolSize;
	private String minimumIdle;
	private String activeConnections;
	private String maxLifeTime;
	private String dataSourceType;
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getMaximumPoolSize() {
		return maximumPoolSize;
	}
	public void setMaximumPoolSize(String maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}
	public String getMinimumIdle() {
		return minimumIdle;
	}
	public void setMinimumIdle(String minimumIdle) {
		this.minimumIdle = minimumIdle;
	}
	public String getActiveConnections() {
		return activeConnections;
	}
	public void setActiveConnections(String activeConnections) {
		this.activeConnections = activeConnections;
	}
	public String getMaxLifeTime() {
		return maxLifeTime;
	}
	public void setMaxLifeTime(String maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}
	public String getDataSourceType() {
		return dataSourceType;
	}
	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
	@Override
	public String toString() {
		return ":" + poolName + ":" + maximumPoolSize + ":" + minimumIdle + ":"
				+ activeConnections + ":true:true:60000:SELECT 1 FROM DUAL:true"
				+ ":" + maxLifeTime + ":" + dataSourceType;
	}

}
