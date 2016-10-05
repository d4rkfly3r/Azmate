package net.d4rkfly3r.irc.azmate.plugins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Created by d4rkfly3r on 5/22/2016.
 */
public final class ClassFinder {

    private static final String ADDON_DIR = "addons";
    private static ArrayList<String> excludedLocations = new ArrayList<String>() {{
        add(File.separatorChar + "jre" + File.separatorChar + "lib" + File.separatorChar);
        add("idea_rt.jar");
        add("xalan-2.6.0.jar");
    }};
    private JarFileLoader jarFileLoader;
    private ArrayList<Class<?>> subClasses;

    public void initialize() {
        jarFileLoader = new JarFileLoader(new URL[]{});
        subClasses = findSubclasses(getClasspathLocations());
    }

    @Nonnull
    public List<Class<?>> getClasses(Class<? extends Annotation> annotationClass) {
        return subClasses.parallelStream().filter(aClass -> aClass.isAnnotationPresent(annotationClass)).collect(Collectors.toList());
    }

    public ArrayList<Class<?>> getAllClasses() {
        return subClasses;
    }

    @Nonnull
    private ArrayList<Class<?>> findSubclasses(@Nonnull Map<URL, String> locations) {
        ArrayList<Class<?>> v = new ArrayList<>();
        ArrayList<Class<?>> w;
        for (URL url : locations.keySet()) {
            w = findSubclasses(url, locations.get(url));
            if ((w.size() > 0)) v.addAll(w);
        }
        return v;
    }

    @Nonnull
    private ArrayList<Class<?>> findSubclasses(@Nonnull URL location, @Nonnull String packageName) {

        Map<Class<?>, URL> thisResult = new TreeMap<>((c1, c2) -> String.valueOf(c1).compareTo(String.valueOf(c2)));
        ArrayList<Class<?>> v = new ArrayList<>();
        List<URL> knownLocations = new ArrayList<>();
        knownLocations.add(location);
        // TODO: add getResourceLocations() to this list
        for (URL url : knownLocations) {
            File directory = new File(url.getFile());
            if (directory.exists()) {
                String[] files = directory.list();
                if (files != null) {
                    for (String file : files) {
                        if (file.endsWith(".class")) {
                            String classname = file.substring(0, file.length() - 6);
                            try {
                                Class c = jarFileLoader.loadClass(packageName + "." + classname);
                                thisResult.put(c, url);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                try {
                    JarURLConnection conn = (JarURLConnection) url.openConnection();
                    JarFile jarFile = conn.getJarFile();
                    Enumeration<JarEntry> e = jarFile.entries();
                    while (e.hasMoreElements()) {
                        JarEntry entry = e.nextElement();
                        String entryName = entry.getName();
                        if (!entry.isDirectory() && entryName.endsWith(".class")) {
                            String classname = entryName.substring(0, entryName.length() - 6);
                            if (classname.startsWith("/")) {
                                classname = classname.substring(1);
                            }
                            classname = classname.replace('/', '.');
                            try {
                                Class c = jarFileLoader.loadClass(classname);
                                thisResult.put(c, url);
                            } catch (NoClassDefFoundError ignored) {

                            } catch (Error | Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }
        }
        v.addAll(thisResult.keySet().stream().collect(Collectors.toList()));
        return v;
    }


    private Map<URL, String> getClasspathLocations() {
        Map<URL, String> map = new TreeMap<>((u1, u2) -> String.valueOf(u1).compareTo(String.valueOf(u2)));

        Path file;

        final String pathSep = System.getProperty("path.separator");
        final String classpath = System.getProperty("java.class.path");

        StringTokenizer st = new StringTokenizer(classpath, pathSep);
        first:
        while (st.hasMoreTokens()) {
            String path = st.nextToken();
            file = Paths.get(path);
            for (String excludedLocation : excludedLocations) {
                if (file.toFile().getPath().contains(excludedLocation)) {
                    continue first;
                }
            }
            include(null, file, map);
        }


        Path addonDir = Paths.get(ADDON_DIR); // TODO URGENT - Get Addons dir from config
        if (Files.notExists(addonDir)) {
            try {
                System.out.println("Creating Addon Directory ( " + addonDir.toAbsolutePath() + " )!");
                Files.createDirectories(addonDir);
            } catch (IOException e) {
                System.err.println("Addon Directory ( " + addonDir.toAbsolutePath() + " ) could not be created!");
                return map;
            }
        }
        if (Files.isDirectory(addonDir)) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(addonDir)) {
                stream.forEach(filePath -> {
                    try {
                        jarFileLoader.addFile("jar:" + filePath.toUri() + "!/");
                        include(null, filePath, map);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (final IOException e) {
                e.printStackTrace();
            }
            // ELSE NO FILES IN ADDONS DIRECTORY
        } else {
            System.err.println("Addon Directory ( " + addonDir.toAbsolutePath() + " ) is not a folder!");
        }

        return map;
    }

    private void include(@Nullable String name, @Nonnull Path filePath, @Nonnull Map<URL, String> map) {
        if (Files.notExists(filePath)) return;
        if (!Files.isDirectory(filePath)) {
            includeJar(filePath, map);
            return;
        }
        if (name == null) {
            name = "";
        } else {
            name += ".";
        }
        final String finalName = name;
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(filePath, Files::isDirectory)) {
            stream.forEach(dirPath -> {
                try {
                    map.put(new URL("file://" + dirPath.toAbsolutePath()), finalName + dirPath.getFileName());
                } catch (IOException ioe) {
                    return;
                }
                include(finalName + dirPath.getFileName(), dirPath, map);

            });
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void includeJar(@Nonnull Path filePath, @Nonnull Map<URL, String> map) {
        if (Files.isDirectory(filePath)) return;
        URL jarURL;
        JarFile jar;
        try {
            jarURL = new URL("file:/" + filePath.toAbsolutePath());
            jarURL = new URL("jar:" + jarURL.toExternalForm() + "!/");
            JarURLConnection conn = (JarURLConnection) jarURL.openConnection();
            jar = conn.getJarFile();
        } catch (final Exception e) {
            return;
        }
        if (jar == null) return;
        map.put(jarURL, "");
    }

    public static class JarFileLoader extends java.net.URLClassLoader {
        public JarFileLoader(URL[] urls) {
            super(urls);
        }

        public void addFile(String path) throws MalformedURLException {
            addURL(new URL(path));
        }
    }
}
