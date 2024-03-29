package hydraheadhunter.commandstatistics.command.feedback;


import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.text.Text.stringifiedTranslatable;

public class AddFeedback {
     private static final String DEFAULT = "commandstatistics.feedback.add.default";

     public  static <T> void provideFeedback (ServerCommandSource source, ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount){
          String formatKey       = DEFAULT;
          Text playerName        = player.getName();
          Text statTypeText      = statType.getName();
          Text statSpecificText ;
          Text statValueText     = Text.literal( String.valueOf(statValue            ));
          Text amountText        = Text.literal( String.valueOf(amount               ));
          Text nextValueText     = Text.literal( String.valueOf(statValue + amount   ));

          if      (statType.equals(Stats.MINED     )) {
               statSpecificText = Text.of("");

          }
          else if (statType.equals(Stats.CRAFTED   ) || statType.equals(Stats.USED ) || statType.equals(Stats.BROKEN) ||statType.equals(Stats.PICKED_UP ) || statType.equals(Stats.DROPPED   )) {
               statSpecificText =          ((Item )         statSpecific).getName()  ;
          }
          else if (statType.equals(Stats.KILLED    )) { statSpecificText =          ((EntityType<?>) statSpecific).getName()  ;}
          else if (statType.equals(Stats.KILLED_BY )) { statSpecificText =          ((EntityType<?>) statSpecific).getName()  ;}
          else                   /*Stats.CUSTOM*/     { statSpecificText = Text.of( ((Identifier)    statSpecific).toString());}

          source.sendFeedback(() -> stringifiedTranslatable( formatKey, playerName, statTypeText, statSpecificText, statValueText, amountText, nextValueText ), false );
     }
     
     public  static <T> void provideFeedback (ServerCommandSource source, ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, int amount, ScoreboardObjective objective){
          String formatKey       = DEFAULT.substring(0, DEFAULT.length()-7) +"score";
          Text playerName        = player.getName();
          Text statTypeText      = statType.getName();
          Text statSpecificText ;
          Text statValueText     = Text.literal( String.valueOf(statValue            ));
          Text amountText        = Text.literal( String.valueOf(amount               ));
          Text nextValueText     = Text.literal( String.valueOf(statValue + amount   ));
          Text objectiveText     = objective.getDisplayName()                          ;

          if      (statType.equals(Stats.MINED     )) { statSpecificText =          ((Block)         statSpecific).getName()  ;}
          else if (statType.equals(Stats.CRAFTED   )) { statSpecificText =          ((Item )         statSpecific).getName()  ;}
          else if (statType.equals(Stats.USED      )) { statSpecificText =          ((Item )         statSpecific).getName()  ;}
          else if (statType.equals(Stats.BROKEN    )) { statSpecificText =          ((Item )         statSpecific).getName()  ;}
          else if (statType.equals(Stats.PICKED_UP )) { statSpecificText =          ((Item )         statSpecific).getName()  ;}
          else if (statType.equals(Stats.DROPPED   )) { statSpecificText =          ((Item )         statSpecific).getName()  ;}
          else if (statType.equals(Stats.KILLED    )) { statSpecificText =          ((EntityType<?>) statSpecific).getName()  ;}
          else if (statType.equals(Stats.KILLED_BY )) { statSpecificText =          ((EntityType<?>) statSpecific).getName()  ;}
          else                   /*Stats.CUSTOM*/     { statSpecificText = Text.of( ((Identifier)    statSpecific).toString());}

          source.sendFeedback(() -> stringifiedTranslatable( formatKey, playerName, statTypeText, statSpecificText, statValueText, amountText, objectiveText, nextValueText ), false );
     }

}
