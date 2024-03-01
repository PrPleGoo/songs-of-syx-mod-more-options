package com.github.argon.sos.moreoptions.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * For reading files from the class path a.k.a. "resources" folder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceService {
    @Getter(lazy = true)
    private final static ResourceService instance = new ResourceService();

    public Optional<Path> getResourcePath(String path) {
        URL url = getClass().getClassLoader().getResource(path);

        return Optional.ofNullable(url).map(url1 -> {
            try {
                return Paths.get(url1.toURI());
            } catch (URISyntaxException e) {
                return null;
            }
        });
    }

    public String readResource(String path) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            //noinspection DataFlowIssue
            return readFromInputStream(inputStream);
        }
    }


    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
