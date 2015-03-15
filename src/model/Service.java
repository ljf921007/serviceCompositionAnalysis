package model;

public class Service {
	private String serviceName;
	//从调用图的第一个服务传递到图中所有的服务中
	private String identifier;
	private Metric[] metrics;
	private String timeRange;
	private String[] downstream;
	
	public String[] getDownstream() {
		return downstream;
	}
	public void setDownstream(String[] downstream) {
		this.downstream = downstream;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public Metric[] getMetrics() {
		return metrics;
	}
	public void setMetrics(Metric[] metrics) {
		this.metrics = metrics;
	}
	public String getTimeRange() {
		return timeRange;
	}
	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

}
