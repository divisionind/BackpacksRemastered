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
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class BackpackCraftEvent implements Listener {
    @EventHandler
    public void onCraftEvent(CraftItemEvent e) {
        if (e.isCancelled()) return;
        try {
            ItemStack item = e.getCurrentItem();
            if (!item.getType().equals(Material.LEATHER_CHESTPLATE)) return; // optimization, does not need to run all that reflection if its not even the right material
            HumanEntity ent = e.getWhoClicked();

            Object craftItemStack = NMSReflector.asNMSCopy(item);
            Object tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);

            if (!NMSReflector.hasNBTKey(tagCompound, "backpack_type")) return;
            int backpack_type = (int)NMSReflector.getNBT(tagCompound, NBTType.INT, "backpack_type");
            BackpackObject backpack = BackpackObject.getByType(backpack_type);
            if (backpack == null || !backpack.hasCraftPermission(ent)) {
                ent.sendMessage(Backpacks.translate(String.format("&cYou do not have permission to craft the %s backpack.", backpack == null ? "null" : backpack.name().toLowerCase())));
                e.setCancelled(true);
                return;
            }

            ACommand.respondf(ent, "&eYou just crafted a %s backpack.", backpack.name().toLowerCase());
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPotentialCombineBackpack(PrepareItemCraftEvent e) {
        ItemStack[] st = e.getInventory().getMatrix();
        int cnull = 0;
        for (ItemStack i : st) {
            if (i == null) cnull++;
        }
        Backpacks.getInstance().getLogger().info(String.format("onPotentialCombineBackpack() = %s = %s", st.length, cnull));
    }
}
