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

import com.divisionind.bprm.nms.reflect.NBTType;
import com.divisionind.bprm.nms.reflect.NMSClass;
import com.divisionind.bprm.nms.reflect.NMSMethod;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class NMSItemStack extends NBTMap {

    private ItemStack item;
    private Object craftItemStack;

    public NMSItemStack(ItemStack item) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        this(item, NMSMethod.asNMSCopy.getMethod().invoke(null, item));
    }

    private NMSItemStack(ItemStack item, Object craftItemStack) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        super(getNBTTagCompound(craftItemStack));
        this.item = item;
        this.craftItemStack = craftItemStack;
    }

    public ItemStack getModifiedItem() throws InvocationTargetException, IllegalAccessException {
        return (ItemStack) NMSMethod.asBukkitCopy.getMethod().invoke(null, craftItemStack);
    }

    public ItemStack getItem() {
        return item;
    }

    public Object getCraftItemStack() {
        return craftItemStack;
    }

    private static Object getNBTTagCompound(Object nmsItemStack) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Object nbtCompound = NMSMethod.getTag.getMethod().invoke(nmsItemStack);
        if (nbtCompound == null) {
            nbtCompound = NMSClass.NBTTagCompound.getClazz().getDeclaredConstructor().newInstance();
            NMSMethod.setTag.getMethod().invoke(nmsItemStack, nbtCompound);
        }
        return nbtCompound;
    }


    public static ItemStack setNBTOnce(ItemStack item, NBTType type, String key, Object value) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        NMSItemStack nmsItem = new NMSItemStack(item);
        nmsItem.setNBT(type, key, value);
        return nmsItem.getModifiedItem();
    }
}
