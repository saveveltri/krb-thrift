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
            System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
            System.setProperty("java.security.auth.login.config", "/tmp/krb5Login-hadoop.conf");

            final Configuration conf = new Configuration();
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", LocalFileSystem.class.getName());
            conf.set("hadoop.security.authentication", "KERBEROS");

            lc = new LoginContext("SampleClient", new TextCallbackHandler());
            lc.login();

            UserGroupInformation.setConfiguration(conf);

            UserGroupInformation.loginUserFromSubject(lc.getSubject());

            SecurityUtil.doAsCurrentUser(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {

                    FileSystem fs = FileSystem.get(new URI("hdfs://radicalbit-master:8020"), conf);
                    RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(new Path("hdfs://radicalbit-master:8020/"), true);

                    while (iterator.hasNext()) {
                        LocatedFileStatus lfs = iterator.next();
                        System.out.println(lfs.toString());
                    }

                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
