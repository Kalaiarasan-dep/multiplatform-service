package in.hashconnect.scheduler.vo;

public class ScheduledJob {
	private Long id;
	private String bean;
	private String status;
	private String error;
	private String context;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
