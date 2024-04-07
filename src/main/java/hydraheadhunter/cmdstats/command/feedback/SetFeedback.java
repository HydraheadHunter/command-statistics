package hydraheadhunter.cmdstats.command.feedback;


import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static net.minecraft.text.Text.*;


public class SetFeedback {
     private static final String BASE_KEY    = join( FEEDBACK_KEY , SET     );
     private static final String INTEGER_KEY = join( BASE_KEY     , INTEGER );
     private static final String SCORE_KEY   = join( BASE_KEY     , SCORE   );
     private static final String TWO_WORDS   = join( JOIN_KEY, "2");
     
     private static final String NO_SUCH_STAT_TYPE_KEY = join(ERROR_KEY,NO_SUCH,STAT_TYPE);

     
// statistics set @p [stat type] [stat] <int>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ServerCommandSource... source ){
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, statText, amountText, QueryFeedback.provideFeedback(player, statType, statSpec, amount) );
     }


// statistics set @p [stat type] [stat] <scoreboard objective>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, statText, objText, amountText, QueryFeedback.provideFeedback(player, statType, statSpec, amount) );
     }
     // statistics set @p [stat type] [stat] <int> <unit>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, statText, amountUnitsText, QueryFeedback.provideFeedback(player, statType, statSpec, adjustedAmount) );
     }
     
     // statistics set @p [stat type] [stat] <scoreboard objective> <unit>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, statText, objText, amountUnitsText, QueryFeedback.provideFeedback(player, statType, statSpec, adjustedAmount) );
     }
     
}
