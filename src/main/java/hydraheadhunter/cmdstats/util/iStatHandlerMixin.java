package hydraheadhunter.cmdstats.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;

import java.io.File;
import java.util.Collection;

public interface iStatHandlerMixin {
    boolean addDirectory(File directoryToAdd, String playerName);
    boolean removeDirectory(File directoryToRemove, String playerName);
    File getFile();
}
