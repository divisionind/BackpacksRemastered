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

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class NMSItemStack {

    private ItemStack item;
    private Object craftItemStack;
    private Object tagCompound;

    public NMSItemStack(ItemStack item) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        this.item = item;
        this.craftItemStack = NMSReflector.asNMSCopy(item);
        this.tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);
    }

    public boolean hasNBT(String key) throws InvocationTargetException, IllegalAccessException {
        return NMSReflector.hasNBTKey(tagCompound, key);
    }

    public Object getNBT(NBTType type, String key) throws IllegalAccessException, InvocationTargetException {
        return NMSReflector.getNBT(tagCompound, type, key);
    }

    public void setNBT(NBTType type, String key, Object value) throws IllegalAccessException, InvocationTargetException {
        NMSReflector.setNBT(tagCompound, type, key, value);
    }

    public NBTMap getAsMap(String key) throws InvocationTargetException, IllegalAccessException {
        return NMSReflector.getAsMap(tagCompound, key);
    }

    public void setAsMap(String key, NBTMap map) throws InvocationTargetException, IllegalAccessException {
        NMSReflector.setAsMap(tagCompound, key, map);
    }

    public void removeNBT(String key) throws InvocationTargetException, IllegalAccessException {
        NMSReflector.removeNBT(tagCompound, key);
    }

    public ItemStack getModifiedItem() throws InvocationTargetException, IllegalAccessException {
        return NMSReflector.asBukkitCopy(craftItemStack);
    }

    public ItemStack getItem() {
        return item;
    }

    public Object getCraftItemStack() {
        return craftItemStack;
    }

    public Object getTagCompound() {
        return tagCompound;
    }
}
