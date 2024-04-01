package hydraheadhunter.cmdstats.command.feedback;

import hydraheadhunter.cmdstats.command.feedback.lang.ConjugateStat;
import hydraheadhunter.cmdstats.command.feedback.lang.ConjugateStatType;
import hydraheadhunter.cmdstats.command.feedback.lang.FormatCustom;
import hydraheadhunter.cmdstats.util.ModTags;
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

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static net.fabricmc.fabric.api.tag.convention.v1.TagUtil.isIn;
import static net.minecraft.text.Text.stringifiedTranslatable;

public class QueryFeedback {
     private static final String BASE_KEY    = join( FEEDBACK_KEY , QUERY    );
     private static final String INTEGER_KEY = join( BASE_KEY     , INTEGER  );
     private static final String SCORE_KEY   = join( BASE_KEY     , SCORE    );
     
     private static final String ERROR_STAT_TYPE = join( BASE_KEY , ERROR, NO_SUCH_STAT_TYPE);
     private static final String JOIN_COLON      = join(JOIN_KEY  , "2", COLON        );
     
     private static final String CUSTOM_KEY      = join(BASE_KEY  , CUSTOM           );
     
     private static final int[] A_LOT_int = {100000, 50000, 5000, 100000};
     
     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpecific, int statValue, ServerCommandSource... source ){
          if (statType.equals(Stats.CUSTOM)) return provideCustomFeedback(player, (Identifier) statSpecific, statValue, source);
          
          String statTypeCode       = castStat(statSpecific);
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
     
     private static <T> String castStat                (                                         T statSpecific                                                   ){
          try { ((Block        ) statSpecific ).getName(); return BLOCK ; } catch (ClassCastException e1) { String block = "not Block"  ;}
          try { ((Item         ) statSpecific ).getName(); return ITEM  ; } catch (ClassCastException e2) { String Item  = "not Item"   ;}
          try { ((EntityType<?>) statSpecific ).getName(); return ENTITY; } catch (ClassCastException e3) { String Entit = "not Entity" ;}
          try { ((Identifier   ) statSpecific ).getPath(); return ID    ; } catch (ClassCastException e3) { String Id    = "not ID"     ;}
          return NO_SUCH_STAT_TYPE;
          }
     private static     String choosePlurality         ( String statTypeCode,                                                        int statValue, boolean isFormat ){
          switch(statValue){
               case 0: return  (isFormat) ? NIL : (Text.translatable( join(PLURALITY_KEY, NIL))).getString();
               case 1: return  (isFormat) ? SINGLE   : (Text.translatable( join(PLURALITY_KEY, SINGLE))).getString();
               case 2: return  (isFormat) ? DUAL     : (Text.translatable( join(PLURALITY_KEY, DUAL  ))).getString();
               default:
                    String conPlural = (Text.translatable( join(PLURALITY_KEY , PLURAL) )).getString();
                    String conA_Lot  = (Text.translatable( join(PLURALITY_KEY , A_LOT) )).getString();
                    return (isFormat) ?
                    statValue >= A_LOT_int[indexFrom(statTypeCode)] ? join(PLURAL    , A_LOT   ) : PLURAL     :
                    statValue >= A_LOT_int[indexFrom(statTypeCode)] ? join(conPlural , conA_Lot) : conPlural  ;
          }
     }
     private static <T> String chooseFormatKey         ( String statTypeCode, StatType<T> statType, T statSpecific, String plurality                                 ){
          String toReturn = join(BASE_KEY, plurality);
          toReturn = (statType.equals(Stats.KILLED    )) ? join(toReturn, KILLED    ):toReturn;
          toReturn = (statType.equals(Stats.KILLED_BY )) ? join(toReturn, KILLED_BY ):toReturn;
          toReturn = join(toReturn, chooseSpecialEntityKey( statTypeCode, statType, statSpecific) );
          
          return toReturn;
     }
     private static <T> String chooseCustomKey         (                                         T statSpecific                                                   ){
          return CUSTOM_KEY; //TODO in v0.7.0 Implement all this bullshit-ass nonsense.
     }
     private static <T> String chooseSpecialEntityKey  ( String statTypeCode, StatType<T> statType, T statSpecific                                                   ){
          if (!statTypeCode.equals(ENTITY)) return EMPTY;
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
     
     private static int indexFrom (String str){
          switch (str){
               case BLOCK : return 0;
               case ITEM  : return 1;
               case ENTITY: return 2;
               case ID    : return 4;
               default    : return 0;
               
          }
     }
     
}
