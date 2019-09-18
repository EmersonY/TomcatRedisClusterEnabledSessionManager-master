package com.r.tomcat.session.management.commons;

/**
 * ISerializer
 *
 * @author Ranjith
 * @since 4.15.9.4_Tomcat
 */
public interface ISerializer
{
	// 实现序列号ClassLoader
  public void setClassLoader(ClassLoader loader);

  // Session内容（key value）序列化 再MD5序列化
	public byte[] attributesHashFrom(IRequestSession session) throws Exception;

	// 1.Session内容（key value）序列化 再MD5序列化
  // 2.把步骤1的结果装在SessionSerializationMetadata里面
  // 3.序列化SessionSerializationMetadata
	public byte[] serializeFrom(IRequestSession session, SessionSerializationMetadata metadata) throws Exception;

	// 1.Redis利用key查询出数据value
  // 2.一参value 二参空Session 三参空实体容器（装Seesion key value Md5 序列化后的结果）
	public void deserializeInto(byte[] data, IRequestSession session, SessionSerializationMetadata metadata) throws Exception;
}
