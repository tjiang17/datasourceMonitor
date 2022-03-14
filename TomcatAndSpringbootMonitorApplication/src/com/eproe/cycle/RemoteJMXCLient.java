package com.eproe.cycle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class RemoteJMXCLient {
  public MBeanServerConnection getMBeanServerConnectionFromRemoteJMXClient(Map<String, String> itemInfo) {
    String userpass = itemInfo.get("--userpass");
    String hostport = itemInfo.get("--hostport");
    String hostname = hostport;
    int index = hostport.indexOf(':');
    if (index > 0)
      hostname = hostname.substring(0, index); 
    MBeanServerConnection mbsc = null;
    try {
      JMXServiceURL rmiurl = new JMXServiceURL(
          "service:jmx:rmi://" + hostport + "/jndi/rmi://" + hostport + "/jmxrmi");
      JMXConnector jmxc = JMXConnectorFactory.connect(rmiurl, formatCredentials(userpass));
      mbsc = jmxc.getMBeanServerConnection();
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return mbsc;
  }
  
  public Map formatCredentials(String userpass) {
    Map<Object, Object> env = null;
    if (userpass == null || userpass.equals("-"))
      return env; 
    int index = userpass.indexOf(':');
    if (index <= 0)
      throw new RuntimeException("Unable to parse: " + userpass); 
    String[] creds = { userpass.substring(0, index), userpass.substring(index + 1) };
    env = new HashMap<Object, Object>(1);
    env.put("jmx.remote.credentials", creds);
    return env;
  }
}
