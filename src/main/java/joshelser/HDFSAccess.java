package joshelser;

import com.sun.security.auth.callback.TextCallbackHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;

import javax.security.auth.login.LoginContext;
import java.net.URI;
import java.security.PrivilegedExceptionAction;

/**
 * export HADOOP_CONF_DIR=/etc/hadoop/conf/
 */
public class HDFSAccess {

    public static void main(String[] args) {

        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
        System.setProperty("java.security.auth.login.config", "/Users/robbenti/develop/radicalbit/playground/krb-thrift/src/main/resources/krb5Login.conf");

        LoginContext lc;

        final Configuration hdConf = new Configuration();
//        hdConf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
//        hdConf.set("fs.file.impl", LocalFileSystem.class.getName());
//        hdConf.set("hadoop.security.authentication", "KERBEROS");

        try {

//            lc = new LoginContext("ThriftClient", new TextCallbackHandler());
//            lc.login();

            System.out.println("Security is enabled " + SecurityUtils.isSecurityEnabled(hdConf));

            //UserGroupInformation.loginUserFromSubject(lc.getSubject());
            UserGroupInformation.loginUserFromKeytab("hdfs-radicalbit_test_cluster@RADICALBIT.IO",
                    "/Users/robbenti/develop/radicalbit/playground/krb-thrift/hdfs.headless.keytab");

            System.out.println("UserGroupInformation.getCurrentUser() " + UserGroupInformation.getCurrentUser());
            System.out.println("UserGroupInformation.getLoginUser() " + UserGroupInformation.getLoginUser());

            UserGroupInformation proxyUser = UserGroupInformation.createProxyUser("roberto.bentivoglio@RADICALBIT.IO",
                    UserGroupInformation.getCurrentUser());
            proxyUser.doAs(new PrivilegedExceptionAction<Void>() {
//            UserGroupInformation.getCurrentUser().doAs(new PrivilegedExceptionAction<Void>() {
//            SecurityUtils.runSecured(hdConf, new SecurityUtils.FlinkSecuredRunner<Void>() {

                @Override
                public Void run() throws Exception {

                    UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
                    System.out.println("UserGroupInformation.getCurrentUser() " + ugi);
                    FileSystem fs = FileSystem.get(new URI("hdfs://radicalbit-master:8020"), hdConf);
                    FileStatus[] files = fs.listStatus(new Path("hdfs://radicalbit-master:8020/"));

                    for (FileStatus fileStatus : files) {
                        System.out.println(fileStatus.toString());
                    }

                    return null;
                }
            });

//            //SecurityUtil.doAsCurrentUser(new PrivilegedExceptionAction<Object>() {
//            UserGroupInformation.getCurrentUser().doAs(new PrivilegedExceptionAction<Object>() {
//                public Object run() throws Exception {
//
//
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
