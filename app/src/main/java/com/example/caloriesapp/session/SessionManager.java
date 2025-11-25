package com.example.caloriesapp.session;

import android.content.Context;
import android.content.SharedPreferences;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionManager {

  private static final String PREF_NAME = "USER_SESSION";

  private static final String KEY_USER_ID = "userId";

  private static final String KEY_USER_EMAIL = "email";

  private static final String TOKEN = "token";

  private final SharedPreferences sharedPreferences;

  public SessionManager(Context context) {
    sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
  }

  public void saveUserSession(int userId, String email, String token) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(KEY_USER_ID, userId);
    editor.putString(KEY_USER_EMAIL, email);
    editor.putString(TOKEN, token);
    editor.apply();
  }

  public String getToken() {
    return sharedPreferences.getString(TOKEN, null);
  }

  public int getUserId() {
    return sharedPreferences.getInt(KEY_USER_ID, -1);
  }

  public String getEmail() {
    return sharedPreferences.getString(KEY_USER_EMAIL, null);
  }

  public boolean isLoggedIn() {
    return getUserId() != -1;
  }

  public void logout() {
    sharedPreferences.edit().clear().apply();
  }

}
