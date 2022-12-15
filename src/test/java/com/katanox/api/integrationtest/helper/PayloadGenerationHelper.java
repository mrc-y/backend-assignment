package com.katanox.api.integrationtest.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PayloadGenerationHelper {

    public static String generatePayloadString(String jsonFile) {
        ClassLoader classLoader = PayloadGenerationHelper.class.getClassLoader();
        try {
            return Files.readString(Paths.get(classLoader.getResource(jsonFile).toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(
                    "Error happened while loading the json file: " + jsonFile + System.lineSeparator() + "Error: " + e.getMessage());
        }
    }
}