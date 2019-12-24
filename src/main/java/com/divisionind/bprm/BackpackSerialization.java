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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BackpackSerialization {

    public static byte[] toByteArrayInventory(Inventory inventory, String name) throws IOException {
        // create output streams
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(bout);

        // write view name and inventory size (not storing this info with backpack type for legacy reasons)
        out.writeInt(inventory.getSize());
        out.writeUTF(name);

        // store items
        for (int i = 0; i < inventory.getSize(); i++) out.writeObject(inventory.getItem(i));

        // flush and close output
        out.flush();
        out.close();
        return bout.toByteArray();
    }

    public static Inventory fromByteArrayInventory(byte[] data) throws IOException, ClassNotFoundException {
        // create input streams
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        BukkitObjectInputStream in = new BukkitObjectInputStream(bin);

        // create inventory based on stored size / title
        Inventory inv = Bukkit.getServer().createInventory(null, in.readInt(), in.readUTF());

        // load items from inventory
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, (ItemStack) in.readObject());

        // close stream, return inventory
        in.close();
        return inv;
    }

    public static byte[] toByteArrayItemStackArray(ItemStack[] items) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(bout);

        for (ItemStack item : items) {
            out.writeObject(item);
        }

        out.flush();
        out.close();
        return bout.toByteArray();
    }

    public static ItemStack[] fromByteArrayItemStackArray(byte[] data, int numItemstacks) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        BukkitObjectInputStream in = new BukkitObjectInputStream(bin);
        ItemStack[] itemArray = new ItemStack[numItemstacks];

        for (int i = 0;i < numItemstacks;i++) {
            itemArray[i] = (ItemStack) in.readObject();
        }

        in.close();
        return itemArray;
    }

    public static byte[] toByteArrayLocation(Location location) throws IOException {
        // create output streams
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(bout);

        // write the location object
        out.writeObject(location);

        // flush and close output
        out.flush();
        out.close();
        return bout.toByteArray();
    }

    public static Location fromByteArrayLocation(byte[] data) throws IOException, ClassNotFoundException {
        // create input streams
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        BukkitObjectInputStream in = new BukkitObjectInputStream(bin);

        // read location from data
        Location location = (Location)in.readObject();

        // close stream, return location
        in.close();
        return location;
    }
}
