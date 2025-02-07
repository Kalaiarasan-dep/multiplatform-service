package in.hashconnect.zoho.vo;

public class ContactPerson {

	private String salutation;
	private String first_name;
	private String last_name;
	private String email;
	private String mobile;
	private Boolean is_primary_contact;
	private String contact_person_id;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean isIs_primary_contact() {
		return is_primary_contact;
	}

	public void setIs_primary_contact(Boolean is_primary_contact) {
		this.is_primary_contact = is_primary_contact;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getContact_person_id() {
		return contact_person_id;
	}

	public void setContact_person_id(String contact_person_id) {
		this.contact_person_id = contact_person_id;
	}

}
