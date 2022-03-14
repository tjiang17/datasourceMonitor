package com.eproe.cycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;

public class TomcatAndSpringbootMonitorApplication {
	enum RunMode {
		local, remote;
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			usage("help");
			System.exit(0);
		}
		ExcuteJMXQuery excuteJMXQuery = new ExcuteJMXQuery();
		Map<String, String> itemInfo = new HashMap<String, String>();
		try {
			if (args[0].equalsIgnoreCase("local")) {
				checkArgs(args, itemInfo);
				localMode(args, excuteJMXQuery, itemInfo);
			} else if (args[0].equalsIgnoreCase("remote")) {
				checkArgs(args, itemInfo);
				remoteMode(args, excuteJMXQuery, itemInfo);
			} else {
				usage("help");
			}
		} catch (Exception e) {
			traceLog();
			e.printStackTrace();
			usage("help");
		}
	}

	private static void traceLog() {
	}

	private static void checkArgs(String[] args, Map<String, String> itemInfo) {
		for (int i = 1; i < args.length; i++) {
			try {
				itemInfo.put(args[i].split("=")[0], args[i].split("=")[1]);
			} catch (Exception e) {
				System.err.println("Wrong args input, please check then input again");
				e.printStackTrace();
			}
		}
		if (args.length <= 1) {
			usage("");
			System.exit(0);
		}
	}

	public static void remoteMode(String[] args, ExcuteJMXQuery excuteJMXQuery, Map<String, String> itemInfo) {
		MBeanServerConnection mBeanServerConnectionFromRemoteJMXClient = null;
		try {
			mBeanServerConnectionFromRemoteJMXClient = (new RemoteJMXCLient())
					.getMBeanServerConnectionFromRemoteJMXClient(itemInfo);
			excuteAndReturnResult(args, excuteJMXQuery, itemInfo, mBeanServerConnectionFromRemoteJMXClient);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void localMode(String[] args, ExcuteJMXQuery excuteJMXQuery, Map<String, String> itemInfo) {
		MBeanServerConnection mBeanServerConnectionFromLocalJMXClient = null;
		try {
			mBeanServerConnectionFromLocalJMXClient = (new LocalJMXClient())
					.getMBeanServerConnectionFromLocalJMXClient(itemInfo.get("--pid"));
			excuteAndReturnResult(args, excuteJMXQuery, itemInfo, mBeanServerConnectionFromLocalJMXClient);
		} catch (Exception e) {
			System.err.println("Wrong args input, please check then input again");
			usage("help");
			e.printStackTrace();
		}
	}

	enum Options {
		serverInfo, port, maxHeap, usedHeap, maxThreads, currentThreadCount, maxConnections, currentthreadcount,
		minSpareThreads, dataSource, servlet, help;
	}

	private static void excuteAndReturnResult(String[] args, ExcuteJMXQuery excuteJMXQuery,
			Map<String, String> itemInfo, MBeanServerConnection mBeanServerConnectionFromLocalJMXClient)
			throws Exception {
		HikariDatSourceMonitor hikariDatSourceMonitor = new HikariDatSourceMonitor();
		List<String[]> attributeDBCP2, attributeDBCP, attributeDruid, attributeHikari = null;
		String[] modelerType;
		ArrayList<String> execute;
		Set<String> finalResult, webModuleSet;
		ArrayList<String> queryResults = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient, null, args);
		String option = itemInfo.get("--option");
		if (option == null)
			option = "all";
		int instead = 0;
		if (option.equalsIgnoreCase("serverInfo"))
			instead = 10;
		if (option.equalsIgnoreCase("port"))
			instead = 20;
		if (option.equalsIgnoreCase("maxHeap"))
			instead = 30;
		if (option.equalsIgnoreCase("usedHeap"))
			instead = 40;
		if (option.equalsIgnoreCase("maxThreads"))
			instead = 50;
		if (option.equalsIgnoreCase("currentThreadCount"))
			instead = 60;
		if (option.equalsIgnoreCase("maxConnections"))
			instead = 70;
		if (option.equalsIgnoreCase("minSpareThreads"))
			instead = 80;
		if (option.equalsIgnoreCase("dataSource"))
			instead = 90;
		if (option.equalsIgnoreCase("servlet"))
			instead = 100;
		Options[] values = Options.values();
		switch (instead) {
		case 10:
			for (String queryResult : queryResults) {
				if (queryResult.contains("type=Server")) {
					String[] serverInfo = { "serverInfo" };
					ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
							queryResult, serverInfo);
					for (String s : arrayList)
						System.out.println(s);
				}
			}
			return;
		case 20:
			for (String queryResult : queryResults) {
				if (queryResult.contains("type=Connector")) {
					StringBuffer result = new StringBuffer("");
					List<String[]> attributePort = (List) new ArrayList<String>();
					String[] port = { "port" };
					String[] protocol = { "protocol" };
					attributePort.add(port);
					attributePort.add(protocol);
					for (String[] attribute : attributePort) {
						ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
								queryResult, attribute);
						for (String s : arrayList) {
							if (s.contains("HTTP") || s.contains("http"))
								continue;
							result.append(s).append(" ");
						}
					}
					System.out.println(result);
				}
			}
			return;
		case 30:
			for (String queryResult : queryResults) {
				if (queryResult.contains("java.lang:type=Memory")) {
					String[] port = { "HeapMemoryUsage" };
					ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
							queryResult, port);
					for (String s : arrayList) {
						String[] items = s.split("\n");
						byte b;
						int i;
						String[] arrayOfString1;
						for (i = (arrayOfString1 = items).length, b = 0; b < i;) {
							String item = arrayOfString1[b];
							if (item.contains("max"))
								System.out.println(String.valueOf(item) + " b");
							b++;
						}
					}
				}
			}
			return;
		case 40:
			for (String queryResult : queryResults) {
				if (queryResult.contains("java.lang:type=Memory")) {
					String[] usedHeap = { "HeapMemoryUsage" };
					ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
							queryResult, usedHeap);
					for (String s : arrayList) {
						String[] items = s.split("\n");
						byte b;
						int i;
						String[] arrayOfString1;
						for (i = (arrayOfString1 = items).length, b = 0; b < i;) {
							String item = arrayOfString1[b];
							if (item.contains("used"))
								System.out.println(String.valueOf(item) + " b");
							b++;
						}
					}
				}
			}
			return;
		case 50:
			for (String queryResult : queryResults) {
				if (queryResult.contains("type=ThreadPool") && queryResult.contains("http-")) {
					String[] maxThreads = { "maxThreads" };
					ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
							queryResult, maxThreads);
					for (String s : arrayList)
						System.out.println(s);
					break;
				}
			}
			return;
		case 60:
			for (String queryResult : queryResults) {
				if (queryResult.contains("type=ThreadPool") && queryResult.contains("http-")) {
					String[] currentThreadCount = { "currentThreadCount" };
					ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
							queryResult, currentThreadCount);
					for (String s : arrayList)
						System.out.println(s);
					break;
				}
			}
			return;
		case 70:
			for (String queryResult : queryResults) {
				if (queryResult.contains("type=ThreadPool") && queryResult.contains("http-")) {
					String[] maxConnections = { "maxConnections" };
					ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
							queryResult, maxConnections);
					for (String s : arrayList)
						System.out.println(s);
					break;
				}
			}
			return;
		case 80:
			for (String queryResult : queryResults) {
				if (queryResult.contains("type=ThreadPool") && queryResult.contains("http-")) {
					String[] minSpareThreads = { "minSpareThreads" };
					ArrayList<String> arrayList = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient,
							queryResult, minSpareThreads);
					for (String s : arrayList)
						System.out.println(s);
				}
			}
			return;
		case 90:
			attributeDBCP2 = (List) new ArrayList<String>();
			attributeDBCP2.add(new String[] { "maxTotal" });
			attributeDBCP2.add(new String[] { "minIdle" });
			attributeDBCP2.add(new String[] { "numActive" });
			attributeDBCP2.add(new String[] { "testWhileIdle" });
			attributeDBCP2.add(new String[] { "testOnBorrow" });
			attributeDBCP2.add(new String[] { "timeBetweenEvictionRunsMillis" });
			attributeDBCP2.add(new String[] { "validationQuery" });
			attributeDBCP2.add(new String[] { "removeAbandonedOnBorrow" });
			attributeDBCP2.add(new String[] { "removeAbandonedTimeout" });
			attributeDBCP2.add(new String[] { "modelerType" });
			attributeDBCP = (List) new ArrayList<String>();
			attributeDBCP.add(new String[] { "maxActive" });
			attributeDBCP.add(new String[] { "minIdle" });
			attributeDBCP.add(new String[] { "numActive" });
			attributeDBCP.add(new String[] { "testWhileIdle" });
			attributeDBCP.add(new String[] { "testOnBorrow" });
			attributeDBCP.add(new String[] { "timeBetweenEvictionRunsMillis" });
			attributeDBCP.add(new String[] { "validationQuery" });
			attributeDBCP.add(new String[] { "removeAbandoned" });
			attributeDBCP.add(new String[] { "removeAbandonedTimeout" });
			attributeDBCP.add(new String[] { "modelerType" });
			attributeDruid = (List) new ArrayList<String>();
			attributeDruid.add(new String[] { "maxActive" });
			attributeDruid.add(new String[] { "minIdle" });
			attributeDruid.add(new String[] { "activeCount" });
			attributeDruid.add(new String[] { "testWhileIdle" });
			attributeDruid.add(new String[] { "testOnBorrow" });
			attributeDruid.add(new String[] { "timeBetweenEvictionRunsMillis" });
			attributeDruid.add(new String[] { "validationQuery" });
			attributeDruid.add(new String[] { "removeAbandoned" });
			attributeDruid.add(new String[] { "removeAbandonedTimeout" });
			attributeDruid.add(new String[] { "modelerType" });

			modelerType = new String[] { "modelerType" };
			execute = null;
			finalResult = new HashSet<String>();
			for (String queryResult : queryResults) {
				if (queryResult.contains("class=javax.sql.DataSource,context=/")
						&& !queryResult.contains("type=Resource")) {
					execute = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, modelerType);
					if (execute.toString().contains("DruidDataSource")) {
						StringBuffer result = new StringBuffer(":");
						String JNDIname = queryResult.split(",")[3].split("=")[1].replace("\"", "");
						result.append(JNDIname).append(":");
						for (String[] command : attributeDruid) {
							execute = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient, queryResult,
									command);
							for (String s : execute)
								result.append(s).append(":");
						}
						finalResult.add(result.toString());
					}
					if (execute.toString().contains("dbcp2")) {
						StringBuffer result = new StringBuffer(":");
						String JNDIname = queryResult.split(",")[3].split("=")[1].replace("\"", "");
						result.append(JNDIname).append(":");
						for (String[] command : attributeDBCP2) {
							execute = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient, queryResult,
									command);
							for (String s : execute)
								result.append(s).append(":");
						}
						finalResult.add(result.toString());
					}
					if (execute.toString().contains("org.apache.tomcat.dbcp.dbcp.BasicDataSource")) {
						StringBuffer result = new StringBuffer(":");
						String JNDIname = queryResult.split(",")[3].split("=")[1].replace("\"", "");
						result.append(JNDIname).append(":");
						for (String[] command : attributeDBCP) {
							execute = excuteJMXQuery.execute(mBeanServerConnectionFromLocalJMXClient, queryResult,
									command);
							for (String s : execute) {
								if (s.contains("not found")) {
									result.append("none").append(":");
									continue;
								}
								result.append(s).append(":");
							}
						}
						finalResult.add(result.toString());
					}
				}

				// Hikari datasource
				if (queryResult.contains("hikari")) {
					if (queryResult.contains("Pool ")) {
						String[] activeConnections = new String[] { "ActiveConnections" };
						String activeConnectionsStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, activeConnections)
								.get(0);
						hikariDatSourceMonitor.setActiveConnections(activeConnectionsStr);
					} else if (queryResult.contains("PoolConfig")) {
						// attributeHikari.add(new String[] { "PoolName" });
						// attributeHikari.add(new String[] { "MaximumPoolSize" });
						// attributeHikari.add(new String[] { "MinimumIdle" });
						// attributeHikari.add(new String[] { "MaxLifetime" });
						// StringBuffer result = new StringBuffer(":");
						// String JNDIname = queryResult.split(",")[3].split(
						// "=")[1].replace("\"", "");
						// result.append(JNDIname).append(":");
						String[] PoolName = new String[] { "PoolName" };
						String[] MaximumPoolSize = new String[] { "MaximumPoolSize" };
						String[] MinimumIdle = new String[] { "MinimumIdle" };
						String[] MaxLifetime = new String[] { "MaxLifetime" };

						String PoolNameStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, PoolName).get(0);
						String MaximumPoolSizeStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, MaximumPoolSize).get(0);
						String MinimumIdleStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, MinimumIdle).get(0);
						String MaxLifetimeStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, MaxLifetime).get(0);
						hikariDatSourceMonitor.setPoolName(PoolNameStr);
						hikariDatSourceMonitor.setMaximumPoolSize(MaximumPoolSizeStr);
						hikariDatSourceMonitor.setMinimumIdle(MinimumIdleStr);
						hikariDatSourceMonitor.setMaxLifeTime(MaxLifetimeStr);
						// Ex: com.zaxxer.hikari:name=dataSource,type=HikariDataSource
						// com.zaxxer.hikari:type=PoolConfig (test111)
						hikariDatSourceMonitor.setDataSourceType(queryResult.split(":")[0]);
						System.out.println(hikariDatSourceMonitor.toString());
					} else if (queryResult.contains("dataSource")) {
						String[] PoolName = new String[] { "PoolName" };
						String[] MaximumPoolSize = new String[] { "MaximumPoolSize" };
						String[] MinimumIdle = new String[] { "MinimumIdle" };
						String[] MaxLifetime = new String[] { "MaxLifetime" };

						String PoolNameStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, PoolName).get(0);
						String MaximumPoolSizeStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, MaximumPoolSize).get(0);
						String MinimumIdleStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, MinimumIdle).get(0);
						String MaxLifetimeStr = excuteJMXQuery
								.execute(mBeanServerConnectionFromLocalJMXClient, queryResult, MaxLifetime).get(0);
						hikariDatSourceMonitor.setPoolName(PoolNameStr);
						hikariDatSourceMonitor.setMaximumPoolSize(MaximumPoolSizeStr);
						hikariDatSourceMonitor.setMinimumIdle(MinimumIdleStr);
						hikariDatSourceMonitor.setMaxLifeTime(MaxLifetimeStr);
						hikariDatSourceMonitor.setDataSourceType(queryResult.split(":")[0]);
						System.out.println(hikariDatSourceMonitor.toString());
						if (hikariDatSourceMonitor.getActiveConnections() == null) {
							System.err.println(
									"You mey need add: 'spring.datasource.hikari.register-mbeans=true' in your application.properties, and if dependency is allowed add the 'spring-boot-starter-actuator' in your pom.xml is better \n");
						}
					}
				}

			}
			System.out.println(
					"DBCP2                                                                                                                                             ");
			System.out.println(
					"jndiname:MaxTotal:MinIdle:NumActive:TestWhileIdle:TestOnBorrow:TimeBetweenEvictionRunsMillis:ValidationQuery:removeAbandonedOnBorrow:RemoveAbandonedTimeout:dataSourceType     ");
			System.out.println(
					"Druid                                                                                                                                            ");
			System.out.println(
					"jndiname:maxActive:minIdle:activeCount:testWhileIdle:testOnBorrow:timeBetweenEvictionRunsMillis:validationQuery:removeAbandoned:removeAbandonedTimeout:dataSourceType    ");
			System.out.println(
					"Hikari                                                                                                                                            ");
			System.out.println(
					"poolName:maximumPoolSize:minimumIdle:activeConnections:testWhileIdle:testOnBorrow:timeBetweenEvictionRunsMillis:connection-test-query:removeAbandoned:maxLifeTime:dataSourceType    ");
			for (String string : finalResult)
				System.out.println(string);
			return;
		case 100:
			webModuleSet = new HashSet<String>();
			for (String queryResult : queryResults) {
				if (queryResult.contains("j2eeType=Servlet")) {
					String webModule = queryResult.split(",")[2].split("=")[1];
					webModuleSet.add(webModule);
				}
			}
			for (String s : webModuleSet)
				System.out.println(s);
			return;
		}
		System.out.println(
				"java -Xbootclasspath/a:$JAVA_HOME/lib/tools.jar -jar local|remote --pid=<pid> [--option=xxx]");
		System.out.println("Ex: --option=serverInfo");
		System.out.println("Ex: --option=port");
		System.out.println("Ex: --option=maxHeap");
		System.out.println("Ex: --option=usedHeap");
		System.out.println("Ex: --option=maxThreads");
		System.out.println("Ex: --option=currentThreadCount");
		System.out.println("Ex: --option=maxConnections");
		System.out.println("Ex: --option=minSpareThreads");
		System.out.println("Ex: --option=dataSource");
		System.out.println("Ex: --option=servlet");
	}

	private static void usage(String help) {
		System.out.println("Ex:");
		System.out.println(
				"java -Xbootclasspath/a:$JAVA_HOME/lib/tools.jar -jar <MyName>.jar local|remote --pid=<pid> --option=servlet");
		System.out.println(
				"java -jar <MyName>.jar remote --userpass=<user:pass>(if has not userpass,set it empty) --hostport=<host:port> --option=servlet");
		System.out.println("Fetch all '--option' args ");
		System.out.println("java -jar <MyName>.jar help");
		System.err.println("Wrong args input, please check then input again");
		System.out.println("java -jar local --pid=<pid> [--option=xxx]");
		System.out.println("Ex: --option=serverInfo");
		System.out.println("Ex: --option=port");
		System.out.println("Ex: --option=maxHeap");
		System.out.println("Ex: --option=usedHeap");
		System.out.println("Ex: --option=maxThreads");
		System.out.println("Ex: --option=currentThreadCount");
		System.out.println("Ex: --option=maxConnections");
		System.out.println("Ex: --option=minSpareThreads");
		System.out.println("Ex: --option=dataSource");
		System.out.println("Ex: --option=servlet");
		System.out.println(
				"DBCP2                                                                                                                                             ");
		System.out.println(
				"jndiname:MaxTotal:MinIdle:NumActive:TestWhileIdle:TestOnBorrow:TimeBetweenEvictionRunsMillis:ValidationQuery:removeAbandonedOnBorrow:RemoveAbandonedTimeout:dataSourceType     ");
		System.out.println(
				"DBCP                                                                                                                                             ");
		System.out.println(
				"jndiname:MaxIdle:MinIdle:NumActive:TestWhileIdle:TestOnBorrow:TimeBetweenEvictionRunsMillis:ValidationQuery:RemoveAbandoned:RemoveAbandonedTimeout:dataSourceType      ");
		System.out.println(
				"Druid                                                                                                                                            ");
		System.out.println(
				"jndiname:maxActive:minIdle:activeCount:testWhileIdle:testOnBorrow:timeBetweenEvictionRunsMillis:validationQuery:removeAbandoned:removeAbandonedTimeout:dataSourceType    ");
	}
}
