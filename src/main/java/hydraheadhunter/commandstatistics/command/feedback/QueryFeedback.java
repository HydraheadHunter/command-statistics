package hydraheadhunter.commandstatistics.command.feedback;

import hydraheadhunter.commandstatistics.CommandStatistics;
import hydraheadhunter.commandstatistics.command.feedback.lang.ConjugateStat;
import hydraheadhunter.commandstatistics.command.feedback.lang.ConjugateStatType;
import hydraheadhunter.commandstatistics.command.feedback.lang.FormatCustom;
import hydraheadhunter.commandstatistics.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static hydraheadhunter.commandstatistics.util.ID_IsIn.customStatIsIn;
import static net.fabricmc.fabric.api.tag.convention.v1.TagUtil.isIn;
import static net.minecraft.text.Text.stringifiedTranslatable;


public class QueryFeedback {
     private static final String DEFAULT_KEY        = "commandstatistics.feedback.query.default";
     private static final String DEFAULT_CUSTOM_KEY = "commandstatistics.feedback.query.custom";
     private static final int BLOCK  = 0;     private static final int ITEM   = 1;     private static final int ENTITY = 2;     private static final int ID     = 3;
     private static final int NO_SUCH_STAT_TYPE= -1;
     private static final int[] A_LOT = {100000, 50000, 5000, 100000};
     
     private static final String PLURALITY_BASE_KEY = CommandStatistics.MOD_ID + ".grammar.plurality";

     private static final String EMPTY         = ""           ;
     private static final String NEG_COUNT     = ".neg"       ;
     private static final String NULL_COUNT    = ".null"      ;
     private static final String SINGLE_COUNT  = ".single"    ;
     private static final String DUAL_COUNT    = ".dual"      ;
     private static final String PLURAL_COUNT  = ".plural"    ;
     private static final String A_LOT_COUNT   = ".alot"      ;
     private static final String KILLED        = ".killed"    ;
     private static final String KILLED_BY     = ".killed_by" ;
     
     private static final String MINECRAFT     = "minecraft"  ;
     
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, ServerCommandSource... source ){
          if (statType.equals(Stats.CUSTOM)) return provideCustomFeedback(player, (Identifier) statSpecific, statValue, source);
          
          int statTypeCode          = castStat(statSpecific);
          String pluralityFormat    = choosePlurality(statTypeCode, statValue, true);
          String pluralityConjugate = choosePlurality(statTypeCode, statValue, false);
          String formatKey          = chooseFormatKey(statTypeCode, statType, statSpecific, pluralityFormat);
          Text playerName           = player.getName();
          Text statTypeText         = ConjugateStatType.conjugateStatType(statType, pluralityConjugate);
          Text statSpecificText     = ConjugateStat.conjugateStat( statSpecific,statValue, pluralityConjugate );
          Text statValueText        = Text.literal( String.valueOf(statValue) );
          
          if (source.length >= 1) {
               Text finalStatSpecificText = statSpecificText;
               source[0].sendFeedback(()->stringifiedTranslatable( formatKey, playerName, statTypeText, finalStatSpecificText, statValueText ),true);
          }
          return stringifiedTranslatable( formatKey, playerName, statTypeText, statSpecificText, statValueText );
     }
     
     private static <T> int    castStat                (                                         T statSpecific                                                   ){
          try { ((Block        ) statSpecific ).getName(); return BLOCK ; } catch (ClassCastException e1) { String block = "not Block"  ;}
          try { ((Item         ) statSpecific ).getName(); return ITEM  ; } catch (ClassCastException e2) { String Item  = "not Item"   ;}
          try { ((EntityType<?>) statSpecific ).getName(); return ENTITY; } catch (ClassCastException e3) { String Entit = "not Entity" ;}
          try { ((Identifier   ) statSpecific ).getPath(); return ID    ; } catch (ClassCastException e3) { String Entit = "not Entity" ;}
          return NO_SUCH_STAT_TYPE;
          }
     private static     String choosePlurality         ( int statTypeCode,                                                        int statValue, boolean isFormat ){
          switch(statValue){
               case 0: return  (isFormat) ? NULL_COUNT   : (Text.translatable( PLURALITY_BASE_KEY + NULL_COUNT  )).getString();
               case 1: return  (isFormat) ? SINGLE_COUNT : (Text.translatable( PLURALITY_BASE_KEY + SINGLE_COUNT)).getString();
               case 2: return  (isFormat) ? DUAL_COUNT   : (Text.translatable( PLURALITY_BASE_KEY + DUAL_COUNT  )).getString();
               default:
                    String conPlural = (Text.translatable( PLURALITY_BASE_KEY + PLURAL_COUNT  )).getString();
                    String conA_Lot  = (Text.translatable( PLURALITY_BASE_KEY + A_LOT_COUNT   )).getString();
                    return (isFormat) ?
                    statValue >= A_LOT[statTypeCode] ? PLURAL_COUNT + A_LOT_COUNT : PLURAL_COUNT:
                    statValue >= A_LOT[statTypeCode] ? conPlural    + conA_Lot    : conPlural   ;
          }
     }
     private static <T> String chooseFormatKey         ( int statTypeCode, StatType<T> statType, T statSpecific, String plurality                                 ){
          String toReturn =DEFAULT_KEY.substring(0,DEFAULT_KEY.length()-8) + plurality;
          toReturn += (statType.equals(Stats.KILLED    )) ? KILLED   :EMPTY;
          toReturn += (statType.equals(Stats.KILLED_BY )) ? KILLED_BY:EMPTY;
          toReturn += chooseSpecialEntityKey( statTypeCode, statType, statSpecific);
          
          return toReturn;
     }
     private static <T> String chooseCustomKey         (                                         T statSpecific                                                   ){
          return DEFAULT_KEY; //TODO in v0.7.0 Implement all this bullshit-ass nonsense.
     }
     private static <T> String chooseSpecialEntityKey  ( int statTypeCode, StatType<T> statType, T statSpecific                                                   ){
          if (statTypeCode != ENTITY) return EMPTY;
          EntityType<?> entityType = ((EntityType<?>)statSpecific);
          String        transKey   = entityType.getTranslationKey();
          String     subTransKey   = transKey.substring(transKey.lastIndexOf("."));
          boolean     isSpecialK   = entityType.isIn(ModTags.Entity_Types.IS_SPECIAL_KILLED    );
          boolean     isSpecialKby = entityType.isIn(ModTags.Entity_Types.IS_SPECIAL_KILLED_BY );
          
          if ( statType.equals(Stats.KILLED   ) && isSpecialK  ) return subTransKey;
          if ( statType.equals(Stats.KILLED_BY) && isSpecialKby) return subTransKey;
          return EMPTY;
     }
     
     // Stats.CUSTOM methods.
     private static <T> MutableText provideCustomFeedback( ServerPlayerEntity player, Identifier statSpecific, int statValue, ServerCommandSource... source ){

          String formatKey       = FormatCustom.provideFormat(statSpecific, statValue);
          Text playerName        = player.getName();
          String verbKey         = FormatCustom.provideVerb(statSpecific, statValue);
          Text verbText          = Text.translatable(verbKey).formatted(Formatting.GOLD);
          Text numberText        = FormatCustom.provideNumberFormat(statSpecific, statValue);
          
          if (customStatIsIn(statSpecific, ModTags.Identifiers.ARG_ORDER_PLAYER_VALUE_VERB))
               return stringifiedTranslatable(formatKey,playerName,numberText,verbText);
          if (customStatIsIn(statSpecific, ModTags.Identifiers.ARG_ORDER_VALUE_PLAYER_VERB))
               return stringifiedTranslatable(formatKey,numberText,playerName,verbText);
          
          return stringifiedTranslatable(formatKey,playerName, verbText, numberText);
     }
     
}
