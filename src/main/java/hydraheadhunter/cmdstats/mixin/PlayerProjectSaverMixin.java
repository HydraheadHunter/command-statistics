	package hydraheadhunter.cmdstats.mixin;
	
	import com.mojang.authlib.GameProfile;
	import com.mojang.logging.LogUtils;
	import hydraheadhunter.cmdstats.CommandStatistics;
	import hydraheadhunter.cmdstats.util.iPlayerProjectSaver;
	import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
	import net.minecraft.entity.player.PlayerEntity;
	import net.minecraft.nbt.NbtCompound;
	import net.minecraft.nbt.NbtElement;import net.minecraft.nbt.NbtList;
	import net.minecraft.server.network.ServerPlayerEntity;
	import net.minecraft.stat.ServerStatHandler;
	import net.minecraft.stat.StatHandler;
	import net.minecraft.util.math.BlockPos;
	import net.minecraft.world.World;
	import org.slf4j.Logger;
	import org.spongepowered.asm.mixin.Mixin;
	import org.spongepowered.asm.mixin.Shadow;
	import org.spongepowered.asm.mixin.injection.At;
	import org.spongepowered.asm.mixin.injection.Inject;
	import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
	
	import java.io.File;
	import java.util.ArrayList;
	import java.util.Collection;import java.util.Iterator;
	
	import static hydraheadhunter.cmdstats.CommandStatistics.MOD_ID;
	import static hydraheadhunter.cmdstats.CommandStatistics.join;
	import static java.lang.String.valueOf;
	
	@Mixin(ServerPlayerEntity.class)
	public abstract class PlayerProjectSaverMixin extends PlayerEntity implements iPlayerProjectSaver {
	private Collection<File> projectDirectories;
	private Collection<File> pausedProjectDirectories;
 
	private NbtList projectDirectoriesNBT;
	private NbtList pausedProjectDirectoriesNBT;
	@Shadow private ServerStatHandler statHandler;
	
	private static Logger MIXIN_LOGGER  = LogUtils.getLogger();
	private static String LOGGER_PREFIX = CommandStatistics.MOD_ID + " ServerPlayerEntity Mixin: ";
	private static boolean DEBUG_MIXIN  = CommandStatistics.CONFIG_MIXIN_DEBUG;
	
	protected PlayerProjectSaverMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) { super(world, pos, yaw, gameProfile); }
	
	public Collection<File> getProjectDirectories() {
     	   return this.projectDirectories !=null? projectDirectories : new ArrayList<>();
     	}
	public Collection<File> getPausedProjectDirectories() {
          	   return this.pausedProjectDirectories !=null? pausedProjectDirectories : new ArrayList<>();
          	}
          	
	public boolean addDirectory(File directoryToAdd){
		
		String name= this.getName().getString();
		String directoryName = directoryToAdd.toString();
		//if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "attempting to add file to project directorys for " + name +".");
		
		if (this.projectDirectories==null) {
		//  if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "initializing projectDirectories.");
		  projectDirectories = new ArrayList<>();
		}
		if (projectDirectories.contains(directoryToAdd)) {
		//  if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + name + "'s projectDirectories already contains " + directoryToAdd.toString() + ". Nothing was added.");
		  return false;
		}
		
		iStatHandlerMixin mixedHandler = (iStatHandlerMixin) ((StatHandler) statHandler);
		if (pausedProjectDirectories==null) pausedProjectDirectories=new ArrayList<>();
		if (pausedProjectDirectories.contains(directoryToAdd)){
			unpauseDirectory(directoryToAdd);
		//	if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + name + "'s projectDirectories had " + directoryToAdd.getName() +" unpaused." );
			return mixedHandler.unpauseStatHandler( new ServerStatHandler(this.getServer(),directoryToAdd), name);
		}
		else if ( !projectDirectories.contains(directoryToAdd)){
			projectDirectories.add(directoryToAdd);
		
		//	if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + name + "'s projectDirectories had " + directoryToAdd.getName() +" added." );
			return mixedHandler.addStatHandler(directoryToAdd, name);
			
		}
		return false;
	}
	
	public boolean removeDirectory(File directoryToRemove){
	   String name= this.getName().getString();
	   if (this.projectDirectories==null || this.projectDirectories.size()<1) {
		  projectDirectories = new ArrayList<>();
		  if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + name +" had no open projects. There was nothing to remove.");
		  return false;
	   }
	   if (projectDirectories.contains(directoryToRemove)){
		  projectDirectories.remove((directoryToRemove));
		  if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + name + "'s project Directories had " + directoryToRemove.getPath() +" removed." );
		  iStatHandlerMixin mixedHandler = (iStatHandlerMixin) ((StatHandler) statHandler);
		  mixedHandler.removeStatHandler(directoryToRemove, name);
		  return true;
	   }
	
	   if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + name + " 's did not contain " +directoryToRemove.getPath() +". Nothing was removed.");
	   return false;
	}
	public boolean resetDirectories(){
		int returnFalseEarlyAtTwo=0;
		try {if ( !projectDirectories.isEmpty()) returnFalseEarlyAtTwo+=1; }
		catch (NullPointerException ignored ){   returnFalseEarlyAtTwo+=1; }
		projectDirectories= new ArrayList<>()   ;
		
		try {if ( !pausedProjectDirectories.isEmpty()) returnFalseEarlyAtTwo+=1; }
		catch (NullPointerException ignored  ){        returnFalseEarlyAtTwo+=1; }
		pausedProjectDirectories= new ArrayList<>()    ;
		
		if (returnFalseEarlyAtTwo==2){return false;}
		return ((iStatHandlerMixin)((StatHandler)(statHandler))).resetStatHandler();
		
	}
	public boolean softResetDirectories(){
	//	float debugFloat=0;
		try {if (projectDirectories.size()<1)  return false; }
		catch (NullPointerException e       ){ return false; }
		
		for (File directory : projectDirectories) {
			//expected values 2~4
			float inLoop = 0.0f;
			pauseDirectory(directory, inLoop);
		}
		projectDirectories= new ArrayList<>();
		
	
		// return ((iStatHandlerMixin)((StatHandler)(statHandler))).softResetStatHandlers();
		return true;
	}
	
	
	//TODO YOU'RE ADDING PROJECT-PAUSE FUNCTIONALITY
	// WHICH IS DISTINCT FROM PROJECT-STOP IN THAT A DIRECTORY WHICH IS PAUSED CAN STILL BE SEARCHED BY PROJECT-QUERY AND PROJECT-STORE.
	public  boolean pauseDirectory(File directoryToPause){
		
		if (projectDirectories==null || projectDirectories.isEmpty() || !projectDirectories.contains(directoryToPause)) return false;
		if (pausedProjectDirectories==null) pausedProjectDirectories = new ArrayList<>();
		
		projectDirectories.remove(directoryToPause);
		
		if( !pausedProjectDirectories.contains(directoryToPause)){
			pausedProjectDirectories.add(directoryToPause);
			iStatHandlerMixin iHandler = (iStatHandlerMixin)(StatHandler)statHandler;
			iHandler.pauseStatHandler( new ServerStatHandler(this.getServer(),directoryToPause), this.getName().getString());
			return true;
		}
		return false;
	}
	private boolean pauseDirectory(File directoryToPause, float isInALoop){
		if (projectDirectories==null || projectDirectories.isEmpty() || !projectDirectories.contains(directoryToPause)) return false;
		if (pausedProjectDirectories==null) pausedProjectDirectories = new ArrayList<>();
		
		if( !pausedProjectDirectories.contains(directoryToPause)){
			pausedProjectDirectories.add(directoryToPause);
			iStatHandlerMixin iHandler = (iStatHandlerMixin)((StatHandler)statHandler);
			iHandler.pauseStatHandler( new ServerStatHandler(this.getServer(),directoryToPause), this.getName().getString());
			return true;
		}
		return false;
	}
	
	public boolean unpauseDirectory(File directoryToUnpause){
		if (pausedProjectDirectories==null || pausedProjectDirectories.isEmpty() || !pausedProjectDirectories.contains(directoryToUnpause)) return false;
		if (projectDirectories==null) projectDirectories = new ArrayList<>();
		
		pausedProjectDirectories.remove(directoryToUnpause);
		
		if( !projectDirectories.contains(directoryToUnpause)){
			projectDirectories.add(directoryToUnpause);
			iStatHandlerMixin iHandler = (iStatHandlerMixin)(StatHandler)statHandler;
			iHandler.unpauseStatHandler( new ServerStatHandler(this.getServer(),directoryToUnpause), this.getName().getString());
			return true;
		}
		return false;
	}
	
	
	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	protected void injectWriteMethod(NbtCompound nbt, CallbackInfo info){
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "injecting writeCustomDataToNbt()");
		String directoryBasekey = join(MOD_ID, "directory");
		String directoriesKey   = join(MOD_ID, "directories",uuidString);
		
		int ii=0;
		if (projectDirectories != null) {
			projectDirectoriesNBT = new NbtList();
			if ( !this.projectDirectories.isEmpty() ) {
				for (File directory : projectDirectories) {
					NbtCompound nbtDirectory = new NbtCompound();
					nbtDirectory.putString( join(directoryBasekey,valueOf(ii)),directory.toString());
					projectDirectoriesNBT.add(nbtDirectory);
					ii+=1;
				}
			}
			
			nbt.put(directoriesKey, projectDirectoriesNBT);
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(directoriesKey);
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "saved " + valueOf(ii)+ " project directories for " + this.getName().getString() );
		}
		// save paused projects
		String directoryPausedBasekey = join(MOD_ID, "directory", "paused");
		String directoriesPausedKey   = join(MOD_ID, "directories", "paused",uuidString);
		ii=0;
		if (pausedProjectDirectories != null) {
			pausedProjectDirectoriesNBT = new NbtList();
			if ( !this.pausedProjectDirectories.isEmpty() ) {
				for (File directory : pausedProjectDirectories) {
					NbtCompound nbtDirectory = new NbtCompound();
					nbtDirectory.putString( join(directoryPausedBasekey,valueOf(ii)),directory.toString());
					pausedProjectDirectoriesNBT.add(nbtDirectory);
					ii+=1;
				}
			}
			nbt.put(directoriesPausedKey, pausedProjectDirectoriesNBT);
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(directoriesPausedKey);
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "saved " + valueOf(ii)+ " paused project directories for " + this.getName().getString() );
		}
	}
	
	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	protected void injectReadMethod(NbtCompound nbt, CallbackInfo info){
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "injecting readCustomDataToNbt()");
		String directoryBasekey = join(MOD_ID, "directory");
		String directoriesKey   = join(MOD_ID, "directories", uuidString);
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "directoriesKey== " + directoriesKey);
		
		if(nbt.contains(directoriesKey, NbtElement.LIST_TYPE)) {
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "nbtList was found.");
			projectDirectories= new ArrayList<>();
               projectDirectoriesNBT= nbt.getList(directoriesKey, NbtElement.COMPOUND_TYPE);
			int ii;
			for (ii = 0; ii < projectDirectoriesNBT.size(); ii+=1) {
				NbtCompound nbtCompound = projectDirectoriesNBT.getCompound(ii);
				projectDirectories.add( new File(nbtCompound.getString( join(directoryBasekey,valueOf(ii)) ) ) );
			}
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "Loaded " +ii+ " project(s) for " +this.getName().getString());
		}
		else{
			if (DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "no compound found with directoriesKey");
		}
		
		//load paused directories
		String directoryPausedBasekey = join(MOD_ID, "directory", "paused");
		String directoriesPausedKey   = join(MOD_ID, "directories", "paused",uuidString);
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "directoriesPausedKey== " + directoriesPausedKey);
		
		if(nbt.contains(directoriesPausedKey, NbtElement.LIST_TYPE)) {
			if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "nbtList was found.");
			pausedProjectDirectories= new ArrayList<>();
			pausedProjectDirectoriesNBT= nbt.getList(directoriesPausedKey, NbtElement.COMPOUND_TYPE);
			int ii;
			for (ii = 0; ii < pausedProjectDirectoriesNBT.size(); ii+=1) {
				NbtCompound nbtCompound = pausedProjectDirectoriesNBT.getCompound(ii);
				pausedProjectDirectories.add( new File(nbtCompound.getString( join(directoryPausedBasekey,valueOf(ii)) ) ) );
			}
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "Loaded " +ii+ " paused project(s) for " +this.getName().getString());
		}
		else{
			if (DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "no compound found with directoriesPausedKey");
		}
		
	}
	
}
