package hydraheadhunter.cmdstats.mixin;

import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
import net.minecraft.client.sound.Sound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.SayCommand;
import net.minecraft.stat.ServerStatHandler;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Collection;

import static hydraheadhunter.cmdstats.CommandStatistics.MOD_ID;

@Mixin(ServerStatHandler.class)
public class ExampleMixin_2 implements iStatHandlerMixin {
	public Collection<File> projectDirectories;
	
	@Override
	public Collection<File> getProjectDirectories() {
		return projectDirectories;
	}
	
	@Override
	public boolean addDirectory(File directoryToAdd){
		if (projectDirectories.contains(directoryToAdd))
			return false;
		projectDirectories.add(directoryToAdd);
		return true;
	}
	
	@Inject(method = "save", at = @At("HEAD") )
	private void save(CallbackInfo info) {
		Logger logger = LoggerFactory.getLogger(MOD_ID);
		try{
		for(File directory:projectDirectories)
		{
			logger.info(directory.getName());
		}
		}
		catch (NullPointerException e){
			logger.info("This player has no projects");
		}
		logger.info("Mixin Test");
	}
}