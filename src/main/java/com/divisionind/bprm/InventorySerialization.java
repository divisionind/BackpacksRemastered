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

package com.divisionind.bprm;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InventorySerialization {

    public static byte[] toByteArray(Inventory inventory, String name) throws IOException {
        // create output streams
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(bout);

        // write view name and inventory size (TODO maybe move these to backpack type?)
        out.writeInt(inventory.getSize());
        out.writeUTF(name);

        // store items
        for (int i = 0; i < inventory.getSize(); i++) out.writeObject(inventory.getItem(i));

        // flush and close output
        out.flush();
        out.close();
        return bout.toByteArray();
    }

    public static Inventory fromByteArray(byte[] data) throws IOException, ClassNotFoundException {
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
}
