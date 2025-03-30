package xyz.lynxs.terrarium.world.gen;


import xyz.lynxs.terrarium.Terrarium;
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
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static xyz.lynxs.terrarium.Terrarium.CONFIG;


public class HeightProvider {
    private static final String CACHE_DIR = "./world/tiles/";
    private static final String TILE_URL = "https://s3.amazonaws.com/elevation-tiles-prod/terrarium/";
    private static final Logger LOGGER = Terrarium.LOGGER;
    public static int offset = (int) (256 * Math.pow(2, CONFIG.zoom))/5;
    public static int size = (int) (256 * Math.pow(2, CONFIG.zoom));
    private static Map<Long, int[][]> cache = new ConcurrentHashMap<>();

    public static void init(){
        offset = (int) (256 * Math.pow(2, CONFIG.zoom))/5;
        size = (int) (256 * Math.pow(2, CONFIG.zoom));
    }

    @Deprecated
    private static void getElevationFromHeightmap(long key, int xTile, int zTile) {


        String cachePath = CACHE_DIR + CONFIG.zoom + "/" + xTile + "/" + zTile + ".png";
        String urlString = TILE_URL + CONFIG.zoom + "/" + xTile + "/" + zTile + ".png";
        //System.out.println(xPixel);
        //System.out.println(yPixel);
        File cacheFile = new File(cachePath);
        if (cacheFile.exists()) {
            try {
                cache.put(key, toIntHeightmap(ImageIO.read(cacheFile)));

            } catch (Exception e) {
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

                    cache.put(key, toIntHeightmap(tileImage));
                }
            } catch (IOException e) {
                LOGGER.error("Failed to download tile: {}", e.getMessage());
                //cache.put(pack(x,z), 64);
            }
        }
    }
    private static int[][] toIntHeightmap(BufferedImage image){
        int[][] arr = new int[image.getWidth()][image.getHeight()];
        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){

                Color rgb = new Color(image.getRGB(i, j));
                double elevation = (rgb.getRed() * 256 + rgb.getGreen() + rgb.getBlue() / 256.0) - 32768;
                //System.out.println(elevation);
                arr[i][j] = (int) ((elevation / 8850) * CONFIG.worldHeight);

            }
        }
        return arr;
    }


    public static int getElevation(int x, int z) {
        int xTile = x / 256;
        int zTile = z / 256;
        int xPixel = x - (xTile * 256);
        int zPixel = z - (zTile * 256);
        long key = pack(xTile,zTile);
        if(cache.size() > 64)
            cache.clear();
        if (!cache.containsKey(key)) {
            getElevationFromHeightmap(key, xTile, zTile);
        }
        return cache.get(key)[xPixel][zPixel];
    }
    public static int getSectionSteepness(int x, int z, int radius) {
        // Calculate bounds of the area to check
        int minX = Math.max(x - radius, 0);
        int maxX = x + radius;
        int minZ = Math.max(z - radius, 0);
        int maxZ = z + radius;

        int minHeight = Integer.MAX_VALUE;
        int maxHeight = Integer.MIN_VALUE;

        // Iterate through all positions in the square area
        for (int currentX = minX; currentX <= maxX; currentX++) {
            for (int currentZ = minZ; currentZ <= maxZ; currentZ++) {
                int elevation = getElevation(currentX, currentZ);
                if (elevation < minHeight) minHeight = elevation;
                if (elevation > maxHeight) maxHeight = elevation;
            }
        }

        // Return the difference (steepness)
        return maxHeight - minHeight;
    }

    public static long pack(int x, int z) {
        return ((long) x & 0xFFFFFFFFL) | ((long) z & 0xFFFFFFFFL) << 32;
    }
}