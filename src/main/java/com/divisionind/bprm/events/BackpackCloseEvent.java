/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019, Andrew Howard, <divisionind.com>
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

import com.divisionind.bprm.ACommand;
import com.divisionind.bprm.BackpackObject;
import com.divisionind.bprm.FakeBackpackViewer;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.nms.reflect.NMS;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class BackpackCloseEvent implements Listener {
    @EventHandler
    public void onBackpackClose(InventoryCloseEvent e) {
        // is it a backpack?
        FakeBackpackViewer viewer = NMS.getBackpackViewer(e.getInventory());
        if (viewer == null)
            return;

        try {
            ItemStack bp = e.getPlayer().getInventory().getChestplate();
            PotentialBackpackItem bpi = new PotentialBackpackItem(bp);

            if (bpi.isBackpack()) {
                int type = bpi.getType();
                BackpackObject backpack = BackpackObject.getByType(type);
                if (backpack == null) {
                    ACommand.respondf(e.getPlayer(),
                            "&cBackpack of type %s does not exist in this version. Why did you downgrade the plugin?",
                            type);
                } else {
                    // does this inv belong to the backpack
                    if (backpack.getTypeId() != BackpackObject.COMBINED.getTypeId()) {
                        if (!viewer.getOwnerBP().getItem().equals(bp) && viewer.getOwnerBP().getType() != BackpackObject.FURNACE.getTypeId()) {
                            return;
                        }
                    }

                    backpack.getHandler().onClose(e, bpi,
                            newItem -> e.getPlayer().getInventory().setChestplate(newItem));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
