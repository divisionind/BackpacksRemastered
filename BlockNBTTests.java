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

package com.divisionind.bprm.commands;

import com.divisionind.bprm.ACommand;
import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTTagInt;
import net.minecraft.server.v1_14_R1.TileEntityChest;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.v1_14_R1.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;
import java.util.Map;

public class Test extends ACommand {
    @Override
    public String alias() {
        return "test";
    }

    @Override
    public String desc() {
        return "a test command";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String permission() {
        return "backpacks.main";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player p = validatePlayer(sender);
        validateArgsLength(args, 2);

        Block b = p.getTargetBlock(null, 10);

        TileEntityChest tent = null;
        CraftBlockEntityState<TileEntityChest> chest = (CraftBlockEntityState<TileEntityChest>)b.getState(); // use mob spawner instead of chest for control block
        try {
            Field f = CraftBlockEntityState.class.getDeclaredField("tileEntity");
            f.setAccessible(true);
            tent = (TileEntityChest) f.get(chest);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (tent == null) return;

        CraftPersistentDataContainer cont = tent.persistentDataContainer;
        Map<String, NBTBase> blockData = cont.getRaw();

        if (args[1].equalsIgnoreCase("set")) {
            blockData.put("test", new NBTTagInt(30));
            ACommand.respond(sender, "&eMeta set");
            return;
        }

        ACommand.respond(sender, "&eMeta:");
        for (String key : blockData.keySet()) ACommand.respondf(sender, "Data: %s", key);
        ACommand.respondf(sender, "Value of test=%s", ((NBTTagInt)blockData.get("test")).asInt());

        // getitem, gi command (just like give command but pulls from chests in me system)

        // NOTE:
        // now find a way to store data with the world save so that an index on these blocks can be built
        // this will prevent me from having to always scan chunks on load for this data (also see about doing this async)
        // add time stamp to the cached data, e.g. ChunkLoadEvent -> Chunk#getTileEntities -> get whether or not has custom block nbt (if fast enough, dont cache) (likely because the server already builds us an index of tile entities)
        // YOU COULD FORGET ALL OF THIS STUFF WITH WORLD SAVING / CHUNK LOADING EVENTS IF YOU DO NOT PLAN TO DO ANYTHING PERIODICALLY AND ONLY RESPOND TO REQUESTS BY A BACKPACK
    }
}
