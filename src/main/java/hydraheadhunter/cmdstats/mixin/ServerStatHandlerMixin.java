package hydraheadhunter.cmdstats.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import hydraheadhunter.cmdstats.CommandStatistics;
import hydraheadhunter.cmdstats.util.ModTags;
import hydraheadhunter.cmdstats.util.iPlayerProjectSaver;
import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
import net.minecraft.block.PumpkinBlock;import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;import org.spongepowered.asm.mixin.Mixin;
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

import static hydraheadhunter.cmdstats.CommandStatistics.customStatIsIn;

@Mixin(ServerStatHandler.class)
public abstract class ServerStatHandlerMixin extends StatHandler implements iStatHandlerMixin{
	private Collection<ServerStatHandler> projectStatHandlers;
	private Collection<ServerStatHandler> pausedProjectStatHandlers;
	@Shadow private MinecraftServer server;
	@Shadow private File file;

	private Logger MIXIN_LOGGER  = LogUtils.getLogger();
	private static String LOGGER_PREFIX = CommandStatistics.MOD_ID + " ServerStatHandler Mixin: ";
	private static boolean DEBUG_MIXIN  = CommandStatistics.CONFIG_MIXIN_DEBUG;

	protected ServerStatHandlerMixin(MinecraftServer server, File file) { super();	}

	public boolean addStatHandler(File directoryToAdd, String playerName) {
		if (projectStatHandlers == null) {
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "initializing projectStatHandlers.");
			projectStatHandlers = new ArrayList<>();
		}

		String directoryString = directoryToAdd.toString();
		String fileName = file.getName();
		String fileToAddName = directoryString + "\\" +fileName;

		ServerStatHandler handlerToAdd = new ServerStatHandler(server, new File(fileToAddName));
		projectStatHandlers.add(handlerToAdd);
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + playerName + " had a project StatHandler associated with " + directoryString + " added.");
		return true;
	}
	public boolean removeStatHandler(File directoryToRemove, String playerName) {
		if (projectStatHandlers == null || projectStatHandlers.size()<1){
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + playerName + "had no project StatHandlers. There was nothing to remove.");
			return false;
		}

		String directoryString  = directoryToRemove.toString();
		String fileName         = file.getName();
		String fileToRemovePath = directoryString + "\\" +fileName;
		File   fileToRemove     = new File(fileToRemovePath);

		if(projectStatHandlers.size()==1) {
			ServerStatHandler handler = (ServerStatHandler) projectStatHandlers.toArray()[0];
			iStatHandlerMixin iHandler = (iStatHandlerMixin) ((StatHandler) handler);
			if (iHandler.getFile().getName().equals(fileToRemove.getName())) {
				projectStatHandlers.remove(handler);
				if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + playerName +" had the project StatHandler associated with " + fileToRemovePath +" removed.\n" +
						"They have no active projects remaining.");
				return true;
			}
		}
		else{
			for (ServerStatHandler handlerItt : projectStatHandlers) {
				iStatHandlerMixin iHandlerItt = (iStatHandlerMixin) ((StatHandler) handlerItt);
				if (iHandlerItt.getFile().getName().equals(fileToRemove.getName())) {
					projectStatHandlers.remove(handlerItt);
					if(DEBUG_MIXIN) MIXIN_LOGGER.info( playerName +"had the projectStatHandler associated with " + fileToRemovePath +" removed.\n"+
							"They have " + projectStatHandlers.size() + " active projects remaining.");
					return true;
				}
			}
		}

		if(DEBUG_MIXIN) MIXIN_LOGGER.info( playerName + " did not have any projectStatHandlers associated with: " + fileToRemovePath + ". Nothing was removed.");
		return false;
	}
	public boolean resetStatHandler(){
		try { if (projectStatHandlers.size()<1)  return false; }
		catch (NullPointerException e         ){ return false; }
		projectStatHandlers= new ArrayList<>();
		return true;
	}
	/*public boolean softResetStatHandlers(){
		try { if (projectStatHandlers.size()<1)  return false; }
		catch (NullPointerException e         ){ return false; }
		for(ServerStatHandler handler: projectStatHandlers)
			pauseStatHandler(handler);
		projectStatHandlers= new ArrayList<>();
		return true;
	}*/
	
	public boolean pauseStatHandler  (ServerStatHandler handlerToPause  , String playerName){
		if(projectStatHandlers== null || projectStatHandlers.isEmpty() || !projectStatHandlers.contains(handlerToPause)) return false;
		if(pausedProjectStatHandlers == null) pausedProjectStatHandlers = new ArrayList<>();
		
		projectStatHandlers.remove(handlerToPause);
		if (!pausedProjectStatHandlers.contains(handlerToPause)) {
			pausedProjectStatHandlers.add(handlerToPause);
			return true;
		}
		return false;
	}
	
	public boolean unpauseStatHandler(ServerStatHandler handlerToUnpause, String playerName){
		if(pausedProjectStatHandlers== null || pausedProjectStatHandlers.isEmpty() || !pausedProjectStatHandlers.contains(handlerToUnpause)) return false;
		if(projectStatHandlers == null) projectStatHandlers = new ArrayList<>();
		
		pausedProjectStatHandlers.remove(handlerToUnpause);
		if (!projectStatHandlers.contains(handlerToUnpause)) {
			projectStatHandlers.add(handlerToUnpause);
			return true;
		}
		return false;
	}
	
	public@Nullable ServerStatHandler getProjectStatHandler(File file){
     		try{
     			for(ServerStatHandler handler: projectStatHandlers){
     				iStatHandlerMixin iHandler= (iStatHandlerMixin) (StatHandler) handler;
     				File checkFile = iHandler.getFile();
     				if (file.toString().equals(checkFile.toString())){
     					return handler;
     				}
     			}
     		}
     		catch (NullPointerException ignored) { }
			try{
				for(ServerStatHandler handler: pausedProjectStatHandlers){
					iStatHandlerMixin iHandler= (iStatHandlerMixin) (StatHandler) handler;
					File checkFile = iHandler.getFile();
					if (file.toString().equals(checkFile.toString())){
						return handler;
					}
				}
			}
			catch (NullPointerException ignored) { }
     		return null;
     	}
		
	public File getFile (){ return this.file; }

	@Inject( method= "save", at=@At("HEAD"))
	public void injectSave(CallbackInfo info) {
		if( projectStatHandlers != null && !projectStatHandlers.isEmpty()){
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "injecting save()");
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "The StatHandler " + this.file.getName() +" has " + projectStatHandlers.size() + " active project(s).");
			for (ServerStatHandler projectHandler: projectStatHandlers){
				projectHandler.save();
			}
		}
		else{
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "saving... " +file.toString());
			return;
		}
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "saving... " +file.toString());
	}

	@Inject( method= "setStat", at=@At("HEAD"))
	public void injectSetStat(PlayerEntity player, Stat<?> stat, int value, CallbackInfo info) {
		//determine if this is a 'tick stat' which is measured in ticks (and therefore updated every tick).
		boolean isExemptDebugging=
		 ( stat.getType().equals(Stats.CUSTOM) &&
			 ( customStatIsIn( (Identifier) stat.getValue(), ModTags.Identifiers.IS_TIME	 )	||
			   customStatIsIn( (Identifier) stat.getValue(), ModTags.Identifiers.IS_REAL_TIME)	||
			   customStatIsIn( (Identifier) stat.getValue(), ModTags.Identifiers.IS_DISTANCE )
			 )		)	;
			int valueDif = Math.max(0,value - this.getStat(stat));
			if( projectStatHandlers != null && !projectStatHandlers.isEmpty()){
				if(DEBUG_MIXIN && !isExemptDebugging) MIXIN_LOGGER.info(LOGGER_PREFIX+"injecting setStat()");
				if(DEBUG_MIXIN && !isExemptDebugging) MIXIN_LOGGER.info(LOGGER_PREFIX + "The StatHandler " + this.file.getName() +" has " + projectStatHandlers.size() + " active project(s).");
				for (ServerStatHandler projectHandler: projectStatHandlers) {
					iStatHandlerMixin iProjectHandler = (iStatHandlerMixin) (StatHandler) projectHandler;
					if(DEBUG_MIXIN &&!isExemptDebugging) MIXIN_LOGGER.info(LOGGER_PREFIX+" increasing stats for " + iProjectHandler.getFile().toString());
					((StatHandler) projectHandler).increaseStat(player, stat, valueDif);
				}
				return;
			}
	}

	@Inject( method= "updateStatSet", at=@At("HEAD"))
	public void injectUpdateStatSet(CallbackInfo info) {
		if( projectStatHandlers != null && !projectStatHandlers.isEmpty()) {
			for (ServerStatHandler projectHandler : projectStatHandlers) {
				//if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "injecting updateStatSet()");

				iStatHandlerMixin iProjectHandler = (iStatHandlerMixin) (StatHandler) projectHandler;
				//if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + " updating pendingStats for " + iProjectHandler.getFile().toString());
				projectHandler.updateStatSet();
			}
			return;
		}
		//if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "injecting updateStatSet()");
	}


}