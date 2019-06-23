/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 Division Industries LLC
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

package com.divisionind.bprm;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSReflector { // TODO load methods/classes once and just invoke them in real-time

    private static final String VERSION = getVersion();
    private static final String SERVER_NMS_PATH = "net.minecraft.server." + VERSION + ".%s";
    private static final String CRAFT_NMS_PATH = "org.bukkit.craftbukkit." + VERSION + ".%s";

    public static String getServerClass(String className) {
        return String.format(SERVER_NMS_PATH, className);
    }

    public static String getCraftClass(String className) {
        return String.format(CRAFT_NMS_PATH, className);
    }

    private static NMSReflector inst;

    public static void initialize() throws ClassNotFoundException, NoSuchMethodException {
        inst = new NMSReflector();
    }

    private Class cCraftItemStack;
    private Class cNBTTagCompound;
    private Class cItemStack;

    private Method masNMSCopy;
    private Method masBukkitCopy;
    private Method mgetTag;
    private Method msetTag;
    private Method mhasKey;

    private NMSReflector() throws ClassNotFoundException, NoSuchMethodException {
        // get NMS classes
        cCraftItemStack = Class.forName(getCraftClass("inventory.CraftItemStack"));
        cNBTTagCompound = Class.forName(getServerClass("NBTTagCompound"));
        cItemStack = Class.forName(getServerClass("ItemStack"));

        // get NMS methods
        masNMSCopy = cCraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        masBukkitCopy = cCraftItemStack.getMethod("asBukkitCopy", Class.forName(getServerClass("ItemStack")));
        mgetTag = cItemStack.getMethod("getTag");
        msetTag = cItemStack.getMethod("setTag", cNBTTagCompound);
        mhasKey = cNBTTagCompound.getMethod("hasKey", String.class);
    }

    public static Object asNMSCopy(ItemStack item) throws InvocationTargetException, IllegalAccessException {
        return inst.masNMSCopy.invoke(null, item);
    }

    public static ItemStack asBukkitCopy(Object item) throws InvocationTargetException, IllegalAccessException {
        return (ItemStack) inst.masBukkitCopy.invoke(null, item);
    }

    public static Object getNBTTagCompound(Object nmsItemStack) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object nbtCompound = inst.mgetTag.invoke(nmsItemStack);
        if (nbtCompound == null) {
            nbtCompound = inst.cNBTTagCompound.newInstance();
            inst.msetTag.invoke(nmsItemStack, nbtCompound);
        }
        return nbtCompound;
    }

    public static boolean hasNBTKey(Object nmsTagCompound, String key) throws InvocationTargetException, IllegalAccessException {
        return (boolean)inst.mhasKey.invoke(nmsTagCompound, key);
    }

    public static void setNBT(Object nmsTagCompound, NBTType type, String key, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setKey = inst.cNBTTagCompound.getMethod(String.format("set%s", type.getType()), String.class, type.getClassType());
        setKey.invoke(nmsTagCompound, key, value);
    }

    public static Object getNBT(Object nmsTagCompound, NBTType type, String key) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getKey = inst.cNBTTagCompound.getMethod(String.format("get%s", type.getType()), String.class);
        return getKey.invoke(nmsTagCompound, key);
    }

    public static ItemStack setNBTOnce(ItemStack item, NBTType type, String key, Object value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object nmsItem = asNMSCopy(item);
        Object tagCompound = getNBTTagCompound(nmsItem);
        setNBT(tagCompound, type, key, value);
        return asBukkitCopy(nmsItem);
    }

    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
