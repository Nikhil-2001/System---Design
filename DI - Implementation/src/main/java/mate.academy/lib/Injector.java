package mate.academy.lib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import mate.academy.service.impl.FileReaderServiceImpl;
import mate.academy.service.impl.ProductParserImpl;
import mate.academy.service.impl.ProductServiceImpl;

public class Injector {
    private static final Set<Class<?>> classes = Set.of(ProductParserImpl.class,
            FileReaderServiceImpl.class, ProductServiceImpl.class);
    private static final Map<Class<?>, Class<?>> interfaceToImpl = new HashMap<>();
    private static Injector injector = new Injector();
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public static Injector getInjector() {
        registerComponents(classes);
        return injector;
    }

    public Object getInstance(Class<?> clazz) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (instances.containsKey(clazz)) {
            return instances.get(clazz);
        }

        if (clazz.isInterface()) {
            Class<?> implClass = interfaceToImpl.get(clazz);
            if (implClass == null) {
                throw new RuntimeException("No implementation found for: " + clazz.getName());
            }
            return getInstance(implClass);
        }

        if (!clazz.isAnnotationPresent(Component.class)) {
            throw new RuntimeException("Cannot create instance: "
                    + clazz.getName() + " is not a @Component");
        } else {
            for (Class<?> iface : clazz.getInterfaces()) {
                interfaceToImpl.put(iface, clazz);
            }
        }

        Object instance = clazz.getDeclaredConstructor().newInstance();

        instances.put(clazz, instance);

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> fieldType = field.getType();
                Object dependency = getInstance(fieldType);
                field.setAccessible(true);
                field.set(instance, dependency);
            }
        }

        return instance;
    }

    private static void registerComponents(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class)) {
                for (Class<?> iface : clazz.getInterfaces()) {
                    interfaceToImpl.put(iface, clazz);
                }
            }
        }
    }
}

