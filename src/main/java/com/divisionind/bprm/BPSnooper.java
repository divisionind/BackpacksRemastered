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

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Deprecated
public class BPSnooper implements Runnable {

    public static final String ADDRESS = "https://bpsnoop.divisionind.com/snoop/"; // maybe /1, /2, etc for api version in future

    private BukkitTask task;

    public BPSnooper() {
        // we dont really want to create a bunch of threads for this if possible (this will be effected by server lag though)
        // its not mission critical that this runs perfectly timed
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                Backpacks.getInstance(),
                this, 20 * 60 * 5, 20 * 60 * 30); // about the same interval as bstats
    }

    public void stop() {
        this.task.cancel();
    }

    @Override
    public void run() {
        // we dont want to throw any exceptions for this either, if it runs ok, great, if not then we dont care
        try {
            // we can get players asynchronously which saves us a lot of hassle
            submitData(Backpacks.VERSION, Bukkit.getOnlinePlayers().size());
        } catch (Exception ex) {}
    }

    private void submitData(String version, int players) throws IOException {
        URL url = new URL(ADDRESS);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        // create post request
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        String encodedData = String.format("Players=%s&BackpacksVersion=%s", players, version);
        byte[] data = encodedData.getBytes(StandardCharsets.UTF_8);

        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("User-Agent", "MC-Server"); // required or spigot screws with ur stuff

        // write out content body
        try (DataOutputStream dout = new DataOutputStream(conn.getOutputStream())) {
            dout.write(data);
        }

        // execute the request
        conn.getResponseCode();
    }
}
