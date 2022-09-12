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
import com.divisionind.bprm.location.SurfaceLocation;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LocationGround implements SurfaceLocation {

    private final World lastWorld;

    public LocationGround(World lastWorld) {
        this.lastWorld = lastWorld;
    }

    @Override
    public void replace(ItemStack newItem, ItemStack surfaceItem) throws UnknownItemLocationException {
        List<Entity> entityList = lastWorld.getEntities();
        entityList.removeIf(entity -> !entity.getType().equals(EntityType.DROPPED_ITEM));

        for (Entity ent : entityList) {
            Item item = (Item) ent;
            if (surfaceItem.equals(item.getItemStack())) {
                item.setItemStack(newItem);
                return;
            }
        }

        throw new UnknownItemLocationException();
    }

    @Override
    public String toString() {
        return "Ground (" + lastWorld.getName() + ")";
    }
}
