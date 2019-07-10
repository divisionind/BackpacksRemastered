/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 Division Industries LLC
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
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.lang.reflect.InvocationTargetException;

public class BackpackDamageEvent implements Listener {
    @EventHandler
    public void onDamageEvent(PlayerItemDamageEvent e) {
        // dont want to stop damage event on elytra | this would be very op (and they never fully break anyway)
        if (e.getItem().getType().equals(Material.ELYTRA)) return;
        try {
            PotentialBackpackItem pbi = new PotentialBackpackItem(e.getItem());
            if (pbi.isBackpack()) e.setCancelled(true);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }
}
