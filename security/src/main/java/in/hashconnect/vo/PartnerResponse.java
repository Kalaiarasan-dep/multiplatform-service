package in.hashconnect.vo;

import java.util.List;

public class PartnerResponse {
	
	private Integer invReqId;
	private List<String> errors;
	
		
	public Integer getInvReqId() {
		return invReqId;
	}
	public void setInvReqId(Integer invReqId) {
		this.invReqId = invReqId;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
}
