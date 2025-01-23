package hydraheadhunter.cmdstats.command.feedback;

import hydraheadhunter.cmdstats.command.feedback.lang.KeySelector;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static hydraheadhunter.cmdstats.command.feedback.CommonFields.*;
import static java.lang.String.valueOf;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;

public class OfflineFeedback {
     private static final String ERROR_KEY = "cmdstats.feedback.error";
     private static final String BASIC_KEY = "basic";
     
     public static MutableText provideErrorFeedback(String key){
          MutableText toReturn= translatable( join(ERROR_KEY,key) ).formatted(ERROR_FORMAT);
          return toReturn;
     }
     
/*


*/
}
