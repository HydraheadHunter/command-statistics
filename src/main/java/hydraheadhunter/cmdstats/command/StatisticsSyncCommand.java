package hydraheadhunter.cmdstats.command;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hydraheadhunter.cmdstats.command.argument.block.BlockArgumentType;
import hydraheadhunter.cmdstats.command.argument.custom_stat.CustomStatArgumentType;
import hydraheadhunter.cmdstats.command.argument.entity_type.EntityTypeArgumentType;
import hydraheadhunter.cmdstats.command.argument.item.ItemArgumentType;
import hydraheadhunter.cmdstats.command.suggestionprovider.BreakableItemSuggestionProvider;
import hydraheadhunter.cmdstats.command.suggestionprovider.CustomStatsSuggestionProvider;
import net.minecraft.block.Block;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static java.lang.String.valueOf;
import static net.minecraft.text.Text.literal;

public class StatisticsSyncCommand {
	
	private static final int EN_ADD       =  2;     private static final int EN_QUERY     =  3;     private static final int EN_REDUCE    =  5;
	private static final int EN_SET       =  7;     private static final int EN_STORE     = 11;     private static final int EN_BROKEN    = 13;
	private static final int EN_CRAFTED   = 17;     private static final int EN_CUSTOM    = 19;     private static final int EN_DROPPED   = 23;
	private static final int EN_KILLED    = 29;     private static final int EN_KILLED_BY = 31;     private static final int EN_MINED     = 37;
	private static final int EN_PICKED_UP = 41;     private static final int EN_USED      = 43;     private static final int EN_FLAT      = 47;
	private static final int EN_INT       = 53;     private static final int EN_OBJECTIVE = 59;     private static final int EN_UNIT      = 61;
	private static final int EN_PROJECT   = 67;
	
	public  static  void registerSYNC(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment ignoredEnvironment ) {

	
	/*
    /stats mood__ @p stat_Type<T> [T stat] [amount] [unit] { $ | # | % } //
   []: a variable argument of some kind.
    $: permission OP needed to access this branch.
    #: Suggestions offered at this node.
    %: This branch's call to execute.
*/
/* /stats                                        */
          
          dispatcher.register(      CommandManager.literal (ROOT_COMMAND+"test")

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//   QUERY
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* /stats sync                               */	.then(		CommandManager.literal (SYNC		 )
/* /stats sync                             $ */						    .requires( (source)  -> source.hasPermissionLevel(1)) //TODO: put this in a config file.
/* /stats sync  mined                        */	  .then(		CommandManager.literal (MINED		 )
/* /stats sync  mined   [B]                  */	   .then(		CommandManager.argument(STAT		 , BlockArgumentType.block(access))
/* /stats sync  mined   [B]                % */	      				    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_MINED))
/* /stats sync  mined   [B] [unit]           */        .then(	CommandManager.argument(UNIT   , StringArgumentType.word())
/* /stats sync  mined   [B] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_MINED*EN_UNIT))
/* /stats sync  mined   [B] [unit]           */	    )
/* /stats sync  mined   [B]                  */	   )
/* /stats sync  mined                        */	  )
/* /stats sync  crafted                      */	  .then(		CommandManager.literal (CRAFTED	 )
/* /stats sync  crafted [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats sync  crafted [I]                % */	          			    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CRAFTED))
/* /stats sync  crafted [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats sync  crafted [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CRAFTED*EN_UNIT))
/* /stats sync  crafted [I] [unit]           */	    )
/* /stats sync  crafted [I]                  */	   )
/* /stats sync  crafted                      */	  )
/* /stats sync  used                         */	  .then(		CommandManager.literal (USED		 )
/* /stats sync  used    [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats sync  used    [I]                % */	        				    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_USED))
/* /stats sync  used    [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats sync  used    [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_USED*EN_UNIT))
/* /stats sync  used    [I] [unit]           */	    )
/* /stats sync  used    [I]                  */	   )
/* /stats sync  used                         */	  )
/* /stats sync  broken                       */	  .then(		CommandManager.literal (BROKEN	 )
/* /stats sync  broken  [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats sync  broken  [I]                # */						    .suggests(              BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats sync  broken  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_BROKEN))
/* /stats sync  broken  [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats sync  broken  [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_BROKEN*EN_UNIT))
/* /stats sync  broken  [I] [unit]           */	    )
/* /stats sync  broken  [I]                  */	   )
/* /stats sync  broken                       */	  )
/* /stats sync  picked                       */	  .then(		CommandManager.literal (PICKED_UP	 )
/* /stats sync  picked  [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats sync  picked  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_PICKED_UP))
/* /stats sync  picked  [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats sync  picked  [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_PICKED_UP*EN_UNIT))
/* /stats sync  picked  [I] [unit]           */	    )
/* /stats sync  picked  [I]                  */	   )
/* /stats sync  picked                       */	  )
/* /stats sync  dropped                      */	  .then(		CommandManager.literal (DROPPED	 )
/* /stats sync  dropped [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats sync  dropped [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_DROPPED))
/* /stats sync  dropped [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats sync  dropped [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_DROPPED*EN_UNIT))
/* /stats sync  dropped [I] [unit]           */	    )
/* /stats sync  dropped [I]                  */	   )
/* /stats sync  dropped                      */	  )
/* /stats sync  killed                       */	  .then(		CommandManager.literal (KILLED	 )
/* /stats sync  killed  [E]                  */	   .then(		CommandManager.argument(STAT		 , EntityTypeArgumentType.type(access))
/* /stats sync  killed  [E]                # */						    .suggests(		 	   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats sync  killed  [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_KILLED))
/* /stats sync  killed  [E]                  */	   )
/* /stats sync  killed                       */	  )
/* /stats sync  killedB [E]                  */	  .then(		CommandManager.literal(KILLED_BY	 )
/* /stats sync  killedB [E]                  */	   .then(		CommandManager.argument(STAT   	 , EntityTypeArgumentType.type(access))
/* /stats sync  killedB [E]                # */						    .suggests(			   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats sync  killedB [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_KILLED_BY))
/* /stats sync  killedB [E]                  */	   )
/* /stats sync  killedB                      */	  )
/* /stats sync  custom  [i]                  */	  .then(		CommandManager.literal (CUSTOM	 )
/* /stats sync  custom  [i]                  */	   .then(		CommandManager.argument(STAT		 , CustomStatArgumentType.stat(access))
/* /stats sync  custom  [i]                # */						    .suggests( 		   new CustomStatsSuggestionProvider() )
/* /stats sync  custom  [i]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CUSTOM))
/* /stats sync  custom  [i] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats sync  custom  [i] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CUSTOM*EN_UNIT))
/* /stats sync  custom  [i] [unit]           */	    )
/* /stats sync  custom  [i]                  */	   )
/* /stats sync  custom                       */	  )
/* /stats sync                               */	 )
/* /stats sync                                   */
											);
	}
	
	private static int DUMMYdecodeExecutionMode( CommandContext<ServerCommandSource> context, int arg) throws CommandSyntaxException {
		ServerCommandSource source= (ServerCommandSource) context.getSource();
		source.sendFeedback(() -> literal( "This is the dummy function. The arg is: " + arg),false);
		return -1;
	}
	
	private static <T> int decodeExecutionMode(CommandContext<ServerCommandSource> context, int arg) throws CommandSyntaxException {
		LOGGER.info("Executing Sync");
		ArrayList<File> jsonFiles= filterJSONsFromDirectory(context);
		
		LOGGER.info("Found "+valueOf(jsonFiles.size())+" Jsons.");
		
		if (jsonFiles.size()>1) {
			JsonParser parser = new JsonParser();
			
			//TODO Make the stat specified by the command matter instead of being hard coded jumps.
			String statTypeArg= determineStatTypeString(        arg);
			String statArg    = determineStatString    (context,arg);
			
			ArrayList<PlayerStat> PlayerStats = new ArrayList<>();
			for (File json: jsonFiles) {
				String UUID= json.getName().substring(0, json.getName().length()-5);
				String playerName= parsePlayerName(json);
				int statValue= parseStat( json, statTypeArg, statArg );
				LOGGER.info( playerName+", "+valueOf(statValue) );
				PlayerStats.add( new PlayerStat( UUID, playerName, statValue) );
			}
			PlayerStats.sort(new Comparator<PlayerStat>() {	@Override	public int compare(PlayerStat o1, PlayerStat o2) {	return o1.value==o2.value? 0: o1.value>o2.value? 1:-1;	} });
			PlayerStats.reversed();
			
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static ArrayList<File> filterJSONsFromDirectory(CommandContext<ServerCommandSource> context){
		File dir = new File ( constructDirectoryName(context.getSource()) );
		ArrayList<File> files = new ArrayList<File>();
		files.addAll(List.of(dir.listFiles()));
		
		ArrayList<File> filteredFiles = new ArrayList<>();
		for (File f: files){ if (isJSON(f)) filteredFiles.add(f); }
		
		return filteredFiles;
	}
	
	private static String constructDirectoryName(ServerCommandSource source){
		World world = source.getWorld();
		MinecraftServer server= world.getServer();
		
		boolean isSaves	= (new File( "./saves")).exists();
		String savesDir	= "/saves";
		String worldDir	= "/" + world.toString().substring(world.toString().indexOf("[")+1,world.toString().indexOf("]")) ;
		String statsDir	= "\\stats";
		
		return "." + (isSaves? savesDir:EMPTY) + worldDir + statsDir;
	}
	
	private static boolean isJSON(File file){ return file.getName().endsWith(".json"); }
	
	private static String parsePlayerName(File jsonFile){
          JsonObject jsonObject = loadJsonIntoObject( jsonFile );
		
		if( jsonObject.has("PlayerName") ){
			String parsedPlayer = jsonObject.get("PlayerName").getAsString();
			return parsedPlayer;
		}
		return "Player Name Data JSON Element Not Found";
     }
	
	private static int parseStat(File jsonFile, String statTypeArg, String statArg){
		JsonObject jsonObject = loadJsonIntoObject( jsonFile );
		
		int toReturn=0;
		if( jsonObject.has("stats") ){               jsonObject= jsonObject.getAsJsonObject("stats"    );
			if (jsonObject.has(statTypeArg)){		jsonObject= jsonObject.getAsJsonObject(statTypeArg);
				if (jsonObject.has(statArg)){      toReturn  = jsonObject.get(statArg).getAsInt()     ; }
			}
		}
		
		return toReturn;
	}
	
	private static JsonObject loadJsonIntoObject(File json){
		JsonParser parser= new JsonParser();
		Scanner scan;
		try {
			scan = new Scanner(json);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("parsed", parser.parseString(scan.nextLine()));
		jsonObject = jsonObject.getAsJsonObject("parsed");
		scan.close();
		
		return jsonObject;
	}
	
	private static String determineStatTypeString(int arg){
		return "minecraft:"+ (
		        arg%EN_MINED    ==0? "mined":
			   arg%EN_CRAFTED  ==0? "crafted":
			   arg%EN_USED     ==0? "used":
			   arg%EN_BROKEN   ==0? "broken":
			   arg%EN_KILLED   ==0? "killed":
			   arg%EN_KILLED_BY==0? "killed_by":
			                        "custom"
		);
	}

	private static <T> String determineStatString(CommandContext<ServerCommandSource> context, int arg) throws CommandSyntaxException {
		String toReturn="";
		Block         statBlock	=null;
		Item          statItem	=null;
		EntityType<?> statEntity	=null;
		Identifier    statID    	=null;
		String projectName		=null;
		
		if (arg % EN_MINED  ==0){
			statBlock  = BlockArgumentType.getBlock( context, STAT ).getBlock();
			toReturn= statBlock.getTranslationKey();
		}
		else if ( (arg%EN_CRAFTED) * (arg%EN_USED) * (arg%EN_BROKEN) * (arg%EN_PICKED_UP) * (arg%EN_DROPPED) == 0 ){
			statItem = ItemArgumentType.getItem( context, STAT ).getItem();
			toReturn= statItem.getTranslationKey();
		}
		else if ((arg%EN_KILLED) * (arg%EN_KILLED_BY)==0){
			statEntity = EntityTypeArgumentType.getEntityType( context, STAT).getEntityType();
			toReturn= statEntity.getTranslationKey();
		}
		else if (arg % EN_CUSTOM==0){
			statID     = (Identifier) CustomStatArgumentType.getStat( context, STAT).getID();
			toReturn= statID.toTranslationKey();
		}
		else {
			//source.sendFeedback(()-> literal("Support for this stat type has not been implemented").formatted(Formatting.RED),false);
			
		}
		
		LOGGER.info("Key found: "+toReturn);
		toReturn= toReturn.replace('.','`');
		while ( toReturn.contains("`") ) {
			if (toReturn.contains(":")) toReturn= toReturn.substring( toReturn.indexOf(':')+1 );
			toReturn= toReturn.replaceFirst("`",":");
			LOGGER.info("Key iterated: "+toReturn);
		}
		LOGGER.info("Key Finalized: "+toReturn);
		return toReturn;
	}
	
	private static class PlayerStat {
		public final String UUID;	public final String playerName;	public final int value;
		PlayerStat(String uuidArg, String keyArg, int valueArg) { UUID=uuidArg; playerName=keyArg; value=valueArg; }
	}
	
}


