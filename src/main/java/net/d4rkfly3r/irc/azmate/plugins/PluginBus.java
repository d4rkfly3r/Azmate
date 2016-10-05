package net.d4rkfly3r.irc.azmate.plugins;

import net.d4rkfly3r.irc.azmate.annotations.Listener;
import net.d4rkfly3r.irc.azmate.annotations.Plugin;
import net.d4rkfly3r.irc.azmate.plugins.events.Event;
import net.d4rkfly3r.irc.azmate.plugins.events.PluginPreInitEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PluginBus {

    private static PluginBus ourInstance = new PluginBus();
    public final HashMap<Class<?>, Object> plugins;
    private final ClassFinder classFinder;

    private PluginBus() {
        this.plugins = new HashMap<>();
        this.classFinder = new ClassFinder();
        this.classFinder.initialize();
    }

    public static PluginBus getInstance() {
        return ourInstance;
    }

    @Nonnull
    public PluginBus fireEvent(@Nonnull Event event) {
        this.plugins.forEach((aClass, instance) -> {
            List<Method> methods = new ArrayList<>();
            Collections.addAll(methods, aClass.getDeclaredMethods());
            this.invokeMethods(instance, methods, event);
        });
        return this;
    }

    private void invokeMethods(@Nonnull Object instance, @Nonnull List<Method> methods, @Nonnull Event event) {
        methods.forEach(method -> {
            if (method.isAnnotationPresent(Listener.class)) {
                if (method.getParameterCount() > 0) {
                    if (method.getParameterTypes()[0] == event.getClass()) {
                        try {
                            method.invoke(instance, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Nonnull
    public PluginBus fireEventToObject(@Nonnull Object instance, @Nonnull Event event) {
        List<Method> methods = new ArrayList<>();
        Collections.addAll(methods, instance.getClass().getDeclaredMethods());
        this.invokeMethods(instance, methods, event);
        return this;
    }

    public void init() {
        this.classFinder.getClasses(Plugin.class).forEach(aClass1 -> {
            try {
                this.plugins.put(aClass1, aClass1.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Plugins: ");
        this.plugins.forEach((aClass, instance) -> {
            System.out.println("\t" + aClass.getName());
            this.fireEventToObject(instance, new PluginPreInitEvent());
        });
    }
}
