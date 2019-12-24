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

import com.divisionind.bprm.location.InventoryLocation;
import com.divisionind.bprm.location.ItemPointerType;
import org.bukkit.block.Block;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;

public class InventoryLocationBlock extends InventoryLocation {

    private BlockInventoryHolder storageBlockInvHolder;

    public InventoryLocationBlock(int lastKnownSlot, BlockInventoryHolder storageBlockInvHolder) {
        super(ItemPointerType.BLOCK, lastKnownSlot);
        this.storageBlockInvHolder = storageBlockInvHolder;
    }

    @Override
    public Inventory resolveInventory() {
        // this should support any future inventory containing blocks
        // note: even if the block is changed to something else, the inventory stored in this state persists
        return storageBlockInvHolder.getInventory();
    }

    @Override
    public String toString() {
        Block storageBlock = storageBlockInvHolder.getBlock();
        return "Block ([" + storageBlock.getType().name() + "/" + storageBlock.getWorld().getName() + "]" + storageBlock.getX() + ", " + storageBlock.getY() + ", " + storageBlock.getZ() + ")";
    }
}
