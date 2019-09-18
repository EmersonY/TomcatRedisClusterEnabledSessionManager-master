package com.r.tomcat.session.management.commons;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Tomcat session serialization object 专门装序列化过后的session二进制数组织
 *
 * @author Ranjith
 * @since 1.0
 */
public class SessionSerializationMetadata implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private byte[] sessionAttributesHash;

	public SessionSerializationMetadata() {
		this.sessionAttributesHash = new byte[0];
	}

	public byte[] getSessionAttributesHash() {
		return sessionAttributesHash;
	}

	public void setSessionAttributesHash(byte[] sessionAttributesHash) {
		this.sessionAttributesHash = sessionAttributesHash;
	}

	public void copyFieldsFrom(SessionSerializationMetadata metadata) {
		this.setSessionAttributesHash(metadata.getSessionAttributesHash());
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(sessionAttributesHash.length);
		out.write(this.sessionAttributesHash);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int hashLength = in.readInt();
		byte[] sessionAttributesHash = new byte[hashLength];
		in.read(sessionAttributesHash, 0, hashLength);
		this.sessionAttributesHash = sessionAttributesHash;
	}
}
