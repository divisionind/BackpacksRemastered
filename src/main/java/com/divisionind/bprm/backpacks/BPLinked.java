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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class BPLinked extends BackpackHandler {
    @Override
    public Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception {
        if (backpack.hasData()) {
            Location chestLocation = BackpackSerialization.fromByteArrayLocation(backpack.getData());
            Block chestBlock = chestLocation.getBlock();

            Inventory inv;
            if (chestBlock.getType().equals(Material.FURNACE)) {
                Furnace furnace = (Furnace)chestBlock.getState();
                inv = furnace.getInventory();
            } else inv = getChestInventory(chestBlock);

            if (inv == null) {
                ACommand.respond(p, "&cThe container to which this bag was linked no longer exists.");
            }

            return inv;
        } else {
            ACommand.respond(p, "&cThis backpack must form a connection before it can be used.");
            return null;
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack, UpdateItemCallback callback) throws Exception { }

    private Inventory getChestInventory(Block block) {
        BlockState blockState = block.getState();
        if (blockState instanceof Chest) {
            Chest chest = (Chest)blockState;
            Inventory inv = chest.getInventory();

            if (inv instanceof DoubleChestInventory) {
                return inv.getHolder().getInventory();
            } else return inv;
        }

        return null;
    }
}