package in.hashconnect.gmb.vo;

public class MediaItem {
	private String name;
	private MediaFormat mediaFormat;
	private LocationAssociation locationAssociation;
	private String googleUrl;
	private String thumbnailUrl;
	private String createTime;
	private Dimensions dimensions;
	private MediaInsights insights;
	private Attribution attribution;
	private String description;
	private String sourceUrl;
	private MediaItemDataRef dataRef;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MediaFormat getMediaFormat() {
		return mediaFormat;
	}

	public void setMediaFormat(MediaFormat mediaFormat) {
		this.mediaFormat = mediaFormat;
	}

	public LocationAssociation getLocationAssociation() {
		return locationAssociation;
	}

	public void setLocationAssociation(LocationAssociation locationAssociation) {
		this.locationAssociation = locationAssociation;
	}

	public String getGoogleUrl() {
		return googleUrl;
	}

	public void setGoogleUrl(String googleUrl) {
		this.googleUrl = googleUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Dimensions getDimensions() {
		return dimensions;
	}

	public void setDimensions(Dimensions dimensions) {
		this.dimensions = dimensions;
	}

	public MediaInsights getInsights() {
		return insights;
	}

	public void setInsights(MediaInsights insights) {
		this.insights = insights;
	}

	public Attribution getAttribution() {
		return attribution;
	}

	public void setAttribution(Attribution attribution) {
		this.attribution = attribution;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public MediaItemDataRef getDataRef() {
		return dataRef;
	}

	public void setDataRef(MediaItemDataRef dataRef) {
		this.dataRef = dataRef;
	}

}
