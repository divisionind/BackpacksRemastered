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

import com.divisionind.bprm.BackpackObject;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.VirtualFurnace;
import com.divisionind.bprm.location.ItemStackPointer;
import com.divisionind.bprm.location.SurfaceLocation;
import com.divisionind.bprm.location.itemlocs.GroundLocation;
import com.divisionind.bprm.location.itemlocs.InventoryLocationBlock;
import com.divisionind.bprm.location.itemlocs.InventoryLocationEnderChest;
import com.divisionind.bprm.location.itemlocs.InventoryLocationPlayer;
import com.divisionind.bprm.nms.reflect.NBTType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class BackpackTrackEvents implements Listener {

    /*
        Events Needed:
        - Item Move

        We can ignore player join/leave because:
            - leave: the uuid will become invalid and the location pointer wont resolve
            - join: nothing really to do if you think about it (especially if I add offline player inventory item locations in the future)
     */

    @EventHandler
    public void onItemMove(InventoryClickEvent e) {
        if (!e.isCancelled() && e.getClickedInventory() != null) {
            switch (e.getAction()) {
                case PLACE_ALL: case SWAP_WITH_CURSOR: // pick and place
                    itemMoveHandler(e.getClickedInventory(), e.getSlot(), e.getCursor(), e.getWhoClicked());
                    break;
                case MOVE_TO_OTHER_INVENTORY: // shift to move
                    // corrects the inventory that the item has been placed in
                    Inventory inv;
                    if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                        inv = e.getView().getTopInventory();
                    } else {
                        inv = e.getView().getBottomInventory();
                    }

                    // slot will equal first free slot in inv, not the result of e.getSlot(). it is more efficient to not include slot
                    itemMoveHandler(inv, 0, e.getCurrentItem(), e.getWhoClicked());
                    break;
                default:
                    break;
            }
        }
    }

    private void itemMoveHandler(Inventory inv, int slot, ItemStack item, HumanEntity entity) {
        UUID playerId = entity.getUniqueId();
        switch (inv.getType()) {
            case PLAYER:
                checkAndUpdate(item, previous -> new InventoryLocationPlayer(slot, playerId));
                break;
            case ENDER_CHEST:
                checkAndUpdate(item, previous -> new InventoryLocationEnderChest(slot, playerId));
                break;
            case CHEST: case DISPENSER: case DROPPER: case HOPPER: case SHULKER_BOX: case BARREL:
                InventoryHolder holder = inv.getHolder();
                if (holder instanceof BlockInventoryHolder) {
                    Block block = ((BlockInventoryHolder) holder).getBlock();
                    checkAndUpdate(item, previous -> new InventoryLocationBlock(slot, block));
                }
                break;
            default:
                break;
        }
    }

//    @EventHandler
//    public void onItemHopperMove(InventoryMoveItemEvent e) {
//        // TODO support the tracking of items moving through hoppers
//    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!e.isCancelled() && e.getEntityType().equals(EntityType.PLAYER)) {
            // if the item slot gets added to this event, a great optimization could be made
            final UUID playerId = e.getEntity().getUniqueId();
            checkAndUpdate(e.getItem().getItemStack(), previous -> new InventoryLocationPlayer(0, playerId));
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        // should encompass chest breaking and player item dropping
        if (!e.isCancelled()) {
            final World lastWorld = e.getEntity().getWorld();
            checkAndUpdate(e.getEntity().getItemStack(), previous -> new GroundLocation(lastWorld));
        }
    }


    private VirtualFurnace getActiveVFurnace(ItemStack item) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        // quickly filter out majority of items here
        if (!item.getType().equals(Material.LEATHER_CHESTPLATE)) return null;

        // is it a furnace backpack?
        PotentialBackpackItem potentialBackpack = new PotentialBackpackItem(item);
        if (!potentialBackpack.isBackpack()) return null;
        if (potentialBackpack.getType() != BackpackObject.FURNACE.getTypeId()) return null;

        // it is a furnace backpack, so
        if (potentialBackpack.hasNBT("furnace_id")) {
            UUID furnaceId = UUID.fromString((String) potentialBackpack.getNBT(NBTType.STRING, "furnace_id"));
            return BackpackFurnaceTickEvent.VIRTUAL_FURNACES.get(furnaceId);
        }

        return null;
    }

    private void checkAndUpdate(ItemStack item, UpdateAction action) {
        try {
            VirtualFurnace vfurnace = getActiveVFurnace(item);
            if (vfurnace != null) {
                ItemStackPointer itemLocation = vfurnace.getItemLocation();
                if (itemLocation != null) {
                    itemLocation.setSurfaceLocation(action.acquire(itemLocation.getSurfaceLocation()));
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) { }
    }

    private interface UpdateAction {
        SurfaceLocation acquire(SurfaceLocation previous);
    }
}
