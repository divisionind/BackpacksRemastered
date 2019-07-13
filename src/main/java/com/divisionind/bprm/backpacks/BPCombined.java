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
import com.divisionind.bprm.LoreBuilder;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.nms.NBTMap;
import com.divisionind.bprm.nms.NBTType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;

public class BPCombined implements BackpackHandler {
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
                NBTMap innerBackpack = storedBackpacks.getAsMap(key);
                int type = (int)innerBackpack.getNBT(NBTType.INT, PotentialBackpackItem.FIELD_NAME_TYPE);
                // TODO
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return display;
    }

    public void onClick(InventoryClickEvent e) {
        // TODO
    }

    @Override
    public void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack) throws Exception { }

    @Override
    public LoreBuilder lore() {
        return new LoreBuilder("A bag capable of storing multiple backpacks")
                .append("for easy use.");
    }
}
