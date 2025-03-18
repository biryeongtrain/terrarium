package com.miir.atlas;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.util.Identifier;

public class Util {
    public static int lon2tile(long lon,int zoom) { return (int) Math.floor((lon+180)/360*Math.pow(2,zoom)); }
    public static int  lat2tile(long lat,int zoom)  { return (int) Math.floor((1-Math.log(Math.tan(lat*Math.PI/180) + 1/Math.cos(lat*Math.PI/180))/Math.PI)/2 *Math.pow(2,zoom)); }
    public static BlockState getBlockState(String key) {
        // Convert the key to a Minecraft identifier (e.g., "TERRACOTTA" -> "minecraft:terracotta")
        Identifier blockId = Identifier.of("minecraft", key.toLowerCase());

        // Look up the block in the registry
        Block block = Registries.BLOCK.get(blockId);

        // Return the block's default state (or AIR if the block is not found)
        return block != null ? block.getDefaultState() : Blocks.SAND.getDefaultState();
    }
}
