package com.goldfinch.gameengine;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BlackMarket {

    private static HashMap<Integer, BlackMarketProduct> actualProducts;
    private static List<UUID> alreadyBought;
    private static boolean cooldown = true;

    public static void getNewProducts() {
        actualProducts = new HashMap<>();
        alreadyBought = new ArrayList<>();

        for (Integer slot : new int[] { 11, 13, 15 }) {
            Map<Integer, BlackMarketProduct> allProducts = new HashMap<Integer, BlackMarketProduct>() {{
                put(0, BlackMarketProduct.setDynamiteProduct());
                put(1, BlackMarketProduct.setKeyProduct());
                put(2, BlackMarketProduct.setRelicContainerProduct());
                put(3, BlackMarketProduct.setRelicDisenchanterProduct());
                put(4, BlackMarketProduct.setRelicProduct());
            }};

            int dice = AnarchyEconomy.getRandom().nextInt(4);
            actualProducts.put(slot, allProducts.get(dice));
        }
    }

    public static ControlledInventory blackMarketInventory = ControlledInventory.builder()
            .title("Чёрный рынок")
            .type(InventoryType.CHEST)
            .rows(5)
            .columns(9)
            .provider(new InventoryProvider() {
                @Override
                public void init(Player p, InventoryContents inventoryContents) { }

                @Override
                public void update(Player p, InventoryContents inventoryContents) {

                    ItemStack info = BukkitTagWorker.writeString(Items.builder()
                            .type(Material.CLAY_BALL)
                            .displayName(Colors.custom(48, 255, 244) + "Информация")
                            .lore(new String[]{
                                    "",
                                    "§fДобро пожаловать на Черный рынок!",
                                    "§fТовары здесь раз в 30 минут.",
                                    "",
                                    "§fЗдесь вы сможете купить редкие предметы,",
                                    "§fкоторые доступны только донатерам,",
                                    "§fлибо самым везучим игрокам!",
                                    "",
                                    "§fДоступные товары для покупки: ",
                                    "§8» " + Colors.custom(250, 16, 126) + "Все виды ключей",
                                    "§8» " + Colors.custom(250, 16, 126) + "Все виды реликвий",
                                    "§8» " + Colors.custom(250, 16, 126) + "Слоты для реликвий",
                                    "§8» " + Colors.custom(250, 16, 126) + "Разрушитель реликвий",
                                    "§8» " + Colors.custom(250, 16, 126) + "Детонатор и динамит",
                            })
                            .build(), "other", "info1");

                    ItemStack productSoldOut = Items.builder()
                            .type(Material.BARRIER)
                            .displayName(Colors.custom(238, 0, 23) + "ТОВАР РАСКУПЛЕН")
                            .lore(new String[]{
                                    "",
                                    "§fК сожалению, ты не успел приобрести",
                                    "§fданный товар и его раскупили!",
                                    "",
                                    "§fНо не растраивайся! Товары обновляются",
                                    "§fкаждые 30 минут, главное успей купить",
                                    "§fто, что тебе нужно первым!"
                            })
                            .build();

                    ItemStack refreshProducts = BukkitTagWorker.writeString(Items.builder()
                            .type(Material.CLAY_BALL)
                            .displayName(Colors.custom(48, 255, 244) + "Обновить товары")
                            .loreLines(
                                    "",
                                    "§fНе нравятся выставленные товары?",
                                    "§fОбнови предложения на Чёрном рынке!",
                                    "§fКстати, они обновятся у всего сервера.",
                                    "",
                                    "§fЦена §8» " + Colors.custom(250, 16, 126) + "10000" + Colors.custom(210, 30, 69) + " ◊",
                                    ""
                            )
                            .build(), "other", "reload");

                    BukkitTagWorker.setInt(info, "color", 51200);

                    inventoryContents.set(30, ClickableItem.empty(info));

                    inventoryContents.set(32, ClickableItem.of(refreshProducts, event -> {

                        if (cooldown) {
                            if (AnarchyEconomy.getPlayer(p.getUniqueId()).getMinerals() >= 10000) {
                                cooldown = false;
                                getNewProducts();

                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    if (player.getOpenInventory().getTitle().equalsIgnoreCase("Чёрный рынок"))
                                        player.closeInventory();
                                });

                                AnarchyEconomy.getPlayer(p.getUniqueId()).setMinerals(AnarchyEconomy.getPlayer(p.getUniqueId()).getMinerals() - 10000);

                                Bukkit.getScheduler().runTaskLater(AnarchyEconomy.getInstance(), () ->
                                        cooldown = true, 150L * 20);

                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    player.sendMessage(new String[]{
                                            "",
                                            "   §fИгрок " + Colors.custom(250, 16, 126) + p.getDisplayName() + " §fобновил товары на " + Colors.custom(250, 16, 126) + "Чёрном рынке!",
                                            "         §fЧёрный рынок находится на спавне",
                                            ""

                                    });
                                    SuperSounds.playSound(player, SuperSounds.EPIC);
                                });

                                SuperSounds.playSound(p, SuperSounds.FINE);
                                p.sendMessage(Formatting.info(Formatting.InfoLevel.FINE,
                                        "Вы успешно обновили Черный рынок!"));
                            } else {
                                SuperSounds.playSound(p, SuperSounds.ERROR);
                                p.sendMessage(Formatting.info(Formatting.InfoLevel.ERROR,
                                        "У вас недостаточно минералов на счету!"));
                            }
                        } else {
                            SuperSounds.playSound(p, SuperSounds.ERROR);
                            p.sendMessage(Formatting.info(Formatting.InfoLevel.ERROR,
                                    "Не спеши! Черный рынок недавно обновляли!"));
                        }
                    }));



                    actualProducts.forEach((slot, blackMarketProduct) -> {
                        if (blackMarketProduct.getAmount() <= 0) {
                            inventoryContents.set(slot, ClickableItem.empty(productSoldOut));
                        } else {
                            ItemStack icon = blackMarketProduct.getIcon().clone();
                            ItemMeta iconMeta = icon.getItemMeta();

                            List<String> oldLore = iconMeta.getLore();
                            List<String> newLore;

                            newLore = oldLore;
                            newLore.add("");
                            newLore.add(Colors.custom(238, 0, 23) + "В НАЛИЧИИ ОСТАЛОСЬ > " + blackMarketProduct.getAmount());

                            iconMeta.setLore(newLore);
                            icon.setItemMeta(iconMeta);

                            inventoryContents.set(slot, ClickableItem.of(icon, event -> {
                                if (alreadyBought.contains(p.getUniqueId()) && actualProducts.get(slot).getAmount() < 5) {
                                    p.sendMessage(Formatting.info(Formatting.InfoLevel.ERROR,
                                            "Не так быстро! Этого товара осталось слишком мало, чтобы ты мог покупать его так быстро!"));

                                    SuperSounds.playSound(p, SuperSounds.ERROR);
                                } else {
                                    blackMarketProduct.getRunnable().accept(event);
                                    actualProducts.get(slot).setAmount(blackMarketProduct.getAmount() - 1);

                                    if (actualProducts.get(slot).getAmount() < 5)
                                        alreadyBought.add(p.getUniqueId());

                                    Bukkit.getScheduler().runTaskLater(AnarchyEconomy.getInstance(), () -> alreadyBought.remove(p.getUniqueId()), 20*30);
                                }

                            }));
                        }
                    });


                }

            }).build();

}
