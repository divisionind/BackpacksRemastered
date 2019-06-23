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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Commands {

    protected static class Help extends ACommand {

        private static final int COMMANDS_PER_PAGE = 4;

        @Override
        public String alias() {
            return "help";
        }

        @Override
        public String desc() {
            return "displays this help information";
        }

        @Override
        public String usage() {
            return "<page>";
        }

        @Override
        public String permission() {
            return "backpacks.help";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            // determine what page of help to display
            int page;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    respond(sender, "&cError: This argument must be an integer.");
                    return;
                }
            } else page = 1;

            int numberOfPages = calculateNumberOfPages();

            if (page > numberOfPages) {
                respondf(sender, "&cError: This help page does not exist. The maximum help page is %s.", numberOfPages);
                return;
            }

            List<ACommand> commands = getOnPage(page);

            respondf(sender, "&bPage (&e%s &b/ &e%s&b)", page, numberOfPages);

            for (ACommand cmd : commands) {
                respondn(sender, String.format("&e/%s %s %s\n  &7%s", label, cmd.alias(), cmd.usage() == null ? "" : cmd.usage(), cmd.desc()));
            }
        }

        public static int calculateNumberOfPages() {
            return (Backpacks.getCommands().size() / COMMANDS_PER_PAGE) + 1;
        }

        public static List<ACommand> getOnPage(int page) {
            List<ACommand> commands = Backpacks.getCommands();
            List<ACommand> newCmds = new ArrayList<>();

            int startCmd = (page - 1) * COMMANDS_PER_PAGE;

            for (int i = startCmd;(i<commands.size() && (i - startCmd) < COMMANDS_PER_PAGE);i++) {
                newCmds.add(commands.get(i));
            }
            return newCmds;
        }
    }

    protected static class ConfigReload extends ACommand {
        @Override
        public String alias() {
            return "config:reload";
        }

        @Override
        public String desc() {
            return "reloads the yml configuration";
        }

        @Override
        public String usage() {
            return null;
        }

        @Override
        public String permission() {
            return "backpacks.config.reload";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            respond(sender, "&cWARNING: This command is not fully supported. Only use this for testing new configurations and ensure to reload the server once you are done.");
            respond(sender, "&eReloading the configuration...");
            Backpacks.getInstance().reloadConfig();
            Backpacks.getInstance().setupFromConfig();
            respond(sender, "&eConfiguration has been reloaded.");
        }
    }

    protected static class Info extends ACommand {
        @Override
        public String alias() {
            return "info";
        }

        @Override
        public String desc() {
            return "displays info about the plugin";
        }

        @Override
        public String usage() {
            return null;
        }

        @Override
        public String permission() {
            return "backpacks.info";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            respond(sender, "&e&lInfo");
            respondn(sender, "&7Created by drew6017 as a remake of his original plugin with more features an optimizations.");
            respondnf(sender, " &eVersion: &a%s", Backpacks.VERSION);
            respondnf(sender, " &eGit Commit: &a%s", Backpacks.GIT_HASH);
            respondnf(sender, " &eGit Build: &a%s", Backpacks.GIT_NUM);
        }
    }

    protected static class ItemInfo extends ACommand {
        @Override
        public String alias() {
            return "item:info";
        }

        @Override
        public String desc() {
            return "displays a list of NBT data for the item you are holding";
        }

        @Override
        public String usage() {
            return null;
        }

        @Override
        public String permission() {
            return "backpacks.item.info";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) {
                respond(sender, "&cYou must be a player to use this command.");
                return;
            }
            Player p = (Player)sender;
            ItemStack item = p.getInventory().getItemInMainHand();

            try {
                Object tagCompound = NMSReflector.getNBTTagCompound(NMSReflector.asNMSCopy(item));
                // show all nbt data
                Set<String> data = NMSReflector.getKeys(tagCompound);
                for (String s : data) {
                    respondn(sender, s);
                }
                respondf(sender, "&eFound %s NBT data entries.", data.size());
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    protected static class ItemInfoGet extends ACommand {
        @Override
        public String alias() {
            return "item:info:get";
        }

        @Override
        public String desc() {
            return "gets the type and value of the NBT data key specified";
        }

        @Override
        public String usage() {
            return "<key>";
        }

        @Override
        public String permission() {
            return "backpacks.item.info.get";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) {
                respond(sender, "&cYou must be a player to use this command.");
                return;
            }
            Player p = (Player)sender;
            ItemStack item = p.getInventory().getItemInMainHand();

            if (args.length == 1) {
                respondiu(sender, label);
                return;
            }

            try {
                Object tagCompound = NMSReflector.getNBTTagCompound(NMSReflector.asNMSCopy(item));

                if (NMSReflector.hasNBTKey(tagCompound, args[1])) {
                    NBTType type = NMSReflector.getKeyType(tagCompound, args[1]);

                    if (type == null) {
                        respondf(sender, "&cCould not resolve data type for key \"%s\".", args[1]);
                        return;
                    }

                    respondf(sender, "&eData:&7 %s", NMSReflector.getNBT(tagCompound, type, args[1]));
                    respondf(sender, "&eData Type:&7 %s", type.name());
                } else {
                    respondf(sender, "&cKey \"%s\" not found. See a list of keys with the item:info command.", args[1]);
                }
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    protected static class ItemGive extends ACommand {
        @Override
        public String alias() {
            return "item:give";
        }

        @Override
        public String desc() {
            return "gives the current player the backpack by type";
        }

        @Override
        public String usage() {
            return "<type> <player:none>";
        }

        @Override
        public String permission() {
            return "backpacks.item.give";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player p;

            // attempt to resolve player object
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    respond(sender, "&cYou must be a player to use this command.");
                    return;
                }
                p = (Player)sender;
            } else
            if (args.length == 3) {
                p = Bukkit.getPlayer(args[2]);
                if (p == null) {
                    respondf(sender, "&cPlayer \"%s\" was not found. Please check your spelling and try again.", args[2]);
                    return;
                }
            } else {
                respondiu(sender, label);
                return;
            }

            // attempt to resolve backpack type
            BackpackItem item = BackpackItem.getByName(args[1]);
            if (item == null) {
                respondf(sender, "&cBackpack of type \"%s\" was not found.", args[1]);
                return;
            }

            p.getInventory().addItem(item.getItem());
            respondf(sender, "&eGave the player a %s backpack.", item.name().toLowerCase());
        }

        @Override
        public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 2) {
                List<String> parts = new ArrayList<>();
                if (args[1].equals("")) {
                    for (BackpackItem item : BackpackItem.values()) parts.add(item.name());
                } else {
                    for (BackpackItem item : BackpackItem.values()) {
                        if (item.name().toLowerCase().startsWith(args[1].toLowerCase())) parts.add(item.name());
                    }
                }
                return parts;
            } else return null;
        }
    }
}
