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

package com.divisionind.bprm.location;

import com.divisionind.bprm.exceptions.UnknownItemLocationException;
import org.bukkit.inventory.ItemStack;

public class ItemStackPointer {

    // TODO support furnace backpacks in other backpacks

    private SurfaceLocation surfaceLocation;
    private final ItemStack surfaceItem;

    public ItemStackPointer(ItemStack surfaceItem, SurfaceLocation surfaceLocation) {
        this.surfaceItem = surfaceItem;
        this.surfaceLocation = surfaceLocation;
    }

    /**
     * Updates itemstack at its current tracked location
     * @param item item to use as updated value
     */
    public void update(ItemStack item) throws UnknownItemLocationException {
        // must have a known surface location to resolve
        if (surfaceLocation == null) throw new UnknownItemLocationException();

        // modify items in backpack recursively, returning ItemStack (which is the modified backpack) until you can call surfaceLocation.replace(item, surfaceItem);
        // currently I am not supporting backpack nesting
        surfaceLocation.replace(item, surfaceItem);
    }

    public ItemStack getSurfaceItem() {
        return surfaceItem;
    }

    /**
     * This will change a lot. It is the actual surface location for the final item.
     * @param surfaceLocation container of item tree that represents this backpack
     */
    public void setSurfaceLocation(SurfaceLocation surfaceLocation) {
        this.surfaceLocation = surfaceLocation;
    }

    public SurfaceLocation getSurfaceLocation() {
        return surfaceLocation;
    }
}
