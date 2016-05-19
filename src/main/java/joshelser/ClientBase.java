/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package joshelser;

import com.beust.jcommander.Parameter;
import com.sun.security.auth.callback.TextCallbackHandler;
import joshelser.thrift.HdfsService;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSaslClientTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.sasl.Sasl;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

/**
 * Client configured to pass its UserGroupInformation/Kerberos credentials across a Thrift RPC
 */
public class ClientBase {
  private static final Logger log = LoggerFactory.getLogger(ClientBase.class);

  static void runClient(String server, int port, String primary, String instance, String dir) throws Exception {

    // Open up a socket to the server:port
    TTransport transport = new TSocket(server, port);
    Map<String,String> saslProperties = new HashMap<String,String>();
    // Use authorization and confidentiality
    saslProperties.put(Sasl.QOP, "auth-conf");

    log.info("Security is enabled: {}", UserGroupInformation.isSecurityEnabled());

    //////////

    Subject subject = Subject.getSubject(AccessController.getContext());

    if (subject != null) {
      log.info("Current subject: {}", subject);
      log.info("Current ugi from subject: {}", UserGroupInformation.getUGIFromSubject(subject));
    }

    //////////


    // Log in via UGI, ensures we have logged in with our KRB credentials
    UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
    log.info("Current user: {}", currentUser);

    // SASL client transport -- does the Kerberos lifting for us
    TSaslClientTransport saslTransport = new TSaslClientTransport(
        "GSSAPI", // tell SASL to use GSSAPI, which supports Kerberos
        null, // authorizationid - null
        primary, // kerberos primary for server - "myprincipal" in myprincipal/my.server.com@MY.REALM
        instance, // kerberos instance for server - "my.server.com" in myprincipal/my.server.com@MY.REALM
        saslProperties, // Properties set, above
        null, // callback handler - null
        transport); // underlying transport

    // Make sure the transport is opened as the user we logged in as
    TUGIAssumingTransport ugiTransport = new TUGIAssumingTransport(saslTransport, currentUser);

    // Setup our thrift client to our custom thrift service
    HdfsService.Client client = new HdfsService.Client(new TBinaryProtocol(ugiTransport));

    // Open the transport
    ugiTransport.open();

    // Invoke the RPC
    String response = client.ls(dir);

    // Print out the result
    System.out.println("$ ls " + dir + "\n" + response);

    // Close the transport (don't leak resources)
    transport.close();
  }
}
