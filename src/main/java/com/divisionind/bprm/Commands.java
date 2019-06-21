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

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    /*
        TODO
        for each subcommand use a colan and dont nest e.g.

        /bp config:set <key> <value>
        /bp config:remove <key>
        etc
     */

    protected static class Help extends ACommand {

        private static final int COMMANDS_PER_PAGE = 6;

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
            respondnop(sender);
        }
    }
}
