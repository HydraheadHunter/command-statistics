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
import static net.minecraft.text.Text.*;

public class FeedbackCommons {
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
     
}
