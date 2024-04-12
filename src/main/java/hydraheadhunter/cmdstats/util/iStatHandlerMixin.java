package hydraheadhunter.cmdstats.util;

import net.minecraft.stat.ServerStatHandler;

import java.io.File;

public interface iStatHandlerMixin {
    boolean addStatHandler    (File directoryToAdd    , String playerName);
    boolean removeStatHandler (File directoryToRemove , String playerName);
    boolean pauseStatHandler  (ServerStatHandler directoryToPause  , String playerName);
    boolean unpauseStatHandler(ServerStatHandler directoryToUnpause, String playerName);
    
    boolean resetStatHandler    ();
    boolean softResetStatHandlers();
    
    ServerStatHandler getProjectStatHandler(File file);
    File getFile();
}
