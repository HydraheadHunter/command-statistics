package hydraheadhunter.cmdstats;

import hydraheadhunter.cmdstats.util.ModTags;
import net.fabricmc.api.ModInitializer;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hydraheadhunter.cmdstats.util.ModRegistries;

import static hydraheadhunter.cmdstats.util.units.iUnit.*;

public class CommandStatistics implements ModInitializer {
	public static final String MOD_ID 		= "cmdstats"		;
	public static final String MINECRAFT	= "minecraft"		;	public static final String MC = "mc";
	
	//Defining /statistics
	public static final String ROOT_COMMAND	= "statistics"	;	public static final String TARGETS     = "targets"   ;
	public static final String STAT        = "stat"        ;	public static final String AMOUNT       = "amount"   ;	public static final String OBJECTIVE   = "objective" ;
	
	public static final String QUERY    	= "query"	 	;	public static final String ADD     = "add"   	;	public static final String STORE	= "store"  ;
	public static final String SET          = "set"  	 	;	public static final String REDUCE  = "reduce"	;	public static final String PROJECT	= "project";
	public static final String SYNC		= "sync"		;
	public static final String INTEGER      = "integer"	;	public static final String SCORE  	= "score" 	;
	public static final String LIST   		= "list"		;	public static final String START	= "start"		;    public static final String STOP 	= "stop"		;
	public static final String PAUSE 		= "pause"		;	public static final String ALL  	= "all"  		;	public static final String PROJECT_NAME= "project name";
	public static final String DUMMY		= "dummy"		;	public static final String UNIT	= "unit"		;
	
	public static final String STAT_TYPE    = "stat_type"  ;
	public static final String MINED       	= "mined"     	;	public static final String CRAFTED     = "crafted"   ;	public static final String USED     	= "used"    ;
	public static final String BROKEN       = "broken"     ;	public static final String PICKED_UP   = "picked_up" ;	public static final String DROPPED    	= "dropped" ;
	public static final String KILLED       = "killed"    	;	public static final String KILLED_BY   = "killed_by" ;
	public static final String CUSTOM      	= "custom"    	;
	
	public static final int    STORE_OP    = 1           	;	public static final int    ADD_OP      = 2         ;	public static final int    SET_OP      = 3;
	public static final int    REDUCE_OP   = 3             ;	public static final int	  PROJECT_OP  = 1		 ;   public static final int    MINIMUM_STAT_VALUE = 0  ;
	
	
//Unit Conversion Factors

//Time

//Translation Keys
	public static final String FEEDBACK_KEY = join(MOD_ID, "feedback" 	 )	;	public static final String SYSTEM_KEY	= join(MOD_ID, "system"		);	public static final String GRAMMAR_KEY  = join(SYSTEM_KEY, "grammar"   )	;
	public static final String STATTYPE_KEY = join(MOD_ID, "stattype"      );
	public static final String PLURALITY_KEY= join(GRAMMAR_KEY, "plurality")	;	public static final String JOIN_KEY     = join(GRAMMAR_KEY, "join" )	;	public static final String TENSE_KEY	= join(GRAMMAR_KEY, "tense"	);
	
//PLURALITIES
	public static final String NEGATIVE    	= "neg"	;	public static final String NIL		= "nil"	;
	public static final String SINGLE       = "single";	public static final String DUAL         = "dual"	;
	public static final String PLURAL       = "plural";	public static final String A_LOT        = "alot"	;
//Gender stuff
	public static final String GENDER_ROOT 	= "gender_"	;
	public static final String IRREGULAR 	= "irregular"	;
	public static final String REGULAR      = ""			;
	public static final String AFFIX        = "affix"		;
	public static final String INDEFINITE   = "indefinite"	;
	public static final String DEFINITE     = "definite"	;
	public static final String FLAT         = "flat"		;
	
//Text Join Options
	public static final String AND		= "and"		;
	public static final String COLON		= "colon"		;
//Other stuff
	public static final String FORMAT		= "format" 	;
	public static final String EMPTY 		= "" 		;
	
	public static final String BLOCK       = "block"     	;
	public static final String ITEM        = "item"      	;
	public static final String ENTITY      = "entity"    	;
	public static final String BLOCK_ITEM  = "block_item"	;
	public static final String ID          = "id"        	;
	
	public static final String ERROR 	          = "error" ;	public static final String UNHANDLEABLE = "unhandlable" ;
	public static final String ERROR_KEY = join(FEEDBACK_KEY,ERROR);
	public static final String UNHANDLABLE_ERROR_KEY = join(ERROR_KEY,UNHANDLEABLE);
	public static final String NO_SUCH   = "no_such"   ;
	public static final String NOT_ENOUGH= "not_enough";

	public static final boolean CONFIG_MIXIN_DEBUG = false;
	
	
	
	
	
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Loading Command Statistics!");
		ModRegistries.registerCommands();
	}
	
	
	public static String join       ( String @NotNull ... strings){
		String toReturn = "";
		for ( String str : strings)
			if (!str.equals(EMPTY)) toReturn = str.equals(strings[0]) ? strings[0]:String.join(".", toReturn, str) ;
		return toReturn;
	}
	public static String join_nl    ( String @NotNull ... strings){
		String toReturn = "";
		for ( String str : strings)
			if (!str.equals(EMPTY)) toReturn = str.equals(strings[0]) ? strings[0]:String.join("\n", toReturn, str) ;
		return toReturn;
	}
	public static String join_colon ( String @NotNull ... strings){
		String toReturn = "";
		for ( String str : strings)
			if (!str.equals(EMPTY)) toReturn = str.equals(strings[0]) ? strings[0]:String.join(":", toReturn, str) ;
		return toReturn;
	}
	
	public static String systemLiteral(String key, Object... args){
		return Text.stringifiedTranslatable(key,args).getString();
	}
	
	public static boolean customStatIsIn( Identifier stat, TagKey<Identifier> tagKey) {
		Registry<Identifier> customStats = Stats.CUSTOM.getRegistry();
		IndexedIterable<RegistryEntry<Identifier>> customStatsItt_able = customStats.getIndexedEntries();
		for (RegistryEntry<Identifier> registryEntry : customStatsItt_able) {
			if (registryEntry.matchesId(stat)) {
				return registryEntry.isIn(tagKey);
			}
		}
		return false;
	}

	@SuppressWarnings({ "DataFlowIssue", "unused" })
	public static <T> String castStat (T statSpecific  ){
		try { ((Block) statSpecific ).getName()			; return BLOCK ; } catch (ClassCastException e1) { String block 	= "not Block"  ;}
		try { ((Item) statSpecific ).getName()			; return ITEM  ; } catch (ClassCastException e2) { String Item  	= "not Item"   ;}
		try { ( (EntityType<?>) statSpecific ).getName()	; return ENTITY; } catch (ClassCastException e3) { String Entity	= "not Entity"	;}
		try { ( (Identifier)    statSpecific ).getPath()	; return ID	; } catch (ClassCastException e3) { String Id 	= "not ID"	;}
		return join(NO_SUCH,STAT_TYPE);
	}
	
	public static String    chooseCustomStatType( Identifier statSpecific ) {
		return 	customStatIsIn( statSpecific, ModTags.Identifiers.IS_TIME)      ? TIME        :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_MC_TIME)   ? MC_TIME     :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_DISTANCE)  ? DISTANCE    :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_DAMAGE)    ? DAMAGE      :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_CAKE)      ? CAKE        :
																	 CUSTOM     ;
		

	}
	
}