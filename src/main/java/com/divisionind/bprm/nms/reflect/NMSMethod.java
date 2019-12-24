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

package com.divisionind.bprm.nms.reflect;

import com.divisionind.bprm.nms.KnownVersion;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

import static com.divisionind.bprm.nms.reflect.NMSClass.*;

public enum NMSMethod {

    asNMSCopy(CraftItemStack, "asNMSCopy", ItemStack.class),
    asBukkitCopy(CraftItemStack, "asBukkitCopy", ItemStack.getClazz()),
    getTag(ItemStack, "getTag"),
    setTag(ItemStack, "setTag", NBTTagCompound.getClazz()),
    hasKey(NBTTagCompound, "hasKey", String.class),
    removeTag(NBTTagCompound, "remove", String.class),
    getTypeId(NBTBase, "getTypeId"),
    getKeys(() -> KnownVersion.v1_13_R1.isBefore() ? NBTTagCompound.getClazz().getMethod("c") : NBTTagCompound.getClazz().getMethod("getKeys")),
    setWorld(TileEntity, "setWorld", World.getClazz()),
    getServer(CraftServer, "getServer"),
    getWorldServer(MinecraftServer, "getWorldServer", DimensionManager.getClazz()),
    tick(TileEntityFurnace, "tick"),
    isBurning(true, true, TileEntityFurnace, "isBurning"),
    save(TileEntityFurnace, "save", NBTTagCompound.getClazz()),
    load(TileEntityFurnace, "load", NBTTagCompound.getClazz()),
    getInventory(CraftInventory, "getInventory"),
    getBukkitEntity(EntityPlayer, "getBukkitEntity");

    private Method method;
    private MethodInitializer methodInitializer;
    private boolean methodPrivate;

    NMSMethod(NMSClass owner, String name, Class... params) {
        this(false, owner, name, params);
    }

    NMSMethod(boolean declared, NMSClass owner, String name, Class... params) {
        this(declared ? () -> owner.getClazz().getDeclaredMethod(name, params) :
                () -> owner.getClazz().getMethod(name, params));
    }

    NMSMethod(boolean methodPrivate, boolean declared, NMSClass owner, String name, Class... params) {
        this(declared, owner, name, params); // declared must always be true if methodPrivate is
        this.methodPrivate = methodPrivate;
    }

    NMSMethod(MethodInitializer methodInitializer) {
        this.methodInitializer = methodInitializer;
    }

    void init() throws NoSuchMethodException {
        this.method = methodInitializer.init();
        if (methodPrivate) this.method.setAccessible(true);
        this.methodInitializer = null; // so the initializer can be garbage collected, we will never need it again
    }

    public Method getMethod() {
        return method;
    }

    private interface MethodInitializer {
        Method init() throws NoSuchMethodException;
    }
}
