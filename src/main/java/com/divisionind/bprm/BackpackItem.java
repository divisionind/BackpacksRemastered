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

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.InvocationTargetException;

public enum BackpackItem {

    SMALL(Color.BLACK, 0, "&aSmall Backpack", "backpacks.craft.small", new Small()),
    LARGE(Color.MAROON, 1, "&aLarge Backpack", "backpacks.craft.large", new Large()),
    LINKED(Color.BLUE, 2, "&aLinked Backpack", "backpacks.craft.linked", new Linked());

    private ItemStack item;
    private int type;
    private String permission;
    private BackpackHandler handler;

    BackpackItem(Color color, int type, String name, String permission, BackpackHandler handler) {
        this.item = getBackpack(color, type, name);
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

    public static BackpackItem getByType(int type) {
        for (BackpackItem bp : values()) {
            if (bp.type == type) return bp;
        }
        return null;
    }

    public static BackpackItem getByName(String name) {
        for (BackpackItem item : values()) {
            if (item.name().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    private static ItemStack getBackpack(Color color, int type, String name) {
        ItemStack backpack = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta)backpack.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(Backpacks.translate(name));
        backpack.setItemMeta(meta);

        // apply backpack_type nbt data
        try {
            return NMSReflector.setNBTOnce(backpack, NBTType.INT, "backpack_type", type);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return null;
        }
    }

    private static class Small implements BackpackHandler {
        @Override
        public void openBackpack(PlayerInteractEvent e, Object craftItemStack, Object tagCompound, boolean hasData) throws Exception {
            Inventory toOpen;
            if (hasData) {
                toOpen = InventorySerialization.fromByteArray((byte[])NMSReflector.getNBT(tagCompound, NBTType.BYTE_ARRAY, "backpack_data"));
            } else {
                toOpen = Bukkit.getServer().createInventory(null, 27, "Small Backpack");
            }
            e.getPlayer().openInventory(toOpen);
        }

        @Override
        public void onClose(InventoryCloseEvent e, Object craftItemStack, Object tagCompound) throws Exception {
            NMSReflector.setNBT(tagCompound, NBTType.BYTE_ARRAY, "backpack_data", InventorySerialization.toByteArray(e.getInventory(), e.getView().getTitle()));
            e.getPlayer().getInventory().setChestplate(NMSReflector.asBukkitCopy(craftItemStack));
        }
    }

    private static class Large implements BackpackHandler {
        @Override
        public void openBackpack(PlayerInteractEvent e, Object craftItemStack, Object tagCompound, boolean hasData) throws Exception {
            Inventory toOpen;
            if (hasData) {
                toOpen = InventorySerialization.fromByteArray((byte[])NMSReflector.getNBT(tagCompound, NBTType.BYTE_ARRAY, "backpack_data"));
            } else {
                toOpen = Bukkit.getServer().createInventory(null, 54, "Large Backpack");
            }
            e.getPlayer().openInventory(toOpen);
        }

        @Override
        public void onClose(InventoryCloseEvent e, Object craftItemStack, Object tagCompound) throws Exception {
            NMSReflector.setNBT(tagCompound, NBTType.BYTE_ARRAY, "backpack_data", InventorySerialization.toByteArray(e.getInventory(), e.getView().getTitle()));
            e.getPlayer().getInventory().setChestplate(NMSReflector.asBukkitCopy(craftItemStack));
        }
    }

    private static class Linked implements BackpackHandler {
        @Override
        public void openBackpack(PlayerInteractEvent e, Object craftItemStack, Object tagCompound, boolean hasData) throws Exception {
            ACommand.respondnop(e.getPlayer());
        }

        @Override
        public void onClose(InventoryCloseEvent e, Object craftItemStack, Object tagCompound) throws Exception {
            ACommand.respondnop(e.getPlayer());
        }
    }
}
