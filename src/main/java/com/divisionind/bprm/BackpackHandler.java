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

package com.divisionind.bprm;

import com.divisionind.bprm.nms.reflect.NMS;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class BackpackHandler {
    /**
     * Opens the supplied item as a backpack for the specified player.
     *
     * @param p player to open the backpack for
     * @param backpack item which has been confirmed to be a backpack
     * @return resulting inventory instance that was opened
     * @throws Exception if there was an error opening the backpack
     */
    public abstract Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception;

    /**
     * Runs when the backpack has been closed. Note: This depends on the
     * inventory containing the {@link FakeBackpackViewer} entity and therefore
     * it should always be added to backpacks when they are opened
     *
     * @param e bukkit inventory close event instance
     * @param backpack backpack item in question
     * @param callback a callback for replacing the item should any info about it be updated whilst it was open
     * @throws Exception if there was an error closing the backpack
     */
    public abstract void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack, UpdateItemCallback callback) throws Exception;

    /**
     * Completes the final steps of opening the backpack. These are things
     * that must be done but are often similar. Unfortunately, bukkits poor
     * and inconsistent inventory opening system is why this exists.
     *
     * @param p player to open the backpack for
     * @param inv inventory created from openBackpack
     * @throws Exception if there was a problem finalizing the opening of the backpack
     */
    public void finalizeBackpackOpen(HumanEntity p, Inventory inv) throws Exception {
        inv.getViewers().add(NMS.FAKE_VIEWER);
        p.openInventory(inv);
    }
}
