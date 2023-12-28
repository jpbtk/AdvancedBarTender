package github.io.jpbtk.advancedbartender;

import github.io.jpbtk.advancedbartender.Commands.ABT;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdvancedBarTender extends JavaPlugin {
    public static AdvancedBarTender plugin;
    public static final String prefix = "§7[§6AdvancedBarTender§7]§r ";
    private Listeners listeners;
    public static Economy econ = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        try {
            getCommand("advancedbartender").setExecutor(new ABT());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            this.listeners = new Listeners();
        } catch (Exception e) {
            getLogger().severe("Listenersのインスタンス化に失敗しました。");
            throw new RuntimeException(e);
        }
        getServer().getPluginManager().registerEvents(this.listeners, this);
        if(!setupEconomy()){
            getLogger().severe("Vaultが見つかりませんでした。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        plugin.getLogger().info(prefix + "§aプラグインが有効になりました。");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        plugin.getLogger().info(prefix + "§cプラグインが無効になりました。");
        super.onDisable();
    }
    private static Boolean setupEconomy() {
        if(getPlugin().getServer().getPluginManager().getPlugin("Vault") == null){
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null){
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static AdvancedBarTender getPlugin(){
        return plugin;
    }
}
