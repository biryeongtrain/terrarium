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

import static com.miir.atlas.world.gen.chunk.AtlasChunkGenerator.zoom;


public class HeightProvider {
    private final int maxHeight;
    private static final String CACHE_DIR = "./world/tiles/";
    private static final String TILE_URL = "https://s3.amazonaws.com/elevation-tiles-prod/terrarium/";
    private static final Logger LOGGER = Atlas.LOGGER;

    private static Map<Long, BufferedImage> cache = new ConcurrentHashMap<>();

    public HeightProvider(int maxHeight) {
        this.maxHeight = maxHeight;

    }


    @Deprecated
    private void getElevationFromHeightmap(int xTile, int zTile) {


        String cachePath = CACHE_DIR + zoom + "/" + xTile + "/" + zTile + ".png";
        String urlString = TILE_URL + zoom + "/" + xTile + "/" + zTile + ".png";
        //System.out.println(xPixel);
        //System.out.println(yPixel);
        File cacheFile = new File(cachePath);
        if (cacheFile.exists()) {
            try {
                cache.put(pack(xTile, zTile), ImageIO.read(cacheFile));

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

                    cache.put(pack(xTile, zTile), tileImage);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to download tile: {}", e.getMessage());
                //cache.put(pack(x,z), 64);
            }
        }
    }
    public int getFromImageCache(int x, int z){
        int xTile = x / 256;
        int zTile = z / 256;
        int xPixel = x - (xTile * 256);
        int zPixel = z - (zTile * 256);
        long key = pack(xTile,zTile);

        if(!cache.containsKey(key)){
            getElevationFromHeightmap(xTile, zTile);
        }
        if(cache.containsKey(key)) {
            Color rgb = new Color(cache.get(key).getRGB(xPixel, zPixel));
            double elevation = (rgb.getRed() * 256 + rgb.getGreen() + rgb.getBlue() / 256.0) - 32768;
            //System.out.println(elevation);
            return (int) ((elevation / 8840) * maxHeight);
        }
        return 64;
    }



    public int getElevation(int x, int z) {
        int size = (int) (Math.pow(2, zoom) * 256);
        if(cache.size() > 128){
            cache.clear();
        }
        if(x > size || z > size){
            return 64;
        }
        //System.out.println(getFromImageCache(x, z));
        return getFromImageCache(x,z);
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