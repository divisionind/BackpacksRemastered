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

public class PotentialBackpackItem extends NMSItemStack {

    public PotentialBackpackItem(ItemStack item) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        super(item);
    }

    public boolean isBackpack() throws InvocationTargetException, IllegalAccessException {
        return hasNBT("backpack_type");
    }

    public int getType() throws IllegalAccessException, InvocationTargetException {
        return (int)getNBT(NBTType.INT, "backpack_type");
    }

    public byte[] getData() throws IllegalAccessException, InvocationTargetException {
        return (byte[]) getNBT(NBTType.BYTE_ARRAY, "backpack_data");
    }

    public boolean hasData() throws InvocationTargetException, IllegalAccessException {
        return hasNBT("backpack_data");
    }

    public BackpackObject getTypeObject() throws InvocationTargetException, IllegalAccessException {
        return BackpackObject.getByType(getType());
    }
}
