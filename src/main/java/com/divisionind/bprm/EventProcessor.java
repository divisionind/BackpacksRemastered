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

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class EventProcessor implements Listener {

    /*
        DB STRUCTURE:

        backpacks: id (long) | type (int) | data (string)

        for small/large, data = base64 serialized inventory
        for linked, data = base64 serialized location of chest/chests
     */

    @EventHandler
    public void onCraftEvent(CraftItemEvent e) {
        try {
            ItemStack item = e.getCurrentItem();
            if (!item.getType().equals(Material.LEATHER_CHESTPLATE)) return; // optimization, does not need to run all that reflection if its not even the right material
            HumanEntity ent = e.getWhoClicked();

            Object craftItemStack = NMSReflector.asNMSCopy(item);
            Object tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);

            if (!NMSReflector.hasNBTKey(tagCompound, "backpack_type")) return;
            int backpack_type = (int)NMSReflector.getNBT(tagCompound, "Int", "backpack_type");
            BackpackRecipes.BackpackItem backpack = BackpackRecipes.BackpackItem.getById(backpack_type);
            if (backpack == null || !backpack.hasCraftPermission(ent)) {
                ent.sendMessage(Backpacks.translate(String.format("&cYou do not have permission to craft the %s backpack.", backpack == null ? "null" : backpack.name().toLowerCase())));
                e.setCancelled(true);
                return;
            }

            NMSReflector.setNBT(tagCompound, "Long", long.class, "backpack_id", 400L); // TODO get next backpack id
            e.setCurrentItem(NMSReflector.asBukkitCopy(craftItemStack));

            ent.sendMessage("You just crafted a backpack.");
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onDamageEvent(PlayerItemDamageEvent e) {
        // if has backpack_id, cancel
        if (!e.getItem().getType().equals(Material.LEATHER_CHESTPLATE)) return; // optimization, does not need to run all that reflection if its not even the right material
        try {
            Object craftItemStack = NMSReflector.asNMSCopy(e.getItem());
            Object tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);
            if (NMSReflector.hasNBTKey(tagCompound, "backpack_id")) e.setCancelled(true);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }
}
