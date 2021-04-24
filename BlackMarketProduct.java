package com.goldfinch.gameengine;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

public class BlackMarketProduct {

    @Getter @Setter private ItemStack icon;
    @Getter @Setter private int price;
    @Getter @Setter private double chance;
    @Getter @Setter private int amount;
    @Getter @Setter private Consumer<InventoryClickEvent> runnable;

    BlackMarketProduct(ItemStack icon, int price, double chance, int amount, Consumer<InventoryClickEvent> runnable) {
        this.icon = icon;
        this.price = price;
        this.chance = chance;
        this.amount = amount;
        this.runnable = runnable;
    }

    static BlackMarketProduct setKeyProduct() {
        RandomCollection<KeyType> random = new RandomCollection<KeyType>() {{
           add(0.80, KeyType.COMMON);
           add(0.15, KeyType.RARE);
           add(0.03, KeyType.EPIC);
           add(0.029, KeyType.MIFIC);
           add(0.001, KeyType.LEGENDARY);
        }};

        KeyType keyType = random.next();

        int price = 0;
        int amount = 0;
        double chance = 0.0;

        switch (keyType) {
            case COMMON:
                price = 33;
                chance = 0.8;
                amount = 200;
                break;

            case RARE:
                price = 500;
                chance = 0.6;
                amount = 100;
                break;

            case EPIC:
                price = 3500;
                chance = 0.4;
                amount = 50;
                break;

            case MIFIC:
                price = 11500;
                chance = 0.3;
                amount = 15;
                break;

            case LEGENDARY:
                price = 36500;
                chance = 0.2;
                amount = 3;
                break;
            default:
                break;
        }

        if (!PluginUtils.getRandom(chance)) {
            return setAnotherProduct();
        }

        int finalPrice = price + AnarchyEconomy.getRandom().nextInt(price);
        int finalAmount = amount + AnarchyEconomy.getRandom().nextInt(amount);

        ItemStack key = keyType.getStack(1);
        ItemMeta keyMeta = key.getItemMeta();
        List<String> lore = keyMeta.getLore();
        lore.add("");
        lore.add(Formatting.keyValue("Цена", Colors.custom(250, 16, 126) + finalPrice + Colors.custom(210, 30, 69) + " ◊"));

        keyMeta.setLore(lore);
        key.setItemMeta(keyMeta);

        return new BlackMarketProduct(key, finalPrice, chance, finalAmount, (event -> {
            if (tryMineralsTransaction(AnarchyEconomy.getPlayer(event.getWhoClicked().getUniqueId()), finalPrice)) {
                event.getWhoClicked().getInventory().addItem(keyType.getStack(1));
                event.getWhoClicked().sendMessage(Formatting.info(Formatting.InfoLevel.FINE, "Вы успешно купили " + keyType.getTitle()));

            }
        }));

    }

    static BlackMarketProduct setAnotherProduct() {
        int dice = AnarchyEconomy.getRandom().nextInt(4);
        switch (dice) {
            case 0: return setRelicProduct();
            case 1: return setDynamiteProduct();
            case 2: return setKeyProduct();
            case 3: return setRelicContainerProduct();
            case 4: return setRelicDisenchanterProduct();
            default: return setAnotherProduct();

        }

    }

    static BlackMarketProduct setRelicProduct() {
        int price = 0;
        double chance = 0.0;

        RandomCollection<RelicType> random = new RandomCollection<RelicType>() {{
           add(0.40, RelicType.SPEED);
           add(0.25, RelicType.HASTE);
           add(0.25, RelicType.EXPLOSION);
           add(0.22, RelicType.JEWEL);
           add(0.10, RelicType.FORTUNE);
           add(0.05, RelicType.IMPULSE);
           add(0.02, RelicType.KEYKEEPER);
        }};

        RelicType relicType = random.next();

        switch (relicType) {
            case SPEED:
                chance = 0.7;
                price = 800;
                break;

            case HASTE:
                chance = 0.6;
                price = 1200;
                break;

            case EXPLOSION:
                chance = 0.5;
                price = 9000;
                break;

            case JEWEL:
                chance = 0.4;
                price = 4500;
                break;

            case FORTUNE:
                chance = 0.4;
                price = 15000;
                break;

            case IMPULSE:
                chance = 0.3;
                price = 9000;
                break;

            case KEYKEEPER:
                chance = 0.2;
                price = 16000;
                break;

            default:
                break;
        }

        if (!PluginUtils.getRandom(chance)) {
            return setAnotherProduct();
        }

        int finalPrice = price + AnarchyEconomy.getRandom().nextInt(price);

        Relic relicToGive = new Relic(relicType, 1+AnarchyEconomy.getRandom().nextInt(98));
        ItemStack relicToShow = relicToGive.getPuttableStack();
        ItemMeta relicToShowMeta = relicToShow.getItemMeta();
        List<String> lore = relicToShowMeta.getLore();
        lore.add("");
        lore.add(Formatting.keyValue("Цена", Colors.custom(250, 16, 126) + finalPrice + Colors.custom(210, 30, 69) + " ◊"));

        relicToShowMeta.setLore(lore);
        relicToShow.setItemMeta(relicToShowMeta);

        return new BlackMarketProduct(relicToShow, finalPrice, chance, 1+AnarchyEconomy.getRandom().nextInt(10), (event -> {
            if (tryMineralsTransaction(AnarchyEconomy.getPlayer(event.getWhoClicked().getUniqueId()), finalPrice)) {
                event.getWhoClicked().getInventory().addItem(relicToGive.getPuttableStack());
                event.getWhoClicked().sendMessage(Formatting.info(Formatting.InfoLevel.FINE, "Вы успешно купили " + relicToGive.getType().getTitle()));

            }
        }));
    }

    static BlackMarketProduct setRelicContainerProduct() {
        double chance = 0.1;

        if (!PluginUtils.getRandom(chance)) {
            return setAnotherProduct();
        }

        int price = 13000 + AnarchyEconomy.getRandom().nextInt(9000);

        ItemStack relicContainer = RelicContainer.getRelicContainer();
        ItemMeta relicContainerMeta = relicContainer.getItemMeta();
        List<String> lore = relicContainerMeta.getLore();
        lore.add("");
        lore.add(Formatting.keyValue("Цена", Colors.custom(250, 16, 126) + price + Colors.custom(210, 30, 69) + " ◊"));

        relicContainerMeta.setLore(lore);
        relicContainer.setItemMeta(relicContainerMeta);

        return new BlackMarketProduct(relicContainer, price, chance, 10+AnarchyEconomy.getRandom().nextInt(5), (event -> {

            if (tryMineralsTransaction(AnarchyEconomy.getPlayer(event.getWhoClicked().getUniqueId()), price)) {
                event.getWhoClicked().getInventory().addItem(RelicContainer.getRelicContainer());
                event.getWhoClicked().sendMessage(Formatting.info(Formatting.InfoLevel.FINE, "Вы успешно купили " + RelicContainer.getRelicContainer().getItemMeta().getDisplayName()));
            }
        }));

    }

    static BlackMarketProduct setRelicDisenchanterProduct() {
        double chance = 0.1;


        if (!PluginUtils.getRandom(chance)) {
            return setAnotherProduct();
        }

        int price = 2500 + AnarchyEconomy.getRandom().nextInt(5000);

        ItemStack relicDisenchanter = RelicDisenchanter.getRelicDisenchanter();
        ItemMeta relicDisenchanterMeta = relicDisenchanter.getItemMeta();
        List<String> lore = relicDisenchanterMeta.getLore();
        lore.add("");
        lore.add(Formatting.keyValue("Цена", Colors.custom(250, 16, 126) + price + Colors.custom(210, 30, 69) + " ◊"));

        relicDisenchanterMeta.setLore(lore);
        relicDisenchanter.setItemMeta(relicDisenchanterMeta);

        return new BlackMarketProduct(relicDisenchanter, price, chance, 6+AnarchyEconomy.getRandom().nextInt(2), (event -> {

            if (tryMineralsTransaction(AnarchyEconomy.getPlayer(event.getWhoClicked().getUniqueId()), price)) {
                event.getWhoClicked().getInventory().addItem(RelicDisenchanter.getRelicDisenchanter());
                event.getWhoClicked().sendMessage(Formatting.info(Formatting.InfoLevel.FINE, "Вы успешно купили " + RelicDisenchanter.getRelicDisenchanter().getItemMeta().getDisplayName()));
            }
        }));

    }

    static BlackMarketProduct setDynamiteProduct() {
        int price = (330+AnarchyEconomy.getRandom().nextInt(100));
        double chance = 0.6;


        if (!PluginUtils.getRandom(chance)) {
            return setAnotherProduct();
        }

        ItemStack dynamiteToShow = DynamiteHandler.getSingleDynamtie().clone();
        ItemMeta dynamiteMeta = dynamiteToShow.getItemMeta();
        List<String> lore = dynamiteMeta.getLore();
        lore.add("");
        lore.add(Formatting.keyValue("Цена", Colors.custom(250, 16, 126) + price + Colors.custom(210, 30, 69) + " ◊"));

        dynamiteMeta.setLore(lore);
        dynamiteToShow.setItemMeta(dynamiteMeta);

        ItemStack dynamite = DynamiteHandler.getSingleDynamtie().clone();


        return new BlackMarketProduct(dynamiteToShow, price, chance, 320+AnarchyEconomy.getRandom().nextInt(640), (event -> {
            if (tryMineralsTransaction(AnarchyEconomy.getPlayer(event.getWhoClicked().getUniqueId()), price)) {
                event.getWhoClicked().getInventory().addItem(dynamite);
                event.getWhoClicked().sendMessage(Formatting.info(Formatting.InfoLevel.FINE, "Вы успешно купили " + dynamite.getItemMeta().getDisplayName()));
            }
        }));

    }

    public static boolean tryMineralsTransaction(PlayerData playerData, int price) {
       if (PlayerUtils.isInventoryHaveEmptySlots(Bukkit.getPlayer(playerData.getUniqueId()))) {
           if (playerData.getMinerals() >= price) {
               playerData.setMinerals(playerData.getMinerals() - price);
               SuperSounds.playSound(Bukkit.getPlayer(playerData.getUniqueId()), SuperSounds.FINE);
               Bukkit.getPlayer(playerData.getUniqueId()).sendMessage(Formatting.info(Formatting.InfoLevel.FINE,
                       "Товар успешно куплен!"));
               return true;
           } else {
               SuperSounds.playSound(Bukkit.getPlayer(playerData.getUniqueId()), SuperSounds.ERROR);
               Bukkit.getPlayer(playerData.getUniqueId()).sendMessage(Formatting.info(Formatting.InfoLevel.ERROR,
                       "У вас недостаточно минералов на счету!"));
               return false;
           }

     } else {
           SuperSounds.playSound(Bukkit.getPlayer(playerData.getUniqueId()), SuperSounds.ERROR);
           Bukkit.getPlayer(playerData.getUniqueId()).sendMessage(Formatting.info(Formatting.InfoLevel.ERROR,
                   "Нам некуда положить вашу покупку! Пожалуйста, освободите хотя бы 1 слот в своём инветаре"));
           return false;
      }

    }
}
