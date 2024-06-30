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
	import java.util.Collection;
	
	import static hydraheadhunter.cmdstats.CommandStatistics.MOD_ID;
	import static hydraheadhunter.cmdstats.CommandStatistics.join;
	import static java.lang.String.valueOf;
	
	@Mixin(ServerPlayerEntity.class)
	public abstract class PlayerProjectSaverMixin extends PlayerEntity implements iPlayerProjectSaver {
	private Collection<File> projectDirectories;
	private Collection<File> pausedDirectories;
 
	private NbtList projectDirectoriesNBT;
	private NbtList pausedProjectDirectoriesNBT;
	@Shadow private ServerStatHandler statHandler;
	
	private static Logger MIXIN_LOGGER  = LogUtils.getLogger();
	private static String LOGGER_PREFIX = CommandStatistics.MOD_ID + " ServerPlayerEntity Mixin: ";
	private static boolean DEBUG_MIXIN  = CommandStatistics.CONFIG_MIXIN_DEBUG;
	
	protected PlayerProjectSaverMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) { super(world, pos, yaw, gameProfile); }
	
	public Collection<File> getProjectDirectories() {
     	   return projectDirectories!=null ? projectDirectories : new ArrayList<>();
     	}
	public Collection<File> getPausedDirectories( ) {
          	   return pausedDirectories !=null ? pausedDirectories  : new ArrayList<>();
          	}
          	
	public boolean addDirectory(File directoryToAdd){
		initDirectoriesIfNull();
		
		if ( ! projectDirectories.contains(directoryToAdd) ){
			pausedDirectories .remove(directoryToAdd);
			projectDirectories.add   (directoryToAdd);
			updateStatHandlers();
			return true;
		}
		return false;
	}
	public boolean pauseDirectory(File directoryToPause, boolean... isPauseAll){
		initDirectoriesIfNull();
     		
		if( !pausedDirectories.contains(directoryToPause)){
			projectDirectories.remove(directoryToPause);
			pausedDirectories .add   (directoryToPause);
			updateStatHandlers();
			return true;
		}
		return false;
	}
	public boolean removeDirectory(File directoryToRemove){
	   if (this.projectDirectories==null) 	projectDirectories = new ArrayList<>();
	   if (this.pausedDirectories==null) 	pausedDirectories  = new ArrayList<>();
	   
	   if(projectDirectories.remove(directoryToRemove) || pausedDirectories.remove(directoryToRemove)){
		   updateStatHandlers();
		   return true;
	   }
	   return false;
	   
	}
	
	public boolean resetDirectories(){
		initDirectoriesIfNull();
		boolean toReturn = projectDirectories.size()+pausedDirectories.size() >0  ;
		projectDirectories  = new ArrayList<>();
		pausedDirectories	= new ArrayList<>();
		updateStatHandlers();
		return toReturn;
	}
	public boolean softResetDirectories(){
		try {if (projectDirectories.size()<1) return false; }
		catch (NullPointerException ignored){ return false; }
		
		Collection<File> localProjectDirectories = new ArrayList<>();
		localProjectDirectories.addAll(projectDirectories);
		for (File directory : localProjectDirectories) {
			pauseDirectory(directory, true);
		}
		
		updateStatHandlers();
		return true;
	}
	
	private void initDirectoriesIfNull(){
     		if(projectDirectories== null) 	projectDirectories = new ArrayList<>();
               if(pausedDirectories == null) 	pausedDirectories  = new ArrayList<>();
               
     	}
     private void updateStatHandlers(){
		this.statHandler.save();
		iStatHandlerMixin mixedHandler = (iStatHandlerMixin) ((StatHandler) statHandler);
		mixedHandler.updateProjectStatHandlers(projectDirectories,pausedDirectories);
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
		if (pausedDirectories != null) {
			pausedProjectDirectoriesNBT = new NbtList();
			if ( !this.pausedDirectories.isEmpty() ) {
				for (File directory : pausedDirectories) {
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
				addDirectory( new File(nbtCompound.getString( join(directoryBasekey,valueOf(ii)) ) ) );
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
			pausedDirectories= new ArrayList<>();
			pausedProjectDirectoriesNBT= nbt.getList(directoriesPausedKey, NbtElement.COMPOUND_TYPE);
			int ii;
			for (ii = 0; ii < pausedProjectDirectoriesNBT.size(); ii+=1) {
				NbtCompound nbtCompound = pausedProjectDirectoriesNBT.getCompound(ii);
				pauseDirectory( new File(nbtCompound.getString( join(directoryPausedBasekey,valueOf(ii)) ) ) );
			}
		if(DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "Loaded " +ii+ " paused project(s) for " +this.getName().getString());
		}
		else{
			if (DEBUG_MIXIN) MIXIN_LOGGER.info(LOGGER_PREFIX + "no compound found with directoriesPausedKey");
		}
		
	}
	
}
