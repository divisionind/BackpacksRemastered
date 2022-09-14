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

package com.divisionind.bprm.events;

import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.UpdateItemCallback;
import com.divisionind.bprm.events.custom.GameTickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Prevents {@link com.divisionind.bprm.BackpackHandler#openBackpack(Player, PotentialBackpackItem)} and
 * {@link com.divisionind.bprm.BackpackHandler#onClose(InventoryCloseEvent, PotentialBackpackItem, UpdateItemCallback)}
 * from occurring in the same tick. (fix for: https://github.com/divisionind/BackpacksRemastered/issues/37)
 */
public class CloseOpenEnforcerEvent implements Listener {

    private final Set<UUID> closedInTick = new HashSet<>();

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        closedInTick.add(e.getPlayer().getUniqueId());
    }

    // last thing in the inv. open call-stack
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvOpen(InventoryOpenEvent e) {
        if (closedBackpackThisTick(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onGameTick(GameTickEvent e) {
        closedInTick.clear();
    }

    public boolean closedBackpackThisTick(UUID playerId) {
        return closedInTick.contains(playerId);
    }
}
