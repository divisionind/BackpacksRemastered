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

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class EventProcessor implements Listener {
    @EventHandler
    public void onCraftEvent(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();
        HumanEntity ent = e.getWhoClicked();
        ent.sendMessage("You just crafted an item.");

        try {
            Object craftItemStack = NMSReflector.asNMSCopy(item);
            Object tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);
            NMSReflector.setNBT(tagCompound, "Long", long.class, "backpack_id", 400L);
            e.setCurrentItem(NMSReflector.asBukkitCopy(craftItemStack));
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onDamageEvent(PlayerItemDamageEvent e) {
        // if has backpack_id, cancel
    }
}
