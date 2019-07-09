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

public class PotentialBackpackItem {

    private ItemStack item;
    private Object craftItemStack;
    private Object tagCompound;

    public PotentialBackpackItem(ItemStack item) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        this.item = item;
        this.craftItemStack = NMSReflector.asNMSCopy(item);
        this.tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);
    }

    public boolean isBackpack() throws InvocationTargetException, IllegalAccessException {
        return NMSReflector.hasNBTKey(tagCompound, "backpack_type");
    }

    public BackpackObject getType() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return BackpackObject.getByType((int)NMSReflector.getNBT(tagCompound, NBTType.INT, "backpack_type"));
    }

    public byte[] getData() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (byte[]) NMSReflector.getNBT(tagCompound, NBTType.BYTE_ARRAY, "backpack_data");
    }

    public boolean hasData() throws InvocationTargetException, IllegalAccessException {
        return NMSReflector.hasNBTKey(tagCompound, "backpack_data");
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
