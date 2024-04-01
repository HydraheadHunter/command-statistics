package hydraheadhunter.commandstatistics.command.feedback;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static net.minecraft.text.Text.stringifiedTranslatable;

public class FeedbackCommons {
     private static final String MINECRAFT = "minecraft.";
     private static final String MC = "mc.";
     private static final String JOIN_COLON = "commandstatistics.grammar.join.2.colon";
     
     public static <T> MutableText chooseStatName( StatType<T> statType, T statSpecific) throws NoSuchFieldException {
          MutableText toReturn;
          if (statType.equals(Stats.MINED)) {
               String statTypeName = MC + "mined";
               String statName = trimMinecraftToMC( ( (Block) statSpecific ).getTranslationKey().substring(6));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.CRAFTED)) {
               String statTypeName = MC + "crafted";
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.USED)) {
               String statTypeName = MC + "used";
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.BROKEN)) {
               String statTypeName = MC + "broken";
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.PICKED_UP)) {
               String statTypeName = MC + "picked_up";
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.DROPPED)) {
               String statTypeName = MC + "dropped";
               String statName = trimMinecraftToMC( ( (Item) statSpecific ).getTranslationKey().substring(5));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.KILLED)) {
               String statTypeName = MC + "killed";
               String statName = trimMinecraftToMC( ( (EntityType<?>) statSpecific ).getTranslationKey().substring(7));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName, statName);
          }
          else if (statType.equals(Stats.KILLED_BY)){
               String statTypeName= MC + "killed_by";
               String statName    = trimMinecraftToMC( ((EntityType<?>) statSpecific).getTranslationKey().substring(7));
               toReturn= stringifiedTranslatable(JOIN_COLON, statTypeName,statName);
          }
          else if (statType.equals(Stats.CUSTOM)){
               String statTypeName= MC + "custom";
               String statName    = trimMinecraftToMC( ((Identifier) statSpecific).getNamespace() + "." + ((Identifier) statSpecific).getPath());
               toReturn=      stringifiedTranslatable(JOIN_COLON, statTypeName,statName);
          }
          
          else throw new NoSuchFieldException();
          
          return toReturn.formatted(Formatting.GOLD);
     }
     
     private static String trimMinecraftToMC(String stringArg){
          if (stringArg.length()<10) return stringArg;
          String subStringArg = stringArg.substring(0,10);
 
          return (subStringArg.equals(MINECRAFT) || subStringArg.equals("minecraft:")) ? MC + stringArg.substring(10): stringArg;
          
     }
     
}
