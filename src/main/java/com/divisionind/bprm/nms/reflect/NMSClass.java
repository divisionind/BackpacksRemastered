/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019, Andrew Howard, <divisionind.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.divisionind.bprm.nms.reflect;

import com.divisionind.bprm.exceptions.FuzzyClassLookupException;

import static com.divisionind.bprm.nms.reflect.NMS.*;
import static com.divisionind.bprm.nms.KnownVersion.*;

/**
 * WARNING: DO NOT MODIFY PREVIOUS VERSION NMS, ONLY APPEND W/ NEW before()'s
 * Otherwise, you will break previous version compatibility.
 * See how the other entries have been appended for examples.
 */
public enum NMSClass {
    CraftItemStack(CRAFT, "inventory.CraftItemStack"),
    NBTTagCompound((v1_18_R1.before() ? SERVER : "net.minecraft.nbt."), "NBTTagCompound"),
    ItemStack((v1_18_R1.before() ? SERVER : "net.minecraft.world.item."), "ItemStack"),
    NBTBase((v1_18_R1.before() ? SERVER : "net.minecraft.nbt."), "NBTBase"),
    TileEntity((v1_18_R1.before() ? SERVER : "net.minecraft.world.level.block.entity."), "TileEntity"),
    TileEntityFurnace((v1_18_R1.before() ? SERVER : "net.minecraft.world.level.block.entity."), "TileEntityFurnace"),
    TileEntityFurnaceFurnace((v1_18_R1.before() ? SERVER : "net.minecraft.world.level.block.entity."), "TileEntityFurnaceFurnace", v1_14_R1.before()),
    CraftServer(CRAFT, "CraftServer"),
    DedicatedServer(SERVER, (v1_18_R1.before() ? "DedicatedServer" : "dedicated.DedicatedServer")),
    MinecraftServer(SERVER, "MinecraftServer"),
    World((v1_18_R1.before() ? SERVER : "net.minecraft.world.level."), "World"),
    DimensionManager((v1_18_R1.before() ? SERVER : "net.minecraft.world.level.dimension."), "DimensionManager"),
    CraftInventoryFurnace(CRAFT, "inventory.CraftInventoryFurnace"),
    CraftInventory(CRAFT, "inventory.CraftInventory"),
    EntityPlayer(SERVER, (v1_18_R1.before() ? "EntityPlayer" : "level.EntityPlayer")),
    PlayerInteractManager(SERVER, (v1_18_R1.before() ? "PlayerInteractManager" : "level.PlayerInteractManager")),
    WorldServer(SERVER, (v1_18_R1.before() ? "WorldServer" : "level.WorldServer")),
    BlockPosition((v1_18_R1.before() ? SERVER : "net.minecraft.core."), "BlockPosition", v1_17_R1.before()),
    IBlockData((v1_18_R1.before() ? SERVER : "net.minecraft.world.level.block.state."), "IBlockData", v1_17_R1.before()),
    CraftTileInventoryConverter_Furnace(CRAFT, "inventory.util.CraftTileInventoryConverter$Furnace", v1_17_R1.before());

    private String path;
    private Class clazz;

    NMSClass(String base, String path) {
        this.path = base + path;
    }

    /**
     * Does not load the class specified if the version is before the specified
     */
    NMSClass(String base, String path, boolean condition) {
        this(base, path);

        // bit of a hack but ehh, ive been planning on recoding the nms stuff for a while so ill fix it later
        if (condition)
            this.path = null;
    }

    void init() throws ClassNotFoundException, FuzzyClassLookupException {
        try {
            if (path != null)
                clazz = Class.forName(path);
        } catch (ClassNotFoundException e) {
            // could not resolve class the traditional/fast way, use slow lookup (1.17+)
            if (classResolver == null) {
                classResolver = new FuzzyClassResolver();
            }

            String[] pathParts = path.split("\\.");
            String partialPath;

            if (path.startsWith(SERVER))
                partialPath = "net.minecraft";
            else
                partialPath = "org.bukkit.craftbukkit";

            path = classResolver.lookup(pathParts[pathParts.length - 1], partialPath); // lookup by class name
            clazz = Class.forName(path);
        }
    }

    public Class getClazz() {
        return clazz;
    }

    public String getPath() {
        return path;
    }

    private static FuzzyClassResolver classResolver;

    public static void cleanup() {
        classResolver = null;
    }
}
