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

package com.divisionind.bprm;

import com.divisionind.bprm.exceptions.UnknownItemLocationException;
import com.divisionind.bprm.nitemlocs.BackpackLocation;
import com.divisionind.bprm.nitemlocs.ItemLocation;
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

    private List<ItemLocation> updateTree;
    private ItemStack surfaceItem;
    private ItemStack oldFurnaceBackpack; // maybe make this an atomic reference (or volatile)

    public ItemStackLocation() {
        this.updateTree = new ArrayList<>();
    }

    /**
     * Updates itemstack at its current tracked location
     * @param item item to use as updated value
     */
    public void update(ItemStack item) throws UnknownItemLocationException {
        int lastIndex = updateTree.size() - 1;
        // backpack locations do not contain enough information to fully update the rest of the tree
        if (updateTree.get(lastIndex) instanceof BackpackLocation) throw new UnknownItemLocationException();

        for (int i = lastIndex; i > -1 ;i--) {
            ItemLocation loc = updateTree.get(i);

        }
    }

    public ItemStack getSurfaceItem() {
        return surfaceItem;
    }

    public ItemStack getOldFurnaceBackpack() {
        return oldFurnaceBackpack;
    }

    private void updateSurfaceItem() {
        for (int i = 0; i < updateTree.size(); i++) {
            if (!(updateTree.get(i) instanceof BackpackLocation)) {
                int correctIndex = i - 1;

                if (correctIndex == -1) {
                    // if there are no BackpackLocations in tree, surfaceItem == the item we are tracking
                    surfaceItem = oldFurnaceBackpack;
                } else {
                    // else, surfaceItem will equal the last instance of a itemstack/backpack container location
                    surfaceItem = ((BackpackLocation)updateTree.get(correctIndex)).getCurrentBackpackItem();
                }
                break;
            }
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
     * @param itemLocation location to add
     */
    public ItemStackLocation appendTree(ItemLocation itemLocation) {
        updateTree.add(itemLocation);
        updateSurfaceItem();
        return this;
    }
}
