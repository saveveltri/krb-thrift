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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

/**
 * Client configured to pass its UserGroupInformation/Kerberos credentials across a Thrift RPC
 */
public class Client extends ClientBase implements ServiceBase {
  private static final Logger log = LoggerFactory.getLogger(Client.class);

  private static class Opts extends ParseBase {
    @Parameter(names = {"-s", "--server"}, required = true, description = "Hostname of Thrift server")
    private String server;

    @Parameter(names = {"--port"}, required = false, description = "Port of the Thrift server, defaults to ")
    private int port = DEFAULT_THRIFT_SERVER_PORT;

    @Parameter(names = {"-p", "--primary"}, required = true, description = "Leading component of the Kerberos principal for the server")
    private String primary;

    @Parameter(names = {"-i", "--instance"}, required = false, description = "Second component of the Kerberos principal for the server")
    private String instance;

    @Parameter(names = {"-d", "--dir"}, required = false, description = "HDFS directory to perform `ls` on")
    private String dir = "/";
  }

  public static void main(final String[] args) throws Exception {
    Opts opts = new Opts();
//
//    // Parse the options
    opts.parseArgs(Client.class, args);

    runClient(opts.server, opts.port, opts.primary, opts.instance, opts.dir);
  }
}
