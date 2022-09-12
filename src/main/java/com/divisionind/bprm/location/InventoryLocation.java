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

package com.divisionind.bprm.location;

import com.divisionind.bprm.exceptions.UnknownItemLocationException;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryLocation implements SurfaceLocation {

    public abstract Inventory resolveInventory() throws UnknownItemLocationException;

    private int lastKnownSlot;

    public InventoryLocation(int lastKnownSlot) {
        this.lastKnownSlot = lastKnownSlot;
    }

    @Override
    public void replace(ItemStack newItem, ItemStack surfaceItem) throws UnknownItemLocationException {
        Inventory inv = resolveInventory();
        ItemStack itemAtLastLoc = inv.getItem(lastKnownSlot);

        if (surfaceItem.equals(itemAtLastLoc)) {
            inv.setItem(lastKnownSlot, newItem);
        } else {
            ItemStack[] contents = inv.getContents();

            for (int i = 0; i < contents.length; i++) {
                if (surfaceItem.equals(contents[i])) {
                    inv.setItem(lastKnownSlot = i, newItem);
                    return;
                }
            }

            throw new UnknownItemLocationException();
        }
    }
}
