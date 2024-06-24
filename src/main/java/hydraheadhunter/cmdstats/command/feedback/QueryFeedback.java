package hydraheadhunter.cmdstats.command.feedback;

import hydraheadhunter.cmdstats.command.feedback.lang.KeySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static java.lang.String.valueOf;
import static net.fabricmc.fabric.api.tag.convention.v1.TagUtil.isIn;
import static net.minecraft.text.Text.*;
import static hydraheadhunter.cmdstats.command.feedback.CommonFields.*;

public class QueryFeedback {
     private static final String QUERY_KEY = "cmdstats.feedback.query";
     private static final String BASIC_KEY = "basic";
     
     public static <T> MutableText provideBasicFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue){
          String statTypeKey=  KeySelector.selectBasicStatTypeKey(statType.getName().toString());
          String statSpecKey=  KeySelector.selectStatKey( statSpec.toString());
          
          MutableText playerNameText= ((MutableText) player.getName())                                                               .formatted(PLAYER_NAME_FORMAT);
          MutableText statNameText =                literal( KeySelector.abbreviateMinecraft( join_colon(statTypeKey, statSpecKey)) ) .formatted(STAT_FORMAT  );
          MutableText statValueText=                literal( valueOf(statValue))                                                     .formatted(VALUE_FORMAT );
          return translatable( join(QUERY_KEY,BASIC_KEY), playerNameText, statNameText, statValueText);
     }
     public static <T> MutableText provideBasicUnitFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, String unitKey){
          String statTypeKey=  KeySelector.selectBasicStatTypeKey(statType.getName().toString());
          String statSpecKey=  KeySelector.selectStatKey( statSpec.toString());
          
          MutableText playerNameText= ((MutableText) player.getName())                                                               .formatted(PLAYER_NAME_FORMAT);
          MutableText statNameText =                literal( KeySelector.abbreviateMinecraft( join_colon(statTypeKey, statSpecKey)) ) .formatted(STAT_FORMAT  );
          MutableText statValueText=                literal( valueOf(statValue))                                                     .formatted(VALUE_FORMAT );
          MutableText unitText     =                translatable( join(unitKey,"label") );
          return translatable( join(QUERY_KEY,BASIC_KEY,UNIT), playerNameText, statNameText, statValueText,unitText);
     }
     
     
     public static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue){
          return null;
     }
/*

     public  static <T> MutableText provideFeedback ( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, ServerCommandSource... source ){
          if (statType.equals(Stats.CUSTOM)) return provideCustomFeedback(player, (Identifier) statSpec, statValue, source);
          
          String statTypeCode       = castStat(statSpec);
          String pluralityFormat    = choosePlurality(statTypeCode, statValue, true);
          String pluralityConjugate = choosePlurality(statTypeCode, statValue, false);
          String formatKey          = chooseFormatKey(statTypeCode, statType, statSpec, pluralityFormat);
          Text playerName           = player.getName();
          Text statTypeText         = ConjugateStatType.conjugateStatType(statType, pluralityConjugate);
          Text statSpecificText     = ConjugateStat.conjugateStat( statSpec,statValue, pluralityConjugate );
          Text statValueText        = Text.literal( valueOf(statValue) );
          
          if (source.length >= 1) {
               Text finalStatSpecificText = statSpecificText;
               source[0].sendFeedback(()->stringifiedTranslatable( formatKey, playerName, statTypeText, finalStatSpecificText, statValueText ),true);
          }
          return stringifiedTranslatable( formatKey, playerName, statTypeText, statSpecificText, statValueText );
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
     private static <T> String chooseFormatKey         ( String statTypeCode, StatType<T> statType, T statSpec, String plurality                                     ){
          String toReturn = join(BASE_KEY, plurality);
          toReturn = (statType.equals(Stats.KILLED    )) ? join(toReturn, KILLED    ):toReturn;
          toReturn = (statType.equals(Stats.KILLED_BY )) ? join(toReturn, KILLED_BY ):toReturn;
          toReturn = join(toReturn, chooseSpecialEntityKey( statTypeCode, statType, statSpec) );
          
          return toReturn;
     }
     private static <T> String chooseCustomKey         (                                         T statSpec                                                          ){
          return CUSTOM_KEY; //TODO in v0.7.0 Implement all this bullshit-ass nonsense.
     }
     private static <T> String chooseSpecialEntityKey  ( String statTypeCode, StatType<T> statType, T statSpec                                                       ){
          if (!statTypeCode.equals(ENTITY)) return EMPTY;
          EntityType<?> entityType = ((EntityType<?>)statSpec);
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
*/
}
