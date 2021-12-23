package com.envisioniot.enos.third_party_protocol.core.element;

import java.util.HashMap;
import java.util.Map;

public class StringI18n {

    private String defaultValue = null;

    private Map<String, String> i18nValue = new HashMap<>();

    public StringI18n() {
    }

    public StringI18n(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * get localized value by locale
     *
     * @param locale a string representation of LOCALE.
     *               You can use Locale.XXXXX.toString() to get string representation of the locale,
     *               such as Locale.US.toString(), Locale.SIMPLIFIED_CHINESE.toString(), etc
     * @return
     */
    public String getLocalizedValue(String locale) {
        return i18nValue.get(locale);
    }

    /**
     * put localized value
     *
     * @param locale         a string representation of LOCALE.
     *                       You can use Locale.XXXXX.toString() to get string representation of the locale,
     *                       such as Locale.US.toString(), Locale.SIMPLIFIED_CHINESE.toString(), etc
     * @param localizedValue
     * @return
     */
    public void addLocalizedValue(String locale, String localizedValue) {
        this.i18nValue.put(locale, localizedValue);
    }

    public Map<String, String> getI18nValue() {
        return i18nValue;
    }

    public void setI18nValue(Map<String, String> i18nValue) {
        this.i18nValue = i18nValue;
    }
}