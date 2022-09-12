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

package com.divisionind.bprm.adapters;

import com.divisionind.bprm.AbilityFunction;
import com.divisionind.bprm.PluginAdaptor;
import com.divisionind.bprm.PluginAdaptorMeta;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@PluginAdaptorMeta(name = "GriefPrevention")
public class AdaptorGriefPrevention extends PluginAdaptor {

    private GriefPrevention parent;

    @Override
    public void onEnable(Plugin parent) throws Exception {
        this.parent = (GriefPrevention) parent;
    }

    @AbilityFunction
    public boolean hasAccessToContainer(Player player, Location location) {
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
