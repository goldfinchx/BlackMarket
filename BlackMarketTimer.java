package com.goldfinch.gameengine;

import org.bukkit.Bukkit;

public class BlackMarketTimer {

        public static int blackMarketCounter;

        public static void startTimer() {
            blackMarketCounter = Bukkit.getScheduler().scheduleSyncRepeatingTask(AnarchyEconomy.getInstance(), () -> {

                BlackMarket.getNewProducts();
                Bukkit.getOnlinePlayers().forEach(player -> {

                            player.sendMessage(new String[]{
                                    "",
                                    "   §fТовары на " + Colors.custom(250, 16, 126) + "Чёрном рынке" + " §fобновились!",
                                    "    §fЧёрный рынок находится на спавне",
                                    ""
                            });
                            SuperSounds.playSound(player, SuperSounds.EPIC);
                });

            }, 200, 20*1800);
        }
    }
