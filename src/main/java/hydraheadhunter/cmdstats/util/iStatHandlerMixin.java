package hydraheadhunter.cmdstats.util;

import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.StatHandler;

import java.io.File;import java.util.Collection;

public interface iStatHandlerMixin {
    void updateProjectStatHandlers   (Collection<File> projectDirectories, Collection<File> pausedDirectories );
    
    ServerStatHandler getProjectStatHandler(File file);
    boolean isProjectActive(ServerStatHandler statHandler);
    File getFile();
}
