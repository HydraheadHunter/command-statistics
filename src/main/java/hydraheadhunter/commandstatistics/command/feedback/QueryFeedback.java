package hydraheadhunter.commandstatistics.command.feedback;

import hydraheadhunter.commandstatistics.command.feedback.lang.ConjugateStat;
import hydraheadhunter.commandstatistics.command.feedback.lang.ConjugateStatType;
import hydraheadhunter.commandstatistics.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.tag.convention.v1.TagUtil.isIn;
import static net.minecraft.text.Text.stringifiedTranslatable;


public class QueryFeedback {
     private static final String DEFAULT_KEY        = "commandstatistics.feedback.query.default";
     private static final String DEFAULT_CUSTOM_KEY = "commandstatistics.feedback.query.custom";
     private static final int BLOCK  = 0;     private static final int ITEM   = 1;     private static final int ENTITY = 2;     private static final int ID     = 3;
     private static final int NO_SUCH_STAT_TYPE= -1;
     private static final int[] A_LOT = {100000, 50000, 5000, -1};
     
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
          
          int statTypeCode       = castStat(statSpecific);
          String plurality       = choosePlurality(statTypeCode, statValue);
          String formatKey       = chooseFormatKey(statTypeCode, statType, statSpecific, plurality);
          Text playerName        = player.getName();
          Text statTypeText      = ConjugateStatType.conjugateStatType(statType, statValue);
          Text statSpecificText  = ConjugateStat.conjugateStat( statSpecific,statValue );
          Text statValueText     = Text.literal( String.valueOf(statValue) );
          
          if (source.length >= 1) {
               Text finalStatSpecificText = statSpecificText;
               source[0].sendFeedback(()->stringifiedTranslatable( formatKey, playerName, statTypeText, finalStatSpecificText, statValueText ),true);
          }
          return stringifiedTranslatable( formatKey, playerName, statTypeText, statSpecificText, statValueText );
     }
     
     private static <T> int    castStat        ( T object ) {
          try { ((Block        ) object ).getName(); return BLOCK ; } catch (ClassCastException e1) { String block = "not Block"  ;}
          try { ((Item         ) object ).getName(); return ITEM  ; } catch (ClassCastException e2) { String Item  = "not Item"   ;}
          try { ((EntityType<?>) object ).getName(); return ENTITY; } catch (ClassCastException e3) { String Entit = "not Entity" ;}
          try { ((Identifier   ) object ).getPath(); return ID    ; } catch (ClassCastException e3) { String Entit = "not Entity" ;}
          return NO_SUCH_STAT_TYPE;
          }
     private static     String choosePlurality ( int statTypeCode,                                       int statValue ){
          switch(statValue){
               case 0: return NULL_COUNT;
               case 1: return SINGLE_COUNT;
               case 2: return DUAL_COUNT;
               default: return statValue >= A_LOT[statTypeCode] ? PLURAL_COUNT + A_LOT_COUNT : PLURAL_COUNT;
          }
     }
     private static <T> String chooseFormatKey ( int statTypeCode, StatType<T> statType, T statSpecific, String plurality ) {
          if (statType.equals(Stats.CUSTOM)) return chooseCustomKey(statSpecific);
          String toReturn =DEFAULT_KEY.substring(0,DEFAULT_KEY.length()-8) + plurality;
          toReturn += (statType.equals(Stats.KILLED    )) ? KILLED   :EMPTY;
          toReturn += (statType.equals(Stats.KILLED_BY )) ? KILLED_BY:EMPTY;
          toReturn += chooseSpecialEntityKey( statTypeCode, statType, statSpecific);
          
          return toReturn;
     }
     private static <T> String chooseCustomKey         (                   T statSpecific ){
          return DEFAULT_KEY; //TODO in v0.7.0 Implement all this bullshit-ass nonsense.
     }
     private static <T> String chooseSpecialEntityKey  ( int statTypeCode, StatType<T> statType, T statSpecific){
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
          String plurality       = choosePlurality(statValue);
          String formatKey       = chooseFormatKey(statSpecific, plurality);
          Text playerName        = player.getName();
          if ( Stats.CUSTOM.getRegistry().getEntry(statSpecific).isIn(ModTags.Identifiers.IS_GENDER_0) );
               System.out.println();
          return stringifiedTranslatable(formatKey,playerName,String.valueOf(statValue));
     }
     
     
     private static String  choosePlurality ( int statValue ){
          switch(statValue){
               case 0: return  NULL_COUNT;
               case 1: return  SINGLE_COUNT;
               case 2: return  DUAL_COUNT;
               default: return PLURAL_COUNT;
          }
     }
     private static String  chooseFormatKey ( Identifier statSpecific, String plurality ) {
          String statSpecificString= (statSpecific.toString()).replace(':','.');
          if ( statSpecificString.substring(0,MINECRAFT.length()).equals(MINECRAFT) )
               statSpecificString = statSpecificString.substring(MINECRAFT.length());
          else
               statSpecificString = "." + statSpecificString;
          return DEFAULT_CUSTOM_KEY + statSpecificString + plurality;
          
     }
     
     
     
     
}
