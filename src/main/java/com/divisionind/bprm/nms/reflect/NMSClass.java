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
import com.divisionind.bprm.nms.KnownVersion;

import static com.divisionind.bprm.nms.reflect.NMS.*;

public enum NMSClass {

    CraftItemStack(CRAFT, "inventory.CraftItemStack"),
    NBTTagCompound((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.nbt."), "NBTTagCompound"),
    ItemStack((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.world.item."), "ItemStack"),
    NBTBase((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.nbt."), "NBTBase"),
    TileEntity((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.world.level.block.entity."), "TileEntity"),
    TileEntityFurnace((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.world.level.block.entity."), "TileEntityFurnace"),
    TileEntityFurnaceFurnace((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.world.level.block.entity."), "TileEntityFurnaceFurnace", KnownVersion.v1_14_R1.isBefore()),
    CraftServer(CRAFT, "CraftServer"),
    DedicatedServer(SERVER, (KnownVersion.v1_18_R1.isBefore() ? "DedicatedServer" : "dedicated.DedicatedServer")),
    MinecraftServer(SERVER, "MinecraftServer"),
    World((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.world.level."), "World"),
    DimensionManager((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.world.level.dimension."), "DimensionManager"),
    CraftInventoryFurnace(CRAFT, "inventory.CraftInventoryFurnace"),
    CraftInventory(CRAFT, "inventory.CraftInventory"),
    EntityPlayer(SERVER, (KnownVersion.v1_18_R1.isBefore() ? "EntityPlayer" : "level.EntityPlayer")),
    PlayerInteractManager(SERVER, (KnownVersion.v1_18_R1.isBefore() ? "PlayerInteractManager" : "level.PlayerInteractManager")),
    WorldServer(SERVER, (KnownVersion.v1_18_R1.isBefore() ? "WorldServer" : "level.WorldServer")),
    BlockPosition((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.core."), "BlockPosition", KnownVersion.v1_17_R1.isBefore()),
    IBlockData((KnownVersion.v1_18_R1.isBefore() ? SERVER : "net.minecraft.world.level.block.state."), "IBlockData", KnownVersion.v1_17_R1.isBefore()),
    CraftTileInventoryConverter_Furnace(CRAFT, "inventory.util.CraftTileInventoryConverter$Furnace", KnownVersion.v1_17_R1.isBefore());

    private String path;
    private Class<?> clazz;

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
