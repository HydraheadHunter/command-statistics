package hydraheadhunter.commandstatistics.util;

import hydraheadhunter.commandstatistics.CommandStatistics;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

//These tags are a hold over from when I was avoiding figuring out how to fix my STATS custom code by doing fancy translation stuff.
// I ripped all that out in favor of bare bones feedback. I might add it back in later when polishing, and if I do, I'll be needing theses.
public class ModTags {
     public static class Blocks{
          public static final TagKey<Block> IS_IRREGULAR_NULL         = createTag("is_irregular_null"      );
          public static final TagKey<Block> IS_IRREGULAR_SINGLE       = createTag("is_irregular_single"    );
          public static final TagKey<Block> IS_IRREGULAR_DUAL         = createTag("is_irregular_dual"      );
          public static final TagKey<Block> IS_IRREGULAR_PLURAL       = createTag("is_irregular_plural"    );
          public static final TagKey<Block> IS_IRREGULAR_MASS         = createTag("is_irregular_mass"      );
          public static final TagKey<Block> IS_DEFINITE               = createTag("is_definite"            );
          public static final TagKey<Block> IS_INDEFINITE             = createTag("is_indefinite"          );

          public static final TagKey<Block> IS_GENDER_0               = createTag("is_gender_0"            );
          public static final TagKey<Block> IS_GENDER_1               = createTag("is_gender_1"            );
          public static final TagKey<Block> IS_GENDER_2               = createTag("is_gender_2"            );
          public static final TagKey<Block> IS_GENDER_3               = createTag("is_gender_3"            );
          public static final TagKey<Block> IS_GENDER_4               = createTag("is_gender_4"            );
          public static final TagKey<Block> IS_GENDER_5               = createTag("is_gender_5"            );
          public static final TagKey<Block> IS_GENDER_6               = createTag("is_gender_6"            );
          public static final TagKey<Block> IS_GENDER_7               = createTag("is_gender_7"            );
          public static final TagKey<Block> IS_GENDER_8               = createTag("is_gender_8"            );
          public static final TagKey<Block> IS_GENDER_9               = createTag("is_gender_9"            );
          public static final TagKey<Block> IS_GENDER_10              = createTag("is_gender_10"           );
          public static final TagKey<Block> IS_GENDER_11              = createTag("is_gender_11"           );
          public static final TagKey<Block> IS_GENDER_12              = createTag("is_gender_12"           );
          public static final TagKey<Block> IS_GENDER_13              = createTag("is_gender_13"           );
          public static final TagKey<Block> IS_GENDER_14              = createTag("is_gender_14"           );
          public static final TagKey<Block> IS_GENDER_15              = createTag("is_gender_15"           );


          public static TagKey<Block> createTag(String name){
               return TagKey.of(RegistryKeys.BLOCK, new Identifier(CommandStatistics.MOD_ID, name));

          }

     }
     public static class Items{
          public static final TagKey<Item> IS_IRREGULAR_NULL         = createTag("is_irregular_null"      );
          public static final TagKey<Item> IS_IRREGULAR_SINGLE       = createTag("is_irregular_single"    );
          public static final TagKey<Item> IS_IRREGULAR_DUAL         = createTag("is_irregular_dual"      );
          public static final TagKey<Item> IS_IRREGULAR_PLURAL       = createTag("is_irregular_plural"    );
          public static final TagKey<Item> IS_IRREGULAR_MASS         = createTag("is_irregular_mass"      );
          public static final TagKey<Item> IS_DEFINITE               = createTag("is_definite"            );
          public static final TagKey<Item> IS_INDEFINITE             = createTag("is_indefinite"          );

          public static final TagKey<Item> IS_GENDER_0               = createTag("is_gender_0"            );
          public static final TagKey<Item> IS_GENDER_1               = createTag("is_gender_1"            );
          public static final TagKey<Item> IS_GENDER_2               = createTag("is_gender_2"            );
          public static final TagKey<Item> IS_GENDER_3               = createTag("is_gender_3"            );
          public static final TagKey<Item> IS_GENDER_4               = createTag("is_gender_4"            );
          public static final TagKey<Item> IS_GENDER_5               = createTag("is_gender_5"            );
          public static final TagKey<Item> IS_GENDER_6               = createTag("is_gender_6"            );
          public static final TagKey<Item> IS_GENDER_7               = createTag("is_gender_7"            );
          public static final TagKey<Item> IS_GENDER_8               = createTag("is_gender_8"            );
          public static final TagKey<Item> IS_GENDER_9               = createTag("is_gender_9"            );
          public static final TagKey<Item> IS_GENDER_10              = createTag("is_gender_10"           );
          public static final TagKey<Item> IS_GENDER_11              = createTag("is_gender_11"           );
          public static final TagKey<Item> IS_GENDER_12              = createTag("is_gender_12"           );
          public static final TagKey<Item> IS_GENDER_13              = createTag("is_gender_13"           );
          public static final TagKey<Item> IS_GENDER_14              = createTag("is_gender_14"           );
          public static final TagKey<Item> IS_GENDER_15              = createTag("is_gender_15"           );

          public static TagKey<Item> createTag(String name){
               return TagKey.of(RegistryKeys.ITEM, new Identifier(CommandStatistics.MOD_ID, name));

          }
     }

     public static class Entity_Types {
          public static final TagKey<EntityType<?>> IS_IRREGULAR_NULL         = createTag("is_irregular_null"      );
          public static final TagKey<EntityType<?>> IS_IRREGULAR_SINGLE       = createTag("is_irregular_single"    );
          public static final TagKey<EntityType<?>> IS_IRREGULAR_DUAL         = createTag("is_irregular_dual"      );
          public static final TagKey<EntityType<?>> IS_IRREGULAR_PLURAL       = createTag("is_irregular_plural"    );
          public static final TagKey<EntityType<?>> IS_IRREGULAR_MASS         = createTag("is_irregular_mass"      );
          public static final TagKey<EntityType<?>> IS_DEFINITE               = createTag("is_definite"            );
          public static final TagKey<EntityType<?>> IS_INDEFINITE             = createTag("is_indefinite"          );

          public static final TagKey<EntityType<?>> IS_GENDER_0               = createTag("is_gender_0"            );
          public static final TagKey<EntityType<?>> IS_GENDER_1               = createTag("is_gender_1"            );
          public static final TagKey<EntityType<?>> IS_GENDER_2               = createTag("is_gender_2"            );
          public static final TagKey<EntityType<?>> IS_GENDER_3               = createTag("is_gender_3"            );
          public static final TagKey<EntityType<?>> IS_GENDER_4               = createTag("is_gender_4"            );
          public static final TagKey<EntityType<?>> IS_GENDER_5               = createTag("is_gender_5"            );
          public static final TagKey<EntityType<?>> IS_GENDER_6               = createTag("is_gender_6"            );
          public static final TagKey<EntityType<?>> IS_GENDER_7               = createTag("is_gender_7"            );
          public static final TagKey<EntityType<?>> IS_GENDER_8               = createTag("is_gender_8"            );
          public static final TagKey<EntityType<?>> IS_GENDER_9               = createTag("is_gender_9"            );
          public static final TagKey<EntityType<?>> IS_GENDER_10              = createTag("is_gender_10"           );
          public static final TagKey<EntityType<?>> IS_GENDER_11              = createTag("is_gender_11"           );
          public static final TagKey<EntityType<?>> IS_GENDER_12              = createTag("is_gender_12"           );
          public static final TagKey<EntityType<?>> IS_GENDER_13              = createTag("is_gender_13"           );
          public static final TagKey<EntityType<?>> IS_GENDER_14              = createTag("is_gender_14"           );
          public static final TagKey<EntityType<?>> IS_GENDER_15              = createTag("is_gender_15"           );
          
          public static final TagKey<EntityType<?>> IS_SPECIAL_KILLED         = createTag("is_special_killed"      );
          public static final TagKey<EntityType<?>> IS_SPECIAL_KILLED_BY      = createTag("is_special_killed_by"   );
          

          public static TagKey<EntityType<?>> createTag(String name){
               return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(CommandStatistics.MOD_ID, name));

          }
     }

     public static class Identifiers{
          public static final TagKey<Identifier> IS_IRREGULAR_NULL         = createTag("is_irregular_null"      );
          public static final TagKey<Identifier> IS_IRREGULAR_SINGLE       = createTag("is_irregular_single"    );
          public static final TagKey<Identifier> IS_IRREGULAR_DUAL         = createTag("is_irregular_dual"      );
          public static final TagKey<Identifier> IS_IRREGULAR_PLURAL       = createTag("is_irregular_plural"    );
          public static final TagKey<Identifier> IS_IRREGULAR_MASS         = createTag("is_irregular_mass"      );

          public static final TagKey<Identifier> IS_GENDER_0               = createTag("is_gender_0"            );
          public static final TagKey<Identifier> IS_GENDER_1               = createTag("is_gender_1"            );
          public static final TagKey<Identifier> IS_GENDER_2               = createTag("is_gender_2"            );
          public static final TagKey<Identifier> IS_GENDER_3               = createTag("is_gender_3"            );
          public static final TagKey<Identifier> IS_GENDER_4               = createTag("is_gender_4"            );
          public static final TagKey<Identifier> IS_GENDER_5               = createTag("is_gender_5"            );
          public static final TagKey<Identifier> IS_GENDER_6               = createTag("is_gender_6"            );
          public static final TagKey<Identifier> IS_GENDER_7               = createTag("is_gender_7"            );
          public static final TagKey<Identifier> IS_GENDER_8               = createTag("is_gender_8"            );
          public static final TagKey<Identifier> IS_GENDER_9               = createTag("is_gender_9"            );
          public static final TagKey<Identifier> IS_GENDER_10              = createTag("is_gender_10"           );
          public static final TagKey<Identifier> IS_GENDER_11              = createTag("is_gender_11"           );
          public static final TagKey<Identifier> IS_GENDER_12              = createTag("is_gender_12"           );
          public static final TagKey<Identifier> IS_GENDER_13              = createTag("is_gender_13"           );
          public static final TagKey<Identifier> IS_GENDER_14              = createTag("is_gender_14"           );
          public static final TagKey<Identifier> IS_GENDER_15              = createTag("is_gender_15"           );

          public static TagKey<Identifier> createTag(String name){
               return TagKey.of(RegistryKeys.CUSTOM_STAT, new Identifier(CommandStatistics.MOD_ID, name));

          }
     }
     
     

}
