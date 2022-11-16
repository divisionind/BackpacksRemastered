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

import com.divisionind.bprm.BackpackHandler;
import com.divisionind.bprm.FakeBackpackViewer;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.nms.NMSItemStack;
import com.divisionind.bprm.nms.reflect.NMS;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BackpackOpenCloseEvent implements Listener  {

    public static final Set<UUID> transactions = new HashSet<>();

    @EventHandler
    public void onBackpackOpen(PlayerInteractEvent e) throws Exception {
        // did right click
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // is item in hand backpack key
            // TODO add support for use from off-hand as well
            ItemStack inhand = e.getPlayer().getInventory().getItemInMainHand();
            if (!inhand.getType().equals(Material.FEATHER))
                return;

            NMSItemStack backpackKey = new NMSItemStack(inhand);
            if (backpackKey.hasNBT("backpack_key")) {
                ItemStack backpackItem = e.getPlayer().getInventory().getChestplate();
                if (backpackItem == null)
                    return;

                PotentialBackpackItem bpi = new PotentialBackpackItem(backpackItem);

                if (bpi.isBackpack()) {
                    // actually open the backpack (along with adding fake viewer for identification)
                    BackpackHandler handler = bpi.getHandler();
                    Inventory inv = handler.openBackpack(e.getPlayer(), bpi);

                    UUID playerId = e.getPlayer().getUniqueId();
                    if (inv == null || transactions.contains(playerId))
                        return;

                    transactions.add(playerId);
                    handler.finalizeBackpackOpen(e.getPlayer(), inv, bpi);
                }
            }
        }
    }

    @EventHandler
    public void onBackpackClose(InventoryCloseEvent e) throws Exception {
        // is it a backpack?
        FakeBackpackViewer viewer = NMS.getBackpackViewer(e.getInventory());
        if (viewer == null)
            return;

        ItemStack bp = e.getPlayer().getInventory().getChestplate();
        PotentialBackpackItem bpi = new PotentialBackpackItem(bp);

        if (bpi.isBackpack()) {
            bpi.getHandler().onClose(e, bpi,
                    newItem -> e.getPlayer().getInventory().setChestplate(newItem));
        }
        transactions.remove(e.getPlayer().getUniqueId());
    }
}
