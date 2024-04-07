package hydraheadhunter.cmdstats.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import hydraheadhunter.cmdstats.util.iPlayerProjectSaver;
import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Mixin(ServerStatHandler.class)
public abstract class ServerStatHandlerMixin extends StatHandler implements iStatHandlerMixin{
	private Collection<ServerStatHandler> projectStatHandlers;
	@Shadow private MinecraftServer server;
	@Shadow private File file;
	private static Logger MIXIN_LOGGER = LogUtils.getLogger();

	protected ServerStatHandlerMixin(MinecraftServer server, File file) {		super();
    }

	@Override
	public boolean addDirectory(File directoryToAdd, String playerName) {
		if (projectStatHandlers == null) projectStatHandlers = new ArrayList<ServerStatHandler>();

		String directoryString = directoryToAdd.toString();
		String fileName = file.getName();
		String fileToAddName = directoryString + "\\" +fileName;

		ServerStatHandler handlerToAdd = new ServerStatHandler(server, new File(fileToAddName));
		projectStatHandlers.add(handlerToAdd);
		MIXIN_LOGGER.info( playerName + " had a project StatHandler associated with " + fileToAddName +" added.");
		return true;
	}

	@Override
	public boolean removeDirectory(File directoryToRemove, String playerName) {
		if (projectStatHandlers == null || projectStatHandlers.size()<1){
			projectStatHandlers = new ArrayList<ServerStatHandler>();
			MIXIN_LOGGER.info( playerName + "had no project StatHandlers. There was nothing to remove.");
			return false;
		}

		String directoryString  = directoryToRemove.toString();
		String fileName         = file.getName();
		String fileToRemovePath = directoryString + "\\" +fileName;
		File   fileToRemove     = new File(fileToRemovePath);

		if(projectStatHandlers.size()==1) {
			ServerStatHandler handler = (ServerStatHandler) projectStatHandlers.toArray()[0];
			iStatHandlerMixin iHandler = (iStatHandlerMixin) ((StatHandler) handler);
MIXIN_LOGGER.info(iHandler.getFile().getName());
MIXIN_LOGGER.info(fileToRemovePath);
			if (iHandler.getFile().getName().equals(fileToRemove.getName())) {
				projectStatHandlers.remove(handler);
				MIXIN_LOGGER.info( playerName +"had the project StatHandler associated with " + fileToRemovePath +" removed.");
				MIXIN_LOGGER.info( "They have no active projects remaining");
				return true;
			}
		}
		else{
			for (ServerStatHandler handlerItt : projectStatHandlers) {
				iStatHandlerMixin iHandlerItt = (iStatHandlerMixin) ((StatHandler) handlerItt);
				if (iHandlerItt.getFile().getName().equals(fileToRemove.getName())) {
					projectStatHandlers.remove(handlerItt);
					MIXIN_LOGGER.info( playerName +"had the project StatHandler associated with " + fileToRemovePath +" removed.");
					MIXIN_LOGGER.info( "They have " + projectStatHandlers.size() + " active projects remaining.");
					return true;
				}
			}
		}

		MIXIN_LOGGER.info( playerName + " did not have a project StatHandler associated with: " + fileToRemovePath);
		return false;
	}

	public File getFile (){ return this.file;}

	@Inject( method= "save", at=@At("HEAD"))
	public void injectSave(CallbackInfo info) {
		if( projectStatHandlers != null && !projectStatHandlers.isEmpty()){
			for (ServerStatHandler projectHandler: projectStatHandlers){
				projectHandler.save();
			}
		}
	}

	@Inject( method= "setStat", at=@At("HEAD"))
	public void setStat(PlayerEntity player, Stat<?> stat, int value, CallbackInfo info) {
		if( projectStatHandlers != null && !projectStatHandlers.isEmpty())
			for (ServerStatHandler projectHandler: projectStatHandlers){
				projectHandler.setStat(player, stat, value);
			}
	}

	/*@Inject( method= "parse", at=@At("HEAD"))
	public void parse(DataFixer dataFixer, String json, CallbackInfo info) {
		if( projectStatHandlers != null && !projectStatHandlers.isEmpty())
			for (ServerStatHandler projectHandler: projectStatHandlers){
				iStatHandlerMixin mixedHandler = (iStatHandlerMixin) (StatHandler) projectHandler;
				File projectFile = mixedHandler.getFile();
                String projectJson = null;
                try                   { projectJson = Files.readString(Paths.get(projectFile.getPath()));}
				catch (IOException e) { throw new RuntimeException(e);                                   }
                projectHandler.parse(dataFixer, projectJson);
			}
	}*/

	@Inject( method= "updateStatSet", at=@At("HEAD"))
	public void updateStatSet(CallbackInfo info) {
		if( projectStatHandlers != null && !projectStatHandlers.isEmpty())
			for (ServerStatHandler projectHandler: projectStatHandlers){
				projectHandler.updateStatSet();
			}
	}

}