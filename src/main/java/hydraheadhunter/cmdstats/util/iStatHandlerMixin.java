package hydraheadhunter.cmdstats.util;

import java.io.File;
import java.util.Collection;

public interface iStatHandlerMixin {
     
     public Collection<File> getProjectDirectories();
     public boolean          addDirectory( File directory);
}
