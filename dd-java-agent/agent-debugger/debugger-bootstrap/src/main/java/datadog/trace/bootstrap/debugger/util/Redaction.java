package datadog.trace.bootstrap.debugger.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Redaction {
  public static final String REDACTED_VALUE = "REDACTED";

  /*
   * based on sentry list: https://github.com/getsentry/sentry-python/blob/fefb454287b771ac31db4e30fa459d9be2f977b8/sentry_sdk/scrubber.py#L17-L58
   */
  private static final Set<String> KEYWORDS =
      new HashSet<>(
          Arrays.asList(
              "password",
              "passwd",
              "secret",
              "apikey",
              "auth",
              "credentials",
              "mysqlpwd",
              "privatekey",
              "token",
              "ipaddress",
              "session",
              // django
              "csrftoken",
              "sessionid",
              // wsgi
              "remoteaddr",
              "xcsrftoken",
              "xforwardedfor",
              "setcookie",
              "cookie",
              "authorization",
              "xapikey",
              "xforwardedfor",
              "xrealip"));

  public static boolean isRedactedKeyword(String name) {
    if (name == null) {
      return false;
    }
    name = normalize(name);
    return KEYWORDS.contains(name);
  }

  private static String normalize(String name) {
    StringBuilder sb = null;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      boolean isUpper = Character.isUpperCase(c);
      boolean isRemovable = isRemovableChar(c);
      if (isUpper || isRemovable || sb != null) {
        if (sb == null) {
          sb = new StringBuilder();
        }
        if (isUpper) {
          sb.append(Character.toLowerCase(c));
        } else if (!isRemovable) {
          sb.append(c);
        }
      }
    }
    return sb != null ? sb.toString() : name;
  }

  private static boolean isRemovableChar(char c) {
    return c == '_' || c == '-' || c == '$' || c == '@';
  }
}
