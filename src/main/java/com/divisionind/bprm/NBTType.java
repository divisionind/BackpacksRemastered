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

public enum NBTType {

    LONG("Long", long.class),
    BOOLEAN("Boolean", boolean.class),
    BYTE("Byte", byte.class),
    BYTE_ARRAY("ByteArray", byte[].class),
    DOUBLE("Double", double.class),
    FLOAT("Float", float.class),
    INT("Int", int.class),
    INT_ARRAY("IntArray", int[].class),
    SHORT("Short", short.class),
    STRING("String", String.class);

    private String type;
    private Class classType;

    NBTType(String type, Class classType) {
        this.type = type;
        this.classType = classType;
    }

    public String getType() {
        return type;
    }

    public Class getClassType() {
        return classType;
    }
}
