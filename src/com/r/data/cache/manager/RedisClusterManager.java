package com.r.data.cache.manager;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import com.r.data.cache.constants.RedisConstants;

/**
 * Redis cluster manager
 *
 * @author Ranjith
 * @since 1.0
 */
public class RedisClusterManager
{
	private Properties properties;

	private static JedisCluster jedisCluster;

	private static RedisClusterManager instance;

	private RedisClusterManager(Properties properties) {
		this.properties = properties;
	}

	/**
	 *创建实例 获取配置文件 redis.properties 装配到properties
	 *
	 * @param: [properties]
	 * @return: com.r.data.cache.manager.RedisClusterManager
	 * @author: caiyf
	 * @date: 2019-9-4
	 */
	public static RedisClusterManager createInstance(Properties properties) {
		instance = new RedisClusterManager(properties);
		instance.connect();
		return instance;
	}

	/**
	 *获取实例
	 *
	 * @param: []
	 * @return: com.r.data.cache.manager.RedisClusterManager
	 * @author: caiyf
	 * @date: 2019-9-4
	 */
	public final static RedisClusterManager getInstance() throws Exception {
		return instance;
	}

	/**
	 *redis属性配置
	 *
	 * @param: []
	 * @return: void
	 * @author: caiyf
	 * @date: 2019-9-4
	 */
	public void connect() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		int maxActive = Integer.parseInt(properties.getProperty(RedisConstants.MAX_ACTIVE, RedisConstants.DEFAULT_MAX_ACTIVE_VALUE));
		poolConfig.setMaxTotal(maxActive);
		boolean testOnBorrow = Boolean.parseBoolean(properties.getProperty(RedisConstants.TEST_ONBORROW, RedisConstants.DEFAULT_TEST_ONBORROW_VALUE));
		poolConfig.setTestOnBorrow(testOnBorrow);
		boolean testOnReturn = Boolean.parseBoolean(properties.getProperty(RedisConstants.TEST_ONRETURN, RedisConstants.DEFAULT_TEST_ONRETURN_VALUE));
		poolConfig.setTestOnReturn(testOnReturn);
		int maxIdle = Integer.parseInt(properties.getProperty(RedisConstants.MAX_IDLE, RedisConstants.DEFAULT_MAX_ACTIVE_VALUE));
		poolConfig.setMaxIdle(maxIdle);
		int minIdle = Integer.parseInt(properties.getProperty(RedisConstants.MIN_IDLE, RedisConstants.DEFAULT_MIN_IDLE_VALUE));
		poolConfig.setMinIdle(minIdle);
		boolean testWhileIdle = Boolean.parseBoolean(properties.getProperty(RedisConstants.TEST_WHILEIDLE, RedisConstants.DEFAULT_TEST_WHILEIDLE_VALUE));
		poolConfig.setTestWhileIdle(testWhileIdle);
		int testNumPerEviction = Integer.parseInt(properties.getProperty(RedisConstants.TEST_NUMPEREVICTION, RedisConstants.DEFAULT_TEST_NUMPEREVICTION_VALUE));
		poolConfig.setNumTestsPerEvictionRun(testNumPerEviction);
		long timeBetweenEviction = Long.parseLong(properties.getProperty(RedisConstants.TIME_BETWEENEVICTION, RedisConstants.DEFAULT_TIME_BETWEENEVICTION_VALUE));
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEviction);
		jedisCluster = new JedisCluster(getJedisClusterNodesSet(properties.getProperty(RedisConstants.HOSTS, Protocol.DEFAULT_HOST.concat(":").concat(String.valueOf(Protocol.DEFAULT_PORT)))), poolConfig);
	}

	/**
	 *获取redis 集群节点 配置文件格式为 127.0.0.1:6379, 127.0.0.2:6379, 127.0.0.2:6380, ....
	 *
	 * @param: [hosts]
	 * @return: java.util.Set<redis.clients.jedis.HostAndPort>
	 * @author: caiyf
	 * @date: 2019-9-11
	 */
	private Set<HostAndPort> getJedisClusterNodesSet(String hosts) {
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		hosts = hosts.replaceAll("\\s", "");
		String[] hostPorts = hosts.split(",");
		for (String hostPort : hostPorts) {
			String[] hostPortArr = hostPort.split(":");
			nodes.add(new HostAndPort(hostPortArr[0], Integer.valueOf(hostPortArr[1])));
		}
		return nodes;
	}

	/**
	 *获取jedisCluster
	 *
	 * @param: []
	 * @return: redis.clients.jedis.JedisCluster
	 * @author: caiyf
	 * @date: 2019-9-11
	 */
	public JedisCluster getJedis() {
		return jedisCluster;
	}
}
