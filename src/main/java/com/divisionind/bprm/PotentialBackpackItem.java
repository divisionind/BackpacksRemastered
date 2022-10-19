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

package com.divisionind.bprm;

import com.divisionind.bprm.exceptions.UnknownBackpackException;
import com.divisionind.bprm.nms.NMSItemStack;
import com.divisionind.bprm.nms.reflect.NBTType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class PotentialBackpackItem extends NMSItemStack {

    public static final String FIELD_NAME_TYPE = "backpack_type";
    public static final String FIELD_NAME_DATA = "backpack_data";

    public PotentialBackpackItem(ItemStack item)
            throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        super(item);
    }

    public boolean isBackpack() throws InvocationTargetException, IllegalAccessException {
        return hasNBT(FIELD_NAME_TYPE);
    }

    public int getType() throws IllegalAccessException, InvocationTargetException {
        return (int) getNBT(NBTType.INT, FIELD_NAME_TYPE);
    }

    public byte[] getData() throws IllegalAccessException, InvocationTargetException {
        return (byte[]) getNBT(NBTType.BYTE_ARRAY, FIELD_NAME_DATA);
    }

    public void setType(int type) throws InvocationTargetException, IllegalAccessException {
        setNBT(NBTType.INT, FIELD_NAME_TYPE, type);
    }

    public void setData(byte[] data) throws InvocationTargetException, IllegalAccessException {
        setNBT(NBTType.BYTE_ARRAY, FIELD_NAME_DATA, data);
    }

    public void setData(Inventory inv, String name) throws IOException, InvocationTargetException, IllegalAccessException {
        // create output streams
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(bout);

        // write view name and inventory size (not storing this info with backpack type for legacy reasons)
        out.writeInt(inv.getSize());
        out.writeUTF(name);

        // store items
        for (int i = 0; i < inv.getSize(); i++) out.writeObject(inv.getItem(i));

        // flush and close output
        out.flush();
        out.close();

        setData(bout.toByteArray());
    }

    public void setData(Location location) throws IOException, InvocationTargetException, IllegalAccessException {
        // create output streams
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(bout);

        // write the location object
        out.writeObject(location);

        // flush and close output
        out.flush();
        out.close();

        setData(bout.toByteArray());
    }

    public Location getDataAsLocation() throws InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        // create input streams
        ByteArrayInputStream bin = new ByteArrayInputStream(getData());
        BukkitObjectInputStream in = new BukkitObjectInputStream(bin);

        // read location from data
        Location location = (Location) in.readObject();

        // close stream, return location
        in.close();
        return location;
    }

    public Inventory getDataAsInventory() throws InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        // create input streams
        ByteArrayInputStream bin = new ByteArrayInputStream(getData());
        BukkitObjectInputStream in = new BukkitObjectInputStream(bin);

        // create inventory based on stored size / title
        Inventory inv = Bukkit.getServer().createInventory(null, in.readInt(), in.readUTF());

        // load items from inventory
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, (ItemStack) in.readObject());

        // close stream, return inventory
        in.close();
        return inv;
    }

    public boolean hasData() throws InvocationTargetException, IllegalAccessException {
        return hasNBT(FIELD_NAME_DATA);
    }

    public BackpackObject getTypeObject() throws InvocationTargetException, IllegalAccessException {
        return BackpackObject.getByType(getType());
    }

    public BackpackHandler getHandler() throws InvocationTargetException, IllegalAccessException, UnknownBackpackException {
        BackpackObject bp = BackpackObject.getByType(getType());
        if (bp != null)
            return bp.getHandler();
        else throw new UnknownBackpackException();
    }
}
