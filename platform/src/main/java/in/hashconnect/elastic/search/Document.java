package in.hashconnect.elastic.search;

import java.util.Date;

public abstract class Document {

	private String id;
	private Date createdOn;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
