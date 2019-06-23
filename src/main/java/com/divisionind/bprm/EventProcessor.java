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

import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class EventProcessor implements Listener {

    private static ConcurrentSet<UUID> openingBackpacks = new ConcurrentSet<>();

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
            BackpackItem backpack = BackpackItem.getByType(backpack_type);
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
    public void onDamageEvent(PlayerItemDamageEvent e) {
        // if has backpack_id, cancel
        if (!e.getItem().getType().equals(Material.LEATHER_CHESTPLATE)) return; // optimization, does not need to run all that reflection if its not even the right material
        try {
            Object craftItemStack = NMSReflector.asNMSCopy(e.getItem());
            Object tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);
            if (NMSReflector.hasNBTKey(tagCompound, "backpack_type")) e.setCancelled(true);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) { // TODO find better way to identify backpack, add a click cooldown to onRightClick() to prevent multiple runs while backpack is opening
        ItemStack bp = e.getPlayer().getInventory().getChestplate();
        if (!e.getView().getTitle().toLowerCase().contains("backpack")) return;
        try {
            Object craftItemStack = NMSReflector.asNMSCopy(bp);
            Object tagCompound = NMSReflector.getNBTTagCompound(craftItemStack);
            if (NMSReflector.hasNBTKey(tagCompound, "backpack_type")) {
                int type = (int)NMSReflector.getNBT(tagCompound, NBTType.INT, "backpack_type");
                BackpackItem backpack = BackpackItem.getByType(type);
                if (backpack == null) {
                    ACommand.respondf(e.getPlayer(), "Backpack of type %s does not exist in this version. Why did you downgrade the plugin?", type);
                } else backpack.getHandler().onClose(e, craftItemStack, tagCompound);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
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

                    Object bpItemStack = NMSReflector.asNMSCopy(backpackItem);
                    Object bpTagCompound = NMSReflector.getNBTTagCompound(bpItemStack);
                    if (NMSReflector.hasNBTKey(bpTagCompound, "backpack_type")) {
                        int type = (int)NMSReflector.getNBT(bpTagCompound, NBTType.INT, "backpack_type");
                        BackpackItem backpack = BackpackItem.getByType(type);
                        if (backpack == null) {
                            ACommand.respondf(e.getPlayer(), "Backpack of type %s does not exist in this version. Why did you downgrade the plugin?", type);
                        } else {
                            // opening backpack, while it opens, disable this code from running
                            UUID playerId = e.getPlayer().getUniqueId();
                            if (openingBackpacks.contains(playerId)) return; // TODO add an option for disabling nesting of backpacks
                            openingBackpacks.add(playerId);
                            // 8ticks = 0.4s should be enough time for the backpack to open (unless someone is lagging)
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Backpacks.getInstance(), () -> openingBackpacks.remove(playerId), Backpacks.getConf().getLong("openBackpackCooldown"));

                            // actually open the backpack
                            backpack.getHandler().openBackpack(e, bpItemStack, bpTagCompound, NMSReflector.hasNBTKey(bpTagCompound, "backpack_data"));
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
