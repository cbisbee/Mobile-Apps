package com.csci405.hikeshare;

/**
 * Created by Charles on 10/25/2017.
 */
import android.app.VoiceInteractor;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;

public class Reflect {

    public final static HashMap<Class<?>, Class<?>> wraps = new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(char.class, Character.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);

    }};

    static Constructor<?> getConstructor(Class clazz, Object... args) {
        Constructor<?> constructor = null;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        ctors:
        for (int i = 0; i < constructors.length; ++i) {
            constructors[i].setAccessible(true);
            Class<?>[] types = constructors[i].getParameterTypes();
            if (types.length != args.length) continue;
            for (int j = 0; j < args.length; ++j) {
                Class<?> type = types[j];
                if (type.isPrimitive()) {
                    type = wraps.get(type);
                }
                if (!type.isAssignableFrom(args[j].getClass())) continue ctors;
            }
            if (constructor != null) {
                throw new IllegalArgumentException("class " + clazz + " has ambiguous constructor");
            }
            constructor = constructors[i];
        }
        if (constructor == null) {
            throw new IllegalArgumentException("class " + clazz + " has no compatible constructor");
        }

        constructor.setAccessible(true);
        Class<?> [] exceptions = constructor.getExceptionTypes();
        for (int i=0; i<exceptions.length; ++i) {
            Class<?> exception = exceptions[i];
            if (exception.isAssignableFrom(RuntimeException.class)) continue;
            if (exception.isAssignableFrom(Error.class)) continue;
            throw new IllegalArgumentException("constructor throws exceptions: " + Arrays.toString(exceptions));
        }

        return constructor;
    }
}