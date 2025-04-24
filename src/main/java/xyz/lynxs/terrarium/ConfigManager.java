package xyz.lynxs.terrarium;


import java.util.ArrayList;
import java.util.List;

import java.util.function.Consumer;
import java.util.stream.Collectors;

record Config<T>(Class<T> clazz, Consumer<T> apply, String filename) {}

public class ConfigManager {
    private static final ArrayList<Config<?>> configs = new ArrayList<>();

    public static <T> T register(Class<T> clazz, String filename, Consumer<T> apply) {
        configs.add(new Config<>(clazz, apply, filename));
        return TerrariumConfig.load(clazz, filename, true);
    }

}