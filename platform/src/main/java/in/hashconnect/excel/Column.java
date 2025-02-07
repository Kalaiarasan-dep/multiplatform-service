package in.hashconnect.excel;

public class Column {
	private String name;
	private String type;
	private String className;
	private Object value;
	private String format;

	public Column(String name, String type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public Column(String name, String type, String className) {
		this.name = name;
		this.type = type;
		this.className = className;
	}

	public Column() {
	}

	public static Column create(Object value) {
		return create(value, null);
	}

	public static Column create(Object value, String format) {
		return new Column(value, format);
	}

	public Column(Object value, String format) {
		this.value = value;
		this.format = format;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", type=" + type + ", className=" + className + ", value=" + value + "]";
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
