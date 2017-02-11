
package io.github.kuohsuanlo.paytax;


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
public class PayTaxPlugin extends JavaPlugin {
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    
    private static final Logger log = Logger.getLogger("Minecraft");
    private double TAX_RATE = 0.07;
    public static Economy econ = null;
    public static String TAX_ACCOUNT;
    public static String YOU_SUCCESSFULLY_PAID;
    public static String WITH_THE_TAX_RATE;
    public static String TOTAL_EXTRA_FREE;
	public static String YOUR_CURRENT_BALANCE;
	public static String TRANSACTION_ERROR;
	public static String TRANSACTION_SUCCESS;
	public static String YOU_GOT_THE_MONEY_FROM;
	public static String WITH_THE_AMOUNT;
	public static String NOT_A_NUMBER;
	public static String NOT_ENOUGH_MONEY;
	public static String NO_SUCH_ID;
    private FileConfiguration config;
    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register our events
        PluginManager pm = getServer().getPluginManager();

    	config = this.getConfig();
    	config.addDefault("version","1.0.0");
    	config.addDefault("TAX_RATE",0.07);
    	config.addDefault("TAX_ACCOUNT","tax");
    	
    	config.addDefault("YOU_SUCCESSFULLY_PAID","You paied ");
    	config.addDefault("WITH_THE_TAX_RATE","with the tax rate ");
    	config.addDefault("TOTAL_EXTRA_FREE","Total extra fee ");
    	config.addDefault("YOUR_CURRENT_BALANCE","You now have ");
    	config.addDefault("TRANSACTION_ERROR","§a[PayTax] : Transcation success.§r");
    	config.addDefault("TRANSACTION_SUCCESS","§a[PayTax] : §cTranscation error.§r");
    	config.addDefault("YOU_GOT_THE_MONEY_FROM","You got the money from");
    	config.addDefault("WITH_THE_AMOUNT","with the amount ");
    	config.addDefault("NOT_A_NUMBER","Not a valid number.");
    	config.addDefault("NOT_ENOUGH_MONEY","You can't afford the amount after taxing : ");
    	config.addDefault("NO_SUCH_ID", "Player not found. Please use /payoff ID amount");
    	config.options().copyDefaults(true);
    	saveConfig();
    	
    	TAX_RATE = config.getDouble("TAX_RATE");
    	TAX_ACCOUNT = config.getString("TAX_ACCOUNT");
    	YOU_SUCCESSFULLY_PAID = config.getString("YOU_SUCCESSFULLY_PAID");
    	WITH_THE_TAX_RATE = config.getString("WITH_THE_TAX_RATE");
    	TOTAL_EXTRA_FREE = config.getString("TOTAL_EXTRA_FREE");
    	YOUR_CURRENT_BALANCE = config.getString("YOUR_CURRENT_BALANCE");
    	TRANSACTION_ERROR = config.getString("TRANSACTION_ERROR");
    	TRANSACTION_SUCCESS = config.getString("TRANSACTION_SUCCESS");
    	YOU_GOT_THE_MONEY_FROM = config.getString("YOU_GOT_THE_MONEY_FROM");
    	WITH_THE_AMOUNT = config.getString("WITH_THE_AMOUNT");
    	NOT_A_NUMBER = config.getString("NOT_A_NUMBER");
    	NOT_ENOUGH_MONEY = config.getString("NOT_ENOUGH_MONEY");
    	NO_SUCH_ID = config.getString("NO_SUCH_ID");
    	config.options().copyDefaults(true);
    	
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender; 
        
        if(command.getLabel().equals("payoff")) {
                    
        	if (split.length == 2) {
                try {
                    String receiver_name = split[0];
                    double tranfered_amount= Double.parseDouble(split[1]);
                    double tranfered_amount_taxed= Double.parseDouble(split[1])*(1.00+TAX_RATE);
                    

                    if (econ.has(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed)){//金錢足夠
                    	EconomyResponse r_send = econ.withdrawPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed);
                    	sender.sendMessage(TRANSACTION_SUCCESS+YOU_SUCCESSFULLY_PAID+receiver_name+" "+econ.format(tranfered_amount)+WITH_THE_TAX_RATE+ Math.round(TAX_RATE*100) +"%\n"+TOTAL_EXTRA_FREE+econ.format(tranfered_amount*TAX_RATE));
                    	sender.sendMessage(YOUR_CURRENT_BALANCE+ econ.format(econ.getBalance(player)));
                    	
                    	//sender.sendMessage("§a轉帳成功: 你成功的支付 玩家§6"+receiver_name+" §a"+econ.format(tranfered_amount)+",並額外§c扣除"+ Math.round(TAX_RATE*100) +"% 手續費§a "+econ.format(tranfered_amount*TAX_RATE));
                    	//sender.sendMessage("§a你目前的餘額為  : "+ econ.format(econ.getBalance(player)));
                    	EconomyResponse r_receive = econ.depositPlayer( this.getServer().getOfflinePlayer(receiver_name), tranfered_amount);
                        
                    	
                		//sender.sendMessage("Debug  : "+ this.getServer().getOfflinePlayer(receiver_name));
                		//sender.sendMessage("Debug  : "+ this.getServer().getOfflinePlayer(receiver_name).getUniqueId());
                	    //sender.sendMessage("Debug  : "+ this.getServer().getPlayer(receiver_name));
                		//sender.sendMessage("Debug  : "+ this.getServer().getPlayer(receiver_name).getUniqueId());
                    	
                    	
                    	if(r_receive.transactionSuccess()  &&  r_send.transactionSuccess() ){
                        	if(this.getServer().getPlayer(receiver_name)!=null){
                        		this.getServer().getPlayer(receiver_name).sendMessage(TRANSACTION_SUCCESS+YOU_GOT_THE_MONEY_FROM+player.getName()+WITH_THE_AMOUNT+econ.format(r_receive.amount));
                        		this.getServer().getPlayer(receiver_name).sendMessage(YOUR_CURRENT_BALANCE+ econ.format(r_receive.balance));
                        		//this.getServer().getPlayer(receiver_name).sendMessage("§a轉帳成功: 你收到了來自 玩家§6"+player.getName()+" §a的金額 "+econ.format(r_receive.amount));
                        		//this.getServer().getPlayer(receiver_name).sendMessage("§a你目前餘額為 "+ econ.format(r_receive.balance));
                        		EconomyResponse r_tax = econ.depositPlayer(this.getServer().getOfflinePlayer(TAX_ACCOUNT),  tranfered_amount*TAX_RATE);
                        		//EconomyResponse r_tax = econ.depositPlayer(this.getServer().getOfflinePlayer(this.getServer().getPlayer("tax").getUniqueId()),  tranfered_amount*TAX_RATE);
                             }
                        }
                        else {
                        	sender.sendMessage(TRANSACTION_ERROR+NOT_A_NUMBER);
                        	sender.sendMessage(YOUR_CURRENT_BALANCE+ econ.format(econ.getBalance(player)));
                        	//sender.sendMessage("§c轉帳錯誤: 未知的金額錯誤");
                        	EconomyResponse r_withdraw = econ.depositPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed);
                        	
                        }
                    }
                    else {
                    	//sender.sendMessage("§c[PayTax] : Transcation err: You can't afford the amount after taxing : "+econ.format(tranfered_amount_taxed)+".");
                    	sender.sendMessage(TRANSACTION_ERROR+NOT_ENOUGH_MONEY+WITH_THE_AMOUNT+econ.format(tranfered_amount_taxed));
                    	sender.sendMessage(YOUR_CURRENT_BALANCE+ econ.format(econ.getBalance(player)));
                    	//r_withdraw = econ.depositPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed);
                    }
            		
            	
                    	
                    	
                    
                    
                    
                } catch (NumberFormatException ex) {
                	//player.sendMessage("§c[PayTax] : Transcation err: not a proper number of money");
                	player.sendMessage(TRANSACTION_ERROR+NOT_A_NUMBER);
                }
            }
        }
        else if(command.getLabel().equals("payon") || command.getLabel().equals("pay")  ||   command.getLabel().equals("paytax")  ){
        	if (split.length == 2) {
                try {
                    String receiver_name = split[0];
                    double tranfered_amount= Double.parseDouble(split[1]);
                    double tranfered_amount_taxed= Double.parseDouble(split[1])*(1.00+TAX_RATE);
                    
                    	if(this.getServer().getPlayer(receiver_name)==null){
                		//if(econ.hasAccount(this.getServer().getOfflinePlayer(receiver_name))){
                        	//sender.sendMessage("§c[PayTax] : Transcation err: Please reassure the receiver's ID. Is he or she online? Use /payoff to transfer money to offline players");
                        	//sender.sendMessage("§c轉帳錯誤: 請確認玩家ID是否輸入錯誤,或是不在線上。 如果想要轉帳給離線玩家,請使用  §e/payoff ID 金額  §c(請仔細確認ID)");
                        	//sender.sendMessage("§a[PayTax] : You now have "+ econ.format(econ.getBalance(player)));
                    		sender.sendMessage(TRANSACTION_ERROR+ NO_SUCH_ID);
                    		sender.sendMessage(YOUR_CURRENT_BALANCE+ econ.format(econ.getBalance(player)));
                        	
                    	}
                    	else{//ID存在
                            if (econ.has(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed)){//金錢足夠
                            	EconomyResponse r_send = econ.withdrawPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed);
                            	sender.sendMessage(TRANSACTION_SUCCESS+YOU_SUCCESSFULLY_PAID+receiver_name+" "+econ.format(tranfered_amount)+WITH_THE_TAX_RATE+ Math.round(TAX_RATE*100) +"%\n"+TOTAL_EXTRA_FREE+econ.format(tranfered_amount*TAX_RATE));
                            	sender.sendMessage(YOUR_CURRENT_BALANCE+ econ.format(econ.getBalance(player)));
                            	
                            	//sender.sendMessage("§a[PayTax] : Transcation success. You paied §6"+receiver_name+" §a"+econ.format(tranfered_amount)+", and§c being taxed "+Math.round(TAX_RATE*100)+"% :§a "+econ.format(tranfered_amount*TAX_RATE));
                            	//sender.sendMessage("§a[PayTax] : You now have "+ econ.format(econ.getBalance(player)));
                            	//sender.sendMessage("§a轉帳成功: 你成功的支付 玩家§6"+receiver_name+" §a"+econ.format(tranfered_amount)+",並額外§c扣除"+ Math.round(TAX_RATE*100) +"% 手續費§a "+econ.format(tranfered_amount*TAX_RATE));
                            	//sender.sendMessage("§a你目前的餘額為  : "+ econ.format(econ.getBalance(player)));
                            	//EconomyResponse r_receive = econ.depositPlayer(this.getServer().getOfflinePlayer(this.getServer().getPlayer(receiver_name).getUniqueId()), tranfered_amount);
                            	EconomyResponse r_receive = econ.depositPlayer( this.getServer().getOfflinePlayer(receiver_name), tranfered_amount);
                                
                            	
                        		//sender.sendMessage("Debug  : "+ this.getServer().getOfflinePlayer(receiver_name));
                        		//sender.sendMessage("Debug  : "+ this.getServer().getOfflinePlayer(receiver_name).getUniqueId());
                        	    //sender.sendMessage("Debug  : "+ this.getServer().getPlayer(receiver_name));
                        		//sender.sendMessage("Debug  : "+ this.getServer().getPlayer(receiver_name).getUniqueId());
                            	

                            	if(r_receive.transactionSuccess()  &&  r_send.transactionSuccess() ){
                                	if(this.getServer().getPlayer(receiver_name)!=null){
                                		this.getServer().getPlayer(receiver_name).sendMessage(TRANSACTION_SUCCESS+YOU_GOT_THE_MONEY_FROM+player.getName()+WITH_THE_AMOUNT+econ.format(r_receive.amount));
                                		this.getServer().getPlayer(receiver_name).sendMessage(YOUR_CURRENT_BALANCE+ econ.format(r_receive.balance));
                                		//this.getServer().getPlayer(receiver_name).sendMessage("§a轉帳成功: 你收到了來自 玩家§6"+player.getName()+" §a的金額 "+econ.format(r_receive.amount));
                                		//this.getServer().getPlayer(receiver_name).sendMessage("§a你目前餘額為 "+ econ.format(r_receive.balance));
                                		EconomyResponse r_tax = econ.depositPlayer(this.getServer().getOfflinePlayer(TAX_ACCOUNT),  tranfered_amount*TAX_RATE);
                                		//EconomyResponse r_tax = econ.depositPlayer(this.getServer().getOfflinePlayer(this.getServer().getPlayer("tax").getUniqueId()),  tranfered_amount*TAX_RATE);
                                     }
                                }
                                else {
                                	sender.sendMessage(TRANSACTION_ERROR+NOT_A_NUMBER);
                                	sender.sendMessage(YOUR_CURRENT_BALANCE+ econ.format(econ.getBalance(player)));
                                	//sender.sendMessage("§c轉帳錯誤: 未知的金額錯誤");
                                	EconomyResponse r_withdraw = econ.depositPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed);
                                	
                                }
                            }
                            else {
                            	//sender.sendMessage("§c[PayTax] : Transcation err: You can't afford the amount after taxing : "+econ.format(tranfered_amount_taxed)+".");
                            	sender.sendMessage(TRANSACTION_ERROR+NOT_ENOUGH_MONEY+WITH_THE_AMOUNT+econ.format(tranfered_amount_taxed));
                            	sender.sendMessage(YOUR_CURRENT_BALANCE+ econ.format(econ.getBalance(player)));
                            	//r_withdraw = econ.depositPlayer(this.getServer().getOfflinePlayer(player.getUniqueId()), tranfered_amount_taxed);
                            }
                    		
                    		
                    	}
                    	
                    	
                    
                    
                    
                } catch (NumberFormatException ex) {
                	//player.sendMessage("§c[PayTax] : Transcation err: not a proper number of money");
                	player.sendMessage(TRANSACTION_ERROR+NOT_A_NUMBER);
                }
            }
        }

		return false;
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


}
