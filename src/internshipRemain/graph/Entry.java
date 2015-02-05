package internshipRemain.graph;


public class Entry {

	private String key;
	private String value;

	public Entry(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null)
			return false;

		if (o instanceof Entry) {
			Entry other = (Entry) o;
			if (key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!key.equals(other.key)) {
				return false;
			}
			
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}

		}
		return true;
	}
	
	public String toString() {
		return "[ key:" + key + ", value: " + value + "]";
	}

}

