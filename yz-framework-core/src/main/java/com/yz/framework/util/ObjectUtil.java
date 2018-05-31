package com.yz.framework.util;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectUtil {
    public static final Object NULL = new Serializable() {
        private static final long serialVersionUID = 7092611880189329093L;

        private Object readResolve() {
            return ObjectUtil.NULL;
        }
    };

    public ObjectUtil() {
    }

    public static Object defaultIfNull(Object object, Object defaultValue) {
        return object != null ? object : defaultValue;
    }

    public static boolean equals(Object object1, Object object2) {
        return ArrayUtil.equals(object1, object2);
    }

    public static int hashCode(Object object) {
        return ArrayUtil.hashCode(object);
    }

    public static int identityHashCode(Object object) {
        return object == null ? 0 : System.identityHashCode(object);
    }

    public static String identityToString(Object object) {
        return object == null ? null : appendIdentityToString((StringBuffer) null, object)
                .toString();
    }

    public static String identityToString(Object object, String nullStr) {
        return object == null ? nullStr : appendIdentityToString((StringBuffer) null, object)
                .toString();
    }

    public static StringBuffer appendIdentityToString(StringBuffer buffer, Object object) {
        if (object == null) {
            return null;
        } else {
            if (buffer == null) {
                buffer = new StringBuffer();
            }

            buffer.append(ClassUtil.getClassNameForObject(object));
            return buffer.append('@').append(Integer.toHexString(identityHashCode(object)));
        }
    }

    public static Object clone(Object array) {
        if (array == null) {
            return null;
        } else if (array instanceof Object[]) {
            return ArrayUtil.clone((Object[]) ((Object[]) array));
        } else if (array instanceof long[]) {
            return ArrayUtil.clone((long[]) ((long[]) array));
        } else if (array instanceof int[]) {
            return ArrayUtil.clone((int[]) ((int[]) array));
        } else if (array instanceof short[]) {
            return ArrayUtil.clone((short[]) ((short[]) array));
        } else if (array instanceof byte[]) {
            return ArrayUtil.clone((byte[]) ((byte[]) array));
        } else if (array instanceof double[]) {
            return ArrayUtil.clone((double[]) ((double[]) array));
        } else if (array instanceof float[]) {
            return ArrayUtil.clone((float[]) ((float[]) array));
        } else if (array instanceof boolean[]) {
            return ArrayUtil.clone((boolean[]) ((boolean[]) array));
        } else if (array instanceof char[]) {
            return ArrayUtil.clone((char[]) ((char[]) array));
        } else if (!(array instanceof Cloneable)) {
            throw new RuntimeException("Object of class " + array.getClass().getName()
                    + " is not Cloneable");
        } else {
            Class clazz = array.getClass();

            try {
                Method cloneMethod = clazz.getMethod("clone", ArrayUtil.EMPTY_CLASS_ARRAY);
                return cloneMethod.invoke(array, ArrayUtil.EMPTY_OBJECT_ARRAY);
            } catch (NoSuchMethodException var3) {
                throw new RuntimeException(var3);
            } catch (IllegalArgumentException var4) {
                throw new RuntimeException(var4);
            } catch (IllegalAccessException var5) {
                throw new RuntimeException(var5);
            } catch (InvocationTargetException var6) {
                throw new RuntimeException(var6);
            }
        }
    }

    public static boolean isSameType(Object object1, Object object2) {
        return object1 != null && object2 != null ? object1.getClass().equals(object2.getClass())
                : true;
    }

    public static String toString(Object object) {
        return object == null ? "" : (object.getClass().isArray() ? ArrayUtil.toString(object)
                : object.toString());
    }

    public static String toString(Object object, String nullStr) {
        return object == null ? nullStr : (object.getClass().isArray() ? ArrayUtil.toString(object)
                : object.toString());
    }

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
