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
import com.divisionind.bprm.nms.NBTType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class BPCombined implements BackpackHandler {

    public static final String NAME = "Combined Backpack";
    private HashMap<UUID, Integer> openBackpacks = new HashMap<>();

    public static Inventory createInv() {
        return Bukkit.createInventory(null, 9, NAME);
    }

    @Override
    public Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception {
        Inventory display;

        if (backpack.hasData()) {
            display = BackpackSerialization.fromByteArrayInventory(backpack.getData());
        } else {
            display = createInv();
        }

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
                    e.getWhoClicked().closeInventory(); // ensure to force close the inventory right after this or else a duplication glitch would be possible

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
        Inventory combinedInv = BackpackSerialization.fromByteArrayInventory(backpack.getData());
        ItemStack bpItemInUse = combinedInv.getItem(slot);
        PotentialBackpackItem inUse = new PotentialBackpackItem(bpItemInUse);

        // attempt to resolve backpack type to get handler for close, if backpack not found, return
        BackpackObject bpo = inUse.getTypeObject();
        if (bpo == null) return;

        // run that backpacks on close handler and update combinedInv with new item
        bpo.getHandler().onClose(e, inUse, newItem -> combinedInv.setItem(slot, newItem));

        // update the combined backpacks data
        backpack.setNBT(NBTType.BYTE_ARRAY, PotentialBackpackItem.FIELD_NAME_DATA, BackpackSerialization.toByteArrayInventory(combinedInv, NAME));

        callback.update(backpack.getModifiedItem());
    }

    @Override
    public LoreBuilder lore() {
        return new LoreBuilder("A bag capable of storing multiple backpacks")
                .append("for easy use.");
    }
}
