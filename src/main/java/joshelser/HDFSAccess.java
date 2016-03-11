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

        LoginContext lc;

        try {

            lc = new LoginContext("ThriftClient", new TextCallbackHandler());
            lc.login();

            final FileSystem fs = FileSystem.get(SecurityUtils.hdConf);

            System.out.println("Security is enabled " + SecurityUtils.isSecurityEnabled());

            //UserGroupInformation.loginUserFromSubject(lc.getSubject());
            UserGroupInformation.loginUserFromKeytab("hdfs-radicalbit_test_cluster@RADICALBIT.IO", "hdfs.headless.keytab");

            System.out.println("UserGroupInformation.getCurrentUser() " + UserGroupInformation.getCurrentUser());


            SecurityUtils.runSecured(new SecurityUtils.FlinkSecuredRunner<Void>(){

                @Override
                public Void run() throws Exception {

                    FileStatus[] files = fs.listStatus(new Path("/"));

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
