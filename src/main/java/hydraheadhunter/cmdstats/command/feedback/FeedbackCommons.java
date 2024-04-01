package hydraheadhunter.cmdstats.command.feedback;

import hydraheadhunter.cmdstats.CommandStatistics;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static net.minecraft.text.Text.stringifiedTranslatable;

public class FeedbackCommons {
     private static final String MINECRAFT = CommandStatistics.MINECRAFT;
     private static final String MC = CommandStatistics.MC;
     private static final String JOIN_COLON = join(CommandStatistics.JOIN_KEY,"2.colon");
     
     public static <T> MutableText chooseStatName( StatType<T> statType, T statSpecific) throws NoSuchFieldException {
          MutableText toReturn;
          if (statType.equals(Stats.MINED)) {
               String statTypeName = join(MC, MINED);
               String statName = trimMinecraftToMC( ( (Block) statSpecific ).getTranslationKey().substring(6));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.CRAFTED)) {
               String statTypeName = join(MC,CRAFTED);
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.USED)) {
               String statTypeName = join(MC, USED);
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.BROKEN)) {
               String statTypeName = join(MC, BROKEN);
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.PICKED_UP)) {
               String statTypeName = join(MC, PICKED_UP);
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.DROPPED)) {
               String statTypeName = join(MC, DROPPED);
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.KILLED)) {
               String statTypeName = join(MC, KILLED);
               String statName = trimMinecraftToMC( ( (EntityType<?>) statSpecific ).getTranslationKey().substring(7));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.KILLED_BY)){
               String statTypeName= join(MC, KILLED_BY);
               String statName    = trimMinecraftToMC( ((EntityType<?>) statSpecific).getTranslationKey().substring(7));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName,statName);
          }
          else if (statType.equals(Stats.CUSTOM)){
               String statTypeName= join(MC, CUSTOM);
               String statName    = trimMinecraftToMC( join (((Identifier) statSpecific).getNamespace(), ((Identifier) statSpecific).getPath() ) );
               toReturn=      stringifiedTranslatable(JOIN_COLON, statTypeName,statName);
          }
          
          else throw new NoSuchFieldException();
          
          return toReturn.formatted(Formatting.GOLD);
     }
     
     private static String trimMinecraftToMC(String stringArg){
          if (stringArg.length()<9) return stringArg;
          String subStringArg = stringArg.substring(0,9);
 
          return (subStringArg.equals(MINECRAFT) ) ? MC + stringArg.substring(9): stringArg;
          
     }
     
}
