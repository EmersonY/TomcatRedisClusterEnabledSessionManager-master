package com.r.tomcat.session.management.redis;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.r.tomcat.session.management.commons.IRequestSession;

/**
 * Tomcat redis session
 *
 * @author Ranjith
 * @since 1.0
 */
public class RedisSession extends StandardSession implements IRequestSession
{
	private Log log = LogFactory.getLog(RedisSession.class);
	
	private static final long serialVersionUID = 2L;
	
	protected Boolean dirty;

	protected HashMap<String, Object> changedAttributes;

	protected static Boolean manualDirtyTrackingSupportEnabled = false;

	protected static String manualDirtyTrackingAttributeKey = "__changed__";

	public static void setManualDirtyTrackingSupportEnabled(Boolean enabled) {
		manualDirtyTrackingSupportEnabled = enabled;
	}

	public static void setManualDirtyTrackingAttributeKey(String key) {
	  manualDirtyTrackingAttributeKey = key;
	}

	public RedisSession(Manager manager) {
		super(manager);
		resetDirtyTracking();
	}

	/**
	 *是否有数据
	 *
	 * @param: []
	 * @return: java.lang.Boolean
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	public Boolean isDirty() {
		return dirty || !changedAttributes.isEmpty();
	}

	/**
	 *获取session属性值
	 *
	 * @param: []
	 * @return: java.util.HashMap<java.lang.String,java.lang.Object>
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	public HashMap<String, Object> getChangedAttributes() {
	  return changedAttributes;
	}

	/**
	 *还原数据状态
	 *
	 * @param: []
	 * @return: void
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	public void resetDirtyTracking() {
		changedAttributes = new HashMap<>();
		dirty = false;
	}

	/**
	 *重写 setAttribute 替换session value 值
	 *
	 * @param: [key, value]
	 * @return: void
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	@Override
	public void setAttribute(String key, Object value) {
		if (manualDirtyTrackingSupportEnabled && manualDirtyTrackingAttributeKey.equals(key)) {
			dirty = true;
			return;
		}
		Object oldValue = getAttribute(key);
		super.setAttribute(key, value);
    //新旧值不能为空 类型不能相同 新值不能等于旧值
		if ((value != null || oldValue != null) && (value == null && oldValue != null || oldValue == null && value != null || !value.getClass().isInstance(oldValue) || !value.equals(oldValue))) {
			//Session改变的时候保存，默认不开启
		  if (this.manager instanceof RedisSessionManager && ((RedisSessionManager) this.manager).getSaveOnChange()) {
				try {
					((RedisSessionManager) this.manager).save(this, true);
				} catch (Exception e) {
					log.error("Error saving session on setAttribute (triggered by saveOnChange=true):", e);
				}
			} else {
				changedAttributes.put(key, value);
			}
		}
	}

	/**
	 *获取属性标签
	 *
	 * @param: [name]
	 * @return: java.lang.Object
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	@Override
	public Object getAttribute(String name) {
		return super.getAttribute(name);
	}

	/**
	 *获取属性标签名字
	 *
	 * @param: []
	 * @return: java.util.Enumeration<java.lang.String>
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		return super.getAttributeNames();
	}

	/**
	 *移除属性标签
	 *
	 * @param: [name]
	 * @return: void
	 * @author: caiyf
	 * @date: 2019-9-5
	 */
	@Override
	public void removeAttribute(String name) {
		super.removeAttribute(name);
		if (this.manager instanceof RedisSessionManager && ((RedisSessionManager) this.manager).getSaveOnChange()) {
			try {
				((RedisSessionManager) this.manager).save(this, true);
			} catch (Exception e) {
				log.error("Error saving session on setAttribute (triggered by saveOnChange=true): ", e);
			}
		} else {
			dirty = true;
		}
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setPrincipal(Principal principal) {
		dirty = true;
		super.setPrincipal(principal);
	}

	@Override
	public void writeObjectData(java.io.ObjectOutputStream out) throws IOException {
		super.writeObjectData(out);
		out.writeLong(this.getCreationTime());
	}

	@Override
	public void readObjectData(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		super.readObjectData(in);
		this.setCreationTime(in.readLong());
	}
}
