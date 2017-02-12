package com.kbsriram.mcpi;


import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class ChatCommandHandler
{
    public final static class Post
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            StringBuilder sb = new StringBuilder();
            String[] args = cmd.getArgs();
            for (int i=0; i<args.length; i++) {
                if (i > 0) { sb.append(","); }
                sb.append(args[i]);
            }
            
            MinecraftServer ms = ws.getMinecraftServer();
            ms.sendMessage(new TextComponentString(sb.toString()));

//            FMLLog.getLogger().log(Level.INFO, "McpiApi chat:" + sb.toString());
            return VOID;
        }
    }
}
