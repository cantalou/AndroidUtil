package com.cantalou.android.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class ReflectUtil {

    private static HashMap<String, Field> fieldCache = new HashMap<String, Field>();

    private static HashMap<String, Method> methodCache = new HashMap<String, Method>();

    /**
     * 给对象target的field属性设置值
     *
     * @param target    对象
     * @param fieldName 属性名, 支持.表达式, 如: field.field.field
     * @param value     值
     * @return 成功 true
     */
    public static boolean set(Object target, String fieldName, Object value) {
        try {
            int dotIndex;
            if ((dotIndex = fieldName.lastIndexOf('.')) != -1) {
                target = get(target, fieldName.substring(0, dotIndex));
                if (target == null) {
                    Log.d("Field:{} not found in class:{}", fieldName.substring(0, dotIndex), target);
                    return false;
                }
                fieldName = fieldName.substring(dotIndex + 1);
            }
            Class<?> clazz = target instanceof Class ? (Class<?>) target : target.getClass();
            Field f = findField(clazz, fieldName);
            if (f == null) {
                Log.d("Field:{} not found in class:{}", fieldName, clazz);
                return false;
            }
            boolean isStatic = Modifier.isStatic(f.getModifiers());
            if (isStatic && clazz != target) {
                Log.d("Can not set a instance field:{} to a class object :{} ", fieldName, clazz);
                return false;
            }
            f.setAccessible(true);
            f.set(isStatic ? null : target, toType(f, value));
            return true;
        } catch (Exception e) {
            Log.e(e);
        }
        return false;
    }

    public static <T> T toType(Field f, Object value) {
        Class<?> type = f.getType();
        if (Integer.class.equals(type) && !(value instanceof Integer)) {
            return (T) (value instanceof String ? Integer.parseInt((String) value) : value);
        } else if (int.class.equals(type) && !(value instanceof Integer)) {
            return (T) (value instanceof String ? Integer.parseInt((String) value) : value);
        } else if (Long.class.equals(type) && !(value instanceof Long)) {
            return (T) (value instanceof String ? Long.parseLong((String) value) : value);
        } else if (long.class.equals(type) && !(value instanceof Long)) {
            return (T) (value instanceof String ? Long.parseLong((String) value) : value);
        } else if (Boolean.class.equals(type) && !(value instanceof Boolean)) {
            return (T) (value instanceof String ? Boolean.parseBoolean((String) value) : value);
        } else if (boolean.class.equals(type) && !(value instanceof Boolean)) {
            return (T) (value instanceof String ? Boolean.parseBoolean((String) value) : value);
        }
        return (T) value;
    }

    /**
     * 获取对象属性的值
     *
     * @param target    对象
     * @param fieldName 属性名, 支持.表达式, 如: target.field.field
     * @return 返回属性值
     */
    public static <T> T get(Object target, String fieldName) {
        if (target == null) {
            return null;
        }
        try {
            Class<?> clazz = target instanceof Class ? (Class<?>) target : target.getClass();
            int dotIndex;
            if ((dotIndex = fieldName.indexOf(".")) == -1) {
                Field f = findField(clazz, fieldName);
                if (f == null) {
                    Log.d("Field:{} not found in class:{}", fieldName, clazz);
                    return null;
                }
                boolean isStatic = Modifier.isStatic(f.getModifiers());
                if (isStatic && clazz != target) {
                    Log.w("Can not get a instance field:{} from class object :{}", fieldName, clazz);
                    return null;
                }
                f.setAccessible(true);
                return (T) f.get(isStatic ? null : target);
            } else {
                Field f = findField(clazz, fieldName.substring(0, dotIndex));
                if (f == null) {
                    Log.d("Field:{} not found in class:{}", fieldName, clazz);
                    return null;
                }
                f.setAccessible(true);
                return get(f.get(Modifier.isStatic(f.getModifiers()) ? null : target), fieldName.substring(dotIndex + 1));

            }
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

    /**
     * 反射调用target对象的methodName方法
     *
     * @param target      对象
     * @param methodName  方法名称
     * @param paramsTypes 参数类型
     * @return 调用结果
     */
    public static <T> T invoke(Object target, String methodName, Class<?>... paramsTypes) {
        return invoke(target, methodName, paramsTypes != null && paramsTypes.length > 0 ? new Class<?>[][]{paramsTypes} : null);
    }

    /**
     * 反射调用target对象的methodName方法
     *
     * @param target      对象
     * @param methodName  方法名称
     * @param paramsTypes 参数类型
     * @param args        参数
     * @return 调用结果
     */
    public static <T> T invoke(Object target, String methodName, Class<?>[] paramsTypes, Object... args) {
        return invoke(target, methodName, paramsTypes != null && paramsTypes.length > 0 ? new Class<?>[][]{paramsTypes} : null,
                args != null && args.length > 0 ? new Object[][]{args} : null);
    }

    /**
     * 反射调用target对象的methodName方法, 方法支持链式调用,methodName如:method1.method2.method3
     *
     * @param target      对象
     * @param methodName  方法名称
     * @param paramsTypes 参数类型
     * @param args        参数
     * @return 调用结果
     */
    public static <T> T invoke(Object target, String methodName, Class<?>[][] paramsTypes, Object[]... args) {
        if (target == null) {
            return null;
        }

        if (paramsTypes != null && paramsTypes.length > 0 && paramsTypes.length != countDot(methodName) + 1) {
            throw new IllegalArgumentException("chain method count does not match the paramsTypes length");
        }
        if (paramsTypes != null && args != null && paramsTypes.length != args.length) {
            throw new IllegalArgumentException("paramsTypes length does not match the args length");
        }
        try {
            String[] methodNames;
            if (methodName.indexOf('.') > 0) {
                methodNames = methodName.split("\\.");
            } else {
                methodNames = new String[]{methodName};
            }
            for (int i = 0; i < methodNames.length; i++) {
                Class<?> clazz = target instanceof Class ? (Class<?>) target : target.getClass();
                Method m = findMethod(clazz, methodNames[i], paramsTypes != null && paramsTypes.length > i ? paramsTypes[i] : null);
                if (m == null) {
                    Log.d("Method:{} not found in class:{}", methodNames[i], clazz);
                    return null;
                }
                m.setAccessible(true);
                target = (T) m.invoke(Modifier.isStatic(m.getModifiers()) ? null : target, args != null && args.length > i ? args[i] : null);
            }
            return (T) target;
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

    /**
     * 统计字符串中"."字符的个数
     *
     * @param str 统计字符串
     * @return 点个数
     */
    private static int countDot(String str) {
        int index = 0;
        if (StringUtils.isBlank(str) || (index = str.indexOf('.')) == -1) {
            return 0;
        }
        int count = 1;
        char[] content = str.toCharArray();
        for (int i = index + 1; i < content.length; i++) {
            if (content[i] == '.') {
                count++;
            }
        }
        return count;
    }

    public static Method findMethod(Class<?> target, String methodName, Class<?>... paramsTypes) {
        if (target == null || StringUtils.isBlank(methodName)) {
            return null;
        }

        String key = target.getSimpleName() + methodName;
        if (paramsTypes != null) {
            StringBuilder sb = new StringBuilder(key);
            for (Class<?> paramsType : paramsTypes) {
                sb.append(paramsType.getSimpleName());
            }
            key = sb.toString();
        }
        Method result = methodCache.get(key);

        // public
        if (result == null) {
            try {
                result = target.getMethod(methodName, paramsTypes);
            } catch (Exception e) {
                // ignore
            }
        }

        // protected,default,private
        if (result == null) {
            while (result == null && target != null) {
                try {
                    result = target.getDeclaredMethod(methodName, paramsTypes);
                } catch (Exception e) {
                    target = target.getSuperclass();
                }
            }
        }

        if (result != null) {
            synchronized (ReflectUtil.class) {
                methodCache.put(target.getSimpleName() + methodName, result);
            }
        }

        return result;
    }

    public static Field findField(Class<?> target, String fieldName) {
        if (target == null || StringUtils.isBlank(fieldName)) {
            return null;
        }

        Field result = fieldCache.get(target.getSimpleName() + fieldName);
        // public
        if (result == null) {
            try {
                result = target.getField(fieldName);
            } catch (Exception e) {
                // ignore
            }
        }

        // protected,default,private
        Class<?> superTarget = target;
        if (result == null) {
            while (result == null && superTarget != null) {
                try {
                    result = superTarget.getDeclaredField(fieldName);
                } catch (Exception e) {
                    superTarget = superTarget.getSuperclass();
                }
            }
        }

        if (result == null) {
            for (Field f : target.getFields()) {
                if (f.getName().equals(fieldName)) {
                    result = f;
                    break;
                }
            }
        }

        if (result == null) {
            for (Field f : target.getDeclaredFields()) {
                if (f.getName().equals(fieldName)) {
                    result = f;
                    break;
                }
            }
        }

        if (result != null) {
            synchronized (ReflectUtil.class) {
                fieldCache.put(target.getSimpleName() + fieldName, result);
            }
        }

        return result;
    }


    /**
     * Returns a {@code Class} object which represents the class with the given
     * name. The name should be the name of a non-primitive class, as described
     * in the {@link Class class definition}. Primitive types can not be found
     * using this method; use {@code int.class} or {@code Integer.TYPE} instead.
     * <p/>
     * <p/>
     * If the class has not yet been loaded, it is loaded and initialized first.
     * This is done through either the class loader of the calling class or one
     * of its parent class loaders. It is possible that a static initializer is
     * run as a result of this call.
     */
    public static <T> Class<T> forName(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> T newInstance(String className, Class<?>[] parameterTypes, Object... params) {
        Class<?> klass = forName(className);
        if (klass == null) {
            return null;
        }

        try {
            Constructor<?> constructor = klass.getConstructor(parameterTypes);
            return (T) constructor.newInstance(params);
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

    /**
     * 创建className名称的实例,选择一个最合适的构造方法
     *
     * @param className 类名
     * @param params    构造参数
     * @return className名称的对象
     */
    public static <T> T newInstance(String className, Object... params) {
        Class<?> klass = forName(className);
        if (klass == null) {
            return null;
        }
        Constructor<?> bestConstructor = null;
        int best = -1;
        for (Constructor<?> constructor : klass.getConstructors()) {
            int match = 0;
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == params.length) {
                match += parameterTypes.length;
            }
            for (int i = 0; i < parameterTypes.length && i < params.length; i++) {
                if (params[i] == null) {
                    continue;
                }
                if (parameterTypes[i].equals(params[i].getClass())) {
                    match++;
                }
            }
            if (match > best) {
                best = match;
                bestConstructor = constructor;
            }
        }
        try {
            return (T) bestConstructor.newInstance(params);
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

    /**
     * @param obj
     * @return
     */
    public static List<Object> findAllFieldValue(Object obj) {
        List<Object> list = null;
        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                list = new ArrayList<Object>(fields.length);
                for (Field f : fields) {
                    f.setAccessible(true);
                    list.add(f.get(obj));
                }
            }
        } catch (Exception e) {
            //ignore
        }
        return list;
    }

    /**
     * @param obj
     * @return
     */
    public static String dumpObjectInfo(Object obj) {
        HashMap<String, Object> fieldValue = new HashMap<String, Object>();
        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field f : fields) {
                    f.setAccessible(true);
                    fieldValue.put(f.getName(), f.get(obj));
                }
            }
        } catch (Exception e) {
            //ignore
        }
        return fieldValue.toString();
    }
}
