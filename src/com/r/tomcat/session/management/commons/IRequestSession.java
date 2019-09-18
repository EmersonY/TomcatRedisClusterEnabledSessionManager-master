package com.r.tomcat.session.management.commons;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * IRequest session
 *
 * @author Ranjith
 * @since 1.0
 */
public interface IRequestSession
{
  // 是否有数据
	public Boolean isDirty();

	// 获取存放 session 的map
	public HashMap<String, Object> getChangedAttributes();

	// 数据重置
	public void resetDirtyTracking();

	// 重写StandardSession setAttribute方法
	public void setAttribute(String name, Object value);

  // 重写StandardSession getAttribute方法
	public Object getAttribute(String name);

  // 重写StandardSession getAttributeNames方法
	public Enumeration<String> getAttributeNames();

  // 重写StandardSession removeAttribute方法
	public void removeAttribute(String name);

	// 重写SetId方法
	public void setId(String id);

	// 重写setPrincipal方法 Spring Security 保存session主要方法
	public void setPrincipal(Principal principal);

	// 重写writeObjectData方法
	public void writeObjectData(ObjectOutputStream out) throws IOException;

  // 重写readObjectData方法
  public void readObjectData(ObjectInputStream in) throws IOException, ClassNotFoundException;
}
