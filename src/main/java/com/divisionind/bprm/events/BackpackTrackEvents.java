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

import org.bukkit.event.Listener;

public class BackpackTrackEvents implements Listener {

    //public static Map<ItemStack, ItemStackLocation> TRACKED_BACKPACKS = new ConcurrentHashMap<>();
    // dont need this, about as fast to just look in VirtualFurnaces table
    // TODO actually implement the tracking

    /*
        Events Needed:
        - Block Break
        - Player Join/Leave
        - Item Move
        - Item Drop
        - Item Pickup
     */
}
