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

import com.divisionind.bprm.exceptions.InvalidAdaptorAbilityException;
import com.divisionind.bprm.exceptions.InvalidAdaptorException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AdaptorManager {

    private final Plugin plugin;
    private final Map<String, PluginAdaptor> pluginAdaptors;
    private final Map<String, PluginAdaptorLoader> pluginAdaptorLoaders;
    private final Map<String, AdaptorAbility> adaptorAbilities;

    public AdaptorManager(Plugin plugin) {
        this.plugin = plugin;
        this.pluginAdaptors = new HashMap<>();
        this.pluginAdaptorLoaders = new HashMap<>();
        this.adaptorAbilities = new HashMap<>();
    }

    /**
     * Creates/Registers loaders for the specified adaptors.
     * Note: This must be called in any plugins onEnable()
     *
     * @param adaptors array of plugin adaptor classes
     * @throws InvalidAdaptorException
     */
    @SafeVarargs
    public final void registerAdaptors(Class<? extends PluginAdaptor>... adaptors) throws InvalidAdaptorException {
        for (Class<? extends PluginAdaptor> adaptor : adaptors) {
            PluginAdaptorLoader loader = new PluginAdaptorLoader(adaptor);
            pluginAdaptorLoaders.put(loader.getMeta().name(), loader);
        }
    }

    /**
     * Loads adaptors using the loaders created by {@link AdaptorManager#registerAdaptors(Class[])}.
     * Note: This must be called after all plugins have been loaded. You can do this by scheduling it
     * in the onEnable() with {@link org.bukkit.scheduler.BukkitScheduler#scheduleSyncDelayedTask(Plugin, Runnable)}
     */
    public void reloadAdaptors() {
        // discard any previously loaded adaptors
        pluginAdaptors.clear();
        adaptorAbilities.clear();

        // initialize plugin adaptors
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

            PluginAdaptorLoader loader = pluginAdaptorLoaders.get(plugin.getName());
            // an adaptor existed
            if (loader != null) {
                // attempt to load adaptor instance
                PluginAdaptor adaptor;
                try {
                    adaptor = loader.load(this);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    this.plugin.getLogger().severe("Failed to create plugin adaptor instance for: " + plugin.getName());
                    e.printStackTrace();
                    continue;
                }

                // perform setup for the plugin adaptor
                try {
                    adaptor.onEnable(plugin);
                } catch (Exception e) {
                    this.plugin.getLogger().severe("Failed to load plugin adaptor for: " + plugin.getName());
                    e.printStackTrace();
                    continue;
                }

                // setup adaptor abilities
                for (Method method : loader.getAdaptorClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(AbilityFunction.class)) {
                        // this method is an ability, register it
                        AbilityFunction abilityFunction = method.getAnnotation(AbilityFunction.class);

                        try {
                            registerAbility(abilityFunction.value().equals("") ? method.getName() : abilityFunction.value(),
                                    new AdaptorAbility(adaptor, method));
                        } catch (InvalidAdaptorAbilityException e) {
                            e.printStackTrace(); // this could never happen
                        }
                    }
                }

                // add adaptor to the registry of loaded adaptors
                pluginAdaptors.put(plugin.getName(), adaptor);
            }
        }

        if (pluginAdaptors.size() > 0) {
            StringBuilder msg = new StringBuilder("Loaded adaptor(s) for: ");
            for (String adaptor : pluginAdaptors.keySet()) msg.append(adaptor).append(", ");
            this.plugin.getLogger().info(msg.substring(0, msg.length() - 2));
        }
    }

    /**
     * Gets an adaptor by its name.
     * @param name
     * @return
     */
    public PluginAdaptor getAdaptor(String name) {
        return pluginAdaptors.get(name);
    }

    /**
     * Registers an adaptor ability.
     * @param name
     * @param ability
     */
    public void registerAbility(String name, AdaptorAbility ability) {
        adaptorAbilities.put(name, ability);
    }

    /**
     * Gets an adaptor ability by name.
     * @param name
     * @return
     */
    public AdaptorAbility getAbility(String name) {
        return adaptorAbilities.get(name);
    }
}
