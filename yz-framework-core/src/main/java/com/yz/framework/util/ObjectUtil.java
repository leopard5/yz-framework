package com.yz.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtil {
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			bytes = baos.toByteArray();
			oos.close();
			oos = null;
			baos.close();
			baos = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sureClose(oos, baos);
		}
		return bytes;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T fromByteArray(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = null;
		Object obj = null;
		try {
			ois = new ObjectInputStream(bais);
			obj = ois.readObject();
			ois.close();
			bais.close();
			ois = null;
			bais = null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			sureClose(bais, ois);
		}

		return obj == null ? null : (T) obj;
	}

	private static void sureClose(ByteArrayInputStream bais, ObjectInputStream ois) {
		try {
			if (ois != null) {
				ois.close();
				ois = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			if (bais != null) {
				bais.close();
				bais = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private static void sureClose(ObjectOutputStream oos, ByteArrayOutputStream baos) {
		try {
			if (oos != null) {
				oos.close();
				oos = null;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			if (baos != null) {
				baos.close();
				baos = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
