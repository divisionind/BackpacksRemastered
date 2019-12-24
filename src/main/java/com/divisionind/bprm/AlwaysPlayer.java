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

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AlwaysPlayer {

    private UUID playerId;

    private Player lastPlayer;
    private boolean isOfflinePlayer;

    public AlwaysPlayer(UUID playerId) {
        this.playerId = playerId;
    }

    // TODO change to nms
    public Player resolvePlayer() {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            if (offlinePlayer.hasPlayedBefore()) {
                GameProfile gameProfile = new GameProfile(playerId, offlinePlayer.getName());

                MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
                WorldServer worldServer = server.getWorldServer(DimensionManager.OVERWORLD);

                EntityPlayer entityPlayer = new EntityPlayer(server, worldServer, gameProfile, new PlayerInteractManager(worldServer));
                player = entityPlayer.getBukkitEntity();
                player.loadData();
                isOfflinePlayer = true;
                return lastPlayer = player; // just trying some stuff
            }
        }

        isOfflinePlayer = false;
        return lastPlayer = player;
    }

    public void safeSave() {
        if (isOfflinePlayer) {
            lastPlayer.saveData();
        }
    }

    public String getName() {
        String playerName;
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            if (offlinePlayer.hasPlayedBefore()) {
                playerName = offlinePlayer.getName();
            } else playerName = "unknown";
        } else {
            playerName = player.getName();
        }

        return playerName;
    }
}
