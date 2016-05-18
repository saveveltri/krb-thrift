package joshelser;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.HashMap;

/**
 * This is simple Java program that retrieve any kerberos principal
 * authenticated with Kerberos using the JDK implementation.
 *
 * The program uses no libraries but JDK itself.
 */
public class GetKrbUser {

  private static final String getKrb5LoginModuleName() {
    return System.getProperty("java.vendor").contains("IBM") ? "com.ibm.security.auth.module.Krb5LoginModule"
            : "com.sun.security.auth.module.Krb5LoginModule";
  }

  private static final AppConfigurationEntry KERBEROS_LOGIN =
          new AppConfigurationEntry(getKrb5LoginModuleName(),
                  AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL, new HashMap<String, String>() {{
            put("useDefaultCcache", "true");
            put("doNotPrompt", "true");
            put("useTicketCache", "true");
            put("renewTGT", "true");
          }});

  public static Configuration kerberosPlaygroundConfiguration = new Configuration() {
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
      return new AppConfigurationEntry[]{KERBEROS_LOGIN};
    }
  };

  public static void main(String[] args) {

    try {

      Subject subject = new Subject();

      LoginContext loginContext = new LoginContext("kerberos-playground", subject, null,
              kerberosPlaygroundConfiguration);

      loginContext.login();

      for (Principal p : subject.getPrincipals()) {
        System.out.println("principal " + p.getName());
      }
    } catch (LoginException e) {
      e.printStackTrace();
    }
  }
}
