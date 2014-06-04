package ro.nextreports.engine.i18n;

import java.io.Serializable;

public class I18nString implements Serializable, Comparable<I18nString> {
		
	private static final long serialVersionUID = 2460235117760988200L;

	/** 
	 * Start and end markup for a report i18n string	 
	 */
	public static final String MARKUP = "@@";
	
	private String key;
	private String value;
	
	public I18nString(String key, String value) {	
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		I18nString other = (I18nString) obj;
		if (key == null) {
			if (other.key != null) return false;
		} else if (!key.equals(other.key)) {
			return false;
		}	
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) {
			return false;
		}	
		return true;
	}

	@Override
	public String toString() {
		return "I18nString [key=" + key + ", value=" + value + "]";
	}

	@Override
	public int compareTo(I18nString o) {				
		return this.getKey().compareTo(o.getKey());
	}
		
}
