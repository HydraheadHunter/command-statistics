package hydraheadhunter.cmdstats.command.feedback.lang;


import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static hydraheadhunter.cmdstats.CommandStatistics.*;

public class ConjugateStatType {
    
     
     public static MutableText conjugateStatType( StatType<?> statType, String plurality ) {
     //Set all logic variable
          String workingKey;
          String statTypeStr = chooseStatType(statType);
     
          workingKey= join(TENSE_KEY, statTypeStr, plurality   );
          String tenseKey = Text.translatable(workingKey).getString();
          workingKey = join(STATTYPE_KEY, statTypeStr , tenseKey   );
          
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
     
     private static String choosePlurality  ( int count  ) {
          return count == 0 ? NIL       :
                 count == 1 ? SINGLE    :
                 count == 2 ? DUAL      :
                 count >= 3 ? PLURAL    :
                              NEGATIVE  ;
     }
     
}



