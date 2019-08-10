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

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class NBTMap {

    private Object tagCompound;

    public NBTMap(Object tagCompound) {
        this.tagCompound = tagCompound;
    }

    public NBTMap() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        this.tagCompound = NMSClass.NBTTagCompound.getClazz().getDeclaredConstructor().newInstance();
    }

    public Object getTagCompound() {
        return tagCompound;
    }

    public void setNBT(NBTType type, String key, Object value) throws InvocationTargetException, IllegalAccessException {
        type.getSet().invoke(tagCompound, key, value);
    }

    public Object getNBT(NBTType type, String key) throws InvocationTargetException, IllegalAccessException {
        return type.getGet().invoke(tagCompound, key);
    }

    public void removeNBT(String key) throws InvocationTargetException, IllegalAccessException {
        NMSMethod.removeTag.getMethod().invoke(tagCompound, key);
    }

    public void setAsMap(String key, NBTMap value) throws InvocationTargetException, IllegalAccessException {
        setNBT(NBTType.COMPOUND, key, value.getTagCompound());
    }

    public NBTMap getAsMap(String key) throws InvocationTargetException, IllegalAccessException {
        Object nbtBase = getNBT(NBTType.COMPOUND, key);
        return new NBTMap(NMSClass.NBTTagCompound.getClazz().cast(nbtBase));
    }

    public boolean hasNBT(String key) throws InvocationTargetException, IllegalAccessException {
        return (boolean) NMSMethod.hasKey.getMethod().invoke(tagCompound, key);
    }

    public Set<String> getKeys() throws InvocationTargetException, IllegalAccessException {
        return (Set<String>)NMSMethod.getKeys.getMethod().invoke(tagCompound);
    }

    public byte getKeyInternalTypeId(String key) throws InvocationTargetException, IllegalAccessException {
        Object nbtBase = NBTType.COMPOUND.getGet().invoke(tagCompound, key);
        return (byte)NMSMethod.getTypeId.getMethod().invoke(nbtBase);
    }
}
