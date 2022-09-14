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

package com.divisionind.bprm;

import com.divisionind.bprm.exceptions.UnknownBackpackException;
import com.divisionind.bprm.nms.NMSItemStack;
import com.divisionind.bprm.nms.reflect.NBTType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class PotentialBackpackItem extends NMSItemStack {

    public static final String FIELD_NAME_TYPE = "backpack_type";
    public static final String FIELD_NAME_DATA = "backpack_data";

    public PotentialBackpackItem(ItemStack item)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        super(item);
    }

    public boolean isBackpack() throws InvocationTargetException, IllegalAccessException {
        return hasNBT(FIELD_NAME_TYPE);
    }

    public int getType() throws IllegalAccessException, InvocationTargetException {
        return (int) getNBT(NBTType.INT, FIELD_NAME_TYPE);
    }

    public byte[] getData() throws IllegalAccessException, InvocationTargetException {
        return (byte[]) getNBT(NBTType.BYTE_ARRAY, FIELD_NAME_DATA);
    }

    public void setType(int type) throws InvocationTargetException, IllegalAccessException {
        setNBT(NBTType.INT, FIELD_NAME_TYPE, type);
    }

    public void setData(byte[] data) throws InvocationTargetException, IllegalAccessException {
        setNBT(NBTType.BYTE_ARRAY, FIELD_NAME_DATA, data);
    }

    public boolean hasData() throws InvocationTargetException, IllegalAccessException {
        return hasNBT(FIELD_NAME_DATA);
    }

    public BackpackObject getTypeObject() throws InvocationTargetException, IllegalAccessException {
        return BackpackObject.getByType(getType());
    }

    public BackpackHandler getHandler() throws InvocationTargetException, IllegalAccessException, UnknownBackpackException {
        BackpackObject bp = BackpackObject.getByType(getType());
        if (bp != null)
            return bp.getHandler();
        else throw new UnknownBackpackException();
    }
}
