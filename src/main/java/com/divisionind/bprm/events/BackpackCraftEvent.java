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

package com.divisionind.bprm.events;

import com.divisionind.bprm.*;
import com.divisionind.bprm.nms.KnownVersion;
import com.divisionind.bprm.nms.NBTMap;
import com.divisionind.bprm.nms.NBTType;
import com.divisionind.bprm.nms.NMSItemStack;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BackpackCraftEvent implements Listener {

    public static final List<Material> COMBINABLE;

    static {
        COMBINABLE = new ArrayList<>();

        // because it used to be called GOLD_CHESTPLATE pre 1.13
        if (KnownVersion.v1_13_R1.isBefore()) {
            COMBINABLE.add(Material.valueOf("GOLD_CHESTPLATE"));
        } else COMBINABLE.add(Material.valueOf("GOLDEN_CHESTPLATE"));
        COMBINABLE.add(Material.DIAMOND_CHESTPLATE);
        COMBINABLE.add(Material.IRON_CHESTPLATE);
        COMBINABLE.add(Material.LEATHER_CHESTPLATE);
        COMBINABLE.add(Material.CHAINMAIL_CHESTPLATE);
        COMBINABLE.add(Material.ELYTRA);
    }

    @EventHandler
    public void onCraftEvent(CraftItemEvent e) {
        if (e.isCancelled()) return;
        try {
            ItemStack item = e.getCurrentItem();
            HumanEntity ent = e.getWhoClicked();
            PotentialBackpackItem backpackItem = new PotentialBackpackItem(item);

            if (!backpackItem.isBackpack()) return;
            int backpack_type = backpackItem.getType();
            BackpackObject backpack = BackpackObject.getByType(backpack_type);
            if (backpack == null || !backpack.hasCraftPermission(ent)) {
                ent.sendMessage(Backpacks.translate(String.format("&cYou do not have permission to craft the %s backpack.", backpack == null ? "null" : backpack.name().toLowerCase())));
                e.setCancelled(true);
                return;
            }

            ACommand.respondf(ent, "&eYou just crafted a %s backpack.", backpack.name().toLowerCase());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPotentialCombineBackpack(PrepareItemCraftEvent e) {
        try {
            // figure out the items in the crafting grid
            ItemStack[] inGrid = e.getInventory().getMatrix();
            List<ItemStack> notNull = new ArrayList<>();
            for (ItemStack item : inGrid) {
                if (item != null) notNull.add(item);
            }

            // if there are only two items in the grid
            if (notNull.size() == 2) {
                // figure out which item is the backpack
                PotentialBackpackItem backpack = null;
                NMSItemStack itemOtherThanBackpack = null;
                for (ItemStack item : notNull) {
                   PotentialBackpackItem potentialBackpackItem = new PotentialBackpackItem(item);
                   if (potentialBackpackItem.isBackpack()) {
                       // there were two backpacks in grid, return
                       if (backpack != null) {
                           PotentialBackpackItem combinedBackpack;
                           PotentialBackpackItem normalBackpack;
                           if (backpack.getType() == BackpackObject.COMBINED.getTypeId()) {
                               combinedBackpack = backpack;
                               normalBackpack = potentialBackpackItem;
                           } else
                           if (potentialBackpackItem.getType() == BackpackObject.COMBINED.getTypeId()) {
                               combinedBackpack = potentialBackpackItem;
                               normalBackpack = backpack;
                           } else return;

                           // resolve tag compound of stored backpacks
                           NBTMap storedBackpacks;
                           if (combinedBackpack.hasNBT("storedBackpacks")) {
                               storedBackpacks = combinedBackpack.getAsMap("storedBackpacks");
                           } else combinedBackpack.setAsMap("storedBackpacks", storedBackpacks = new NBTMap());
                           Set<String> keys = storedBackpacks.getKeys();

                           // verify no more than 9 backpacks have been added
                           if (keys.size() == 9) return;

                           // get lowest index (make this better later)
                           int lowest = 0;
                           for (int i = 0;i<9;i++) {
                               if (!keys.contains(Integer.toString(i))) {
                                   lowest = i;
                                   break;
                               }
                           }

                           // set backpack item at that index, return result
                           storedBackpacks.setNBT(NBTType.BYTE_ARRAY, Integer.toString(lowest), BackpackSerialization.toByteArrayItemStack(normalBackpack.getItem()));
                           e.getInventory().setResult(combinedBackpack.getModifiedItem());
                           return;
                       }

                       backpack = potentialBackpackItem;
                   } else itemOtherThanBackpack = potentialBackpackItem;
                }

                // if there is no backpack in grid, we are done
                if (backpack == null || itemOtherThanBackpack == null) return;

                Material type = itemOtherThanBackpack.getItem().getType();
                if (COMBINABLE.contains(type)) {
                    // if backpack has already be combined, stop it from being combined again (also allows backpacks to be combined infinitely with leather chestplates)
                    if (!backpack.getItem().getType().equals(Material.LEATHER_CHESTPLATE)) return;

                    int bpType = backpack.getType();

                    // temporarily disables combined backpack with this, will add in future
                    if (bpType == BackpackObject.COMBINED.getTypeId()) return;

                    // if other items type is contained in our list of combinables
                    itemOtherThanBackpack.setNBT(NBTType.INT, "backpack_type", type); // TODO make this more generic so it can be used with the combined backpack
                    if (backpack.hasData()) itemOtherThanBackpack.setNBT(NBTType.BYTE_ARRAY, "backpack_data", backpack.getData());

                    // copy lore from backpack and add it to the bottom of this items lore
                    ItemStack result = itemOtherThanBackpack.getModifiedItem();
                    ItemMeta meta = result.getItemMeta();
                    List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

                    BackpackObject bpo = BackpackObject.getByType(bpType);
                    if (bpo == null) return;
                    lore.add("");
                    lore.addAll(bpo.getHandler().lore().build());
                    meta.setLore(lore);
                    result.setItemMeta(meta);

                    // set resulting item
                    e.getInventory().setResult(result);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
