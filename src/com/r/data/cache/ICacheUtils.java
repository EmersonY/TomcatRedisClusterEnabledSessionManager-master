package com.r.data.cache;

/**
 *Redis 增删改查方法
 *
 * @param:
 * @return:
 * @author: caiyf
 * @date: 2019-9-11
 */
public interface ICacheUtils
{
  // 状态是否可用 redis 连接是否错误
	public boolean isAvailable();

	// redis set 方法 重复set 无效
	public void setByteArray(byte[] key, byte[] value);

	// redis get 方法
	public byte[] getByteArray(String key);

	// redis delete 方法
	public void deleteKey(String key);

  // redis setnx 方法 如果有值 不变 没值 值替换
	public Long setStringIfKeyNotExists(byte[] key, byte[] value);

	// redis expire 方法 设置值 + 过期时间
	public void expire(byte[] key, int ttl);
}
