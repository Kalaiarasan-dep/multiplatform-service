package in.hashconnect.notification.service.vo;

public class Parameters {
	private String type;
	private String text;

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image image;
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
