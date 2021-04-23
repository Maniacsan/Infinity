package org.infinity.utils;

import java.lang.reflect.Field;

import org.apache.commons.lang3.ClassUtils;

public class Reflections {

	@SuppressWarnings("deprecation")
	public static Field getField(final Class<?> cls, String obfName, String deobfName) {
		if (cls == null)
			return null;

		Field field = null;
		for (Class<?> cls1 = cls; cls1 != null; cls1 = cls1.getSuperclass()) {
			try {
				field = cls1.getDeclaredField(obfName);
			} catch (Exception e) {
				try {
					field = cls1.getDeclaredField(deobfName);
				} catch (Exception e1) {
					continue;
				}
			}

			if (!field.isAccessible()) {
				field.setAccessible(true);
			}

			return field;
		}

		for (final Class<?> class1 : ClassUtils.getAllInterfaces(cls)) {
			try {
				field = class1.getField(obfName);
			} catch (Exception e) {
				try {
					field = class1.getField(deobfName);
				} catch (Exception e1) {
					continue;
				}
			}

			return field;
		}

		throw new RuntimeException("Error reflecting field: " + deobfName + "/" + obfName + " @" + cls.getSimpleName());
	}

	@SuppressWarnings("deprecation")
	public static Object getFieldValue(final Object target, String obfName, String deobfName) {
		if (target == null)
			return null;

		Class<?> cls = target.getClass();
		Field field = null;
		for (Class<?> cls1 = cls; cls1 != null; cls1 = cls1.getSuperclass()) {
			try {
				field = cls1.getDeclaredField(obfName);
			} catch (Exception e) {
				try {
					field = cls1.getDeclaredField(deobfName);
				} catch (Exception e1) {
					continue;
				}
			}

			if (!field.isAccessible()) {
				field.setAccessible(true);
			}

			try {
				return field.get(target);
			} catch (Exception e) {
				throw new RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @"
						+ target.getClass().getSimpleName());
			}
		}

		for (final Class<?> class1 : ClassUtils.getAllInterfaces(cls)) {
			try {
				field = class1.getField(obfName);
			} catch (Exception e) {
				try {
					field = class1.getField(deobfName);
				} catch (Exception e1) {
					continue;
				}
			}

			try {
				return field.get(target);
			} catch (Exception e) {
				throw new RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @"
						+ target.getClass().getSimpleName());
			}
		}

		throw new RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @"
				+ target.getClass().getSimpleName());
	}

}
