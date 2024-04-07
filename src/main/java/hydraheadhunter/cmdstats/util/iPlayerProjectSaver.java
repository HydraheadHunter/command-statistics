package hydraheadhunter.cmdstats.util;

import com.ibm.icu.impl.ICUBinary;
import net.minecraft.text.Text;

import java.io.File;
import java.util.Collection;

public interface iPlayerProjectSaver {
    Collection<File> getProjectDirectories();
    boolean addDirectory   (File directoryToAdd);
    boolean removeDirectory(File directoryToAdd);

    Text getName();
}
