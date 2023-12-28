package github.io.jpbtk.advancedbartender.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static github.io.jpbtk.advancedbartender.AdvancedBarTender.plugin;

public class ABT implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        File playerdata = new File("plugins/AdvancedBartender/playerdata/" + player.getUniqueId() + ".yml");
        YamlConfiguration playerdataYml = YamlConfiguration.loadConfiguration(playerdata);
        File[] bartenderlist = new File("plugins/AdvancedBartender/bartender/").listFiles();
        if (args.length == 0) {
            player.sendMessage("§a§l/abt help §r§a: §r§aこのヘルプを表示します。");
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage("§a§lAdvancedBartender §r§aのコマンド一覧");
            player.sendMessage("§a§l/abt help §r§a: §r§aこのヘルプを表示します。");
            player.sendMessage("§a§l/abt list §r§a: §r§aバーテンダーの一覧を表示します。");
            player.sendMessage("§a§l/abt info <バーテンダー名> §r§a: §r§aバーテンダーの情報を表示します。");
            player.sendMessage("§a§l/abt hire <バーテンダー名> §r§a: §r§aバーテンダーを雇います。");
            player.sendMessage("§a§l/abt fire <バーテンダー名> §r§a: §r§aバーテンダーを解雇します。");
            player.sendMessage("§a§l/abt set <バーテンダー名> <アクション> §r§a: §r§aバーテンダーのアクションを設定します。");
            player.sendMessage("§a§l/abt reset <バーテンダー名> §r§a: §r§aバーテンダーのアクションをリセットします。");
            player.sendMessage("§a§l/abt reload §r§a: §r§aAdvancedBartenderをリロードします。");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage("§a§lAdvancedBartender §r§aのバーテンダー一覧");
            for (File bartender : bartenderlist) {
                player.sendMessage("§a§l" + bartender.getName().replace(".yml", ""));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args[1].equalsIgnoreCase("bartender")) {
                if (args.length == 3) {
                    File bartender = new File("plugins/AdvancedBartender/bartender/" + args[2] + ".yml");
                    YamlConfiguration bartenderYml = YamlConfiguration.loadConfiguration(bartender);
                    bartenderYml.set("name", args[2]);
                    bartenderYml.set("action", "wave");
                    try {
                        bartenderYml.save(bartender);
                        player.sendMessage("§a§l" + args[2] + "§r§aを作成しました。");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage("§c§l引数が不正です。");
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("drink")) {
                if (args.length >= 3 || args.length <= 6) {
                    File drink = new File("plugins/AdvancedBartender/drink/" + args[2] + ".yml");
                    ItemStack drinkItem = new ItemStack(Material.POTION);
                    PotionMeta drinkMeta = (PotionMeta) drinkItem.getItemMeta();
                    drinkMeta.setDisplayName(args[2].replace("&", "§"));
                    NamespacedKey key = new NamespacedKey(plugin, "abt");
                    drinkMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "abt");
                    if (args.length == 6) {
                        Color color = Color.fromRGB(Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                        drinkMeta.setColor(color);
                    }
                    drinkItem.setItemMeta(drinkMeta);
                    List<String> action = new ArrayList<>();
                    ItemStack diamond = new ItemStack(Material.DIAMOND);
                    action.add("give " + diamond);
                    action.add("say " + args[2] + "&r§aを飲んで、ダイヤモンドを当てました。");
                    List<String> action2 = new ArrayList<>();
                    action2.add("givemoney 10000");
                    action2.add("serverstock payall");
                    action2.add("say &c&lジャックポット！&r§aを飲んで、10000円とストックを全額獲得しました！");
                    action2.add("broadcast &c&l%player%&r&cが" + args[2] + "&r§cを飲んで、ジャックポットを当てました！");
                    List<String> failaction = new ArrayList<>();
                    failaction.add("serverstock 10");
                    failaction.add("say " + args[2] + "&r§aのストックが10増えました。");
                    List<String> failaction2 = new ArrayList<>();
                    failaction2.add("say &cハズレです。");
                    YamlConfiguration drinkYml = YamlConfiguration.loadConfiguration(drink);
                    drinkYml.set("name", args[2]);
                    drinkYml.set("item", drinkItem);
                    drinkYml.set("price", 100);
                    drinkYml.set("chance", 5);
                    drinkYml.set("bunbo", 100);
                    drinkYml.set("serverstock", 0);
                    drinkYml.set("isserverstockshow", false); //trueでストックを表示する、falseで?????と表示する
                    drinkYml.set("effect", "none"); // 未実装
                    drinkYml.set("action.1.action", action);
                    drinkYml.set("action.1.chance", 1);
                    drinkYml.set("action.2.action", action2);
                    drinkYml.set("action.2.chance", 1);
                    drinkYml.set("failaction.1.action", failaction);
                    drinkYml.set("failaction.1.chance", 1);
                    drinkYml.set("failaction.2.action", failaction2);
                    drinkYml.set("failaction.2.chance", 2);
                    try {
                        drinkYml.save(drink);
                        player.sendMessage("§a§l" + args[2] + "§r§aを作成しました。");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage("§c§l引数が不正です。");
                }
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length >= 3) {
                File bartender = new File("plugins/AdvancedBartender/bartender/" + args[1] + ".yml");
                YamlConfiguration bartenderYml = YamlConfiguration.loadConfiguration(bartender);
                File drink = new File("plugins/AdvancedBartender/drink/" + args[2] + ".yml");
                YamlConfiguration drinkYml = YamlConfiguration.loadConfiguration(drink);
                List<String> drinklist = bartenderYml.getStringList("drink");
                drinklist.add(args[2]);
                bartenderYml.set("drink", drinklist);
                try {
                    bartenderYml.save(bartender);
                    player.sendMessage("§a§l" + args[1] + "§r§aに§a§l" + args[2] + "§r§aを追加しました。");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("open")) {
            if (args.length == 2) {
                File bartender = new File("plugins/AdvancedBartender/bartender/" + args[1] + ".yml");
                YamlConfiguration bartenderYml = YamlConfiguration.loadConfiguration(bartender);
                List<String> drinklist = bartenderYml.getStringList("drink");
                Inventory gui = Bukkit.createInventory(null, 18, "§a§lAdvancedBartender");
                for (String drink : drinklist) {
                    File drinkFile = new File("plugins/AdvancedBartender/drink/" + drink + ".yml");
                    YamlConfiguration drinkYml = YamlConfiguration.loadConfiguration(drinkFile);
                    ItemStack drinkItem = drinkYml.getItemStack("item");
                    ItemMeta drinkMeta = drinkItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add("§a§l" + drinkYml.getString("price") + "円");
                    lore.add("§a§l" + drinkYml.getString("chance") + "%の確率で当たります。");
                    if (drinkYml.getBoolean("isserverstockshow")) {
                        lore.add("§a§lストック: " + drinkYml.getString("serverstock"));
                    } else {
                        lore.add("§a§lストック: ?????");
                    }
                    drinkMeta.setLore(lore);
                    drinkItem.setItemMeta(drinkMeta);
                    gui.addItem(drinkItem);
                }
                player.openInventory(gui);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tab = new ArrayList<>();
        if (!sender.isOp()) {
            return null;
        }
        if (args.length == 1) {
            tab.add("help");
            tab.add("list");
            tab.add("create");
            tab.add("add");
            tab.add("open");
        }
        if (args.length == 2) {
            if (args[0].equals("create")) {
                tab.add("bartender");
                tab.add("drink");
            }
            if (args[0].equals("add")) {
                File[] bartenderlist = new File("plugins/AdvancedBartender/bartender/").listFiles();
                for (File bartender : bartenderlist) {
                    tab.add(bartender.getName().replace(".yml", ""));
                }
            }
            if (args[0].equals("open")) {
                File[] bartenderlist = new File("plugins/AdvancedBartender/bartender/").listFiles();
                for (File bartender : bartenderlist) {
                    tab.add(bartender.getName().replace(".yml", ""));
                }
            }
        }
        if (args.length == 3) {
            if (args[0].equals("add")) {
                File[] drinklist = new File("plugins/AdvancedBartender/drink/").listFiles();
                for (File drink : drinklist) {
                    tab.add(drink.getName().replace(".yml", ""));
                }
            }
        }
        if (args.length == 4) {
            if (args[0].equals("create")) {
                if (args[1].equals("drink")) {
                    tab.add("R");
                }
            }
        }
        if (args.length == 5) {
            if (args[0].equals("create")) {
                if (args[1].equals("drink")) {
                    tab.add("G");
                }
            }
        }
        if (args.length == 6) {
            if (args[0].equals("create")) {
                if (args[1].equals("drink")) {
                    tab.add("B");
                }
            }
        }
        return tab;
    }
}
