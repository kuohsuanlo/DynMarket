
package io.github.kuohsuanlo.dynmarket;


import java.io.File;
import java.util.logging.Logger;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
/**
 * Sample plugin for Bukkit
 *
 * @author Dinnerbone
 */
public class DynMarket extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
   
    public static Economy econ = null;
    public final int max_id= 453;
    public int anchor_id = 264; 
    private mmaterial[] mobj ;
    private FileConfiguration config;


    
    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }
    
    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
        	reportMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
       
        readConfig();
        setupPriceMarket();
        
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender; 
        
        if(command.getLabel().equals("dynsell")) {
        	
        }
        else if(command.getLabel().equals("dynbuy")) {
            
        	
        }

		return false;
    }
    
    
    public String getPluginName() {
		return getDescription().getName();
	}
	private String getQuotedPluginName() {
		return "[" + getPluginName() + "]";
	}
	public void reportMessage(String message) {
		if (!message.startsWith("["))
			message = " " + message;
		log.info(getQuotedPluginName() + message);
	}
	public void reportMessage(String message1, String message2) {
		reportMessage(message1);
		log.info(" \\__" + message2);
	}

    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private void setupPriceMarket(){
    	this.mobj = new mmaterial[this.max_id+1];
    	
    	int[] pool_num= new int[16];
    	double[] price_num = new double[16];
    	for(int i=0;i<16;i++){
    		pool_num[i] = 100;
    		price_num[i] = 100;
    	}
    	
    	//Anchor item
    	price_num[anchor_id]= 1;
    	
    	for(int i=0;i<=this.max_id;i++){
    		this.mobj[i] = new mmaterial(i, price_num, pool_num);
    	}
    }

    private void readConfig(){
    	 
        // Register our events
        PluginManager pm = getServer().getPluginManager();

    	config = this.getConfig();
    	config.addDefault("ANCHOR_ITEM_ID", 264);
    	config.options().copyDefaults(true);
    	saveConfig();
    
    	config.options().copyDefaults(true);
    }

}
