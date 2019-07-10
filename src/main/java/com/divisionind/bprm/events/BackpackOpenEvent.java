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
            ItemStack inhand = e.getPlayer().getInventory().getItemInMainHand(); // TODO add support for use from off-hand as well
            if (!inhand.getType().equals(Material.FEATHER)) return;
            try {
                Object handItemStack = NMSReflector.asNMSCopy(inhand);
                Object handTagCompound = NMSReflector.getNBTTagCompound(handItemStack);
                if (NMSReflector.hasNBTKey(handTagCompound, "backpack_key")) {
                    ItemStack backpackItem = e.getPlayer().getInventory().getChestplate();
                    if (backpackItem == null) return;
                    PotentialBackpackItem bpi = new PotentialBackpackItem(backpackItem);

                    // is wearing backpack
                    if (bpi.isBackpack()) {
                        int backpackType = bpi.getType();
                        BackpackObject backpack = BackpackObject.getByType(backpackType);
                        if (backpack == null) {
                            ACommand.respondf(e.getPlayer(), "&cBackpack of type %s does not exist in this version. Why did you downgrade the plugin?", backpackType);
                        } else {
                            // opening backpack, while it opens, disable this code from running
                            UUID playerId = e.getPlayer().getUniqueId();
                            if (openingBackpacks.contains(playerId)) return; // TODO add an option for disabling nesting of backpacks
                            openingBackpacks.add(playerId);
                            // 8ticks = 0.4s should be enough time for the backpack to open (unless someone is lagging)
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Backpacks.getInstance(), () -> openingBackpacks.remove(playerId), Backpacks.openBackpackCooldown);

                            // actually open the backpack (along with adding fake viewer for identification)
                            Inventory inv = backpack.getHandler().openBackpack(e.getPlayer(), bpi);
                            if (inv == null) return;
                            inv.getViewers().add(FakeBackpackViewer.INSTANCE);
                            e.getPlayer().openInventory(inv);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
