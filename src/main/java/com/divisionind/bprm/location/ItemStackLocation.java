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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackLocation {

    // first - last in tree
    // last - (e.g. chest) in tree

    //   || as the actual furnace backpack is not actually included in the updateTree
    // e.g. |Furnace Backpack| -> Combined Backpack -> Large Backpack -> Chest
    // ---------------------------------------------------------------------
    // so, to update the furnace item, we need to:
    // - [get the chest inventory]
    // - locate the backpack item,          [get its inventory]
    // - locate the combined backpack item, [get its inventory]
    // - locate the furnace backpack, then replace it
    // --------------------------------------------------------
    // - [replace combined backpack with new]
    // - [replace large backpack with new]
    // - done.

    // TODO track the backpacks location, we need to track the last [ItemLocation] in chain that is an instance of [BackpackLocation]

    private List<BackpackLocation> updateTree;
    private SurfaceLocation surfaceLocation;
    private ItemStack surfaceItem;
    private ItemStack currentFurnaceBackpack; // maybe make this an atomic reference (or volatile)

    public ItemStackLocation(ItemStack currentFurnaceBackpack) {
        this.updateTree = new ArrayList<>();
        this.currentFurnaceBackpack = currentFurnaceBackpack;
    }

    /**
     * Updates itemstack at its current tracked location
     * @param item item to use as updated value
     */
    public void update(ItemStack item) throws UnknownItemLocationException {
        // must have a known surface location to resolve
        if (surfaceLocation == null) throw new UnknownItemLocationException();

        // modify items in backpack recursively, returning ItemStack (which is the modified backpack) until you can call surfaceLocation.replace(item, surfaceItem);

    }

    public ItemStack getSurfaceItem() {
        return surfaceItem;
    }

    public ItemStack getCurrentFurnaceBackpack() {
        return currentFurnaceBackpack;
    }

    // we resolve this here so we dont have to do this look up every time we need the surface item (for tracking)
    private void updateSurfaceItem() {
        int treeSize = updateTree.size();
        if (treeSize == 0) {
            surfaceItem = currentFurnaceBackpack;
        } else {
            surfaceItem = updateTree.get(treeSize - 1).getCurrentBackpackItem();
        }
    }

    /**
     * Removes the last index of the update tree.
     */
    public ItemStackLocation popTree() {
        int treeSize = updateTree.size();
        if (treeSize != 0) {
            updateTree.remove(treeSize - 1);
            updateSurfaceItem();
        }
        return this;
    }

    /**
     * Adds an item location branch to end of the update tree.
     * @param backpackLocation location to add
     */
    public ItemStackLocation appendTree(BackpackLocation backpackLocation) {
        updateTree.add(backpackLocation);
        updateSurfaceItem();
        return this;
    }

    /**
     * This will change a lot. It is the actual surface location for the final item.
     * @param surfaceLocation container of item tree that represents this backpack
     */
    public void setSurfaceLocation(SurfaceLocation surfaceLocation) {
        this.surfaceLocation = surfaceLocation;
    }
}
