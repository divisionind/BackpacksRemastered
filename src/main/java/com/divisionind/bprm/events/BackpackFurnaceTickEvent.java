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

import com.divisionind.bprm.VirtualFurnace;
import com.divisionind.bprm.events.custom.GameTickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackpackFurnaceTickEvent implements Listener {

    // does not necessarily have to be a ConcurrentHashMap because this is run in the game loop
    // with inventory actions (only this way because of inefficient removal of entries)
    public static Map<UUID, VirtualFurnace> VIRTUAL_FURNACES = new ConcurrentHashMap<>();

    @EventHandler
    public void onGameTick(GameTickEvent event) {
        VIRTUAL_FURNACES.forEach((key, value) -> {
            if (!value.isBurning() && value.isReleased()) {
                // TODO need to at least try to update the furnace backpack's NBT data before exiting here
                // also need to attempt to update NBT data on reload/shutdown
                VIRTUAL_FURNACES.remove(key);
            } else {
                try {
                    // will throw inconsequential exception when trying to set blockstate to lit or unlit
                    // catch and ignore
                    value.tick();
                } catch (Exception e) { }
            }
        });
    }
}
