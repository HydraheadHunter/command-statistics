package hydraheadhunter.cmdstats.command.feedback;


import hydraheadhunter.cmdstats.CommandStatistics;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static hydraheadhunter.cmdstats.CommandStatistics.join;
import static net.minecraft.text.Text.stringifiedTranslatable;

public class StoreFeedback {
     private static final String BASE_KEY    = join( CommandStatistics.FEEDBACK_KEY , CommandStatistics.STORE     );

     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective, ServerCommandSource... source ){
          
          MutableText objText = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
     
          if (source.length>0) source[0].sendFeedback(() -> Text.literal("Providing Store Feedback"), false);
          return stringifiedTranslatable(BASE_KEY, objText, QueryFeedback.provideFeedback(player, statType, statSpec, statValue));
     }

}
