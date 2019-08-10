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

import com.divisionind.bprm.backpacks.*;
import com.divisionind.bprm.nms.NMSReflector;
import com.divisionind.bprm.nms.reflect.NBTType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public enum BackpackObject {

    SMALL(Color.BLACK, 0, new BPSmall()),
    LARGE(Color.MAROON, 1, new BPLarge()),
    LINKED(Color.BLUE, 2, new BPLinked()),
    COMBINED(Color.AQUA, 3, new BPCombined()),
    CRAFT(Color.ORANGE, 4, new BPCraft()),
    ENDER(Color.GREEN, 5, new BPEnder());

    private ItemStack item;
    private List<String> lore;

    private Color color;
    private int type;
    private String permission;
    private BackpackHandler handler;

    BackpackObject(Color color, int type, BackpackHandler handler) {
        this.color = color;
        this.type = type;
        this.permission = "backpacks.craft." + name().toLowerCase();
        this.handler = handler;
    }

    void init(String name, List<String> lore) {
        this.lore = lore;
        this.item = getBackpack(color, type, name, lore);
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

    public List<String> getLore() {
        return lore;
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

    private static ItemStack getBackpack(Color color, int type, String name, List<String> lore) {
        ItemStack backpack = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta)backpack.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(Backpacks.translate(name));
        meta.setLore(lore);
        backpack.setItemMeta(meta);

        // apply backpack_type nbt data
        try {
            return NMSReflector.setNBTOnce(backpack, NBTType.INT, PotentialBackpackItem.FIELD_NAME_TYPE, type);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return null;
        }
    }
}
