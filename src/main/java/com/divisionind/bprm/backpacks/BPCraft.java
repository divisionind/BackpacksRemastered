/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 - 2020, Andrew Howard, <divisionind.com>
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
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.UpdateItemCallback;
import com.divisionind.bprm.nms.reflect.NMS;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class BPCraft extends BackpackHandler {
    @Override
    public Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception {
        InventoryView inventoryView = p.openWorkbench(null, true);
        if (inventoryView != null) {
            return inventoryView.getTopInventory();
        } else return null;
    }

    @Override
    public void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack, UpdateItemCallback callback) throws Exception {
        // TODO possibly save the contents of the workbench, this would make life a lot easier
    }

    @Override
    public void finalizeBackpackOpen(HumanEntity p, Inventory inv) throws Exception {
        inv.getViewers().add(NMS.FAKE_VIEWER); // we do not want to open the backpack here because it was probably already opened
    }
}
