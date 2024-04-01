package hydraheadhunter.commandstatistics.command.feedback;


import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StoreFeedback {
     private static final String BASE_KEY = "commandstatistics.feedback.store";

     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, ScoreboardObjective objective, ServerCommandSource... source ){
          
          MutableText objText = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
     
          if (source.length>0) source[0].sendFeedback(() -> Text.literal("Providing Store Feedback"), false);
          return Text.stringifiedTranslatable(BASE_KEY, objText, QueryFeedback.provideFeedback(player, statType, statSpecific, statValue));
     }

}
