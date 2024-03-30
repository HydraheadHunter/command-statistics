package hydraheadhunter.commandstatistics.command.feedback.lang;

import hydraheadhunter.commandstatistics.CommandStatistics;
import hydraheadhunter.commandstatistics.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConjugateStatType {
     private static final int[] PRIMES = { 2, 3, 5, 7, 11, 13, 17, 19, 23 };
     private static final int BLOCK_STRING_CUT = 15;
     private static final int ITEM_STRING_CUT = 14;
     private static final int ENTITY_STRING_CUT = 16;
     
     private static final int BLOCK_ITEM=2;
     private static final int BLOCK = 3;
     private static final int ITEM = 5;
     private static final int ENTITY = 7;
     
     private static final int FLAT = 0;
     private static final int INDEFINITE = 1;
     private static final int DEFINITE = 2;
     
     private static final String AIR_KEY             = "block.minecraft.air";
     private static final String GRAMMAR_KEY_ROOT    = CommandStatistics.MOD_ID + ".grammar";
     private static final String AFFIX_KEY_ROOT      = GRAMMAR_KEY_ROOT + ".affix";
     private static final String DEFINITE_KEY_ROOT   = GRAMMAR_KEY_ROOT + ".definite";
     private static final String INDEFINITE_KEY_ROOT = GRAMMAR_KEY_ROOT + ".indefinite";
     private static final String GENDER_ROOT = ".gender_";
     
     private static final String NEG_COUNT    = ".neg"    ;
     private static final String NULL_COUNT   = ".null"   ;
     private static final String SINGLE_COUNT = ".single" ;
     private static final String DUAL_COUNT   = ".dual"   ;
     private static final String PLURAL_COUNT = ".plural" ;
     
     private static final String BASE_TENSE_KEY   = CommandStatistics.MOD_ID + ".grammar.tense";
     private static final String BASE_TYPE_KEY    = CommandStatistics.MOD_ID + ".stattype"     ;
     private static final String MINED      = ".mined"     ;
     private static final String CRAFTED    = ".mined"     ;
     private static final String USED       = ".used"      ;
     private static final String BROKEN     = ".broken"    ;
     private static final String PICKED_UP  = ".picked_up" ;
     private static final String DROPPED    = ".dropped"   ;
     private static final String KILLED     = ".killed"    ;
     private static final String KILLED_BY  = ".killed_by" ;
     private static final String CUSTOM     = ".custom"    ;
     private static final String EMPTY      = ""           ;
     
     public static MutableText conjugateStatType( StatType<?> statType, int statValue ) {
     //Set all logic variable
          String workingKey = BASE_TENSE_KEY;
          String statTypeKey = chooseStatType(statType);
          String plurality= choosePlurality(statValue);
     
          workingKey += statTypeKey + plurality;
          String tenseKey = Text.translatable(workingKey).getString();
          workingKey = BASE_TYPE_KEY + statTypeKey + tenseKey ;
          
          return Text.translatable(workingKey);
          
     }
     private static String chooseStatType( StatType<?> statType ) {
          if (statType.equals(Stats.MINED)     ) { return MINED     ; }
          if (statType.equals(Stats.CRAFTED)   ) { return CRAFTED   ; }
          if (statType.equals(Stats.USED)      ) { return USED      ; }
          if (statType.equals(Stats.BROKEN)    ) { return BROKEN    ; }
          if (statType.equals(Stats.PICKED_UP) ) { return PICKED_UP ; }
          if (statType.equals(Stats.DROPPED)   ) { return DROPPED   ; }
          if (statType.equals(Stats.KILLED)    ) { return KILLED    ; }
          if (statType.equals(Stats.KILLED_BY) ) { return KILLED_BY ; }
          if (statType.equals(Stats.CUSTOM)    ) { return CUSTOM    ; }
          return EMPTY;
          
     }
     
     private static     String choosePlurality  ( int   count                             ) {
          return count == 0 ? NULL_COUNT   :
                 count == 1 ? SINGLE_COUNT :
                 count == 2 ? DUAL_COUNT   :
                 count >= 3 ? PLURAL_COUNT :
                              NEG_COUNT    ;
     }
     
}



