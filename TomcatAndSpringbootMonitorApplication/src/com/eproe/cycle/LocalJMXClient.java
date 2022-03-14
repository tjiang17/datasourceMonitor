package com.eproe.cycle;

import java.io.File;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.VirtualMachine;

public class LocalJMXClient {
	public MBeanServerConnection getMBeanServerConnectionFromLocalJMXClient(
			String pid) {
		MBeanServerConnection mbsc = null;
		try {
			JMXConnector jmxc = null;
			VirtualMachine vm = VirtualMachine.attach(pid);
			Set<String> stringPropertyNames = vm.getAgentProperties()
												.stringPropertyNames();
			String connectorAddr = vm	.getAgentProperties()
										.getProperty(
												"com.sun.management.jmxremote.localConnectorAddress");
			if (connectorAddr == null) {
				String agent = String.valueOf(
						vm.getSystemProperties().getProperty("java.home"))
						+ File.separator + "lib" + File.separator
						+ "management-agent.jar";
				vm.loadAgent(agent);
				connectorAddr = vm	.getAgentProperties()
									.getProperty(
											"com.sun.management.jmxremote.localConnectorAddress");
			}
			JMXServiceURL serviceURL = new JMXServiceURL(connectorAddr);
			jmxc = JMXConnectorFactory.connect(serviceURL);
			mbsc = jmxc.getMBeanServerConnection();
		} catch (Exception e) {
			System.err.println(
					"Usage: java -cp ${JAVA_HOME}/lib/tools.jar -jar <myname>.jar ...");
			e.printStackTrace();
		}
		return mbsc;
	}
}
