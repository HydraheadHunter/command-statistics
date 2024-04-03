package hydraheadhunter.cmdstats.util;

import hydraheadhunter.cmdstats.command.StatisticsCommand;
import hydraheadhunter.cmdstats.command.argument.BlockArgumentType;
import hydraheadhunter.cmdstats.command.argument.EntityTypeArgumentType;
import hydraheadhunter.cmdstats.command.argument.ItemArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
public class ModRegistries {

     public static void registerCommands(){
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerSTATISITCS);
          
          ArgumentTypeRegistry.registerArgumentType(
               new Identifier(MOD_ID, "block"),
               BlockArgumentType.class, ConstantArgumentSerializer.of(BlockArgumentType::block)
          );
          
          ArgumentTypeRegistry.registerArgumentType(
               new Identifier(MOD_ID, "item"),
               ItemArgumentType.class, ConstantArgumentSerializer.of(ItemArgumentType::item)
          );
          ArgumentTypeRegistry.registerArgumentType(
               new Identifier(MOD_ID, "entity"),
               EntityTypeArgumentType.class, ConstantArgumentSerializer.of(EntityTypeArgumentType::type)
          );
     }
}
