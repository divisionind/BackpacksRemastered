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

import com.divisionind.bprm.BackpackObject;
import com.divisionind.bprm.FakeBackpackViewer;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.backpacks.BPCombined;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.reflect.InvocationTargetException;

public class BackpackInvClickEvent implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // if is backpack inventory
        if (e.getInventory().getViewers().contains(FakeBackpackViewer.INSTANCE)) {
            try {
                PotentialBackpackItem backpack = new PotentialBackpackItem(e.getWhoClicked().getInventory().getChestplate());
                if (backpack.isBackpack()) {
                    BackpackObject bpo = backpack.getTypeObject();

                    // if backpack is combined bp, run click event in handler
                    if (bpo != null && bpo.equals(BackpackObject.COMBINED)) {
                        ((BPCombined)bpo.getHandler()).onClick(e);
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
                ex.printStackTrace();
            }
        }
    }
}
