package com.fireflies.govtfireflies;

public class Document {
	private String name;
	private String by;
	private String to;
	private String time;
	private String type;

	public Document() {
	}

	public Document(String name, String by, String to, String time, String type) {
		this.name = name;
		this.by = by;
		this.to = to;
		this.time = time;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getBy() {
		return by;
	}

	public String getTo() {
		return to;
	}

	public String getTime() {
		return time;
	}

	public String getType() {
		return type;
	}

	public String getFileName() {
		return name + "." + type;
	}

	@Override
	public String toString() {
		return "Document{" +
				"name='" + name + '\'' +
				", by='" + by + '\'' +
				", to='" + to + '\'' +
				", time='" + time + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}
