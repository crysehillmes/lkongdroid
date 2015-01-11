package org.cryse.utils.preference;

import android.content.SharedPreferences;

public class LongPreference {
  private final SharedPreferences preferences;
  private final String key;
  private final Long defaultValue;

  public LongPreference(SharedPreferences preferences, String key) {
    this(preferences, key, 0l);
  }

  public LongPreference(SharedPreferences preferences, String key, long defaultValue) {
    this.preferences = preferences;
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public long get() {
    return preferences.getLong(key, defaultValue);
  }

  public boolean isSet() {
    return preferences.contains(key);
  }

  public void set(long value) {
    preferences.edit().putLong(key, value).apply();
  }

  public void delete() {
    preferences.edit().remove(key).apply();
  }
}
