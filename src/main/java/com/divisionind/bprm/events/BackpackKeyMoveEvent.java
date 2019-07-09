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

import com.divisionind.bprm.BackpackRecipes;
import com.divisionind.bprm.FakeBackpackViewer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BackpackKeyMoveEvent implements Listener {
    @EventHandler
    public void onBackpackKeyMove(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (BackpackRecipes.BACKPACK_KEY.equals(item)) {
            if (e.getInventory().getViewers().contains(FakeBackpackViewer.INSTANCE)) {
                e.setCancelled(true);
            }
        }
    }
}
