/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.util;
public final class Locale implements java.io.Serializable, java.lang.Cloneable {
	public static final class Builder {
		public Builder() { } 
		public java.util.Locale.Builder addUnicodeLocaleAttribute(java.lang.String var0) { return null; }
		public java.util.Locale build() { return null; }
		public java.util.Locale.Builder clear() { return null; }
		public java.util.Locale.Builder clearExtensions() { return null; }
		public java.util.Locale.Builder removeUnicodeLocaleAttribute(java.lang.String var0) { return null; }
		public java.util.Locale.Builder setExtension(char var0, java.lang.String var1) { return null; }
		public java.util.Locale.Builder setLanguage(java.lang.String var0) { return null; }
		public java.util.Locale.Builder setLanguageTag(java.lang.String var0) { return null; }
		public java.util.Locale.Builder setLocale(java.util.Locale var0) { return null; }
		public java.util.Locale.Builder setRegion(java.lang.String var0) { return null; }
		public java.util.Locale.Builder setScript(java.lang.String var0) { return null; }
		public java.util.Locale.Builder setUnicodeLocaleKeyword(java.lang.String var0, java.lang.String var1) { return null; }
		public java.util.Locale.Builder setVariant(java.lang.String var0) { return null; }
	}
	public enum Category {
		DISPLAY,
		FORMAT;
	}
	public final static java.util.Locale CANADA; static { CANADA = null; }
	public final static java.util.Locale CANADA_FRENCH; static { CANADA_FRENCH = null; }
	public final static java.util.Locale CHINA; static { CHINA = null; }
	public final static java.util.Locale CHINESE; static { CHINESE = null; }
	public final static java.util.Locale ENGLISH; static { ENGLISH = null; }
	public final static java.util.Locale FRANCE; static { FRANCE = null; }
	public final static java.util.Locale FRENCH; static { FRENCH = null; }
	public final static java.util.Locale GERMAN; static { GERMAN = null; }
	public final static java.util.Locale GERMANY; static { GERMANY = null; }
	public final static java.util.Locale ITALIAN; static { ITALIAN = null; }
	public final static java.util.Locale ITALY; static { ITALY = null; }
	public final static java.util.Locale JAPAN; static { JAPAN = null; }
	public final static java.util.Locale JAPANESE; static { JAPANESE = null; }
	public final static java.util.Locale KOREA; static { KOREA = null; }
	public final static java.util.Locale KOREAN; static { KOREAN = null; }
	public final static java.util.Locale PRC; static { PRC = null; }
	public final static char PRIVATE_USE_EXTENSION = 120;
	public final static java.util.Locale ROOT; static { ROOT = null; }
	public final static java.util.Locale SIMPLIFIED_CHINESE; static { SIMPLIFIED_CHINESE = null; }
	public final static java.util.Locale TAIWAN; static { TAIWAN = null; }
	public final static java.util.Locale TRADITIONAL_CHINESE; static { TRADITIONAL_CHINESE = null; }
	public final static java.util.Locale UK; static { UK = null; }
	public final static char UNICODE_LOCALE_EXTENSION = 117;
	public final static java.util.Locale US; static { US = null; }
	public Locale(java.lang.String var0) { } 
	public Locale(java.lang.String var0, java.lang.String var1) { } 
	public Locale(java.lang.String var0, java.lang.String var1, java.lang.String var2) { } 
	public java.lang.Object clone() { return null; }
	public static java.util.Locale forLanguageTag(java.lang.String var0) { return null; }
	public static java.util.Locale[] getAvailableLocales() { return null; }
	public java.lang.String getCountry() { return null; }
	public static java.util.Locale getDefault() { return null; }
	public static java.util.Locale getDefault(java.util.Locale.Category var0) { return null; }
	public final java.lang.String getDisplayCountry() { return null; }
	public java.lang.String getDisplayCountry(java.util.Locale var0) { return null; }
	public final java.lang.String getDisplayLanguage() { return null; }
	public java.lang.String getDisplayLanguage(java.util.Locale var0) { return null; }
	public final java.lang.String getDisplayName() { return null; }
	public java.lang.String getDisplayName(java.util.Locale var0) { return null; }
	public java.lang.String getDisplayScript() { return null; }
	public java.lang.String getDisplayScript(java.util.Locale var0) { return null; }
	public final java.lang.String getDisplayVariant() { return null; }
	public java.lang.String getDisplayVariant(java.util.Locale var0) { return null; }
	public java.lang.String getExtension(char var0) { return null; }
	public java.util.Set<java.lang.Character> getExtensionKeys() { return null; }
	public java.lang.String getISO3Country() { return null; }
	public java.lang.String getISO3Language() { return null; }
	public static java.lang.String[] getISOCountries() { return null; }
	public static java.lang.String[] getISOLanguages() { return null; }
	public java.lang.String getLanguage() { return null; }
	public java.lang.String getScript() { return null; }
	public java.util.Set<java.lang.String> getUnicodeLocaleAttributes() { return null; }
	public java.util.Set<java.lang.String> getUnicodeLocaleKeys() { return null; }
	public java.lang.String getUnicodeLocaleType(java.lang.String var0) { return null; }
	public java.lang.String getVariant() { return null; }
	public static void setDefault(java.util.Locale.Category var0, java.util.Locale var1) { }
	public static void setDefault(java.util.Locale var0) { }
	public java.lang.String toLanguageTag() { return null; }
	public final java.lang.String toString() { return null; }
}

