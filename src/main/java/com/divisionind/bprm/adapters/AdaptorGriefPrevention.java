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

package com.divisionind.bprm.adapters;

import com.divisionind.bprm.AbilityFunction;
import com.divisionind.bprm.AdaptorManager;
import com.divisionind.bprm.PluginAdaptor;
import com.divisionind.bprm.PluginAdaptorLoader;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AdaptorGriefPrevention extends PluginAdaptor {

    private GriefPrevention parent;

    public AdaptorGriefPrevention(AdaptorManager manager, PluginAdaptorLoader loader) {
        super(manager, loader);
    }

    @Override
    public void init(Plugin parent) throws Exception {
        this.parent = (GriefPrevention) parent;

        registerAbility("hasAccessToContainer", Player.class, Location.class);
    }

    @AbilityFunction
    public boolean hasAccessToContainer(Player player, Location location) {
        /*
                    claim = this.dataStore.getClaimAt(clickedBlock.getLocation(), false, playerData.lastClaim);
                    if (claim != null) {
                        playerData.lastClaim = claim;
                        noBuildReason = claim.allowContainers(player);
                        if (noBuildReason != null) {
                            event.setCancelled(true);
                            var10000 = this.instance;
                            GriefPrevention.sendMessage(player, TextMode.Err, noBuildReason);
                            return;
                        }
                    }
             */

        PlayerData data = parent.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = parent.dataStore.getClaimAt(location, false, data.lastClaim);

        if (claim == null) {
            return true;
        } else {
            data.lastClaim = claim;
            return claim.allowContainers(player) == null;
        }
    }
}
