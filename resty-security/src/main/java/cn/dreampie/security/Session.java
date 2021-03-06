package cn.dreampie.security;

import cn.dreampie.common.util.Maper;

import java.util.Map;

/**
 * Created by wangrenhui on 14/12/23.
 */
public class Session {
  public static final String SESSION_DEF_KEY = "sessionGroup";
  private final Map<String, String> values;
  private final Principal principal;
  private final int expires;

  public Session(Map<String, String> values, Principal principal, int expires) {
    this.values = values;
    this.principal = principal;
    this.expires = expires;
  }

  Session setExpires(int duration) {
    return updateCurrent(new Session(values, principal, duration));
  }

  public int getExpires() {
    return expires;
  }

  public Principal getPrincipal() {
    return principal;
  }

  public Map<String, String> getValues() {
    return values;
  }

  //------------------current session-------------------------------

  private static final ThreadLocal<Session> current = new ThreadLocal<Session>();


  static void setCurrent(Session session) {
    if (session == null) {
      current.remove();
    } else {
      current.set(session);
    }
  }

  static Session current() {
    return current.get();
  }

  private Session updateCurrent(Session newSession) {
    if (this == current()) {
      current.set(newSession);
    }
    return newSession;
  }

  String get(String key) {
    return values.get(key);
  }

  Session set(String key, String value) {
    // create new map by using a mutable map, not a builder, in case the the given entry overrides a previous one
    Map<String, String> newValues = Maper.copyOf(values);
    if (value == null) {
      newValues.remove(key);
    } else {
      newValues.put(key, value);
    }
    return updateCurrent(new Session(Maper.copyOf(newValues), principal, expires));
  }

  Session authenticateAs(Principal principal) {
    return updateCurrent(new Session(values, principal, expires)).set(Principal.PRINCIPAL_DEF_KEY, principal.getUsername());
  }

  Session clearPrincipal() {
    return updateCurrent(new Session(values, null, expires)).set(Principal.PRINCIPAL_DEF_KEY, null);
  }
}
