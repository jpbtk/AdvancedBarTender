package github.io.jpbtk.advancedbartender;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;
import java.util.Random;

import static github.io.jpbtk.advancedbartender.AdvancedBarTender.*;

public class Listeners implements Listener {
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "abt"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            // インベントリからアイテムを消す
            Inventory inventory = player.getInventory();
            inventory.removeItem(item);
            File drink = new File("plugins/AdvancedBartender/drink/" + item.getItemMeta().getDisplayName().replace("§", "&") + ".yml");
            if (!drink.exists()) {
                player.sendMessage(prefix + "§c§lエラー: §r§cドリンクファイルが存在しません。");
            }
            YamlConfiguration drinkYml = YamlConfiguration.loadConfiguration(drink);
            String name = item.getItemMeta().getDisplayName();
            int price = drinkYml.getInt("price");
            int chance = drinkYml.getInt("chance");
            int bunbo = drinkYml.getInt("bunbo");
            // 1からbosuuの値でランダムな数値を取得する。
            Random rand = new Random();
            int random = rand.nextInt(bunbo) + 1;
            int test = 0;
            // randomの値がchanceの値以下の場合
            if (random <= chance) {
                int num = drinkYml.getConfigurationSection("action").getKeys(false).size();
                for (int i = 1; i <= num; i++) {
                    int chance2 = drinkYml.getInt("action." + i + ".chance");
                    test = test + chance2;
                }
                random = rand.nextInt(test) + 1;
                test = 0;
                for (int i = 1; i <= num; i++) {
                    List<String> actionlist = drinkYml.getStringList("action." + i + ".action");
                    int chance2 = drinkYml.getInt("action." + i + ".chance");
                    test = test + chance2;
                    if (random <= test) {
                        for (String action2 : actionlist) {
                            if (action2.startsWith("give ")) {
                                ItemStack item2 = new ItemStack(Material.valueOf(action2.replace("give ", "")));
                                player.getInventory().addItem(item2);
                                player.sendMessage(prefix + "§a§l" + item2.getItemMeta().getDisplayName() + "§r§aを獲得しました。");
                            }
                            if (action2.startsWith("say ")) {
                                String message = action2.replace("say ", "");
                                message = message.replace("&", "§");
                                message = message.replace("%player%", player.getName());
                                player.sendMessage(prefix + message);
                            }
                            if (action2.startsWith("broadcast ")) {
                                String message = action2.replace("broadcast ", "");
                                message = message.replace("&", "§");
                                message = message.replace("%player%", player.getName());
                                plugin.getServer().broadcastMessage(prefix + message);
                            }
                            if (action2.startsWith("givemoney ")) {
                                int money = Integer.parseInt(action2.replace("givemoney ", ""));
                                econ.depositPlayer(player, money);
                                player.sendMessage(prefix + "§a§l" + money + "§r§a円を獲得しました。");
                            }
                            if (action2.startsWith("serverstock ")) {
                                int money = drinkYml.getInt("serverstock");
                                String stock = action2.replace("serverstock ", "");
                                if (stock.startsWith("payall")) {
                                    drinkYml.set("serverstock", 0);
                                    try {
                                        drinkYml.save(drink);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    econ.depositPlayer(player, money);
                                    player.sendMessage(prefix + "§a§l" + money + "§r§a円を獲得しました。");
                                } else {
                                    int stock2 = Integer.parseInt(stock);
                                    drinkYml.set("serverstock", money + stock2);
                                    try {
                                        drinkYml.save(drink);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    player.sendMessage(prefix + "§a§l" + stock2 + "§r§aのストックを獲得しました。");
                                }
                            }
                        }
                        break;
                    }
                }
            } else {
                int num = drinkYml.getConfigurationSection("failaction").getKeys(false).size();
                for (int i = 1; i <= num; i++) {
                    int chance2 = drinkYml.getInt("failaction." + i + ".chance");
                    test = test + chance2;
                }
                random = rand.nextInt(test) + 1;
                test = 0;
                for (int i = 1; i <= num; i++) {
                    List<String> failactionlist = drinkYml.getStringList("failaction." + i + ".action");
                    int chance2 = drinkYml.getInt("failaction." + i + ".chance");
                    test = test + chance2;
                    if (random <= test) {
                        for (String failaction2 : failactionlist) {
                            if (failaction2.startsWith("give ")) {
                                ItemStack item2 = new ItemStack(Material.valueOf(failaction2.replace("give ", "")));
                                player.getInventory().addItem(item2);
                                player.sendMessage(prefix + "§a§l" + item2.getItemMeta().getDisplayName() + "§r§aを獲得しました。");
                            }
                            if (failaction2.startsWith("say ")) {
                                String message = failaction2.replace("say ", "");
                                message = message.replace("&", "§");
                                message = message.replace("%player%", player.getName());
                                player.sendMessage(prefix + message);
                            }
                            if (failaction2.startsWith("broadcast ")) {
                                String message = failaction2.replace("broadcast ", "");
                                message = message.replace("&", "§");
                                message = message.replace("%player%", player.getName());
                                plugin.getServer().broadcastMessage(prefix + message);
                            }
                            if (failaction2.startsWith("givemoney ")) {
                                int money = Integer.parseInt(failaction2.replace("givemoney ", ""));
                                econ.depositPlayer(player, money);
                                player.sendMessage(prefix + "§a§l" + money + "§r§a円を獲得しました。");
                            }
                            if (failaction2.startsWith("serverstock ")) {
                                int money = drinkYml.getInt("serverstock");
                                String stock = failaction2.replace("serverstock ", "");
                                if (stock.startsWith("payall")) {
                                    drinkYml.set("serverstock", 0);
                                    try {
                                        drinkYml.save(drink);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    econ.depositPlayer(player, money);
                                    player.sendMessage(prefix + "§a§l" + money + "§r§a円を獲得しました。");
                                } else {
                                    int stock2 = Integer.parseInt(stock);
                                    drinkYml.set("serverstock", money + stock2);
                                    try {
                                        drinkYml.save(drink);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    player.sendMessage(prefix + "§a§l" + stock2 + "§r§aのストックを獲得しました。");
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase("§a§lAdvancedBartender")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "abt"), PersistentDataType.STRING)) {
                File drink = new File("plugins/AdvancedBartender/drink/" + item.getItemMeta().getDisplayName() + ".yml");
                if (!drink.exists()) {
                    player.sendMessage(prefix + "§c§lエラー: §r§cドリンクファイルが存在しません。");
                }
                YamlConfiguration drinkYml = YamlConfiguration.loadConfiguration(drink);
                if (econ.getBalance(player) >= drinkYml.getInt("price")) {
                    econ.withdrawPlayer(player, drinkYml.getInt("price"));
                    player.sendMessage(prefix + "§a§l" + drinkYml.getString("name") + "§r§aを" + drinkYml.getInt("price") + "§r§a円で購入しました。");
                    player.getInventory().addItem(item);
                } else {
                    player.sendMessage(prefix + "§c§lエラー: §r§cお金が足りません。");
                }
            }
        }
    }
}
