package hydraheadhunter.cmdstats.command.feedback;


import net.minecraft.block.Block;import net.minecraft.entity.EntityType;import net.minecraft.item.Item;import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;import net.minecraft.text.MutableText;
import net.minecraft.text.Text;import net.minecraft.util.Formatting;import net.minecraft.util.Identifier;

import static hydraheadhunter.cmdstats.CommandStatistics.join;
import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static net.minecraft.text.Text.*;

public class GeneralFeedback {

/*
// statistics store @p [stat type] [stat] [objective]
	public  static <T> MutableText provideStoreFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective, ServerCommandSource... source ){
          
          MutableText objText = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
     
          if (source.length>0) source[0].sendFeedback(() -> Text.literal("Providing Store Feedback"), false);
          return stringifiedTranslatable(BASE_KEY, objText, QueryFeedback.provideFeedback(player, statType, statSpec, statValue));
     }
	
// statistics add @p [stat type] [stat] <int>
     public  static <T> MutableText provideAddFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ServerCommandSource... source ){
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, statValue+amount) );
     }
     // statistics add @p [stat type] [stat] <scoreboard objective>
     public  static <T> MutableText provideAddFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, objText, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, statValue+amount) );
     }
     // statistics add @p [stat type] [stat] <int> <unit>
     public  static <T> MutableText provideAddFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, amountUnitsText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, statValue+adjustedAmount) );
     }
     // statistics add @p [stat type] [stat] <scoreboard objective> <unit>
     public  static <T> MutableText provideAddFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, objText, amountUnitsText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, statValue+adjustedAmount) );
     }

// statistics set @p [stat type] [stat] <int>
     public  static <T> MutableText provideReduceFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return amount > statValue ?
               stringifiedTranslatable( join( INTEGER_KEY, NOT_ENOUGH), playerName, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
               stringifiedTranslatable       (INTEGER_KEY             , statText  , amountText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-amount  ) );
     }
	// statistics set @p [stat type] [stat] <scoreboard objective>
     public  static <T> MutableText provideReduceFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return amount > statValue ?
               stringifiedTranslatable( join (SCORE_KEY, NOT_ENOUGH), playerName, objText, amountText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
               stringifiedTranslatable(       SCORE_KEY,              statText  , objText, amountText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-amount  ) );
          
     }
	// statistics set @p [stat type] [stat]  <int> <unit>
     public  static <T> MutableText provideReduceFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, int adjustedAmount, String unit, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return adjustedAmount > statValue ?
          stringifiedTranslatable( join( INTEGER_KEY, NOT_ENOUGH ), playerName, amountUnitsText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
          stringifiedTranslatable      ( INTEGER_KEY              , statText  , amountUnitsText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-adjustedAmount  ) );
     }
	// statistics set @p [stat type] [stat] <scoreboard objective> <unit>
     public  static <T> MutableText provideReduceFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, int adjustedAmount, String unit, ServerCommandSource... source ){
          Text playerName = player.getName();
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return Text.translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return adjustedAmount > statValue ?
               stringifiedTranslatable( join (SCORE_KEY, NOT_ENOUGH), playerName, objText, amountUnitsText, statText, QueryFeedback.provideFeedback(player, statType, statSpec, MINIMUM_STAT_VALUE) ):
               stringifiedTranslatable(       SCORE_KEY,              statText  , objText, amountUnitsText,           QueryFeedback.provideFeedback(player, statType, statSpec, statValue-adjustedAmount  ) );
          
     }

// statistics set @p [stat type] [stat] <int>
     public  static <T> MutableText provideSetFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ServerCommandSource... source ){
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, statText, amountText, QueryFeedback.provideFeedback(player, statType, statSpec, amount) );
     }
	// statistics set @p [stat type] [stat] <scoreboard objective>
     public  static <T> MutableText provideSetFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = Text.literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, statText, objText, amountText, QueryFeedback.provideFeedback(player, statType, statSpec, amount) );
     }
     // statistics set @p [stat type] [stat] <int> <unit>
     public  static <T> MutableText provideSetFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(INTEGER_KEY, statText, amountUnitsText, QueryFeedback.provideFeedback(player, statType, statSpec, adjustedAmount) );
     }
     // statistics set @p [stat type] [stat] <scoreboard objective> <unit>
     public  static <T> MutableText provideSetFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int amount, ScoreboardObjective objective, int adjustedAmount, String unit, ServerCommandSource... source ){
          MutableText objText    = ((MutableText)objective.getDisplayName()).formatted(Formatting.YELLOW);
          MutableText amountText = literal(String.valueOf(amount)).formatted(Formatting.GREEN);
          MutableText amountUnitsText = stringifiedTranslatable(TWO_WORDS, amountText, literal(unit) );
          MutableText statText;
          try {statText = chooseStatName(statType,statSpec);} catch (NoSuchFieldException e) {return translatable(NO_SUCH_STAT_TYPE_KEY).formatted(Formatting.RED);}
          
          return stringifiedTranslatable(SCORE_KEY, statText, objText, amountUnitsText, QueryFeedback.provideFeedback(player, statType, statSpec, adjustedAmount) );
     }
     
	     private static final String JOIN_COLON = join(JOIN_KEY, COLON);
          private static final int BLOCK_TRIM  = 6;
          private static final int ITEM_TRIM   = 5;
          private static final int ENTITY_TRIM = 7;
          
          public static <T> MutableText chooseStatName( StatType<T> statType, T statSpec) throws NoSuchFieldException {
               MutableText toReturn;
               if (statType.equals(Stats.MINED))          {
                    String statTypeName = join(MC, MINED);
                    String statName = trimMinecraftToMC( ( (Block) statSpec ).getTranslationKey().substring(BLOCK_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
               }
               else if (statType.equals(Stats.CRAFTED))   {
                    String statTypeName = join(MC,CRAFTED);
                    String statName = trimMinecraftToMC( ( (Item) statSpec ).getTranslationKey().substring(ITEM_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
               }
               else if (statType.equals(Stats.USED))      {
                    String statTypeName = join(MC, USED);
                    String statName = trimMinecraftToMC( ( (Item) statSpec ).getTranslationKey().substring(ITEM_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
               }
               else if (statType.equals(Stats.BROKEN))    {
                    String statTypeName = join(MC, BROKEN);
                    String statName = trimMinecraftToMC( ( (Item) statSpec ).getTranslationKey().substring(ITEM_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
               }
               else if (statType.equals(Stats.PICKED_UP)) {
                    String statTypeName = join(MC, PICKED_UP);
                    String statName = trimMinecraftToMC( ( (Item) statSpec ).getTranslationKey().substring(ITEM_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
               }
               else if (statType.equals(Stats.DROPPED))   {
                    String statTypeName = join(MC, DROPPED);
                    String statName = trimMinecraftToMC( ( (Item) statSpec ).getTranslationKey().substring(ITEM_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
               }
               else if (statType.equals(Stats.KILLED))    {
                    String statTypeName = join(MC, KILLED);
                    String statName = trimMinecraftToMC( ( (EntityType<?>) statSpec ).getTranslationKey().substring(ENTITY_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
               }
               else if (statType.equals(Stats.KILLED_BY)) {
                    String statTypeName= join(MC, KILLED_BY);
                    String statName    = trimMinecraftToMC( ((EntityType<?>) statSpec).getTranslationKey().substring(ENTITY_TRIM));
                    toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName,statName);
               }
               else if (statType.equals(Stats.CUSTOM))    {
                    String statTypeName= join(MC, CUSTOM);
                    String statName    = trimMinecraftToMC( join (((Identifier) statSpec).getNamespace(), ((Identifier) statSpec).getPath() ) );
                    toReturn=      stringifiedTranslatable(JOIN_COLON, statTypeName,statName);
               }
               
               else throw new NoSuchFieldException();
               
               return toReturn.formatted(Formatting.GOLD);
          }
          
          private static String trimMinecraftToMC(String stringArg){
               if (stringArg.length()<MINECRAFT.length()) return stringArg;
               String subStringArg = stringArg.substring(0,MINECRAFT.length());
      
               return (subStringArg.equals(MINECRAFT) ) ? MC + stringArg.substring(MINECRAFT.length()): stringArg;
               
          }
          
	
*/
}
