package hydraheadhunter.commandstatistics.command.feedback;


import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.text.Text.stringifiedTranslatable;


public class SetFeedback {
     private static final String BASE_KEY = "commandstatistics.feedback.set";
     private static final String INTEGER_KEY = BASE_KEY + ".integer";
     private static final String SCORE_KEY   = BASE_KEY + ".score";
     
     private static final String ERROR_STAT_TYPE = BASE_KEY + ".error.stat_type";
     private static final String JOIN_COLON = "commandstatistics.grammar.join.2.colon";
     
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount, ServerCommandSource... source ){
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpecific);} catch (NoSuchFieldException e) {return Text.translatable(ERROR_STAT_TYPE).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, statText, amountText, QueryFeedback.provideFeedback(player, statType, statSpecific, statValue+amount) );
     }
     
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount, ScoreboardObjective objective, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = FeedbackCommons.chooseStatName(statType,statSpecific);} catch (NoSuchFieldException e) {return Text.translatable(ERROR_STAT_TYPE).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, statText, objText, amountText, QueryFeedback.provideFeedback(player, statType, statSpecific, statValue+amount) );
     }
     
}
