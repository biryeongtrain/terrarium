package xyz.lynxs.terrarium.world.gen;

import org.slf4j.Logger;
import xyz.lynxs.terrarium.Terrarium;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static xyz.lynxs.terrarium.Terrarium.CONFIG;
import static xyz.lynxs.terrarium.Terrarium.CONFIG1;
import static xyz.lynxs.terrarium.Util.pack;

public class BiomeProvider {

        private static final String CACHE_DIR = "/climate/";
        private static final Logger LOGGER = Terrarium.LOGGER;
        private static final Map<Long, short[][][]> biomeMap= new ConcurrentHashMap<>();


        private static BufferedImage getClimateFromHeightmap(int xTile, int zTile) {


            String cachePath = CONFIG1.CACHE_DIR + CACHE_DIR + CONFIG.month + "/" + 8 + "/" + xTile + "/" + zTile + ".png";
            URI uri = CONFIG1.TEMPERATURE_URL.resolve(CONFIG.month + "/" + 8 + "/" + xTile + "/" + zTile + ".png");

            File cacheFile = new File(cachePath);
            if (cacheFile.exists()) {
                try {
                    return ImageIO.read(cacheFile);

                } catch (Exception e) {
                    LOGGER.error("Failed to load tile from cache: {}", e.getMessage());
                }
            }
                try {
                    try (InputStream inputStream = uri.toURL().openStream()) {
                        BufferedImage tileImage = ImageIO.read(inputStream);

                        if (tileImage == null) {
                            throw new IOException("Failed to read image from URL: " + uri);
                        }

                        Path cacheDir = Paths.get(cacheFile.getParent());
                        if (!Files.exists(cacheDir)) {
                            Files.createDirectories(cacheDir);
                        }
                        ImageIO.write(tileImage, "png", cacheFile);
                        return tileImage;

                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to download tile: {}", e.getMessage());
                }
                return new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
        }

        private static short[][][] toIntHeightmap(BufferedImage image){
            short[][][] arr = new short[2][image.getWidth()][image.getHeight()];
                for(int k = 0; k < arr.length; k++) {
                    for (int i = 0; i < image.getWidth(); i++) {
                        for (int j = 0; j < image.getHeight(); j++) {
                            Color color = new Color(image.getRGB(i, j));
                            arr[k][i][j] = (short) (k == 0 ? color.getRed() : color.getGreen());
                        }
                    }
                }
            return arr;
        }


    public static double getClimate(int x, int z, boolean category) {
        if(biomeMap.size() > 16) biomeMap.clear();
        // Convert world coordinates to temperature data coordinates
        double scaleFactor = Math.pow(2, 8 - CONFIG.zoom);
        int scaledX = (int)(x * scaleFactor);
        int scaledZ = (int)(z * scaleFactor);
        int Xtile = scaledX/256;
        int Ztile = scaledZ/256;
        // Get base temperature values
        // Bilinear interpolation for smooth transitions
        int tileX = Math.abs(scaledX % 256);
        int tileZ = Math.abs(scaledZ % 256);

        return (bilinearInterpolate(biomeMap.computeIfAbsent(pack(Xtile, Ztile), k -> toIntHeightmap(getClimateFromHeightmap(Xtile, Ztile)))[category ? 0 : 1], tileX, tileZ) / 111) - (category ? 0 : 1);
    }


    private static double bilinearInterpolate(short[][] tile, double x, double z) {
        int x1 = (int)x;
        int z1 = (int)z;
        int x2 = Math.min(x1 + 1, tile.length - 1);
        int z2 = Math.min(z1 + 1, tile[0].length - 1);

        double fracX = x - x1;
        double fracZ = z - z1;

        // Bilinear interpolation
        return (1-fracX)*(1-fracZ)*tile[x1][z1] +
                fracX*(1-fracZ)*tile[x2][z1] +
                (1-fracX)*fracZ*tile[x1][z2] +
                fracX*fracZ*tile[x2][z2];
    }
}
