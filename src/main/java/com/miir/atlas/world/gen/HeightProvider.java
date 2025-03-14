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


public class HeightProvider {
    private final int maxHeight;
    private static final String CACHE_DIR = "./world/tiles/";
    private static final String TILE_URL = "https://s3.amazonaws.com/elevation-tiles-prod/terrarium/";
    private static final Logger LOGGER = Atlas.LOGGER;
    public HeightProvider(int maxHeight) {
        this.maxHeight = maxHeight;

    }



    private int getElevationFromHeightmap(int x, int y, int zoom) {
        int xTile = x / 256;
        int yTile = y / 256;
        int xPixel = x - xTile * 256;
        int yPixel = y - yTile * 256;
        String cachePath = CACHE_DIR + zoom + "/" + xTile + "/" + yTile + ".png";
        String urlString = TILE_URL + zoom + "/" + xTile + "/" + yTile + ".png";
        //System.out.println(xPixel);
        //System.out.println(yPixel);
        File cacheFile = new File(cachePath);
        if (cacheFile.exists()) {
            try {
                BufferedImage tileImage = ImageIO.read(cacheFile);

                Color rgb = new Color(tileImage.getRGB(xPixel , yPixel));
                double elevation = (rgb.getRed() * 256 + rgb.getGreen()  + rgb.getBlue()  / 256.0) - 32768;
                return (int) ((elevation / 8840) * maxHeight) + 100;

            } catch (IOException e) {
                LOGGER.error("Failed to load tile from cache: {}", e.getMessage());
            }
        }

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

                Color rgb = new Color(tileImage.getRGB(xPixel, yPixel));
                double elevation = (rgb.getRed() * 256 + rgb.getGreen()  + rgb.getBlue()  / 256.0) - 32768;
                return (int) ((elevation / 8840) * maxHeight) + 100;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to download tile: {}", e.getMessage());
            return 64;
        }
    }

    public int getElevation(int x, int y) {
        return getElevationFromHeightmap(x, y, 15);
    }
}