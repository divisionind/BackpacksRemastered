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

import com.divisionind.bprm.backpacks.BPLarge;
import com.divisionind.bprm.backpacks.BPLinked;
import com.divisionind.bprm.backpacks.BPSmall;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.InvocationTargetException;

public enum BackpackObject {

    SMALL(Color.BLACK, 0, "&aSmall Backpack", "backpacks.craft.small", new BPSmall()),
    LARGE(Color.MAROON, 1, "&aLarge Backpack", "backpacks.craft.large", new BPLarge()),
    LINKED(Color.BLUE, 2, "&aLinked Backpack", "backpacks.craft.linked", new BPLinked());

    private ItemStack item;
    private int type;
    private String permission;
    private BackpackHandler handler;

    BackpackObject(Color color, int type, String name, String permission, BackpackHandler handler) {
        this.item = getBackpack(color, type, name, handler.lore());
        this.type = type;
        this.permission = permission;
        this.handler = handler;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getTypeId() {
        return type;
    }

    public boolean hasCraftPermission(HumanEntity entity) {
        return entity.hasPermission(permission);
    }

    public BackpackHandler getHandler() {
        return handler;
    }

    public static BackpackObject getByType(int type) {
        for (BackpackObject bp : values()) {
            if (bp.type == type) return bp;
        }
        return null;
    }

    public static BackpackObject getByName(String name) {
        for (BackpackObject item : values()) {
            if (item.name().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    private static ItemStack getBackpack(Color color, int type, String name, LoreBuilder lore) {
        ItemStack backpack = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta)backpack.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(Backpacks.translate(name));
        meta.setLore(lore.build());
        backpack.setItemMeta(meta);

        // apply backpack_type nbt data
        try {
            return NMSReflector.setNBTOnce(backpack, NBTType.INT, "backpack_type", type);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return null;
        }
    }
}
