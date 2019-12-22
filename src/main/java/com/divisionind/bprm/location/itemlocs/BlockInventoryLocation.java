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

import com.divisionind.bprm.backpacks.BPLinked;
import com.divisionind.bprm.exceptions.UnknownItemLocationException;
import com.divisionind.bprm.location.InventoryLocation;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

public class BlockInventoryLocation extends InventoryLocation {

    private Block storageBlock;

    public BlockInventoryLocation(int lastKnownSlot, Block storageBlock) {
        super(lastKnownSlot);
        this.storageBlock = storageBlock;
    }

    @Override
    public Inventory resolveInventory() throws UnknownItemLocationException {
        // TODO attempt to support other block types here as well
        Inventory chestInv = BPLinked.getChestInventory(storageBlock);
        if (chestInv == null) throw new UnknownItemLocationException();
        return chestInv;
    }

    @Override
    public String toString() {
        return "Block (" + storageBlock.getType().name() + ")";
    }
}
