package io.github.easy4j.dingtalk.arch;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NamespaceArchitectureTest {

    private static final String LEGACY_PACKAGE = "com/dingtalk/spring/boot";
    private static final String SPRING_PACKAGE = "org/springframework";
    private static final String NEW_PACKAGE = "io/github/easy4j/dingtalk";

    @Test
    void sdkJarHidesLegacyAndSpringClasses() throws IOException, URISyntaxException {
        Optional<Path> jar = findSdkJarIfPresent();
        Assumptions.assumeTrue(jar.isPresent(),
                "SDK JAR not yet produced by this build phase (compile -> test runs before package); "
                        + "rerun after ./mvnw package or via verify phase.");

        int legacyEntries = 0;
        int springEntries = 0;
        int newEntries = 0;
        try (JarInputStream jin = new JarInputStream(Files.newInputStream(jar.get()))) {
            JarEntry entry;
            while ((entry = jin.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (name.startsWith(LEGACY_PACKAGE) && name.endsWith(".class")) {
                    legacyEntries++;
                }
                if (name.startsWith(SPRING_PACKAGE) && name.endsWith(".class")) {
                    springEntries++;
                }
                if (name.startsWith(NEW_PACKAGE) && name.endsWith(".class")) {
                    newEntries++;
                }
            }
        }
        final int springCount = springEntries;
        final int legacyCount = legacyEntries;
        final int newCount = newEntries;

        assertTrue(newCount > 0,
                () -> "Expected io.github.easy4j.dingtalk classes in jar, found 0");
        assertTrue(legacyCount == 0,
                () -> "Legacy com.dingtalk.spring.boot classes must be 0 after namespace migration, found: " + legacyCount);
        assertFalse(springCount > 0,
                () -> "Spring-owned classes must not ship inside SDK jar: found " + springCount + " under " + SPRING_PACKAGE);
    }

    private static Optional<Path> findSdkJarIfPresent() throws URISyntaxException, IOException {
        URL location = NamespaceArchitectureTest.class.getProtectionDomain().getCodeSource().getLocation();
        URI classesUri = location.toURI();
        Path classesDir = Paths.get(classesUri);
        Path target = classesDir.getParent();
        if (target == null || !Files.isDirectory(target)) {
            return Optional.empty();
        }
        long classesMtime = Files.getLastModifiedTime(classesDir).toMillis();
        try (Stream<Path> list = Files.list(target)) {
            return list.filter(p -> p.getFileName().toString().matches("dingtalk-sdk-extension-.*\\.jar"))
                    .filter(p -> !p.getFileName().toString().contains("-sources"))
                    .filter(p -> !p.getFileName().toString().contains("-javadoc"))
                    .filter(p -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis() >= classesMtime;
                        } catch (IOException ignored) {
                            return false;
                        }
                    })
                    .findFirst();
        }
    }
}
