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

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

import static com.divisionind.bprm.nms.reflect.NMSClass.*;
import static com.divisionind.bprm.nms.KnownVersion.*;

/**
 * WARNING: DO NOT MODIFY PREVIOUS VERSION NMS, ONLY APPEND W/ NEW before()'s
 * Otherwise, you will break previous version compatibility.
 * See how the other entries have been appended for examples.
 */
@SuppressWarnings("unchecked")
public enum NMSMethod {
    asNMSCopy(CraftItemStack, "asNMSCopy", ItemStack.class),
    asBukkitCopy(CraftItemStack, "asBukkitCopy", ItemStack.getClazz()),
    getTag(ItemStack, (v1_18_R1.before() ? "getTag" :
            v1_18_R2.before() ? "s" : v1_19_R1.before() ? "t" : "v")),
    setTag(ItemStack, (v1_18_R1.before() ? "setTag" : "c"), NBTTagCompound.getClazz()),
    hasKey(NBTTagCompound, (v1_18_R1.before() ? "hasKey" : "e"), String.class),
    removeTag(NBTTagCompound, (v1_18_R1.before() ? "remove" : "r"), String.class),
    getTypeId(NBTBase, (v1_18_R1.before() ? "getTypeId" : "a")),
    getKeys(() -> v1_13_R1.before() ?
            NBTTagCompound.getClazz().getMethod("c") :
            NBTTagCompound.getClazz().getMethod(v1_18_R1.before() ? "getKeys" : "d")),
    getServer(CraftServer, "getServer"),
    getWorldServer(() -> v1_13_R1.before() ?
            MinecraftServer.getClazz().getMethod((v1_18_R1.before() ? "getWorldServer" : "a"), int.class) :
            fuzzyMethodLookup(MinecraftServer.getClazz(), false,
                    (method) -> method.getName().equals(v1_18_R1.before() ? "getWorldServer" : "a") ? 0 : 1)),
    tick(() -> v1_13_R1.before() ?
            TileEntityFurnace.getClazz().getMethod("e") : (v1_17_R1.before() ?
            TileEntityFurnace.getClazz().getMethod("tick") :
            TileEntityFurnace.getClazz().getMethod("a", World.getClazz(), BlockPosition.getClazz(), IBlockData.getClazz(), TileEntityFurnace.getClazz()))),
    isBurning(true, true, TileEntityFurnace, (v1_18_R1.before() ? "isBurning" : "i")),
    save(true, true, TileEntityFurnace, (v1_18_R1.before() ? "save" : v1_19_R1.before() ? "a" : "b"), NBTTagCompound.getClazz()),
    load(() -> v1_16_R1.before() ?
            TileEntityFurnace.getClazz().getMethod((v1_18_R1.before() ? "load" : "a"), NBTTagCompound.getClazz()) :
            fuzzyMethodLookup(TileEntityFurnace.getClazz(), false,
                    (method) -> method.getName().equals(v1_18_R1.before() ? "load" : "a") ? 0 : 1)),
    getInventory(CraftInventory, "getInventory"),
    getBukkitEntity(EntityPlayer, "getBukkitEntity"),
    getWorld(() ->      v1_17_R1.before() ? null : TileEntity.getClazz().getMethod(v1_18_R1.before() ? "getWorld" : "k")),
    getPosition(() ->   v1_17_R1.before() ? null : TileEntity.getClazz().getMethod(v1_18_R1.before() ? "getPosition" : "p")),
    getBlock(() ->      v1_17_R1.before() ? null : TileEntity.getClazz().getMethod(v1_18_R1.before() ? "getBlock" : "q")),
    getTileEntity(() -> v1_17_R1.before() ? null : CraftTileInventoryConverter_Furnace
            .getClazz().getMethod("getTileEntity"));

    private Method method;
    private MethodInitializer methodInitializer;
    private boolean methodPrivate;

    NMSMethod(NMSClass owner, String name, Class... params) {
        this(false, owner, name, params);
    }

    NMSMethod(boolean declared, NMSClass owner, String name, Class... params) {
        this(declared ? () -> owner.getClazz().getDeclaredMethod(name, params) :
                () -> owner.getClazz().getMethod(name, params));
    }

    NMSMethod(boolean methodPrivate, boolean declared, NMSClass owner, String name, Class... params) {
        this(declared, owner, name, params); // declared must always be true if methodPrivate is
        this.methodPrivate = methodPrivate;
    }

    NMSMethod(MethodInitializer methodInitializer) {
        this.methodInitializer = methodInitializer;
    }

    void init() throws NoSuchMethodException {
        this.method = methodInitializer.init();

        if (methodPrivate)
            this.method.setAccessible(true);

        this.methodInitializer = null; // so the initializer can be garbage collected, we will never need it again
    }

    public Method getMethod() {
        return method;
    }

    private static Method fuzzyMethodLookup(Class<?> clazz, boolean declared, Comparable<Method> condition)
            throws NoSuchMethodException {
        for (Method method : (declared ? clazz.getDeclaredMethods() : clazz.getMethods())) {
            if (condition.compareTo(method) == 0)
                return method;
        }

        throw new NoSuchMethodException(String.format("Failed to match fuzzy method lookup in class: %s.",
                clazz.getName()));
    }

    private interface MethodInitializer {
        Method init() throws NoSuchMethodException;
    }
}
