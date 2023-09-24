package com.github.argon.sos.moreoptions.util;

import java.lang.reflect.Method;
import java.util.Optional;

public class MethodUtil {
    public static boolean isGetterMethod(Method method) {
        // don't call methods with params
        if (method.getParameterCount() > 0) {
            return false;
        }
        String methodName = method.getName();

        // get()
        if (methodName.startsWith("get") && methodName.length() == 3) {
            return true;
        }

        // is()
        if (methodName.startsWith("is") && methodName.length() == 2) {
            return true;
        }

        // isXxx...()
        if (methodName.startsWith("is") && methodName.length() > 2 && Character.isUpperCase(methodName.charAt(2))) {
            return true;
        }

        // getXxx..()
        if (methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
            return true;
        }

        return false;
    }

    public static boolean isSetterMethod(Method method) {
        // only methods with 1 parameter
        if (method.getParameterCount() != 1) {
            return false;
        }

        // only methods without return value
        if (!method.getReturnType().equals(Void.TYPE)) {
            return false;
        }

        String methodName = method.getName();

        // set(...)
        if (methodName.startsWith("set") && methodName.length() == 3) {
            return true;
        }

        // setXxx..(...)
        if (methodName.startsWith("set") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
            return true;
        }

        return false;
    }

    public static String extractSetterGetterFieldName(Method method) {
        String methodName = method.getName();
        String name = stripSetterGetterMethodPrefix(methodName);

        return StringUtil.unCapitalize(name);
    }

    private static String stripSetterGetterMethodPrefix(String methodName) {
        String name;

        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            name = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            name = methodName.substring(2);
        } else {
            name = methodName;
        }

        return name;
    }

    public static Optional<Method> getGetter(String fieldName, Class<?> clazz) {
        String getterName = "get" + StringUtil.capitalize(fieldName);

        try {
            return Optional.of(clazz.getDeclaredMethod(getterName));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static Optional<Method> getSetter(String fieldName, Class<?> clazz, Class<?>... types) {
        String setterName = "set" + StringUtil.capitalize(fieldName);

        try {
            return Optional.of(clazz.getDeclaredMethod(setterName, types));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }
}
