package hydraheadhunter.cmdstats.command.feedback;


import hydraheadhunter.cmdstats.CommandStatistics;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.stringifiedTranslatable;


public class ReduceFeedback {
     private static final String BASE_KEY    = join( FEEDBACK_KEY , REDUCE  );
     private static final String INTEGER_KEY = join( BASE_KEY     , INTEGER );
     private static final String SCORE_KEY   = join( BASE_KEY     , SCORE   );
     private static final String JOIN_COLON  = join( JOIN_KEY     ,COLON    );
     private static final String TWO_WORDS   = join( JOIN_KEY, "2");
     
     private static final String NO_SUCH_STAT_TYPE_KEY = join(ERROR_KEY,NO_SUCH,STAT_TYPE);
     
// statistics set @p [stat type] [stat] <int>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return amount > statValue ?
               stringifiedTranslatable( join( INTEGER_KEY, NOT_ENOUGH), playerName, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
               stringifiedTranslatable       (INTEGER_KEY             , statText  , amountText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-amount  ) );
     }
     

     // statistics set @p [stat type] [stat] <scoreboard objective>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return amount > statValue ?
               stringifiedTranslatable( join (SCORE_KEY, NOT_ENOUGH), playerName, objText, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
               stringifiedTranslatable(       SCORE_KEY,              statText  , objText, amountText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-amount  ) );
          
     }

// statistics set @p [stat type] [stat]  <int> <unit>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, int adjustedAmount, String unit, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return adjustedAmount > statValue ?
          stringifiedTranslatable( join( INTEGER_KEY, NOT_ENOUGH ), playerName, amountUnitsText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
          stringifiedTranslatable      ( INTEGER_KEY              , statText  , amountUnitsText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-adjustedAmount  ) );
     }
     
// statistics set @p [stat type] [stat] <scoreboard objective> <unit>
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, int adjustedAmount, String unit, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return adjustedAmount > statValue ?
               stringifiedTranslatable( join (SCORE_KEY, NOT_ENOUGH), playerName, objText, amountUnitsText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
               stringifiedTranslatable(       SCORE_KEY,              statText  , objText, amountUnitsText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-adjustedAmount  ) );
          
     }
     
}
