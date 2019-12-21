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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackLocation {

    // TODO use a funneling system so
    // if in Combined Backpack (process)
    // if in Combined Backpack in Chest (process)
    // a list of actions would work create

    // maybe pass updater data as well (data specific to each one

    private List<ItemLocationCallback> updateTree;

    public ItemStackLocation() {
        this.updateTree = new ArrayList<>();
    }

    /**
     * Updates itemstack at its current tracked location
     * @param item item to use as updated value
     */
    public void update(ItemStack item) throws UnknownItemLocationException {
        // start from bottom and go to top of list
        for (int i = updateTree.size(); i > -1 ; i--) {

        }
    }
}
