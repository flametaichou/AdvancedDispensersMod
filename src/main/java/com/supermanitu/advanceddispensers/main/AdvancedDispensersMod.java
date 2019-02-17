package com.supermanitu.advanceddispensers.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.BooleanUtils;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.supermanitu.advanceddispensers.proxies.CommonProxy;
import com.supermanitu.advanceddispensers.user.BlockUser;
import com.supermanitu.advanceddispensers.user.UserConfig;
import com.supermanitu.advanceddispensers.user.TileEntityUser;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = AdvancedDispensersMod.MODID, version = AdvancedDispensersMod.VERSION)
public class AdvancedDispensersMod 
{
	public static final String MODID = "advanceddispensers";
	public static final String VERSION = "1.2.0-b8 for 1.7.2";
	
	@Instance("advanceddispensers")
    public static AdvancedDispensersMod instance;
	
	@SidedProxy(clientSide = "com.supermanitu.advanceddispensers.proxies.ClientProxy", serverSide= "com.supermanitu.advanceddispensers.proxies.CommonProxy")
    public static CommonProxy proxy;
	
	//Blocks
	public static BlockUser blockUser;
	
	//Items
	
	//Misc
	public static CreativeTabs advancedDispensersTab;
	public static int renderID;
	
	//Rest
	
	 @EventHandler
	 public void preInit(FMLPreInitializationEvent event)
	 {
		 Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		 config.load();
		 
		 UserConfig userConfig = new UserConfig(config);
		 
		 //Initialization
		 advancedDispensersTab = new AdvancedDispensersTab("advanceddispenserstabs");
		 
		 proxy.registerRenderers();
		 
		 NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());

		 //User
		 if(userConfig.isEnabled())
		 {
			 blockUser = new BlockUser(userConfig.getTick(), userConfig.isFakePlayerEnabled(), userConfig.getMaxBlockCount());
			 GameRegistry.registerBlock(blockUser, blockUser.getUnlocalizedName().substring(5));
			 GameRegistry.registerTileEntity(TileEntityUser.class, "tileEntityUser");
			 GameRegistry.addShapedRecipe(new ItemStack(blockUser, 1), blockUser.getRecipe());
		 }
		 
		 config.save();
	 }

	@EventHandler
	 public void init(FMLInitializationEvent event)
	 {
		 
	 }
	 
	 @EventHandler
	 public void postInit(FMLPostInitializationEvent event)
	 {
		 
	 }
	 
	 private boolean isActualVersion()
	 {
		 URL url = null;
		 try 
		 {
			 url = new URL("http://www.wasdgames.de/Version/advanceddispensers.html");
		 } 
		 catch (MalformedURLException e) 
		 {
			 System.err.println(I18n.format("error.versionCheck"));
			 e.printStackTrace();
			 return true;
		 }

		 InputStreamReader is = null;
		 try 
		 {
			 is = new InputStreamReader(url.openStream());
		 }
		 catch (IOException e) 
		 {
			 System.out.println(I18n.format("error.versionCheck"));
			 return true;
		 }
		 BufferedReader in = new BufferedReader(is);

		 try 
		 {
			 if(VERSION.contains(in.readLine()))
			 {
				 in.close();
				 is.close();
				 return true;
			 }
		 } 
		 catch (IOException e) 
		 {
			 System.out.println(I18n.format("error.versionCheck"));
			 e.printStackTrace();
			 return true;
		 }

		 try 
		 {
			 in.close();
			 is.close();
		 }
		 catch (IOException e)
		 {
			 System.out.println(I18n.format("error.versionCheck"));
			 e.printStackTrace();
			 return true;
		 }

		 return false;
	 }
}
