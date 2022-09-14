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

package com.divisionind.bprm.backpacks;

import com.divisionind.bprm.BackpackHandler;
import com.divisionind.bprm.PotentialBackpackItem;
import com.divisionind.bprm.UpdateItemCallback;
import com.divisionind.bprm.VirtualFurnace;
import com.divisionind.bprm.events.BackpackFurnaceTickEvent;
import com.divisionind.bprm.location.ItemStackPointer;
import com.divisionind.bprm.location.itemlocs.InventoryLocationPlayer;
import com.divisionind.bprm.nms.KnownVersion;
import com.divisionind.bprm.nms.NBTMap;
import com.divisionind.bprm.nms.reflect.NBTType;
import com.divisionind.bprm.nms.reflect.NMS;
import com.divisionind.bprm.nms.reflect.NMSClass;
import com.divisionind.bprm.nms.reflect.NMSMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

public class BPFurnace extends BackpackHandler {

    // TODO if the player is wearing the backpack (and furnacing), make it emit light around them (later update)
    //   (also, yes, this is possible, see mc protocol wiki)

    @Override
    public Inventory openBackpack(Player p, PotentialBackpackItem backpack) throws Exception {
        Object furnace = null;
        if (backpack.hasNBT("furnace_id")) {
            UUID id = UUID.fromString((String) backpack.getNBT(NBTType.STRING, "furnace_id"));
            VirtualFurnace vFern = BackpackFurnaceTickEvent.VIRTUAL_FURNACES.get(id);

            if (vFern != null) {
                // virtual furnace identified
                furnace = vFern.getFurnace();
                vFern.setReleased(false);
            }
        }

        // contains the magic that makes our virtual furnace
        if (furnace == null) {
            // create fake furnace tile entity
            if (KnownVersion.v1_14_R1.isBefore()) {
                // furnace = new TileEntityFurnace(); // only works pre-1.14
                furnace = NMSClass.TileEntityFurnace.getClazz().getDeclaredConstructor().newInstance();
            } else
            if (KnownVersion.v1_17_R1.isBefore()) {
                // furnace = new TileEntityFurnaceFurnace();
                furnace = NMSClass.TileEntityFurnaceFurnace.getClazz().getDeclaredConstructor().newInstance();
            } else {
                // this would probably work back to v1_14_R1 but ehh I cant be bothered testing
                furnace = NMSClass.CraftTileInventoryConverter_Furnace.getClazz().getDeclaredConstructor().newInstance();
                furnace = NMSMethod.getTileEntity.getMethod().invoke(furnace);
            }
            // assign it a dimension
            Object craftServer = NMSClass.CraftServer.getClazz().cast(Bukkit.getServer());
            Object dedicatedServer = NMSMethod.getServer.getMethod().invoke(craftServer);
            Object worldServer = NMS.getWorldServer(dedicatedServer);
            NMS.TileEntity_world.set(furnace, worldServer);

            if (backpack.hasNBT("furnace_data")) {
                //furnace.load((NBTTagCompound) backpack.getAsMap("furnace_data").getTagCompound());
                Object furnaceDataTC = backpack.getAsMap("furnace_data").getTagCompound();
                if (KnownVersion.v1_16_R1.isBefore()) {
                    NMSMethod.load.getMethod().invoke(furnace, furnaceDataTC);
                } else
                if (KnownVersion.v1_17_R1.isBefore()) {
                    NMSMethod.load.getMethod().invoke(furnace, null, furnaceDataTC);
                } else {
                    NMSMethod.load.getMethod().invoke(furnace, furnaceDataTC); // back to old method, make up your mind
                }
            }

            UUID furnaceId = UUID.randomUUID();
            BackpackFurnaceTickEvent.VIRTUAL_FURNACES.put(furnaceId, new VirtualFurnace(furnace));
        }


        // create an inventory to represent the furnace
        //new CraftInventoryFurnace(furnace);
        return (Inventory) NMSClass.CraftInventoryFurnace.getClazz()
                .getDeclaredConstructor(NMSClass.TileEntityFurnace.getClazz()).newInstance(furnace);
    }

    @Override
    public void onClose(InventoryCloseEvent e, PotentialBackpackItem backpack, UpdateItemCallback callback) throws Exception {
        // ((CraftInventory)inv).getInventory() == IInventory which is our instance of TileEntityFurnace,
        //   we use this for identifying
        Inventory inv = e.getInventory();
        //TileEntityFurnace furnace = (TileEntityFurnace) ((CraftInventory)inv).getInventory();
        Object craftInventory = NMSClass.CraftInventory.getClazz().cast(inv);
        Object iInventory = NMSMethod.getInventory.getMethod().invoke(craftInventory);
        Object furnace = NMSClass.TileEntityFurnace.getClazz().cast(iInventory);

        // looks up virtual furnace from table
        Map.Entry<UUID, VirtualFurnace> vFurnaceEntry = locateVirtualFurnace(furnace);
        if (vFurnaceEntry != null) {
            backpack.setNBT(NBTType.STRING, "furnace_id", vFurnaceEntry.getKey().toString());
            vFurnaceEntry.getValue().setReleased(true);
        }

        updateFurnaceDataTo(furnace, backpack);
        ItemStack modifiedItem = backpack.getModifiedItem();

        // need to check again for null
        if (vFurnaceEntry != null) {
            vFurnaceEntry.getValue().setItemLocation(new ItemStackPointer(
                    modifiedItem,
                    new InventoryLocationPlayer(102, e.getPlayer().getUniqueId())));
            // 102 == chestplate slot, good first place to look
        }

        // update item post NBT modification
        callback.update(modifiedItem);
    }

    public static void updateFurnaceDataTo(Object furnace, NBTMap backpack)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // update backpack contents with inventory
        NBTMap nbtMap = new NBTMap();
        //furnace.save((NBTTagCompound) nbtMap.getTagCompound());
        NMSMethod.save.getMethod().invoke(furnace, nbtMap.getTagCompound());
        backpack.setAsMap("furnace_data", nbtMap);
    }

    public static Map.Entry<UUID, VirtualFurnace> locateVirtualFurnace(Object furnace) {
        for (Map.Entry<UUID, VirtualFurnace> entry : BackpackFurnaceTickEvent.VIRTUAL_FURNACES.entrySet()) {
            if (entry.getValue().getFurnace() == furnace) {
                return entry;
            }
        }

        return null;
    }
}

// ** BASED OFF OF Bukkit.createInventory(null, InventoryType.FURNACE);
// ** add support for more from: org.bukkit.craftbukkit.vX_XX_RX.inventory.util.CraftInventoryCreator
//public static class Furnace extends CraftTileInventoryConverter {
//    public Furnace() {
//    }
//
//    public IInventory getTileEntity() {
//        TileEntityFurnace furnace = new TileEntityFurnaceFurnace();
//        furnace.setWorld(MinecraftServer.getServer().getWorldServer(DimensionManager.OVERWORLD));
//        return furnace;
//    }
//
//    public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
//        IInventory tileEntity = this.getTileEntity();
//        ((TileEntityFurnace)tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
//        return this.getInventory(tileEntity);
//    }
//
//    public Inventory getInventory(IInventory tileEntity) {
//        return new CraftInventoryFurnace((TileEntityFurnace)tileEntity);
//    }
//}