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

package com.divisionind.bprm.location.itemlocs;

import com.divisionind.bprm.AlwaysPlayer;
import com.divisionind.bprm.exceptions.UnknownItemLocationException;
import com.divisionind.bprm.location.InventoryLocation;
import com.divisionind.bprm.location.ItemPointerType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryLocationPlayer extends InventoryLocation {

    protected AlwaysPlayer alwaysPlayer;

    public InventoryLocationPlayer(int lastKnownSlot, UUID playerId) {
        super(ItemPointerType.PLAYER, lastKnownSlot);
        this.alwaysPlayer = new AlwaysPlayer(playerId);
    }

    protected InventoryLocationPlayer(int lastKnownSlot, UUID playerId, ItemPointerType itemPointerType) {
        super(itemPointerType, lastKnownSlot);
        this.alwaysPlayer = new AlwaysPlayer(playerId);
    }

    @Override
    public Inventory resolveInventory() throws UnknownItemLocationException {
        Player player = alwaysPlayer.resolvePlayer();
        if (player == null) throw new UnknownItemLocationException();
        return player.getInventory();
    }

    @Override
    public void replace(ItemStack newItem, ItemStack surfaceItem) throws UnknownItemLocationException {
        super.replace(newItem, surfaceItem);
        alwaysPlayer.safeSave(); // must save the player data if it is an instance we created ourselves
    }

    @Override
    public String toString() {
        return "Player (" + alwaysPlayer.getName() + ")";
    }
}
