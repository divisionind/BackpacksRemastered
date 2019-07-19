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

package com.divisionind.bprm.backpacks;

import com.divisionind.bprm.BackpackHandler;
import com.divisionind.bprm.Backpacks;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.UpdateItemCallback;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BPCraft implements BackpackHandler {
    @Override
    public Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception {
        Inventory inv = p.openWorkbench(null, true).getTopInventory();
        inv.getViewers().add(Backpacks.FAKE_VIEWER); // we have to add this explicitly on this backpack because we are returning null
        return null;
    }

    @Override
    public void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack, UpdateItemCallback callback) throws Exception {
        ItemStack item = e.getInventory().getItem(0); // 0 = output | 1-9 = input
        // i was thinking about making the crafting backpack save the contents of the workbench but idk
    }
}
