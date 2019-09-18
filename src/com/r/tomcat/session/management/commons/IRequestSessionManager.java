package com.r.tomcat.session.management.commons;

import java.io.IOException;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Session;

/**
 * IRequest session manager
 *
 * @author Ranjith
 * @since 1.0
 */
public interface IRequestSessionManager
{
  // get Session 持久化策略
	public String getSessionPersistPolicies();

	// set Session 持久化策略
	public void setSessionPersistPolicies(String sessionPersistPolicies);

	// get SaveOnChange 策略
	public boolean getSaveOnChange();

	// get AlwaysSaveAfterRequest 策略
	public boolean getAlwaysSaveAfterRequest();

	// 接口附带重写实现 没实际意义 新增监听
	public void addLifecycleListener(LifecycleListener listener);

	// 接口附带重写实现 没实际意义 查找监听
	public LifecycleListener[] findLifecycleListeners();

  // 接口附带重写实现 没实际意义 删除监听
	public void removeLifecycleListener(LifecycleListener listener);

	// 创建Session 重写
	public Session createSession(String requestedSessionId);

	// 创建一个空的Session 重写
	public Session createEmptySession();

	// 保存一个Session 重写
	public void add(Session session);

	// 查找一个Session 重写
	public Session findSession(String id) throws IOException;

	// 获取Redis中的Session
	public byte[] loadSessionDataFromRedis(String id) throws IOException;

	// 1.将SessionId与SessionData
  // 2.反序列化成
  // 3.SessionSerializationMetadata与RedisSessionDeserializedSessionContainer对象
  // 4.放入DeserializedSessionContainer容器之中
	public DeserializedSessionContainer sessionFromSerializedData(String id, byte[] data) throws IOException;

	// 将当前Session序列号成byte[]保存到Redis
	public void save(Session session) throws Exception;

	// 将当前Session序列号成byte[]保存到Redis 强力保存
	public void save(Session session, boolean forceSave) throws Exception;

	// 删除Redis中的Session信息
	public void remove(Session session);

	// 二参无效 无效方法
	public void remove(Session session, boolean update);

	// Session 值有变化都走这个方法 判断是Add还是Remove
	public void afterRequest();
}
