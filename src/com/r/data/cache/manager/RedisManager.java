package com.r.data.cache.manager;

import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import com.r.data.cache.constants.RedisConstants;

/**
 * Redis manager
 *
 * @author Ranjith
 * @since 1.0
 */
public class RedisManager
{
	private static RedisManager instance;

	private static JedisPool pool;

	private Properties properties;

	private RedisManager(Properties properties) {
		this.properties = properties;
	}

	public static RedisManager createInstance(Properties properties) {
		instance = new RedisManager(properties);
		instance.connect();
		return instance;
	}

	public final static RedisManager getInstance() throws Exception {
		return instance;
	}

	public void connect() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		int maxActive = Integer.parseInt(properties.getProperty(RedisConstants.MAX_ACTIVE, RedisConstants.DEFAULT_MAX_ACTIVE_VALUE));
		poolConfig.setMaxTotal(maxActive);
		boolean testOnBorrow = Boolean.parseBoolean(properties.getProperty(RedisConstants.TEST_ONBORROW, RedisConstants.DEFAULT_TEST_ONBORROW_VALUE));
		poolConfig.setTestOnBorrow(testOnBorrow);
		boolean testOnReturn = Boolean.parseBoolean(properties.getProperty(RedisConstants.TEST_ONRETURN, RedisConstants.DEFAULT_TEST_ONRETURN_VALUE));
		poolConfig.setTestOnReturn(testOnReturn);
		int maxIdle = Integer.parseInt(properties.getProperty(RedisConstants.MAX_ACTIVE, RedisConstants.DEFAULT_MAX_ACTIVE_VALUE));
		poolConfig.setMaxIdle(maxIdle);
		int minIdle = Integer.parseInt(properties.getProperty(RedisConstants.MIN_IDLE, RedisConstants.DEFAULT_MIN_IDLE_VALUE));
		poolConfig.setMinIdle(minIdle);
		boolean testWhileIdle = Boolean.parseBoolean(properties.getProperty(RedisConstants.TEST_WHILEIDLE, RedisConstants.DEFAULT_TEST_WHILEIDLE_VALUE));
		poolConfig.setTestWhileIdle(testWhileIdle);
		int testNumPerEviction = Integer.parseInt(properties.getProperty(RedisConstants.TEST_NUMPEREVICTION, RedisConstants.DEFAULT_TEST_NUMPEREVICTION_VALUE));
		poolConfig.setNumTestsPerEvictionRun(testNumPerEviction);
		long timeBetweenEviction = Long.parseLong(properties.getProperty(RedisConstants.TIME_BETWEENEVICTION, RedisConstants.DEFAULT_TIME_BETWEENEVICTION_VALUE));
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEviction);
		String hosts = properties.getProperty(RedisConstants.HOSTS, Protocol.DEFAULT_HOST.concat(":").concat(String.valueOf(Protocol.DEFAULT_PORT)));
		String host = null;
		int port = 0;
		hosts = hosts.replaceAll("\\s", "");
		String[] hostPorts = hosts.split(",");
		for (String hostPort : hostPorts) {
			String[] hostPortArr = hostPort.split(":");
			host = hostPortArr[0];
			port = Integer.valueOf(hostPortArr[1]);
			if (!host.isEmpty() && port != 0) {
				break;
			}
		}
		String password = properties.getProperty(RedisConstants.PASSWORD);
		if (password == null || password == "" || password.isEmpty()) {
			pool = new JedisPool(poolConfig, host, port);
		} else {
			pool = new JedisPool(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, password);
		}
	}

	public void release() {
		pool.destroy();
	}

	public Jedis getJedis() {
		return pool.getResource();
	}
}
