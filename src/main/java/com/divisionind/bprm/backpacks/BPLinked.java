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

package com.divisionind.bprm.backpacks;

import com.divisionind.bprm.ACommand;
import com.divisionind.bprm.BackpackHandler;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.UpdateItemCallback;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class BPLinked extends BackpackHandler {
    @Override
    public Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception {
        if (backpack.hasData()) {
            Location location = backpack.getDataAsLocation();
            Block block = location.getBlock();
            Material blockType = block.getType();

            Inventory inv;
            if (blockType.equals(Material.CHEST)) {
                inv = getChestInventory(block);
            } else {
                BlockState state = block.getState();

                if (state instanceof Container) {
                    inv = ((Container) state).getInventory();
                } else {
                    inv = null;
                }
            }

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
    public void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack, UpdateItemCallback callback)
            throws Exception { }

    public static Inventory getChestInventory(Block block) {
        BlockState blockState = block.getState();
        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;
            Inventory inv = chest.getInventory();

            if (inv instanceof DoubleChestInventory) {
                return inv.getHolder().getInventory();
            } else return inv;
        }

        return null;
    }
}