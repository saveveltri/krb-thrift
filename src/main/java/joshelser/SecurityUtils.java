package joshelser;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivilegedExceptionAction;

/**
 * A utility class that lets program code run in a security context provided by the
 * Hadoop security user groups.
 *
 * The secure context will for example pick up authentication information from Kerberos.
 */
public final class SecurityUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);

    // load Hadoop configuration when loading the security utils.
    private static Configuration hdConf = new Configuration();


    public static boolean isSecurityEnabled() {
        UserGroupInformation.setConfiguration(hdConf);
        return UserGroupInformation.isSecurityEnabled();
    }

    public static <T> T runSecured(final FlinkSecuredRunner<T> runner) throws Exception {
        UserGroupInformation.setConfiguration(hdConf);
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        if (!ugi.hasKerberosCredentials()) {
            LOG.error("Security is enabled but no Kerberos credentials have been found. " +
                    "You may authenticate using the kinit command.");
        }
        return ugi.doAs(new PrivilegedExceptionAction<T>() {
            @Override
            public T run() throws Exception {
                return runner.run(hdConf);
            }
        });
    }

    public interface FlinkSecuredRunner<T> {
        T run(Configuration hdConf) throws Exception;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private SecurityUtils() {
        throw new RuntimeException();
    }
}
