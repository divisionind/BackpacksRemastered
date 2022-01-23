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

package com.divisionind.bprm.events;

import com.divisionind.bprm.*;
import com.divisionind.bprm.nms.NMSItemStack;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BackpackOpenEvent implements Listener {

    private static ConcurrentSet<UUID> openingBackpacks = new ConcurrentSet<>();

    @EventHandler
    public void onBackpackOpen(PlayerInteractEvent e) {
        // did right click
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // is item in hand backpack key
            // TODO add support for use from off-hand as well
            ItemStack inhand = e.getPlayer().getInventory().getItemInMainHand();

            if (!inhand.getType().equals(Material.FEATHER))
                return;

            try {
                NMSItemStack backpackKey = new NMSItemStack(inhand);

                if (backpackKey.hasNBT("backpack_key")) {
                    ItemStack backpackItem = e.getPlayer().getInventory().getChestplate();
                    if (backpackItem == null)
                        return;

                    PotentialBackpackItem bpi = new PotentialBackpackItem(backpackItem);

                    // is wearing backpack
                    if (bpi.isBackpack()) {
                        int backpackType = bpi.getType();
                        BackpackObject backpack = BackpackObject.getByType(backpackType);
                        if (backpack == null) {
                            ACommand.respondf(e.getPlayer(), "&cBackpack of type %s does not exist in this version. " +
                                    "Why did you downgrade the plugin?", backpackType);
                        } else {
                            if (backpack.equals(BackpackObject.COMBINED)) {
                                // this bp broke and ive been meaning to re-write is code for a while cause it kinda sucks
                                // until I have time to do that, I am just going to disable it
                                ACommand.respondf(e.getPlayer(), "&eThe combined backpack is temporarily disabled... sorry. You can still remove your backpacks from it using the \"&7/bp split&e\" command.");
                                return;
                            }

                            // opening backpack, while it opens, disable this code from running
                            UUID playerId = e.getPlayer().getUniqueId();
                            if (openingBackpacks.contains(playerId))
                                return;

                            openingBackpacks.add(playerId);
                            // 8ticks = 0.4s should be enough time for the backpack to open (unless someone is lagging)
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Backpacks.getInstance(),
                                    () -> openingBackpacks.remove(playerId), Backpacks.OPEN_BACKPACK_COOLDOWN);

                            // actually open the backpack (along with adding fake viewer for identification)
                            BackpackHandler handler = backpack.getHandler();
                            Inventory inv = handler.openBackpack(e.getPlayer(), bpi);
                            if (inv == null)
                                return;

                            handler.finalizeBackpackOpen(e.getPlayer(), inv, bpi);
                        }
                    }
                }
            } catch (Exception ex) {
                // TODO respond that there was an error opening the backpack and to contact server admin
                ex.printStackTrace();
            }
        }
    }
}
