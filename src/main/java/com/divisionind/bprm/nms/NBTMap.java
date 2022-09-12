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

package com.divisionind.bprm.nms;

import com.divisionind.bprm.nms.reflect.NBTType;
import com.divisionind.bprm.nms.reflect.NMSClass;
import com.divisionind.bprm.nms.reflect.NMSMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class NBTMap {

    private final Object tagCompound;

    public NBTMap(Object tagCompound) {
        this.tagCompound = tagCompound;
    }

    public NBTMap()
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        this.tagCompound = NMSClass.NBTTagCompound.getClazz().getDeclaredConstructor().newInstance();
    }

    public Object getTagCompound() {
        return tagCompound;
    }

    public void setNBT(NBTType type, String key, Object value) throws InvocationTargetException, IllegalAccessException {
        type.getSet().invoke(tagCompound, key, value);
    }

    @Deprecated
    public void setNBT(String key, Object value) throws InvocationTargetException, IllegalAccessException {
        // this does not work because it gets the object class of primitives
        //   (can fix by mapping primitives to their class object)
        setNBT(NBTType.getByClass(value.getClass()), key, value);
    }

    public Object getNBT(NBTType type, String key) throws InvocationTargetException, IllegalAccessException {
        return type.getGet().invoke(tagCompound, key);
    }

    public <T> T getNBT(Class<T> clazz, String key) throws InvocationTargetException, IllegalAccessException {
        // about 40% slower than the alternative in practice, see POCNBTMapSpeedTest,
        // thats only a difference of 18ns(time for light to travel ~5 meters) per an operation
        return (T) getNBT(NBTType.getByClass(clazz), key);
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
        return (Set<String>) NMSMethod.getKeys.getMethod().invoke(tagCompound);
    }

    public byte getKeyInternalTypeId(String key) throws InvocationTargetException, IllegalAccessException {
        Object nbtBase = NBTType.COMPOUND.getGet().invoke(tagCompound, key);
        return (byte) NMSMethod.getTypeId.getMethod().invoke(nbtBase);
    }
}
