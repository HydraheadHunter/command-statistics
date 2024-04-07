package hydraheadhunter.cmdstats.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import hydraheadhunter.cmdstats.util.iPlayerProjectSaver;
import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static hydraheadhunter.cmdstats.CommandStatistics.MOD_ID;
import static hydraheadhunter.cmdstats.CommandStatistics.join;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerProjectSaverMixin extends PlayerEntity implements iPlayerProjectSaver {
    private int MAXIMUM_NUMBER_ACTIVE_PROJECTS = 20;
    private Collection<File> projectDirectories;
    private NbtCompound projectDirectoriesNBT;
    @Shadow private ServerStatHandler statHandler;

    private static Logger MIXIN_LOGGER = LogUtils.getLogger();


    protected PlayerProjectSaverMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public Collection<File> getProjectDirectories() {
        return this.projectDirectories !=null? projectDirectories : new ArrayList<File>();
    }

    public boolean addDirectory(File directoryToAdd){
        String name= this.getName().getString();
        if (this.projectDirectories==null)
            projectDirectories= new ArrayList<>();
        if (projectDirectories.contains(directoryToAdd)) {
            MIXIN_LOGGER.info( name + " is already had the project directory: " + directoryToAdd.toString());
            return false;
        }
        projectDirectories.add(directoryToAdd);
        MIXIN_LOGGER.info( name + " had the project directory," + directoryToAdd.getName() +", added." );
        iStatHandlerMixin mixedHandler = (iStatHandlerMixin) ((StatHandler) statHandler);
        return mixedHandler.addDirectory(directoryToAdd, name);
    }

    public boolean removeDirectory(File directoryToRemove){
        String name= this.getName().getString();
        if (this.projectDirectories==null || this.projectDirectories.size()<1) {
            projectDirectories = new ArrayList<>();
            MIXIN_LOGGER.info( name +" had no open projects. There was nothing to remove.");
            return false;
        }
        if (projectDirectories.contains(directoryToRemove)){
            projectDirectories.remove((directoryToRemove));
            MIXIN_LOGGER.info( name + " had the project directory, " + directoryToRemove.getPath() +", removed." );
            iStatHandlerMixin mixedHandler = (iStatHandlerMixin) ((StatHandler) statHandler);
            mixedHandler.removeDirectory(directoryToRemove, name);
            return true;
        }

        MIXIN_LOGGER.info( name + " did not have the project directory, " +directoryToRemove.getPath() +". Nothing was removed.");
        return false;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfo info){
        projectDirectoriesNBT = (projectDirectoriesNBT!= null)? projectDirectoriesNBT: new NbtCompound();
        if (projectDirectories != null) {
            int ii=0;
            for (File dir : projectDirectories) {
                projectDirectoriesNBT.putString( join(MOD_ID, "directory", String.valueOf(ii)), dir.toString());
                ii+=1;
            }
            if (projectDirectoriesNBT != null) nbt.put( join(MOD_ID,"directories", uuidString),projectDirectoriesNBT);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo info){

        if(nbt.contains( join(MOD_ID,"directories"),10)) {
            projectDirectoriesNBT = nbt.getCompound(join(MOD_ID, "directories",uuidString));
        }
        if(projectDirectoriesNBT!=null) {
            for(int ii=0; ii<MAXIMUM_NUMBER_ACTIVE_PROJECTS; ii+=1){
                if (projectDirectoriesNBT.contains( join(MOD_ID,"directory",String.valueOf(ii)))){
                    String NbtContent = projectDirectoriesNBT.getString((join(MOD_ID,"directory",String.valueOf(ii))));
                    File dir_ii = new File(NbtContent);
                    addDirectory(dir_ii);
                }
            }
        }
    }

}
