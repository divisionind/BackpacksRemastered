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

package com.divisionind.bprm.nms.reflect;

import java.lang.reflect.Method;

public enum NBTType {

    LONG("Long", long.class, 4),
    BOOLEAN("Boolean", boolean.class, 255), // booleans are considered bytes | 1 = true | 0 = false
    BYTE("Byte", byte.class, 1),
    BYTE_ARRAY("ByteArray", byte[].class, 7),
    DOUBLE("Double", double.class, 6),
    FLOAT("Float", float.class, 5),
    INT("Int", int.class, 3),
    INT_ARRAY("IntArray", int[].class, 11),
    SHORT("Short", short.class, 2),
    STRING("String", String.class, 8),
    COMPOUND("", null, 10);

    private String type;
    private Class classType;
    private byte internalId;
    private Method set;
    private Method get;

    NBTType(String type, Class classType, int internalId) {
        this.type = type;
        this.classType = classType;
        this.internalId = (byte)internalId;
    }

    public String getType() {
        return type;
    }

    public Class getClassType() {
        return classType;
    }

    public byte getInternalId() {
        return internalId;
    }

    public Method getSet() {
        return set;
    }

    public Method getGet() {
        return get;
    }

    public static NBTType getByInternalId(byte id) {
        for (NBTType type : values()) {
            if (type.internalId == id) return type;
        }
        return null;
    }

    void init(Class cNBTTagCompound) throws NoSuchMethodException {
        set = cNBTTagCompound.getMethod(String.format("set%s", getType()), String.class, getClassType());
        get = cNBTTagCompound.getMethod(String.format("get%s", getType()), String.class);
    }

    void setClassType(Class classType) {
        this.classType = classType;
    }
}
