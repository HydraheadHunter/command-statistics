package hydraheadhunter.cmdstats.mixin;

import com.mojang.datafixers.DataFixer;
import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

@Mixin(StatHandler.class)
public abstract class StatHandlerMixin {
	private Collection<ServerStatHandler> projectStatHandlers;

	protected StatHandlerMixin(MinecraftServer server, File file) {	super(); }

	@Inject( method= "increaseStat", at=@At("HEAD"))
	public void increaseStat(PlayerEntity player, Stat<?> stat, int value, CallbackInfo info) {
		if( projectStatHandlers != null && !projectStatHandlers.isEmpty())
			for (ServerStatHandler projectHandler: projectStatHandlers){
				projectHandler.increaseStat(player, stat, value);
			}
	}

}