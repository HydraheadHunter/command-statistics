package hydraheadhunter.commandstatistics.util;

import hydraheadhunter.commandstatistics.command.StatisticsCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModRegistries {

     public static void registerCommands(){
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerQUERY      );
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerSTORE);
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerADD        );
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerSET        );
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerREDUCE     );

          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerADDobj     );
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerSETobj     );
          CommandRegistrationCallback.EVENT.register(StatisticsCommand::registerREDUCEobj  );

     }
}
