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

    private Class classCraftItemStack;
    private Class classNBTTagCompound;
    private Class classItemStack;

    private Method methodasNMSCopy;
    private Method methodasBukkitCopy;
    private Method methodgetTag;
    private Method methodsetTag;
    private Method methodhasKey;

    private NMSReflector() throws ClassNotFoundException, NoSuchMethodException {
        // get NMS classes
        classCraftItemStack = Class.forName(getCraftClass("inventory.CraftItemStack"));
        classNBTTagCompound = Class.forName(getServerClass("NBTTagCompound"));
        classItemStack = Class.forName(getServerClass("ItemStack"));

        // get NMS methods
        methodasNMSCopy = classCraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        methodasBukkitCopy = classCraftItemStack.getMethod("asBukkitCopy", Class.forName(getServerClass("ItemStack")));
        methodgetTag = classItemStack.getMethod("getTag");
        methodsetTag = classItemStack.getMethod("setTag");
        methodhasKey = classNBTTagCompound.getMethod("hasKey", String.class);
    }

    public static Object asNMSCopy(ItemStack item) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class craftItemStack = Class.forName(getCraftClass("inventory.CraftItemStack"));
        Method asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
        return asNMSCopy.invoke(null, item);
    }

    public static ItemStack asBukkitCopy(Object item) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class craftItemStack = Class.forName(getCraftClass("inventory.CraftItemStack"));
        Method asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", Class.forName(getServerClass("ItemStack")));
        return (ItemStack) asBukkitCopy.invoke(null, item);
    }

    public static Object getNBTTagCompound(Object nmsItemStack) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class itemStack = Class.forName(getServerClass("ItemStack"));
        Method getTag = itemStack.getMethod("getTag");
        Object nbtCompound = getTag.invoke(nmsItemStack);
        if (nbtCompound == null) {
            Class tagCompoundClass = Class.forName(getServerClass("NBTTagCompound"));
            nbtCompound = tagCompoundClass.newInstance();
            Method setTag = itemStack.getMethod("setTag", tagCompoundClass);
            setTag.invoke(nmsItemStack, nbtCompound);
        }
        return nbtCompound;
    }

    public static boolean hasNBTKey(Object nmsTagCompound, String key) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class tagCompoundClass = Class.forName(getServerClass("NBTTagCompound"));
        Method hasKey = tagCompoundClass.getMethod("hasKey", String.class);
        return (boolean)hasKey.invoke(nmsTagCompound, key);
    }

    public static void setNBT(Object nmsTagCompound, NBTType type, String key, Object value) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class tagCompoundClass = Class.forName(getServerClass("NBTTagCompound"));
        Method setKey = tagCompoundClass.getMethod(String.format("set%s", type.getType()), String.class, type.getClassType());
        setKey.invoke(nmsTagCompound, key, value);
    }

    public static Object getNBT(Object nmsTagCompound, NBTType type, String key) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class tagCompoundClass = Class.forName(getServerClass("NBTTagCompound"));
        Method getKey = tagCompoundClass.getMethod(String.format("get%s", type.getType()), String.class);
        return getKey.invoke(nmsTagCompound, key);
    }

    public static ItemStack setNBTOnce(ItemStack item, NBTType type, String key, Object value) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
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
