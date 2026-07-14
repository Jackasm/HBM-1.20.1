package com.hbm.tileentity;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface IConfigurableMachine {

    /**
     * The name for the JSON object for this machine
     */
    String getConfigName();

    /**
     * Reads the JSON object and sets the machine's parameters, use defaults and ignore if a value is not yet present
     */
    void readIfPresent(JsonObject obj);

    /**
     * Writes the entire config for this machine using the relevant values
     */
    void writeConfig(JsonWriter writer) throws IOException;

    // В Java 8+ можно оставить default методы
    static boolean grab(JsonObject obj, String name, boolean def) {
        return obj.has(name) ? obj.get(name).getAsBoolean() : def;
    }

    static int grab(JsonObject obj, String name, int def) {
        return obj.has(name) ? obj.get(name).getAsInt() : def;
    }

    static long grab(JsonObject obj, String name, long def) {
        return obj.has(name) ? obj.get(name).getAsLong() : def;
    }

    static double grab(JsonObject obj, String name, double def) {
        return obj.has(name) ? obj.get(name).getAsDouble() : def;
    }
}