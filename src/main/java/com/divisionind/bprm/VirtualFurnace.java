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

import com.divisionind.bprm.location.ItemStackPointer;
import com.divisionind.bprm.nms.reflect.NMSMethod;

import java.lang.reflect.InvocationTargetException;

/**
 * Part of the magic that makes the furnace backpack work
 */
public class VirtualFurnace {

    private Object furnace;

    /* if not opened, the backpack is released -> then when it stops burning, it can be removed from the TickEvent */
    private boolean released;

    private ItemStackPointer itemLocation;

    public VirtualFurnace(Object furnace) {
        this.furnace = furnace;
        this.released = false;
    }

    public ItemStackPointer getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(ItemStackPointer itemLocation) {
        this.itemLocation = itemLocation;
    }

    public Object getFurnace() {
        return furnace;
    }

    public boolean isBurning() {
        // furnace.burnTime > 0;
        try {
            return (boolean) NMSMethod.isBurning.getMethod().invoke(furnace);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void tick() throws InvocationTargetException, IllegalAccessException {
        //furnace.tick();
        NMSMethod.tick.getMethod().invoke(furnace);
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }
}