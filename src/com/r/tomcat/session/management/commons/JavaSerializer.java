package com.r.tomcat.session.management.commons;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.catalina.util.CustomObjectInputStream;

/**
 * Java serializer ISerializer 接口实现
 *
 * @author Ranjith
 * @since 1.0
 */
public class JavaSerializer implements ISerializer
{
	private ClassLoader loader;

	@Override
	public void setClassLoader(ClassLoader loader) {
		this.loader = loader;
	}

	/**
	 *属性序列化
	 *
	 * @param: [session]
	 * @return: byte[]
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	public byte[] attributesHashFrom(IRequestSession session) throws Exception {
		byte[] serialized = null;
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		for (Enumeration<String> enumerator = session.getAttributeNames(); enumerator.hasMoreElements();) {
			String key = enumerator.nextElement();
			attributes.put(key, session.getAttribute(key));
		}
		try (
		  ByteArrayOutputStream bos = new ByteArrayOutputStream();
		  ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));) {
			oos.writeUnshared(attributes);
			oos.flush();
			serialized = bos.toByteArray();
		}
		MessageDigest digester = null;
		try {
			digester = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			throw new Exception("Unable to get MessageDigest instance for MD5");
		}
		return digester.digest(serialized);
	}

	/**
	 *session 序列化
	 *
	 * @param: [session, metadata]
	 * @return: byte[]
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	@Override
	public byte[] serializeFrom(IRequestSession session, SessionSerializationMetadata metadata) throws Exception {
		byte[] serialized = null;
		try (
		  ByteArrayOutputStream bos = new ByteArrayOutputStream();
		  ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));) {
			oos.writeObject(metadata);
			session.writeObjectData(oos);
			oos.flush();
			serialized = bos.toByteArray();
		}
		return serialized;
	}

	/**
	 *字节流反序列化
	 *
	 * @param: [data, session, metadata]
	 * @return: void
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	@Override
	public void deserializeInto(byte[] data, IRequestSession session, SessionSerializationMetadata metadata) throws Exception {
		try (
		  BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
		  ObjectInputStream ois = new CustomObjectInputStream(bis, loader);) {
			SessionSerializationMetadata serializedMetadata = (SessionSerializationMetadata) ois.readObject();
			metadata.copyFieldsFrom(serializedMetadata);
			session.readObjectData(ois);
		}
	}
}
