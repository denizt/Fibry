package eu.lucaventuri.common;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Some utilities
 *
 * @author Luca Venturi
 */
public final class SystemUtils {
    static private final boolean assertsEnabled;

    static {
        // Check if the asserts are enabled
        boolean tempAssertsEnabled = false;
        assert tempAssertsEnabled = true;

        assertsEnabled = tempAssertsEnabled;
    }

    private SystemUtils() {
    }

    /**
     * Sleeps some ms
     *
     * @param ms ms to sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms < 0 ? 0 : ms);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Sleeps some ms
     *
     * @param ms ms to sleep
     */
    public static void sleepEnsure(long ms) {
        long target = System.currentTimeMillis() + ms;

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            sleepEnsure(target - System.currentTimeMillis());
        }
    }

    /**
     * Sleeps a random time
     *
     * @param ms maximum ms to sleep
     */
    public static void randomSleep(int ms) {
        sleep(new Random().nextInt(ms) + 1);
    }

    /**
     * Sleeps a random time
     *
     * @param minMs minimum ms to sleep
     * @param maxMs maximum ms to sleep
     */
    public static void randomSleep(int minMs, int maxMs) {
        assert maxMs > minMs;

        sleep(new Random().nextInt(maxMs - minMs) + minMs);
    }

    /**
     * @param parent starting directory
     * @return a list with all the files, recursively
     */
    public static List<File> getAllFiles(File parent) throws IOException {
        if (parent == null) return Collections.emptyList();
        return Files.list(parent.toPath()).map(Path::toFile).collect(Collectors.toList());
    }

    /**
     * @param parent starting directory
     * @return a list with all the files
     */
    public static Stream<File> getAllFilesStream(File parent) throws IOException {
        return Files.list(parent.toPath()).map(Path::toFile);
    }

    /**
     * Close without exceptions
     *
     * @param clo closeable to close
     */
    public static void close(Closeable clo) {
        if (clo == null)
            return;

        try {
            clo.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * @return true if the asserts are enabled, and therefore the program is running in debug mode
     */
    public static boolean getAssertsEnabled() {
        return assertsEnabled;
    }

    /**
     * @param className name of the class to find
     * @return the class, or null if it is not available
     */
    @SuppressWarnings("rawtypes")
    public static Class findClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}