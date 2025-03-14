package com.miir.atlas.world.gen;


import com.miir.atlas.Atlas;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HeightProvider {
    private final int maxHeight;
    private static final String CACHE_DIR = "./world/tiles/";
    private static final String TILE_URL = "https://s3.amazonaws.com/elevation-tiles-prod/terrarium/";
    private static final Logger LOGGER = Atlas.LOGGER;

    private static Map<Long, Integer> cache = new ConcurrentHashMap<Long, Integer>();

    public HeightProvider(int maxHeight) {
        this.maxHeight = maxHeight;

    }



    private int getElevationFromHeightmap(int x, int z, int zoom) {
        int xTile = x / 256;
        int yTile = z / 256;
        int xPixel = x - xTile * 256;
        int yPixel = z - yTile * 256;
        String cachePath = CACHE_DIR + zoom + "/" + xTile + "/" + yTile + ".png";
        String urlString = TILE_URL + zoom + "/" + xTile + "/" + yTile + ".png";
        //System.out.println(xPixel);
        //System.out.println(yPixel);
        File cacheFile = new File(cachePath);
        if (cacheFile.exists()) {
            try {
                BufferedImage tileImage = ImageIO.read(cacheFile);
                for(int i = 0; i < tileImage.getWidth(); i++) {
                    for(int k = 0; k < tileImage.getHeight(); k++) {
                        Color rgb = new Color(tileImage.getRGB(i,k));
                        double elevation = (rgb.getRed() * 256 + rgb.getGreen() + rgb.getBlue() / 256.0) - 32768;
                        cache.put(pack(x + i, z + k),(int) ((elevation / 8840) * maxHeight) + 80); // TODO: Remove temp vars
                    }
                }

            } catch (IOException e) {
                LOGGER.error("Failed to load tile from cache: {}", e.getMessage());
            }
        }
        else {
            try {
                URL url = new URL(urlString);
                try (InputStream inputStream = url.openStream()) {
                    BufferedImage tileImage = ImageIO.read(inputStream);
                    if (tileImage == null) {
                        throw new IOException("Failed to read image from URL: " + urlString);
                    }

                    Path cacheDir = Paths.get(cacheFile.getParent());
                    if (!Files.exists(cacheDir)) {
                        Files.createDirectories(cacheDir);
                    }
                    ImageIO.write(tileImage, "png", cacheFile);

                    for(int i = 0; i < tileImage.getWidth(); i++) {
                        for(int k = 0; k < tileImage.getHeight(); k++) {
                            Color rgb = new Color(tileImage.getRGB(i,k));
                            double elevation = (rgb.getRed() * 256 + rgb.getGreen() + rgb.getBlue() / 256.0) - 32768;
                            cache.put(pack(x + i, z + k),(int) ((elevation / 8840) * maxHeight) + 80); // TODO: Remove temp vars
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to download tile: {}", e.getMessage());
                return 64;
            }
        }
        return cache.get(pack(x,z));
    }

    public int checkCache(int x, int z){
        long key = pack(x, z);
        if(cache.containsKey(key)){
            return cache.get(key);
        }
         return getElevationFromHeightmap(x, z, 15);
    }

    public int getElevation(int x, int z) {
        return checkCache(x, z);
    }

    public static long pack(int x, int z) {
        return ((long) x & 0xFFFFFFFFL) | ((long) z & 0xFFFFFFFFL) << 32;
    }
    public static int unpackX(long packed) {
        return (int) (packed >>> 32 & 0xFFFFFFFFL);
    }

    public static int unpackZ(long packed) {
        return (int) (packed & 0xFFFFFFFFL);
    }
}