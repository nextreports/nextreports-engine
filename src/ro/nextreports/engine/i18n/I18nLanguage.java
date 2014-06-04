package ro.nextreports.engine.i18n;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class I18nLanguage implements Serializable {
		
	private static final long serialVersionUID = 4565906365057455638L;
	
	private String name;
	private boolean isDefault;
	private List<I18nString> i18nStrings;
	
	public I18nLanguage(String name, boolean isDefault) {	
		this.name = name;
		this.isDefault = isDefault;
		this.i18nStrings = new ArrayList<I18nString>();
	}

	public List<I18nString> getI18nStrings() {
		Collections.sort(i18nStrings, new Comparator<I18nString>() {

			@Override
			public int compare(I18nString o1, I18nString o2) {				
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		return i18nStrings;
	}

	public void setI18nStrings(List<I18nString> i18nStrings) {
		this.i18nStrings = i18nStrings;
	}

	public String getName() {
		return name;
	}

	public boolean isDefault() {
		return isDefault;
	}
		
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((i18nStrings == null) ? 0 : i18nStrings.hashCode());
		result = prime * result + (isDefault ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		I18nLanguage other = (I18nLanguage) obj;
		if (i18nStrings == null) {
			if (other.i18nStrings != null) return false;
		} else if (!i18nStrings.equals(other.i18nStrings)) {
			return false;
		}	
		if (isDefault != other.isDefault) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) {
			return false;
		}	
		return true;
	}

	@Override
	public String toString() {
		return "I18nLanguage [name=" + name + ", isDefault=" + isDefault + ", i18nStrings=" + i18nStrings + "]";
	}			

}
