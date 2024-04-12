package hydraheadhunter.cmdstats.util;

import net.minecraft.text.Text;

import java.io.File;
import java.util.Collection;

public interface iPlayerProjectSaver {
	Collection<File> getProjectDirectories();
	Collection<File> getPausedProjectDirectories();
	boolean addDirectory    (File directoryToAdd    );
	boolean removeDirectory (File directoryToRemove );
	boolean pauseDirectory  (File directoryToPause  );
	boolean unpauseDirectory(File directoryToUnpause);
	
	boolean resetDirectories    ();
	boolean softResetDirectories();
	
	Text getName();
}
