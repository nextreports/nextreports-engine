package ro.nextreports.engine.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.util.ReportUtil;

public class I18nUtil {
	
	public static I18nLanguage getDefaultLanguage(ReportLayout layout) {
		return getDefaultLanguage(layout.getLanguages());
	}
	
	public static I18nLanguage getDefaultLanguage(Chart chart) {
		return getDefaultLanguage(chart.getLanguages());
	}
	
	private static I18nLanguage getDefaultLanguage(List<I18nLanguage> languages) {		
		if ((languages == null) || (languages.size() == 0)) {
			return null;
		} else {
			for (I18nLanguage language : languages) {
				if (language.isDefault()) {
					return language;
				}
			}
		}
		return null;
	}
	
	public static I18nLanguage getLocaleLanguage(Chart chart) {		
		return getLocaleLanguage(chart.getLanguages());
	}
	
	public static I18nLanguage getLocaleLanguage(ReportLayout layout) {		
		return getLocaleLanguage(layout.getLanguages());
	}
	
	private static I18nLanguage getLocaleLanguage(List<I18nLanguage> languages) {				
		if ((languages == null) || (languages.size() == 0)) {
			return null;
		} else {
			Locale locale = Locale.getDefault();
		    String name = locale.getLanguage() + "_" + locale.getCountry();		    
			for (I18nLanguage language : languages) {
				if (language.getName().equals(name)) {					
					return language;
				}
			}			
			// if local language not defined, use defaulut language
			return I18nUtil.getDefaultLanguage(languages);
		}		
	}
	
	public static I18nLanguage getLanguageByName(ReportLayout layout, String name) {
		return getLanguageByName(layout.getLanguages(), name);
	}
	
	public static I18nLanguage getLanguageByName(Chart chart, String name) {
		return getLanguageByName(chart.getLanguages(), name);
	}
	
	private static I18nLanguage getLanguageByName(List<I18nLanguage> languages, String name) {		
		if ((languages == null) || (languages.size() == 0)) {
			return null;
		} else {
			for (I18nLanguage language : languages) {
				if (language.getName().equals(name)) {
					return language;
				}
			}
		}
		return null;
	}
	
	public static String getString(String key, I18nLanguage language) {
		List<I18nString> strings = language.getI18nStrings();
		for (I18nString s : strings) {
			if (s.getKey().equals(key)) {
				return s.getValue();
			}
		}
		return key;		
	}
	
}
