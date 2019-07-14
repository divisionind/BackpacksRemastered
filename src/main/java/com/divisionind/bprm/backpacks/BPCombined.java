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

import com.divisionind.bprm.*;
import com.divisionind.bprm.nms.NBTMap;
import com.divisionind.bprm.nms.NBTType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class BPCombined implements BackpackHandler {

    private HashMap<UUID, Integer> openBackpacks = new HashMap<>();

    @Override
    public Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception {
        Inventory display = Bukkit.createInventory(null, 9, "Combined Backpack");

        // ensure storedBackpacks var is initialized
        NBTMap storedBackpacks;
        if (backpack.hasNBT("storedBackpacks")) {
            storedBackpacks = backpack.getAsMap("storedBackpacks");
        } else backpack.setAsMap("storedBackpacks", storedBackpacks = new NBTMap());

        storedBackpacks.getKeys().forEach(key -> {
            int slot = Integer.parseInt(key);
            try {
                ItemStack bpInternal = BackpackSerialization.fromByteArrayItemStack((byte[])storedBackpacks.getNBT(NBTType.BYTE_ARRAY, key));
                display.setItem(slot, bpInternal);
            } catch (InvocationTargetException | IllegalAccessException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        return display;
    }

    public void onClick(InventoryClickEvent e) {
        // prevents this event for running for anyone who has a subbackpack open
        if (openBackpacks.containsKey(e.getWhoClicked().getUniqueId())) return;

        e.setCancelled(true);
        ItemStack cur = e.getCurrentItem();
        if (cur == null || e.getSlot() > 8) return;
        try {
            PotentialBackpackItem backpack = new PotentialBackpackItem(cur);
            if (backpack.isBackpack()) {
                int type = backpack.getType();
                BackpackObject bpo = BackpackObject.getByType(type);
                if (bpo == null) {
                    ACommand.respondf(e.getWhoClicked(), "&cBackpack of type %s does not exist in this version. Why did you downgrade the plugin?", type);
                } else {
                    // remove backpack identifier viewer so the onClose event is not triggered by this open event
                    e.getClickedInventory().getViewers().remove(FakeBackpackViewer.INSTANCE);

                    // open clicked backpack
                    Inventory inv = bpo.getHandler().openBackpack((Player) e.getWhoClicked(), backpack);
                    if (inv == null) return;
                    inv.getViewers().add(FakeBackpackViewer.INSTANCE);
                    openBackpacks.put(e.getWhoClicked().getUniqueId(), e.getSlot());
                    e.getWhoClicked().openInventory(inv);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack, UpdateItemCallback callback) throws Exception {
        // return if no backpack was opened
        if (!openBackpacks.containsKey(e.getPlayer().getUniqueId())) return;

        // get slot from map and backpack item of that slot in the combined backpack
        int slot = openBackpacks.remove(e.getPlayer().getUniqueId());
        NBTMap storedBackpacks = backpack.getAsMap("storedBackpacks");
        ItemStack item = BackpackSerialization.fromByteArrayItemStack((byte[])storedBackpacks.getNBT(NBTType.BYTE_ARRAY, Integer.toString(slot)));

        // parse subbackpack item as backpack
        PotentialBackpackItem subback = new PotentialBackpackItem(item);
        BackpackObject bpo = subback.getTypeObject();
        if (bpo == null) return;

        // create a new update callback to update the item within the backpack on close
        bpo.getHandler().onClose(e, subback, newItem -> {
            try {
                storedBackpacks.setNBT(NBTType.BYTE_ARRAY, Integer.toString(slot), BackpackSerialization.toByteArrayItemStack(newItem));
            } catch (InvocationTargetException | IllegalAccessException | IOException ex) {
                ex.printStackTrace();
            }
        });

        // update the main backpack item as usual
        callback.update(backpack.getModifiedItem());
    }

    @Override
    public LoreBuilder lore() {
        return new LoreBuilder("A bag capable of storing multiple backpacks")
                .append("for easy use.");
    }
}
