package com.cantalou.android.util;

import android.util.SparseArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ReflectUtil {

    private static Map<String, Field> fieldCache = new HashMap<String, Field>();

    private static SparseArray<Method> methodCache = new SparseArray();

    private static SparseArray<Object> methodNotFound = new SparseArray();

    //    private static HashMap<String ,Method> methodCache = new HashMap<String ,Method>();
    private static HashSet<String> notFound = new HashSet<String>();

    private static Class<?>[] primitiveType = new Class[]{byte.class, short.class, char.class, short.class, int.class, float.class, long.class, double.class};

    private static Class<?>[] wrapperType = new Class[]{Byte.class, Short.class, Character.class, Short.class, Integer.class, Float.class, Long.class, Double.class};

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

    /**
     * 根据目标Field进行类型转换
     *
     * @param value String类型
     * @return
     */
    public static <T> T toType(Field f, Object value) {

        if (!(value instanceof String)) {
            return (T) value;
        }

        Class<?> type = f.getType();

        if (Integer.class.equals(type) || int.class.equals(type)) {
            return (T) (Integer) Integer.parseInt((String) value);
        }

        if (Long.class.equals(type) || long.class.equals(type)) {
            return (T) (Long) Long.parseLong((String) value);
        }

        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return (T) (Boolean) Boolean.parseBoolean((String) value);
        }

        if (Float.class.equals(type) || float.class.equals(type)) {
            return (T) (Float) Float.parseFloat((String) value);
        }

        if (Double.class.equals(type) || double.class.equals(type)) {
            return (T) (Double) Double.parseDouble((String) value);
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
     * 反射调用target对象的methodName方法, 方法支持链式调用,methodName如:method1.method2.method3
     *
     * @param target
     * @param methodName
     * @param params
     * @return 方法执行结果
     */
    public static <T> T invoke(Object target, String methodName, Object... params) {
        if (params.length > 0) {
            Class<?>[] paramsTypes = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramsTypes[i] = params[i].getClass();
            }
            return invokeImpl(target, methodName, new Class<?>[][]{paramsTypes}, new Object[][]{params});
        } else {
            return invokeImpl(target, methodName, null);
        }
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
        if (paramsTypes != null) {
            return invokeImpl(target, methodName, new Class<?>[][]{paramsTypes}, new Object[][]{args});
        } else {
            return invokeImpl(target, methodName, null);
        }
    }

    /**
     * 反射调用target对象的methodName方法, 方法支持链式调用,methodName如:method1.method2.method3
     *
     * @return 调用结果
     */
    public static <T> T invokeImpl(Object target, String methodName, Class<?>[][] paramsTypes, Object[]... args) {
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
                Method m = findMethod(clazz, methodNames[i], paramsTypes != null && paramsTypes.length > i ? paramsTypes[i] : new Class[]{});
                if (m == null) {
                    Log.d("Method:{} not found in class:{}", methodNames[i], clazz);
                    return null;
                }
                m.setAccessible(true);
                target = (T) m.invoke(target, args != null && args.length > i ? args[i] : null);
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
        int index;
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

    /**
     * 查找指定名称的Method对象
     *
     * @return
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramsTypes) {

        if (clazz == null || StringUtils.isBlank(methodName)) {
            return null;
        }

        int key = clazz.hashCode() ^ (methodName.hashCode() * 131);
        for (Class<?> paramsType : paramsTypes) {
            key ^= (paramsType.hashCode() * 131);
        }

        Method result = methodCache.get(key);
        if (result != null && methodName.equals(result.getName())) {
            return result;
        }

        if (methodNotFound.get(key) != null) {
            return null;
        }

        // public method(include interface superClass)
        if (result == null) {
            try {
                result = clazz.getMethod(methodName, paramsTypes);
            } catch (Exception e) {
            }
        }

        // protected,default,private methods(include superClass)
        Class superClazz = clazz;
        while (result == null && superClazz != null) {
            try {
                result = clazz.getDeclaredMethod(methodName, paramsTypes);
                if (result != null) {
                    break;
                }
            } catch (Exception e) {
            }
            superClazz = superClazz.getSuperclass();
        }

        //fuzzy public methods (include interface superClass)
        if (result == null) {
            for (Method method : clazz.getMethods()) {
                if (methodName.equals(method.getName()) && match(method, paramsTypes)) {
                    result = method;
                    break;
                }
            }
        }

        //fuzzy protected,default,private methods(include superClass)
        superClazz = clazz;
        while (result == null && superClazz != null) {
            try {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (methodName.equals(method.getName()) && match(method, paramsTypes)) {
                        result = method;
                        break;
                    }
                }
            } catch (Exception e) {
            }
            superClazz = superClazz.getSuperclass();
        }


        if (result != null) {
            synchronized (ReflectUtil.class) {
                methodCache.put(key, result);
            }
        } else {
            methodNotFound.put(key, "");
        }

        return result;
    }

    /**
     * Check all param <b>paramsTypes<b/> can be assign to method parameter type
     *
     * @param method
     * @param paramsTypes
     * @return
     */
    private static boolean match(Method method, Class<?>... paramsTypes) {
        Class<?>[] methodParamTypes = method.getParameterTypes();

        if (methodParamTypes.length != paramsTypes.length) {
            return false;
        }

        if (paramsTypes.length == 0) {
            return true;
        }

        for (int i = 0; i < methodParamTypes.length; i++) {
            if (methodParamTypes[i].isPrimitive()) {
                for (int j = 0; j < primitiveType.length; j++) {
                    if (methodParamTypes[i] == primitiveType[j] && paramsTypes[i] != wrapperType[j]) {
                        return false;
                    }
                }
            } else if (!methodParamTypes[i].isAssignableFrom(paramsTypes[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查找指定名称的
     *
     * @param target
     * @param fieldName
     * @return
     */
    public static Field findField(Class<?> target, String fieldName) {

        if (target == null || StringUtils.isBlank(fieldName)) {
            return null;
        }

        String key = target.getName() + "." + fieldName;
        if (notFound.contains(key)) {
            return null;
        }

        Field result = fieldCache.get(key);

        // public field
        if (result == null) {
            try {
                result = target.getField(fieldName);
            } catch (Exception e) {
                // ignore
            }
        }

        //public fields
        if (result == null) {
            Field[] fields = target.getFields();
            for (Field field : fields) {
                if (fieldName.equals(field.getName())) {
                    result = field;
                    break;
                }
            }
        }

        // protected, default, private
        while (result == null && target != null) {
            try {
                result = target.getDeclaredField(fieldName);
                if (result == null) {
                    for (Field field : target.getDeclaredFields()) {
                        if (fieldName.equals(field.getName())) {
                            result = field;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
            }
            target = target.getSuperclass();
        }

        if (result != null) {
            synchronized (ReflectUtil.class) {
                fieldCache.put(key, result);
            }
        } else {
            notFound.add(key);
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
            Log.w("ForName error {}", e);
            return null;
        }
    }

    public static <T> T newInstance(String className, Class<?>[] parameterTypes, Object... params) {
        Class<?> klass = forName(className);
        if (klass == null) {
            return null;
        }
        return newInstance(klass, parameterTypes, params);
    }

    public static <T> T newInstance(Class clazz, Class<?>[] parameterTypes, Object... params) {

        try {
            Constructor<?> constructor = clazz.getConstructor(parameterTypes);
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
        Class<?> clazz = forName(className);
        if (clazz == null) {
            return null;
        }
        Constructor<?> bestConstructor = null;
        int best = -1;
        for (Constructor<?> constructor : clazz.getConstructors()) {
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
    public static Map<String, ? extends Object> findAllFieldAndValue(Object obj) {
        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                HashMap<String, Object> nameValue = new HashMap<>(fields.length);
                for (Field f : fields) {
                    f.setAccessible(true);
                    nameValue.put(f.getName(), f.get(obj));
                }
                return nameValue;
            }
        } catch (Exception e) {
            //ignore
        }
        return Collections.emptyMap();
    }

    /**
     * Dump all object field info
     *
     * @param obj
     * @return className, fieldName , fieldValue
     */
    public static String dumpObjectInfo(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().toString()).append("{");
        Map<String, ? extends Object> nameValues = findAllFieldAndValue(obj);
        if (nameValues.size() > 0) {
            for (Map.Entry<String, ? extends Object> entry : nameValues.entrySet()) {
                sb.append(entry.getKey()).append(",").append(entry.getValue()).append(";");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
