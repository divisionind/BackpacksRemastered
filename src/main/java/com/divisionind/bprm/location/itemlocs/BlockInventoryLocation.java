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

package com.divisionind.bprm.location.itemlocs;

import com.divisionind.bprm.exceptions.UnknownItemLocationException;
import com.divisionind.bprm.location.InventoryLocation;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class BlockInventoryLocation extends InventoryLocation {

    private Location blockLocation;

    public BlockInventoryLocation(int lastKnownSlot, Location blockLocation) {
        super(lastKnownSlot);
        this.blockLocation = blockLocation;
    }

    @Override
    public Inventory resolveInventory() throws UnknownItemLocationException {
        return null;
    }
}
