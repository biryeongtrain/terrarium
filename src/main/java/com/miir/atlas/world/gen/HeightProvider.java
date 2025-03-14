package com.miir.atlas.world.gen;


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
    //private final Map<PointI, Integer> points;
    private static final String CACHE_DIR = "./world/tiles/";
    private static final String TILE_URL = "https://s3.amazonaws.com/elevation-tiles-prod/terrarium/";

    public HeightProvider(int maxHeight) {
        this.maxHeight = maxHeight;
        //this.points = new ConcurrentHashMap<>(); // Use ConcurrentHashMap for thread safety
        //ensureCacheDirectoryExists();
    }


/*
    public void loadPixelsInRange(int x, int z) {
        int[][] heightArr = getHeightArray(x, z);
        if (heightArr == null) {
            System.err.println("Failed to load height array for coordinates (" + x + ", " + z + ")");
            return;
        }

        for (int i = 0; i < heightArr.length; i++) {
            for (int j = 0; j < heightArr[i].length; j++) {
                PointI point = new PointI(x + i, z + j);
                points.putIfAbsent(point, heightArr[i][j]); // Only add if not already present
            }
        }
    }

    private int[][] getHeightArray(int x, int z) {
        BufferedImage image = getHeightmap(x, z, 15);
        if (image == null) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int[][] elevations = new int[height][width];
        int maxElevation = Integer.MIN_VALUE;

        try {
            for (int yx = 0; yx < height; yx++) {
                for (int xx = 0; xx < width; xx++) {
                    int rgb = image.getRGB(xx, yx);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    double elevation = (red * 256 + green + blue / 256.0) - 32768;
                    elevations[yx][xx] = (int) elevation;

                    if (elevation > maxElevation) {
                        maxElevation = (int) elevation;
                    }
                }
            }

            if (maxElevation > 0) {
                for (int yx = 0; yx < height; yx++) {
                    for (int xx = 0; xx < width; xx++) {
                        elevations[yx][xx] = (elevations[yx][xx] * maxHeight) / maxElevation;
                    }
                }
            }
        } finally {
            image.flush(); // Release resources held by the BufferedImage
        }

        return elevations;
    }
*/

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
                System.err.println("Failed to load tile from cache: " + e.getMessage());
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
            System.err.println("Failed to download tile: " + e.getMessage());
            return 64;
        }
    }

    public int lerp(int x, int y) {
        System.out.println("Atlas");
        return getElevationFromHeightmap(x, y, 15);
        // Return 0 if the point is still not in the map
    }
}