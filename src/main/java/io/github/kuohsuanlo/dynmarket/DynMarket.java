
package io.github.kuohsuanlo.dynmarket;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
public class DynMarket extends JavaPlugin  implements CommandExecutor {
    private static final Logger log = Logger.getLogger("Minecraft");
   
    public static Economy econ = null;
    public final int max_id= 453;
    public int anchor_id = 264; 
    public double selling_tax_rate = 0.0;
    public double buying_tax_rate = 0.0;
    private mmaterial[] mobj ;
    private FileConfiguration config;


    
    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        savePrice();
    }
    
    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
        	reportMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        readConfig();
        loadPrice();
        
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender; 
        
        if(command.getName().equalsIgnoreCase("dsell")) {
        	if (sender instanceof Player) {
        		int id = 0;
				int data = 0;
				int sold_number = 0;
        		if (args.length == 0 ) {
					id = player.getItemInHand().getType().getId();
					data = player.getItemInHand().getData().getData();
					sold_number = player.getItemInHand().getAmount();
        		}
        		else if (args.length==1  ) {
    		    	if(args[0].equals("hand")){
    					id = player.getItemInHand().getType().getId();
    					data = player.getItemInHand().getData().getData();
    					sold_number = player.getItemInHand().getAmount();
        			}
        		}
        		
				if(id<=max_id  &&  id>0){
					double total_price =0;
					for(int i=0;i<sold_number;i++){
						player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
						mobj[id].mpool_number[data]+=1;
						double price = ((double)mobj[anchor_id].mpool_number[0]/mobj[id].mpool_number[data])*(1-selling_tax_rate);
						total_price +=price;
						EconomyResponse r_receive = econ.depositPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), price);  
					}

					BigDecimal bd= new BigDecimal(total_price);   
					bd=bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					

		    		BigDecimal pr= new BigDecimal(econ.getBalance(player));   
		    		pr=pr.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					player.sendMessage(ChatColor.GREEN+this.getQuotedPluginName()+ChatColor.GRAY+" : "+"You sold "+id+":"+data+" * "+sold_number+" for total price : "+ChatColor.GOLD+bd+ChatColor.GRAY+". "+mobj[id].mpool_number[data]+" left in stock.");
					player.sendMessage(ChatColor.GREEN+this.getQuotedPluginName()+ChatColor.GRAY+" : "+"You now have $"+pr);
					this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN+this.getQuotedPluginName()+ChatColor.GRAY+" : "+player.getName()+" sold "+id+":"+data+" * "+sold_number+" for total price : "+ChatColor.GOLD+bd+ChatColor.GRAY+". "+mobj[id].mpool_number[data]+" left in stock.");
				}
				
		    	
		    }
        }
        else if(command.getName().equalsIgnoreCase("dbuy")) {
	    	if (sender instanceof Player) {
	    		int id=0;
	    		int data=0;
	    		int number=0;
	    		if (args.length == 2 ) {
	    			if(args[0].equals("hand")){
						id = player.getItemInHand().getType().getId();
						data = player.getItemInHand().getData().getData();
	    			}
	    			else{
	    				String id_data[] = args[0].split(":");
		    			if(id_data.length==1){
		    				id=Integer.valueOf(id_data[0]);
		    				data = 0;
		    			}
		    			else if(id_data.length==2){
		    				id=Integer.valueOf(id_data[0]);
		    				data = Integer.valueOf(id_data[1]);
		    			}
	    			}
	    			
	    			number = Integer.valueOf( args[1]);
		    	}
	    		else if (args.length == 1 ) {
	    			if(args[0].equals("hand")){
						id = player.getItemInHand().getType().getId();
						data = player.getItemInHand().getData().getData();
	    			}
	    			else{
		    			String id_data[] = args[0].split(":");
		    			if(id_data.length==1){
		    				id=Integer.valueOf(id_data[0]);
		    				data = 0;
		    			}
		    			else if(id_data.length==2){
		    				id=Integer.valueOf(id_data[0]);
		    				data = Integer.valueOf(id_data[1]);
		    			}
	    			}
	    			number=1;
		    	}
	    		else if(args.length==0){
	    			id = player.getItemInHand().getType().getId();
					data = player.getItemInHand().getData().getData();
					number=1;
	    		}
	    		if(id>0){
	    			if(mobj[id].mpool_number[data]<=0){
		    			player.sendMessage(ChatColor.GREEN+this.getQuotedPluginName()+ChatColor.GRAY+" : "+id+":"+data+" * "+mobj[id].mpool_number[data]+" left in stock.");
		    		}
		    		else{
			    		double total_price=0;
			    		int sold_number=0;
			    		for(int i =0;i<number;i++){
			    			if(mobj[id].mpool_number[data]>0){
			    				double price = ((double)mobj[anchor_id].mpool_number[0]/mobj[id].mpool_number[data])*(1+buying_tax_rate);
			    				total_price += price;
			    				mobj[id].mpool_number[data]--;
			    				sold_number++;
			    				EconomyResponse r_receive = econ.withdrawPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), price);  
			    				
			    				if(!r_receive.transactionSuccess()){
			    					sold_number--;
			    					mobj[id].mpool_number[data]++;
			    					total_price -=price;
			    					break;
			    				}
			    			}
			    			else{
			    				break;
			    			}
			    			
			    		}
			    		BigDecimal bd= new BigDecimal(total_price);   
						bd=bd.setScale(2, BigDecimal.ROUND_HALF_UP);
						

			    		BigDecimal pr= new BigDecimal(econ.getBalance(player));   
			    		pr=pr.setScale(2, BigDecimal.ROUND_HALF_UP);
			    		

	    				ItemStack IS = new  ItemStack( id,sold_number,(short)0,(byte)data);
	    				player.getInventory().addItem(IS);
			    		
						player.sendMessage(ChatColor.GREEN+this.getQuotedPluginName()+ChatColor.GRAY+" : "+"You bought "+id+":"+data+" * "+sold_number+" for total price : "+ChatColor.GOLD+bd+ChatColor.GRAY+". "+mobj[id].mpool_number[data]+" left in stock.");
						player.sendMessage(ChatColor.GREEN+this.getQuotedPluginName()+ChatColor.GRAY+" : "+"You now have $"+pr);
						this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN+this.getQuotedPluginName()+ChatColor.GRAY+" : "+player.getName()+" bought "+id+":"+data+" * "+sold_number+" for total price : "+ChatColor.GOLD+bd+ChatColor.GRAY+". "+mobj[id].mpool_number[data]+" left in stock.");
		    		}
	    		}
	    		
		    }
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
    
   
    private String PRICE_FILE_LOCATION = "./plugins/"+this.getPluginName()+"/current_price.yml";
    private static boolean createFolder(String path){
   	 
  	   	File file = new File(path);
  	   	if(!file.exists()){
  	   		new File(path).mkdirs();
  	   		return true;
  	   	}
  	   	return false;

    }  
    private void loadPrice(){
    	this.mobj = new mmaterial[this.max_id+1];
    	for(int i=0;i<=this.max_id;i++){
    		int[] pool_num= new int[16];
        	for(int d=0;d<16;d++){
        		pool_num[d] = 100;
        	}
    		this.mobj[i] = new mmaterial(i, pool_num);
    	}
    	try {
      	   	String path = PRICE_FILE_LOCATION;
      	   	File file = new File(path);
      	   	if(file.exists()){
  	   		BufferedReader in = new BufferedReader(new FileReader(path));
  	   		String line = "";
  	   		while ((line = in.readLine()) != null) {
  	   			
  	   			String parts[] = line.split("/");
  	   			String id_data[] = parts[0].split(":");
  	   			int id = Integer.valueOf(id_data[0]);
  	   			int data = Integer.valueOf(id_data[1]);
  	   			this.mobj[id].mpool_number[data] = Integer.valueOf(parts[1]);
  	   		}
  	   		in.close();
      	   	
            
      	   	}
   		} catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
    }
    private void savePrice(){
    	
    	try {
      	   	File file = new File(PRICE_FILE_LOCATION);
     	    file.createNewFile();
     	   
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for(int i=0;i<=this.max_id;i++){
        		
        		for(int d=0;d<16;d++){
 	               bw.write(mobj[i].id+":"+d+ "/"+mobj[i].mpool_number[d]);
 	               bw.newLine();
            	}
        	}
            
		
	      		 
			
            bw.flush();
 			bw.close();
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    }
    private void readConfig(){
    	 
        // Register our events

        createFolder("./plugins/"+this.getPluginName());
        PluginManager pm = getServer().getPluginManager();

    	config = this.getConfig();
    	config.addDefault("ANCHOR_ITEM_ID", 264);
    	config.addDefault("SELLING_TAX_RATE", 0.07);
    	config.addDefault("BUYING_TAX_RATE", 0.03);
    	config.options().copyDefaults(true);
    	saveConfig();
    
    	config.options().copyDefaults(true);
    	
    	selling_tax_rate = config.getDouble("SELLING_TAX_RATE");
    	buying_tax_rate = config.getDouble("BUYING_TAX_RATE");
    	anchor_id = config.getInt("ANCHOR_ITEM_ID");
    
    }

}
