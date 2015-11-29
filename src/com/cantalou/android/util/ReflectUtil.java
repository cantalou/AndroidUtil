package com.cantalou.android.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class ReflectUtil {
	private static HashMap<String, Field> fieldCache = new HashMap<String, Field>();

	private static HashMap<String, Method> methodCache = new HashMap<String, Method>();

	/**
	 * 给对象target的field属性设置值
	 *
	 * @param target
	 *            对象
	 * @param fieldName
	 *            属性名, 支持.表达式, 如: field.field.field
	 * @param value
	 *            值
	 * @return 成功 true
	 */
	public static boolean set(Object target, String fieldName, Object value) {
		try {
			int dotIndex;
			if ((dotIndex = fieldName.lastIndexOf('.')) != -1) {
				target = get(target, fieldName.substring(0, dotIndex));
				fieldName = fieldName.substring(dotIndex + 1);
			}
			Field f = findField(target.getClass(), fieldName);
			if (f == null) {
				return false;
			}
			f.setAccessible(true);
			f.set(Modifier.isStatic(f.getModifiers()) ? null : target, value);
			return true;
		} catch (Exception e) {
			Log.e(e);
		}
		return false;
	}

	/**
	 * 获取对象属性的值
	 *
	 * @param target
	 *            对象
	 * @param fieldName
	 *            属性名, 支持.表达式, 如: target.field.field
	 * @return 返回属性值
	 */
	public static <T> T get(Object target, String fieldName) {
		try {
			Class<?> clazz = target instanceof Class ? (Class<?>) target : target.getClass();
			int dotIndex;
			if ((dotIndex = fieldName.indexOf(".")) != -1) {
				Field f = findField(clazz, fieldName.substring(0, dotIndex));
				f.setAccessible(true);
				return get(Modifier.isStatic(f.getModifiers()) ? f.getType() : f.get(target), fieldName.substring(dotIndex + 1));
			} else {
				Field f = findField(clazz, fieldName);
				f.setAccessible(true);
				return (T) f.get(Modifier.isStatic(f.getModifiers()) ? null : target);
			}
		} catch (Exception e) {
			Log.e(e);
		}
		return null;
	}

	public static <T> T invoke(Object target, String methodName, Class<?>... paramsType) {
		return invoke(target, methodName, paramsType, new Object[0]);
	}

	public static <T> T invoke(Object target, String methodName, Class<?> paramsType, Object... args) {
		return invoke(target, methodName, new Class<?>[] { paramsType }, args);
	}

	public static <T> T invoke(Object target, String methodName, Class<?>[] paramsType, Object... args) {
		try {
			Method m = findMethod(target.getClass(), methodName, paramsType);
			if (m == null) {
				return null;
			}
			m.setAccessible(true);
			return (T) m.invoke(target, args);
		} catch (Exception e) {
			Log.e(e);
		}
		return null;
	}

	public static <T> T invoke(Class<?> clazz, String methodName, Class<?>[] paramsType, Object... args) {
		try {
			Method m = findMethod(clazz, methodName, paramsType);
			if (m == null) {
				return null;
			}
			m.setAccessible(true);
			return (T) m.invoke(null, args);
		} catch (Exception e) {
			Log.e(e);
		}
		return null;
	}

	private static Method findMethod(Class<?> target, String methodName, Class<?>... paramsType) {
		if (target == null || StringUtils.isBlank(methodName)) {
			return null;
		}

		Method result = methodCache.get(target.getSimpleName() + methodName);
		// public
		if (result == null) {
			try {
				result = target.getMethod(methodName, paramsType);
			} catch (Exception e) {
				//ignore
			}
		}

		// protected,default,private
		if (result == null) {
			while (result == null && target != null) {
				try {
					result = target.getDeclaredMethod(methodName, paramsType);
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

	private static Field findField(Class<?> target, String fieldName) {
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
		if (result == null) {
			while (result == null && target != null) {
				try {
					result = target.getDeclaredField(fieldName);
				} catch (Exception e) {
					target = target.getSuperclass();
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
	 *
	 * <p>
	 * If the class has not yet been loaded, it is loaded and initialized first.
	 * This is done through either the class loader of the calling class or one
	 * of its parent class loaders. It is possible that a static initializer is
	 * run as a result of this call.
	 */
	public static Class<?> forName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			Log.e(e);
			return null;
		}
	}
}
