package hydraheadhunter.cmdstats.command.feedback;


import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static hydraheadhunter.cmdstats.CommandStatistics.join;
import static net.minecraft.text.Text.stringifiedTranslatable;
import static hydraheadhunter.cmdstats.CommandStatistics.*;

public class AddFeedback {
     private static final String BASE_KEY    = join( FEEDBACK_KEY , ADD     );
     private static final String INTEGER_KEY = join( BASE_KEY     , INTEGER );
     private static final String SCORE_KEY   = join( BASE_KEY     , SCORE   );
     
     private static final String ERROR_STAT_TYPE_KEY = join(BASE_KEY, ERROR, NO_SUCH_STAT_TYPE);
     
// statistics add @p [stat type] [stat] <int>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount, ServerCommandSource... source ){
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpecific);} catch (NoSuchFieldException e) {return Text.translatable(ERROR_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpecific, statValue+amount) );
     }
     
// statistics add @p [stat type] [stat] <int> <unit>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpecific);} catch (NoSuchFieldException e) {return Text.translatable(ERROR_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpecific, statValue+adjustedAmount) );
     }
     
// statistics add @p [stat type] [stat] <scoreboard objective>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount, ScoreboardObjective objective, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpecific);} catch (NoSuchFieldException e) {return Text.translatable(ERROR_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, objText, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpecific, statValue+amount) );
     }
     
// statistics add @p [stat type] [stat] <scoreboard objective> <unit>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount, ScoreboardObjective objective, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpecific);} catch (NoSuchFieldException e) {return Text.translatable(ERROR_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, objText, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpecific, statValue+adjustedAmount) );
     }
}
