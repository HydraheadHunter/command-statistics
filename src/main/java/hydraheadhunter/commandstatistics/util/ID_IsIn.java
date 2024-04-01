package hydraheadhunter.commandstatistics.util;

import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class ID_IsIn {
     
     public static boolean customStatIsIn( Identifier stat, TagKey<Identifier> tagKey) {
          return Stats.CUSTOM.getRegistry().getEntry(stat).isIn( tagKey );
     }
     
}
