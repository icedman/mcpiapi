package com.kbsriram.mcpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
//import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLLog;

final class ExternalPythonCommand implements ICommand {
	public static MinecraftServer ms = null;

	public ExternalPythonCommand(MinecraftServer s) {
		// TODO Auto-generated constructor stub
		ms = s;
	}

	@Override
	public int compareTo(ICommand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		{
			return "python command [args...]";
		}
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		List<String> l = new ArrayList<String>();
		l.add("py");
		l.add("python");
		return l;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		// TODO Auto-generated method stub
		FMLLog.getLogger().log(Level.INFO, "execute:" + args);

		if (args.length == 0) {
			sendError(sender, "Invalid arguments");
			return;
		}

		if (!s_safearg.matcher(args[0]).matches()) {
			sendError(sender, "Invalid module name: `" + args[0] + "'");
			return;
		}

		// Check file exists.
		File moddir;
		try {
			moddir = (new File("mcpimods/python")).getCanonicalFile();
		} catch (IOException ioe) {
			sendError(sender, "Unable to get full path: `" + ioe.getMessage() + "'");
			return;
		}
		File target = new File(moddir, args[0] + ".py");

		if (!target.canRead()) {
			sendError(sender, "Could not find command: `" + target + "'");
			return;
		}

		// Set up the command line appropriately.
		ArrayList<String> sargs = new ArrayList<String>();
		sargs.add("python");
		sargs.add(target.toString());
		for (int i = 1; i < args.length; i++) {
			sargs.add(args[i]);
		}

		// Set up the process with a suitable working directory, and reset
		// stderr to stdio
		ProcessBuilder pb = new ProcessBuilder(sargs);
		pb.directory(moddir);
		pb.redirectErrorStream(true);
		Process p;
		try {
			p = pb.start();
		} catch (IOException ioe) {
			sendError(sender, "Could not start python: " + ioe.getMessage());
			return;
		}
		// Drain its stdout/stderr stream.
		Thread drainer = new Drainer(p.getInputStream(), args[0]);
		drainer.setDaemon(true);
		drainer.start();

	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	private final static void sendError(ICommandSender sender, String m) {
		// sender.addChatMessage(new ChatComponentText(m));
		ms.sendMessage(new TextComponentString(m));
	}

	private final static Pattern s_safearg = Pattern.compile("[a-zA-Z0-9_]+");

	// used to absorb stdout/stderr in a separate thread.
	private final static class Drainer extends Thread {
		private Drainer(InputStream in, String id) {
			super("drain-" + id);
			m_in = in;
			m_id = id;
		}

		@Override
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(m_in));
				String line;
				while ((line = br.readLine()) != null) {
					System.out.println(m_id + ">" + line);
				}
			} catch (Throwable th) {
				th.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (Throwable ign) {
					}
				}
			}
		}

		private final InputStream m_in;
		private final String m_id;
	}
}
