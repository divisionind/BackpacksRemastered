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

public abstract class ItemLocationCallback {

    private ItemLocationType type;

    public ItemLocationCallback(ItemLocationType type) {
        this.type = type;
    }

    public ItemLocationType getType() {
        return type;
    }

    /**
     * updates an item at the resolved location
     * @param newItem new value of the item
     * @param oldItem last known value of the item
     * @return the item the backpack was stored in (if it was stored in another item), else null
     * @throws UnknownItemLocationException if the item's location can not be resolved
     */
    public abstract ItemStack update(ItemStack newItem, ItemStack oldItem) throws UnknownItemLocationException; // TODO return inventory here
}
