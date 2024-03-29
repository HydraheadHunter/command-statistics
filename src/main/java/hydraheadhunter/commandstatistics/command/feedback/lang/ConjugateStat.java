package hydraheadhunter.commandstatistics.command.feedback.lang;

import hydraheadhunter.commandstatistics.CommandStatistics;
import hydraheadhunter.commandstatistics.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConjugateStat {
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
     
     private static final String AIR_KEY               = "block.minecraft.air";
     private static final String GRAMMAR_KEY_ROOT = CommandStatistics.MOD_ID + ".grammar";
     private static final String AFFIX_KEY_ROOT = GRAMMAR_KEY_ROOT + ".affix";
     private static final String DEFINITE_KEY_ROOT = GRAMMAR_KEY_ROOT + ".definite";
     private static final String INDEFINITE_KEY_ROOT = GRAMMAR_KEY_ROOT + ".indefinite";
     private static final String GENDER_ROOT = ".gender_";
     
     private static final String NEG_COUNT = ".neg";
     private static final String NULL_COUNT = ".null";
     private static final String SINGLE_COUNT = ".single";
     private static final String DUAL_COUNT = ".dual";
     private static final String PLURAL_COUNT = ".plural";
     private static final String IRREGULAR = ".irregular";
     private static final String REGULAR = "";
     
     public static <T> MutableText conjugateStat( T statSpecific, int statValue ) {
     //Set all logic variable
          int objectType = castStat_2(statSpecific);
          String workingTranslationKey = chooseDefaultKey_2(objectType, statSpecific);
          String plurality = choosePlurality_2(statValue);
          String irregularity = isIrregular(objectType, statSpecific, statValue);
          int article = chooseArticle_2(objectType, statSpecific);
          int[] genders = chooseGender(objectType, statSpecific);
          
     //get root word
          MutableText rootWord, affixedWord;
          if (isIrregular(irregularity)) {
               workingTranslationKey = stitch(cut(objectType, workingTranslationKey), plurality);
               rootWord = Text.translatable(workingTranslationKey);
               affixedWord = rootWord;
          }
          else {
     //get affixed word
               rootWord = Text.translatable(workingTranslationKey);
               affixedWord = rootWord;
               if (isGender(genders, 0))
                    affixedWord = Text.stringifiedTranslatable(AFFIX_KEY_ROOT + plurality + GENDER_ROOT + "0", rootWord);
               for (int ii = 1;ii <= 15;ii += 1) {
                    if (isGender(genders, ii))
                         affixedWord = Text.stringifiedTranslatable(AFFIX_KEY_ROOT + plurality + GENDER_ROOT + String.valueOf(ii), affixedWord);
               }
          }
          affixedWord.formatted(Formatting.GOLD);

          //get articles
          MutableText articledWord = affixedWord;
          if (isGender(genders, 0) && article == DEFINITE)
               articledWord = Text.stringifiedTranslatable(DEFINITE_KEY_ROOT + plurality + GENDER_ROOT + "0", affixedWord);
          if (isGender(genders, 0) && article == INDEFINITE)
               articledWord = Text.stringifiedTranslatable(INDEFINITE_KEY_ROOT + plurality + GENDER_ROOT + "0", affixedWord);
          for (int ii = 1;ii <= 15;ii += 1) {
               if (isGender(genders, ii) && article == DEFINITE)
                    articledWord = Text.stringifiedTranslatable(DEFINITE_KEY_ROOT + plurality + GENDER_ROOT + String.valueOf(ii), articledWord);
               if (isGender(genders, ii) && article == INDEFINITE)
                    articledWord = Text.stringifiedTranslatable(INDEFINITE_KEY_ROOT + plurality + GENDER_ROOT + String.valueOf(ii), articledWord);
          }
          
          return articledWord;
          
     }
     private static <T> int castStat_2(                    T object             ) {
          try {
               Block block = (Block) object;
               return BLOCK;
          }
          catch (ClassCastException e1) {
               try {
                    Item item = (Item) object;
                    Block  block = Block.getBlockFromItem(item);
                    return block.getTranslationKey().equals(AIR_KEY) ? ITEM:BLOCK_ITEM;
               }
               catch (ClassCastException e2) {
                    try {
                         ( (EntityType<?>) object ).isIn(ModTags.Entities.IS_DEFINITE);
                         return ENTITY;
                    }
                    catch (ClassCastException e3) {
                         return -1;
                    }
               }
          }
     }
     
     private static <T> String chooseDefaultKey_2 ( int   objectType,  T object             ) {
          Block objectBlock = null;
          switch (objectType) {
               case BLOCK_ITEM: objectBlock = Block.getBlockFromItem( (Item) object);
               case BLOCK:  return ( (Block) (objectBlock==null ? object:objectBlock) ).getTranslationKey();
               case ITEM:   return ( (Item)                       object              ).getTranslationKey();
               case ENTITY: return ( (EntityType<?>)              object              ).getTranslationKey();
               default: return "";
          }
     }
     
     private static     String choosePlurality_2  ( int   count                             ) {
          return count == 0 ? NULL_COUNT :
                 count == 1 ? SINGLE_COUNT :
                 count == 2 ? DUAL_COUNT :
                 count >= 3 ? PLURAL_COUNT :
                 NEG_COUNT;
     }
     private static <T> int    chooseArticle_2    ( int   objectType,  T object             ) {
          if (isIndefinite_2(objectType, object)) return INDEFINITE;
          if (isDefinite(objectType, object)) return DEFINITE;
          return 0;
     }
     
     private static <T> boolean isIndefinite_2    ( int   objectType,  T object             ) {
          switch(objectType){
               case BLOCK:
                    return((Block)object ).getDefaultState().isIn( ModTags.Blocks.IS_INDEFINITE );
               case BLOCK_ITEM:
                    return (Block.getBlockFromItem((Item)object )).getDefaultState().isIn( ModTags.Blocks.IS_INDEFINITE );
               case ITEM:
                    return((Item)object ).getDefaultStack().isIn( ModTags.Items.IS_INDEFINITE );
               case ENTITY:
                    return((EntityType<?>)object ).isIn( ModTags.Entities.IS_INDEFINITE );
               
               default:
                    return false;
          }
     }
     
     private static <T> boolean isDefinite        ( int   objectType,  T object             ) {
               switch (objectType) {
                    case BLOCK:
                         return ( (Block) object ).getDefaultState().isIn(ModTags.Blocks.IS_DEFINITE);
                    case BLOCK_ITEM:
                         return (Block.getBlockFromItem((Item)object )).getDefaultState().isIn( ModTags.Blocks.IS_DEFINITE );
                    case ITEM:
                         return ( (Item) object ).getDefaultStack().isIn(ModTags.Items.IS_DEFINITE);
                    case ENTITY:
                         return ( (EntityType<?>) object ).isIn(ModTags.Entities.IS_DEFINITE);
                    default:
                         return false;
               }
          }
          private static     boolean  isFlat      ( int   article                           ) {
          return article == FLAT;
          
     }
     
     private static <T> String   isIrregular      ( int    objectType,  T object, int count ) {
          switch (objectType) {
               case BLOCK:
                    BlockState blockState = ( (Block) object ).getDefaultState();
                    if (blockState.isIn(ModTags.Blocks.IS_IRREGULAR_MASS)) return IRREGULAR;
                    switch (count) {
                         case 0:  return blockState.isIn(ModTags.Blocks.IS_IRREGULAR_NULL  ) ? IRREGULAR : REGULAR;
                         case 1:  return blockState.isIn(ModTags.Blocks.IS_IRREGULAR_SINGLE) ? IRREGULAR : REGULAR;
                         case 2:  return blockState.isIn(ModTags.Blocks.IS_IRREGULAR_DUAL  ) ? IRREGULAR : REGULAR;
                         default: return blockState.isIn(ModTags.Blocks.IS_IRREGULAR_PLURAL) ? IRREGULAR : REGULAR;
                    }
               case ITEM:
                    ItemStack itemStack = ( (Item) object ).getDefaultStack();
                    if (itemStack.isIn(ModTags.Items.IS_IRREGULAR_MASS)) return IRREGULAR;
                    switch (count) {
                         case 0:  return itemStack.isIn(ModTags.Items.IS_IRREGULAR_NULL  ) ? IRREGULAR : REGULAR;
                         case 1:  return itemStack.isIn(ModTags.Items.IS_IRREGULAR_SINGLE) ? IRREGULAR : REGULAR;
                         case 2:  return itemStack.isIn(ModTags.Items.IS_IRREGULAR_DUAL  ) ? IRREGULAR : REGULAR;
                         default: return itemStack.isIn(ModTags.Items.IS_IRREGULAR_PLURAL) ? IRREGULAR : REGULAR;
                    }
               case ENTITY:
                    EntityType<?> entityType = ( (EntityType<?>) object );
                    if (entityType.isIn(ModTags.Entities.IS_IRREGULAR_MASS)) return IRREGULAR;
                    switch (count) {
                         case 0:  return entityType.isIn(ModTags.Entities.IS_IRREGULAR_NULL  ) ? IRREGULAR : REGULAR;
                         case 1:  return entityType.isIn(ModTags.Entities.IS_IRREGULAR_SINGLE) ? IRREGULAR : REGULAR;
                         case 2:  return entityType.isIn(ModTags.Entities.IS_IRREGULAR_DUAL  ) ? IRREGULAR : REGULAR;
                         default: return entityType.isIn(ModTags.Entities.IS_IRREGULAR_PLURAL) ? IRREGULAR : REGULAR;
                    }
               default:           return REGULAR;
          }
          
          
     }
     private static     boolean  isIrregular      ( String irregularity                     ) {
          return irregularity.equals(IRREGULAR);
          
     }
     
     private static <T> int[]    chooseGender     ( int    objectType,  T object            ) {
          int[] toReturn = { 1, 1 };
          Block object_2 = null;
          switch (objectType) {
               case BLOCK_ITEM:
                    object_2 = Block.getBlockFromItem( ((Item) object) );
               case BLOCK:
                    if (object_2 == null) object_2= (Block) object;
                    BlockState blockState = ( (Block) object_2 ).getDefaultState();
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_0)  ) ? 2  : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_1)  ) ? 3  : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_2)  ) ? 5  : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_3)  ) ? 7  : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_4)  ) ? 11 : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_5)  ) ? 13 : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_6)  ) ? 17 : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_7)  ) ? 19 : 1;
                    toReturn[0] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_8)  ) ? 23 : 1;
                    toReturn[1] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_9)  ) ? 2  : 1;
                    toReturn[1] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_10) ) ? 3  : 1;
                    toReturn[1] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_11) ) ? 5  : 1;
                    toReturn[1] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_12) ) ? 7  : 1;
                    toReturn[1] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_13) ) ? 11 : 1;
                    toReturn[1] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_14) ) ? 13 : 1;
                    toReturn[1] *= ( blockState.isIn(ModTags.Blocks.IS_GENDER_15) ) ? 17 : 1;
                    break;
               case ITEM:
                    ItemStack itemStack = ( (Item) object ).getDefaultStack();
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_0)  ) ?  2 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_1)  ) ?  3 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_2)  ) ?  5 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_3)  ) ?  7 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_4)  ) ? 11 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_5)  ) ? 13 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_6)  ) ? 17 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_7)  ) ? 19 : 1;
                    toReturn[0] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_8)  ) ? 23 : 1;
                    toReturn[1] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_9)  ) ?  2 : 1;
                    toReturn[1] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_10) ) ?  3 : 1;
                    toReturn[1] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_11) ) ?  5 : 1;
                    toReturn[1] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_12) ) ?  7 : 1;
                    toReturn[1] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_13) ) ? 11 : 1;
                    toReturn[1] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_14) ) ? 13 : 1;
                    toReturn[1] *= ( itemStack.isIn(ModTags.Items.IS_GENDER_15) ) ? 17 : 1;
                    break;
               case ENTITY:
                    EntityType<?> entityType = ( (EntityType<?>) object );
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_0)  ) ?  2 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_1)  ) ?  3 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_2)  ) ?  5 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_3)  ) ?  7 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_4)  ) ? 11 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_5)  ) ? 13 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_6)  ) ? 17 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_7)  ) ? 19 : 1;
                    toReturn[0] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_8)  ) ? 23 : 1;
                    toReturn[1] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_9)  ) ?  2 : 1;
                    toReturn[1] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_10) ) ?  3 : 1;
                    toReturn[1] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_11) ) ?  5 : 1;
                    toReturn[1] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_12) ) ?  7 : 1;
                    toReturn[1] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_13) ) ? 11 : 1;
                    toReturn[1] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_14) ) ? 13 : 1;
                    toReturn[1] *= ( entityType.isIn(ModTags.Entities.IS_GENDER_15) ) ? 17 : 1;
                    break;
          }
          return toReturn;
     }
     private static     boolean  isGender         ( int[]  genders,     int    key          ) {
          return genders[key < 9 ? 0 : 1] % PRIMES[key % 9] == 0;
     }
     
     private static     String   cut              ( int    objectType,  String key          ) {
          switch (objectType) {
               case BLOCK: case BLOCK_ITEM:
                    return key.substring(BLOCK_STRING_CUT);
               case ITEM:
                    return key.substring(ITEM_STRING_CUT);
               case ENTITY:
                    return key.substring(ENTITY_STRING_CUT);
               default:
                    return key;
          }
     }
     private static     String   stitch           ( String key,         String plurality    ) {
          return CommandStatistics.MOD_ID + key + plurality + IRREGULAR;
          
     }
}



