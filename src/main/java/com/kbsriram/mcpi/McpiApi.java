package com.kbsriram.mcpi;

import org.apache.logging.log4j.Level;

import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = McpiApi.MODID, version = McpiApi.VERSION)
public class McpiApi
{
    private static final int API_PORT = 4711;
    
    public static final String MODID = "com.kbsriram.mcpi";
    public static final String VERSION = "1.0";
    
    MinecraftServer ms;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {	
        // some example code
//        System.out.println("DIRT BLOCK >> "+Blocks.DIRT.getUnlocalizedName());
        FMLLog.getLogger().log(Level.INFO, "McpiApi init");
    }
    
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
    	ms = event.getServer();
    	
        // This exposes a command for the player to run python commands
        // externally. Intended to be used to run mcpi client apps
        // more conveniently.
    	FMLLog.getLogger().log(Level.INFO, "McpiApi onServerStarting");
        event.registerServerCommand(new ExternalPythonCommand(ms));
    }

    // This strange initialization hook is because it seems that the
    // server mod isn't being called in SSP mode.
    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event)
    {
    	FMLLog.getLogger().log(Level.INFO, "McpiApi onServerStarted");
//        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
//            return;
//        }

        // create server thread.
       m_cs = new CommandServer(API_PORT);
       
//
//        // EngineTickHandler runs on every world tick, and
//        // looks for new commands to process.
       MinecraftForge.EVENT_BUS.register
            (new EngineTickHandler(m_cs, ms));
//
//        // PlayerTickHandler runs on every player tick, and
//        // handles any pending player move events.
        MinecraftForge.EVENT_BUS.register
            (new EventCommandHandler.PlayerEventHandler());
//
//
//        // EventCommandHandler.BlockHandler is called on various
//        // block events. Used to save data for event.* commands
        MinecraftForge.EVENT_BUS.register
            (new EventCommandHandler.BlockEventHandler());
//
//
//        // Start up the server thread.
       m_cs.start();
       
       FMLLog.getLogger().log(Level.INFO, "server started");
       
    }
    
    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event)
    {
    	FMLLog.getLogger().log(Level.INFO, "McpiApi onServerStopped");
       if (m_cs != null) {
           try { m_cs.stopServer(); }
           finally {
               m_cs = null;
           }
       }
    }

    private CommandServer m_cs = null;
}
