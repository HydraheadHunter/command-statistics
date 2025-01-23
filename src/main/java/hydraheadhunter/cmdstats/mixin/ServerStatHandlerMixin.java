package hydraheadhunter.cmdstats.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import hydraheadhunter.cmdstats.CommandStatistics;
import hydraheadhunter.cmdstats.util.ModTags;
import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.*;

import static hydraheadhunter.cmdstats.CommandStatistics.customStatIsIn;

@Mixin(ServerStatHandler.class)
public abstract class ServerStatHandlerMixin extends StatHandler implements iStatHandlerMixin{
	
	private Collection<ServerStatHandler> projectStatHandlers;
	private Collection<ServerStatHandler> pausedStatHandlers;
	@Shadow private MinecraftServer server;
	@Shadow private File file;
	
	@Shadow protected abstract String asString();
	
	private final Logger MIXIN_LOGGER  = LogUtils.getLogger();
	private static final String LOGGER_PREFIX = CommandStatistics.MOD_ID + " ServerStatHandler Mixin: ";
	private static final boolean DEBUG_MIXIN  = CommandStatistics.CONFIG_MIXIN_DEBUG;
	
	protected ServerStatHandlerMixin(MinecraftServer server, File file) { super();	}
	
	
//iHandler Methods and Helpers
	public void updateProjectStatHandlers( Collection<File> projectDirectories, Collection<File> pausedDirectories){
		projectStatHandlers = new ArrayList<>();
		pausedStatHandlers  = new ArrayList<>();
		
		for (File directory:projectDirectories)	projectStatHandlers.add( createHandlerToAdd(directory));
		for (File directory:pausedDirectories)	pausedStatHandlers .add( createHandlerToAdd(directory));
	}
	
	private ServerStatHandler createHandlerToAdd(File directoryToCheck){
		String directoryString = directoryToCheck.toString();
		String fileName = file.getName();
		String newFileName = directoryString + "\\" +fileName;
		return new ServerStatHandler( this.server, new File( newFileName) );
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
				for(ServerStatHandler handler: pausedStatHandlers){
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
		
	public boolean isProjectActive(ServerStatHandler serverStatHandler){
		iStatHandlerMixin iHandler_arg = (iStatHandlerMixin) serverStatHandler;
		File file = iHandler_arg.getFile();
		
		try{
			for(ServerStatHandler handler: projectStatHandlers){
				iStatHandlerMixin iHandler= (iStatHandlerMixin) (StatHandler) handler;
				File checkFile = iHandler.getFile();
				if (file.toString().equals(checkFile.toString())){
					return true;
				}
			}
		}
		catch (NullPointerException ignored) { }
		return false;
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
			   customStatIsIn( (Identifier) stat.getValue(), ModTags.Identifiers.IS_MC_TIME)	||
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

	//Prepends a Playername Json object to the stats JSON for offline playerlookup.
	@Inject(method= "asString", at=@At("TAIL"), cancellable = true)
	public void injectAsString(CallbackInfoReturnable cir){
		String fileName = file.getName();
		PlayerEntity player = server.getPlayerManager().getPlayer( UUID.fromString( fileName.substring(0,fileName.length()-5) ) );
		String PlayerName= player.getName().getString();
		
		String rawJSONstring= (String) cir.getReturnValue();
		JsonObject rawJSON= new JsonParser().parse(rawJSONstring).getAsJsonObject();
		JsonElement statsElement= rawJSON.get("stats");
		JsonElement versionElement= rawJSON.get("DataVersion");
		
		JsonObject object = new JsonObject();
		object.addProperty( "PlayerName", PlayerName);
		object.add("stats", statsElement);
		object.add("DataVersion", versionElement);
		
		//MIXIN_LOGGER.info( object.toString());
		cir.setReturnValue( object.toString());
		
	}
}