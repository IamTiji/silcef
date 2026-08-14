package com.tiji.silcef.internals;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.launch.knot.Knot;
import org.lwjgl.system.Platform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JcefLoader implements PreLaunchEntrypoint {
    public static final String NATIVE_PATH =
            Path.of("./silcef/natives")
                    .toAbsolutePath()
                    .normalize()
                    .toString();

    private static final Map<Platform, String> NATIVES = Map.of(
            Platform.WINDOWS, "https://github.com/IamTiji/java-cef/releases/download/1.0-beta.1/windows-x64.zip"
    );

    @Override
    public void onPreLaunch() {
        Path javaLibrary = Path.of(NATIVE_PATH, "jcef.jar");
        if (!javaLibrary.toFile().exists()) {
            System.out.println("Silcef Natives were not found! Attempting to download...");

            if (Platform.getArchitecture() == Platform.Architecture.X86) {
                throw new UnsupportedOperationException(
                        "Your system is unsupported x86 device! You need to build natives yourself.");
            }

            URI uri;
            try {
                uri = new URI(NATIVES.get(Platform.get()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                throw new UnsupportedOperationException(
                        "Your operating system is unsupported!"
                );
            }

            File archive;
            try (HttpClient httpClient = HttpClient.newBuilder()
                                                   .followRedirects(HttpClient.Redirect.NORMAL)
                                                   .build()) {
                archive = File.createTempFile("silcef", ".zip");
                httpClient.send(
                        HttpRequest.newBuilder()
                                .setHeader("User-Agent", "Silcef +https://github.com/IamTiji/silcef")
                                .uri(uri)
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofFile(archive.toPath())
                );
                System.out.println("Downloaded natives! Extracting...");
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }

            try {
                extractFile(uri);
                System.out.println("Extraction complete!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            archive.delete();
        }

        Knot.getLauncher().addToClassPath(javaLibrary);
    }

    private static void extractFile(URI uri) throws IOException {
        Files.createDirectories(Path.of(NATIVE_PATH));

        try (InputStream is = uri.toURL().openStream()) {
            ZipInputStream zis = new ZipInputStream(is);

            ZipEntry entry = zis.getNextEntry();
            byte[] buffer = new byte[512];
            while (entry != null) {
                File file = new File(NATIVE_PATH, entry.getName());
                if (entry.isDirectory()) {
                    if (!file.mkdir()) throw new IOException("Failed to create dir");
                } else {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }

                entry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }
    }
}
