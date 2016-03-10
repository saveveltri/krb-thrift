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
            final Configuration conf = new Configuration();
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", LocalFileSystem.class.getName());
//            conf.set("hadoop.security.authentication", "KERBEROS");
            final FileSystem fs = FileSystem.get(conf);

            lc = new LoginContext("ThriftClient", new TextCallbackHandler());
            lc.login();

            UserGroupInformation.setConfiguration(conf);

            UserGroupInformation.loginUserFromSubject(lc.getSubject());

            SecurityUtil.doAsCurrentUser(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {

                    FileStatus[] files = fs.listStatus(new Path("/"));

                    for (FileStatus fileStatus : files) {
                        System.out.println(fileStatus.toString());
                    }

                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
