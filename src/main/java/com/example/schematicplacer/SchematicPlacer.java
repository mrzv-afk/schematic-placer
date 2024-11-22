package com.example.schematicplacer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class SchematicPlacer extends JavaPlugin {
    private final Map<String, Material> blockDataCache = new HashMap<>();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File schematicsDir = new File(getDataFolder(), "schematics");
        if (!schematicsDir.exists()) {
            schematicsDir.mkdirs();
        }

        getCommand("loadschematic").setExecutor(new LoadSchematicCommand(this));
        getLogger().info("SchematicPlacer успешно включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SchematicPlacer выключен!");
    }

    public void placeSchematic(Location location, File file, float yaw) {
        try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
            short width = input.readShort();
            short height = input.readShort();
            short length = input.readShort();

            byte[] blocks = new byte[width * height * length];
            input.readFully(blocks);

            byte[] data = new byte[width * height * length];
            input.readFully(data);

            float rotation = -yaw;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < length; z++) {
                        int index = y * width * length + z * width + x;
                        int blockId = blocks[index] & 0xFF;
                        byte blockData = data[index];

                        if (blockId == 0) continue;

                        Vector rotatedPos = rotateAround(new Vector(x, y, z), rotation);
                        Location blockLoc = location.clone().add(rotatedPos);
                        Block block = blockLoc.getBlock();

                        Material material = getMaterial(blockId, blockData);
                        if (material != null) {
                            block.setType(material, false);
                        }
                    }
                }
            }

            getLogger().info("Схематика успешно размещена!");
        } catch (IOException e) {
            getLogger().severe("Ошибка при загрузке схематики: " + e.getMessage());
        }
    }

    private Material getMaterial(int blockId, byte data) {
        String cacheKey = blockId + ":" + data;
        return blockDataCache.computeIfAbsent(cacheKey, k -> {
            switch (blockId) {
                case 1: return Material.STONE;
                case 2: return Material.GRASS_BLOCK;
                case 3: return Material.DIRT;
                case 4: return Material.COBBLESTONE;
                case 5: return Material.OAK_PLANKS;
                case 12: return Material.SAND;
                case 13: return Material.GRAVEL;
                case 17: return Material.OAK_LOG;
                case 18: return Material.OAK_LEAVES;
                case 20: return Material.GLASS;
                case 24: return Material.SANDSTONE;
                case 98: return Material.STONE_BRICKS;
                case 133: return Material.EMERALD_BLOCK;
                default: return null;
            }
        });
    }

    private Vector rotateAround(Vector pos, float rotation) {
        double rad = Math.toRadians(rotation);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        double x = pos.getX() * cos - pos.getZ() * sin;
        double z = pos.getX() * sin + pos.getZ() * cos;
        return new Vector(x, pos.getY(), z);
    }
}
