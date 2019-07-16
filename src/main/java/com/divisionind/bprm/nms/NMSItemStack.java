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

package com.divisionind.bprm.nms;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class NMSItemStack extends NBTMap {

    private ItemStack item;
    private Object craftItemStack;

    public NMSItemStack(ItemStack item) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        this(item, NMSReflector.asNMSCopy(item));
    }

    private NMSItemStack(ItemStack item, Object craftItemStack) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        super(NMSReflector.getNBTTagCompound(craftItemStack));
        this.item = item;
        this.craftItemStack = craftItemStack;
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
}
