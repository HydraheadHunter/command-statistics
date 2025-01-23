package hydraheadhunter.cmdstats.command;

import com.google.gson.JsonElement;
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
import hydraheadhunter.cmdstats.command.feedback.GeneralFeedback;
import hydraheadhunter.cmdstats.command.feedback.OfflineFeedback;
import hydraheadhunter.cmdstats.command.feedback.ProjectFeedback;
import hydraheadhunter.cmdstats.command.feedback.QueryFeedback;
import hydraheadhunter.cmdstats.command.suggestionprovider.BreakableItemSuggestionProvider;
import hydraheadhunter.cmdstats.command.suggestionprovider.CustomStatsSuggestionProvider;
import hydraheadhunter.cmdstats.util.iPlayerProjectSaver;
import hydraheadhunter.cmdstats.util.iStatHandlerMixin;
import hydraheadhunter.cmdstats.util.units.iUnit;
import hydraheadhunter.cmdstats.util.units.item.stack.NonStack;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static hydraheadhunter.cmdstats.util.ModTags.Identifiers.*;
import static hydraheadhunter.cmdstats.util.units.iUnit.*;
import static java.lang.String.valueOf;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.stringifiedTranslatable;

//TODO: Current issue, Offline queries not working. Word is being interpretted as a player


@SuppressWarnings({"RedundantThrows", "BoundedWildcard"})
public class StatisticsCommandForOfflinePlayers {
     private static final String PLAYERNAME= "Player Name";
                                                       private static final int EN_QUERY     =  3;
                                                       private static final int EN_STORE     = 11;     private static final int EN_BROKEN    = 13;
     private static final int EN_CRAFTED   = 17;       private static final int EN_CUSTOM    = 19;     private static final int EN_DROPPED   = 23;
     private static final int EN_KILLED    = 29;       private static final int EN_KILLED_BY = 31;     private static final int EN_MINED     = 37;
     private static final int EN_PICKED_UP = 41;       private static final int EN_USED      = 43;     private static final int EN_FLAT      = 47;
     private static final int EN_INT       = 53;       private static final int EN_OBJECTIVE = 59;     private static final int EN_UNIT      = 61;
     private static final int EN_PROJECT   = 67;
     
     private static final String NO_SUCH_UNIT_KEY = join(UNHANDLABLE_ERROR_KEY, NO_SUCH, UNIT );
     private static final CommandSyntaxException NO_SUCH_UNIT_EXCEPTION = new CommandSyntaxException( CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(), Text.translatable( NO_SUCH_UNIT_KEY ) );
     
	private static final String PROJECT_NAME_RESERVED_KEY  = "project_name_reserved";
     private static final String PROJECT_NOT_TRACKED        = "project_not_tracked";
	private static final String PROJECT_NOT_FOUND          = "project_not_found";
     private static final String WORLD_NOT_FOUND            = "world.not_found";
     private static final String PLAYER_NOT_FOUND           = "player.not_found";
 
//TODO: Implement QUERY for OFFLINE players

//TODO: Put QUERY EXECUTION OP in a config file
//TODO: Implement 'Units' Argument for QUERY | STORE (Format the stat's output in terms of units)
//Execution OP 1 (Cannot change player statistics )
// /statistics query @p <statType<T (Block | Item | EntityType<?> | Identifier )>> <stat<T>>
     public static     void registerSTATISITCS(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment ignoredEnvironment ) {
/*
    /stats mood__ @p stat_Type<T> [T stat] [amount] [unit] { $ | # | % } //
   []: a variable argument of some kind.
    $: permission OP needed to access this branch.
    #: Suggestions offered at this node.
    %: This branch's call to execute.
*/
/* /stats                                        */
          
          dispatcher.register(      CommandManager.literal (ROOT_COMMAND)
          
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//   PROJECT
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* /stats projec                                 */	.then(		CommandManager.literal (PROJECT	 )
/* /stats projec                               $ */						    .requires( (source)  -> source.hasPermissionLevel(PROJECT_OP))
/* /stats projec list                            */	 .then(		CommandManager.literal (LIST		 )
/* /stats projec list  @p                        */	  .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats projec list  @p                      % */						    .executes( (context) -> executeProjectLIST ( context))
/* /stats projec list  @p                        */	  )
/* /stats projec list                            */	 )
/* /stats projec start                           */	 .then(		CommandManager.literal (START		 )
/* /stats projec start @p                        */	  .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats projec start @p [N]                    */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats projec start @p [N]                  % */						    .executes( (context) -> executeProjectSTART( context, StringArgumentType.getString(context,PROJECT_NAME)))
/* /stats projec start @p [N]                    */	   )
/* /stats projec start @p                        */	  )
/* /stats projec start dm                        */	  .then(		CommandManager.literal(DUMMY)
/* /stats projec start dm [N]                    */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats projec start dm [N]                  % */						    .executes( (context) -> executeProjectSTART_DUMMY( context, StringArgumentType.getString(context,PROJECT_NAME)))
/* /stats projec start dm [N]                    */	   )
/* /stats projec start dm                        */	  )
/* /stats projec start                           */	 )
/* /stats projec pause                           */	 .then(		CommandManager.literal (PAUSE       )
/* /stats projec pause @p                        */	  .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats projec pause @p all                    */	   .then(		CommandManager.literal (ALL		 )
/* /stats projec pause @p all                  % */						    .executes( (context) -> executeProjectPAUSE(context, ALL))
/* /stats projec pause @p all                    */	   )
/* /stats projec pause @p [N]                    */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats projec pause @p [N]                  % */						    .executes( (context) -> executeProjectPAUSE( context, StringArgumentType.getString(context,PROJECT_NAME)))
/* /stats projec pause @p [N]                    */	   )
/* /stats projec pause @p                        */	  )
/* /stats projec pause @p                        */	 )
/* /stats projec stop                            */	 .then(		CommandManager.literal (STOP		 )
/* /stats projec stop  @p                        */	  .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats projec stop  @p all                    */	   .then(		CommandManager.literal (ALL		 )
/* /stats projec stop  @p all                  % */						    .executes( (context) -> executeProjectSTOP ( context, ALL))
/* /stats projec stop  @p all                    */	   )
/* /stats projec stop  @p [N]                    */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats projec stop  @p [N]                  % */						    .executes( (context) -> executeProjectSTOP ( context, StringArgumentType.getString(context,PROJECT_NAME)))
/* /stats projec stop  @p [N]                    */	   )
/* /stats projec stop  @p                        */	  )
/* /stats projec stop  @p                        */	 )

/* /stats pjt query                              */	 .then(		CommandManager.literal (QUERY		 )
/* /stats pjt query @p                           */	  .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats pjt query @p [N]                       */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats pjt query @p [N] mined                 */	    .then(	CommandManager.literal (MINED		 )
/* /stats pjt query @p [N] mined   [B]           */	     .then(	CommandManager.argument(STAT		 , BlockArgumentType.block(access))
/* /stats pjt query @p [N] mined   [B]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_MINED*EN_PROJECT))
/* /stats pjt query @p [N] mined   [B]           */	     )
/* /stats pjt query @p [N] mined                 */	    )
/* /stats pjt query @p [N] crafted               */	    .then(	CommandManager.literal(CRAFTED	 )
/* /stats pjt query @p [N] crafted [I]           */	     .then(	CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats pjt query @p [N] crafted [I]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CRAFTED*EN_PROJECT))
/* /stats pjt query @p [N] crafted [I]           */	     )
/* /stats pjt query @p [N] crafted               */	    )
/* /stats pjt query @p [N] used                  */	    .then(	CommandManager.literal (USED		 )
/* /stats pjt query @p [N] used    [I]           */	     .then(	CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats pjt query @p [N] used    [I]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_USED*EN_PROJECT))
/* /stats pjt query @p [N] used    [I]           */	     )
/* /stats pjt query @p [N] used                  */	    )
/* /stats pjt query @p [N] broken                */	    .then(	CommandManager.literal (BROKEN	 )
/* /stats pjt query @p [N] broken  [I]           */	     .then(	CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats pjt query @p [N] broken  [I]         # */						    .suggests(			   BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats pjt query @p [N] broken  [I]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_BROKEN*EN_PROJECT))
/* /stats pjt query @p [N] broken  [I]           */	     )
/* /stats pjt query @p [N] broken                */	    )
/* /stats pjt query @p [N] picked                */	    .then(	CommandManager.literal (PICKED_UP	 )
/* /stats pjt query @p [N] picked  [I]           */	     .then(	CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats pjt query @p [N] picked  [I]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_PICKED_UP*EN_PROJECT))
/* /stats pjt query @p [N] picked  [I]           */	     )
/* /stats pjt query @p [N] picked                */	    )
/* /stats pjt query @p [N] dropped               */	    .then(	CommandManager.literal (DROPPED	 )
/* /stats pjt query @p [N] dropped [I]           */	     .then(	CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats pjt query @p [N] dropped [I]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_DROPPED*EN_PROJECT))
/* /stats pjt query @p [N] dropped [I]           */	     )
/* /stats pjt query @p [N] dropped               */	    )
/* /stats pjt query @p [N] killed                */	    .then(	CommandManager.literal (KILLED	 )
/* /stats pjt query @p [N] killed  [E]           */	      .then(	CommandManager.argument(STAT		 , EntityTypeArgumentType.type(access))
/* /stats pjt query @p [N] killed  [E]         # */						    .suggests(			   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats pjt query @p [N] killed  [E]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_KILLED*EN_PROJECT))
/* /stats pjt query @p [N] killed  [E]           */	     )
/* /stats pjt query @p [N] killed                */	    )
/* /stats pjt query @p [N] killedB [E]           */	    .then(	CommandManager.literal (KILLED_BY	 )
/* /stats pjt query @p [N] killedB [E]           */	     .then(	CommandManager.argument(STAT		 , EntityTypeArgumentType.type(access))
/* /stats pjt query @p [N] killedB [E]         # */						    .suggests(			   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats pjt query @p [N] killedB [E]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_KILLED_BY*EN_PROJECT))
/* /stats pjt query @p [N] killedB [E]           */	     )
/* /stats pjt query @p [N] killedB               */	    )
/* /stats pjt query @p [N] custom  [i]           */	    .then(	CommandManager.literal (CUSTOM	 )
/* /stats pjt query @p [N] custom  [i]           */	     .then(	CommandManager.argument(STAT		 , CustomStatArgumentType.stat(access))
/* /stats pjt query @p [N] custom  [i]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CUSTOM*EN_PROJECT))
/* /stats pjt query @p [N] custom  [i]           */	     )
/* /stats pjt query @p [N] custom                */	    )
/* /stats pjt query @p [N]                       */	   )
/* /stats pjt query @p                           */	  )
/* /stats pjt query                              */	 )
/* /stats pjt store                              */	 .then(		CommandManager.literal (STORE)
/* /stats pjt store @p                           */	  .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats pjt store @p [N]                       */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats pjt store @p [N] mined                 */	    .then(	CommandManager.literal (MINED  )
/* /stats pjt store @p [N] mined   [B]           */	     .then(	CommandManager.argument(STAT   ,BlockArgumentType.block(access))
/* /stats pjt store @p [N] mined   [B] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] mined   [B] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_MINED*EN_PROJECT))
/* /stats pjt store @p [N] mined   [B] [score]   */	      )
/* /stats pjt store @p [N] mined   [B]           */	     )
/* /stats pjt store @p [N] mined                 */	    )
/* /stats pjt store @p [N] crafted               */	    .then(	CommandManager.literal (CRAFTED)
/* /stats pjt store @p [N] crafted [I]           */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats pjt store @p [N] crafted [I] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] crafted [I] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_CRAFTED*EN_PROJECT))
/* /stats pjt store @p [N] crafted [I] [score]   */	      )
/* /stats pjt store @p [N] crafted [I]           */	     )
/* /stats pjt store @p [N] crafted               */	    )
/* /stats pjt store @p [N] used                  */	    .then(	CommandManager.literal (USED   )
/* /stats pjt store @p [N] used    [I]           */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats pjt store @p [N] used    [I] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] used    [I] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_USED*EN_PROJECT))
/* /stats pjt store @p [N] used    [I] [score]   */	      )
/* /stats pjt store @p [N] used    [I]           */	     )
/* /stats pjt store @p [N] used                  */	    )
/* /stats pjt store @p [N] broken                */	    .then(	CommandManager.literal (BROKEN )
/* /stats pjt store @p [N] broken  [I]           */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats pjt store @p [N] broken  [I]         # */						    .suggests( BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats pjt store @p [N] broken  [I] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] broken  [I] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_BROKEN*EN_PROJECT))
/* /stats pjt store @p [N] broken  [I] [score]   */	      )
/* /stats pjt store @p [N] broken  [I]           */	     )
/* /stats pjt store @p [N] broken                */	    )
/* /stats pjt store @p [N] picked                */	    .then(	CommandManager.literal (PICKED_UP   )
/* /stats pjt store @p [N] picked  [I]           */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats pjt store @p [N] picked  [I] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] picked  [I] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_PICKED_UP*EN_PROJECT))
/* /stats pjt store @p [N] picked  [I] [score]   */	      )
/* /stats pjt store @p [N] picked  [I]           */	     )
/* /stats pjt store @p [N] picked                */	    )
/* /stats pjt store @p [N] dropped               */	    .then(	CommandManager.literal (DROPPED )
/* /stats pjt store @p [N] dropped [I]           */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats pjt store @p [N] dropped [I] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] dropped [I] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_DROPPED*EN_PROJECT))
/* /stats pjt store @p [N] dropped [I] [score]   */	      )
/* /stats pjt store @p [N] dropped [I]           */	     )
/* /stats pjt store @p [N] dropped               */	    )
/* /stats pjt store @p [N] killed                */	    .then(	CommandManager.literal (KILLED  )
/* /stats pjt store @p [N] killed  [E]           */	     .then(	CommandManager.argument(STAT   ,EntityTypeArgumentType.type(access))
/* /stats pjt store @p [N] killed  [E]         # */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats pjt store @p [N] killed  [E] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] killed  [E] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_KILLED*EN_PROJECT))
/* /stats pjt store @p [N] killed  [E] [score]   */	      )
/* /stats pjt store @p [N] killed  [E]           */	     )
/* /stats pjt store @p [N] killed                */	    )
/* /stats pjt store @p [N] killedB               */	    .then(	CommandManager.literal (KILLED_BY)
/* /stats pjt store @p [N] killedB [E]           */	     .then(	CommandManager.argument(STAT   ,EntityTypeArgumentType.type(access))
/* /stats pjt store @p [N] killedB [E]         # */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats pjt store @p [N] killedB [E] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] killedB [E] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_KILLED_BY*EN_PROJECT))
/* /stats pjt store @p [N] killedB [E] [score]   */	      )
/* /stats pjt store @p [N] killedB [E]           */	     )
/* /stats pjt store @p [N] killedB               */	    )
/* /stats pjt store @p [N] custom                */	    .then(	CommandManager.literal (CUSTOM  )
/* /stats pjt store @p [N] custom  [i]           */	     .then(	CommandManager.argument(STAT   , CustomStatArgumentType.stat(access))
/* /stats pjt store @p [N] custom  [i] [score]   */	      .then(	CommandManager.argument(OBJECTIVE   , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats pjt store @p [N] custom  [i] [score] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_CUSTOM*EN_PROJECT))
/* /stats pjt store @p [N] custom  [i] [score]   */	      )
/* /stats pjt store @p [N] custom  [i]           */	     )
/* /stats pjt store @p [N] custom                */	    )
/* /stats pjt store @p [N]                       */	   )
/* /stats pjt store @p                           */	  )
/* /stats pjt store                              */	 )
/* /stats pjt                                    */	)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//   QUERY
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* /stats query                                  */	.then(		CommandManager.literal (QUERY		 )
/* /stats query                                $ */						    .requires( (source)  -> source.hasPermissionLevel(1)) //TODO: put this in a config file.
/* /stats query  @p                              */	 .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats query  @p mined                        */	  .then(		CommandManager.literal (MINED		 )
/* /stats query  @p mined   [B]                  */	   .then(		CommandManager.argument(STAT		 , BlockArgumentType.block(access))
/* /stats query  @p mined   [B]                % */	      				    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_MINED))
/* /stats query  @p mined   [B] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p mined   [B] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_MINED*EN_UNIT))
/* /stats query  @p mined   [B] [unit]           */	    )
/* /stats query  @p mined   [B]                  */	   )
/* /stats query  @p mined                        */	  )
/* /stats query  @p crafted                      */	  .then(		CommandManager.literal (CRAFTED	 )
/* /stats query  @p crafted [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats query  @p crafted [I]                % */	          			    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CRAFTED))
/* /stats query  @p crafted [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p crafted [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CRAFTED*EN_UNIT))
/* /stats query  @p crafted [I] [unit]           */	    )
/* /stats query  @p crafted [I]                  */	   )
/* /stats query  @p crafted                      */	  )
/* /stats query  @p used                         */	  .then(		CommandManager.literal (USED		 )
/* /stats query  @p used    [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats query  @p used    [I]                % */	        				    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_USED))
/* /stats query  @p used    [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p used    [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_USED*EN_UNIT))
/* /stats query  @p used    [I] [unit]           */	    )
/* /stats query  @p used    [I]                  */	   )
/* /stats query  @p used                         */	  )
/* /stats query  @p broken                       */	  .then(		CommandManager.literal (BROKEN	 )
/* /stats query  @p broken  [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats query  @p broken  [I]                # */						    .suggests(              BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats query  @p broken  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_BROKEN))
/* /stats query  @p broken  [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p broken  [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_BROKEN*EN_UNIT))
/* /stats query  @p broken  [I] [unit]           */	    )
/* /stats query  @p broken  [I]                  */	   )
/* /stats query  @p broken                       */	  )
/* /stats query  @p picked                       */	  .then(		CommandManager.literal (PICKED_UP	 )
/* /stats query  @p picked  [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats query  @p picked  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_PICKED_UP))
/* /stats query  @p picked  [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p picked  [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_PICKED_UP*EN_UNIT))
/* /stats query  @p picked  [I] [unit]           */	    )
/* /stats query  @p picked  [I]                  */	   )
/* /stats query  @p picked                       */	  )
/* /stats query  @p dropped                      */	  .then(		CommandManager.literal (DROPPED	 )
/* /stats query  @p dropped [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats query  @p dropped [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_DROPPED))
/* /stats query  @p dropped [I] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p dropped [I] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_DROPPED*EN_UNIT))
/* /stats query  @p dropped [I] [unit]           */	    )
/* /stats query  @p dropped [I]                  */	   )
/* /stats query  @p dropped                      */	  )
/* /stats query  @p killed                       */	  .then(		CommandManager.literal (KILLED	 )
/* /stats query  @p killed  [E]                  */	   .then(		CommandManager.argument(STAT		 , EntityTypeArgumentType.type(access))
/* /stats query  @p killed  [E]                # */						    .suggests(		 	   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats query  @p killed  [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_KILLED))
/* /stats query  @p killed  [E]                  */	   )
/* /stats query  @p killed                       */	  )
/* /stats query  @p killedB [E]                  */	  .then(		CommandManager.literal(KILLED_BY	 )
/* /stats query  @p killedB [E]                  */	   .then(		CommandManager.argument(STAT   	 , EntityTypeArgumentType.type(access))
/* /stats query  @p killedB [E]                # */						    .suggests(			   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats query  @p killedB [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_KILLED_BY))
/* /stats query  @p killedB [E]                  */	   )
/* /stats query  @p killedB                      */	  )
/* /stats query  @p custom  [i]                  */	  .then(		CommandManager.literal (CUSTOM	 )
/* /stats query  @p custom  [i]                  */	   .then(		CommandManager.argument(STAT		 , CustomStatArgumentType.stat(access))
/* /stats query  @p custom  [i]                # */						    .suggests( 		   new CustomStatsSuggestionProvider() )
/* /stats query  @p custom  [i]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CUSTOM))
/* /stats query  @p custom  [i] [unit]           */        .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p custom  [i] [unit]         % */						    .executes( (context) -> decodeExecutionMode( context, EN_QUERY*EN_CUSTOM*EN_UNIT))
/* /stats query  @p custom  [i] [unit]           */	    )
/* /stats query  @p custom  [i]                  */	   )
/* /stats query  @p custom                       */	  )
/* /stats query  @p                              */	 )
/* /stats query  wd                              */
/* /stats query                                  */	 )

/* /stats store                                  */	.then(		CommandManager.literal (STORE		 )
/* /stats store                                $ */						    .requires( (source)  -> source.hasPermissionLevel(STORE_OP))
/* /stats store  @p                              */	 .then(		CommandManager.argument(PLAYERNAME	 , StringArgumentType.word())
/* /stats store  @p mined                        */	  .then(		CommandManager.literal (MINED		 )
/* /stats store  @p mined   [B]                  */	   .then(		CommandManager.argument(STAT		 , BlockArgumentType.block(access))
/* /stats store  @p mined   [B] [score]          */	    .then(	CommandManager.argument(OBJECTIVE	 , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p mined   [B] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_MINED))
/* /stats query  @p mined   [B] [score] [unit]   */         .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p mined   [B] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_MINED*EN_UNIT))
/* /stats query  @p mined   [B] [score] [unit]   */	     )
/* /stats store  @p mined   [B] [score]          */	    )
/* /stats store  @p mined   [B]                  */	   )
/* /stats store  @p mined                        */	  )
/* /stats store  @p crafted                      */	  .then(		CommandManager.literal (CRAFTED	 )
/* /stats store  @p crafted [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats store  @p crafted [I] [score]          */	    .then(	CommandManager.argument(OBJECTIVE   , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p crafted [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_CRAFTED))
/* /stats query  @p crafted [I] [score] [unit]   */         .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p crafted [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_CRAFTED*EN_UNIT))
/* /stats query  @p crafted [I] [score] [unit]   */	     )
/* /stats store  @p crafted [I] [score]          */	    )
/* /stats store  @p crafted [I]                  */	   )
/* /stats store  @p crafted                      */	  )
/* /stats store  @p used                         */	  .then(		CommandManager.literal (USED		 )
/* /stats store  @p used    [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats store  @p used    [I] [score]          */	    .then(	CommandManager.argument(OBJECTIVE	 , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p used    [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_USED))
/* /stats query  @p used    [I] [score] [unit]   */         .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p used    [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_USED*EN_UNIT))
/* /stats query  @p used    [I] [score] [unit]   */	     )
/* /stats store  @p used    [I] [score]          */	    )
/* /stats store  @p used    [I]                  */	   )
/* /stats store  @p used                         */	  )
/* /stats store  @p broken                       */	  .then(		CommandManager.literal (BROKEN	 )
/* /stats store  @p broken  [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats store  @p broken  [I]                # */						    .suggests(			   BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats store  @p broken  [I] [score]          */	    .then(	CommandManager.argument(OBJECTIVE   , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p broken  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_BROKEN))
/* /stats query  @p broken  [I] [score] [unit]   */         .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p broken  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_BROKEN*EN_UNIT))
/* /stats query  @p broken  [I] [score] [unit]   */	     )
/* /stats store  @p broken  [I] [score]          */	    )
/* /stats store  @p broken  [I]                  */	   )
/* /stats store  @p broken                       */	  )
/* /stats store  @p picked                       */	  .then(		CommandManager.literal (PICKED_UP	 )
/* /stats store  @p picked  [I]                  */	   .then(		CommandManager.argument(STAT		 ,ItemArgumentType.item(access))
/* /stats store  @p picked  [I] [score]          */	    .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p picked  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_PICKED_UP))
/* /stats query  @p picked  [I] [score] [unit]   */         .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p picked  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_PICKED_UP*EN_UNIT))
/* /stats query  @p picked  [I] [score] [unit]   */	     )
/* /stats store  @p picked  [I] [score]          */	    )
/* /stats store  @p picked  [I]                  */	   )
/* /stats store  @p picked                       */	  )
/* /stats store  @p dropped                      */	  .then(		CommandManager.literal (DROPPED	 )
/* /stats store  @p dropped [I]                  */	   .then(		CommandManager.argument(STAT		 , ItemArgumentType.item(access))
/* /stats store  @p dropped [I] [score]          */	    .then(	CommandManager.argument(OBJECTIVE   , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p dropped [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_DROPPED))
/* /stats query  @p dropped [I] [score] [unit]   */         .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p dropped [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_DROPPED*EN_UNIT))
/* /stats query  @p dropped [I] [score] [unit]   */	     )
/* /stats store  @p dropped [I] [score]          */	    )
/* /stats store  @p dropped [I]                  */	   )
/* /stats store  @p dropped                      */	  )
/* /stats store  @p killed                       */	  .then(		CommandManager.literal (KILLED	 )
/* /stats store  @p killed  [E]                  */	   .then(		CommandManager.argument(STAT		 , EntityTypeArgumentType.type(access))
/* /stats store  @p killed  [E]                # */						    .suggests(			   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats store  @p killed  [E] [score]          */	    .then(	CommandManager.argument(OBJECTIVE	 , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p killed  [E] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_KILLED))
/* /stats store  @p killed  [E] [score]          */	    )
/* /stats store  @p killed  [E]                  */	   )
/* /stats store  @p killed                       */	  )
/* /stats store  @p killedB                      */	  .then(		CommandManager.literal (KILLED_BY	 )
/* /stats store  @p killedB [E]                  */	   .then(		CommandManager.argument(STAT		 , EntityTypeArgumentType.type(access))
/* /stats store  @p killedB [E]                # */						    .suggests(			   SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats store  @p killedB [E] [score]          */	    .then(	CommandManager.argument(OBJECTIVE	 , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p killedB [E] [score]          */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_KILLED_BY))
/* /stats store  @p killedB [E] [score]          */	    )
/* /stats store  @p killedB [E]                  */	   )
/* /stats store  @p killedB                      */	  )
/* /stats store  @p custom                       */	  .then(		CommandManager.literal (CUSTOM	 )
/* /stats store  @p custom  [i]                  */	   .then(		CommandManager.argument(STAT		 , CustomStatArgumentType.stat(access))
/* /stats store  @p custom  [i] [score]          */	    .then(	CommandManager.argument(OBJECTIVE	 , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats store  @p custom  [i] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_CUSTOM))
/* /stats query  @p custom  [i] [score] [unit]   */         .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats query  @p custom  [i] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_STORE*EN_CUSTOM*EN_UNIT))
/* /stats query  @p custom  [i] [score] [unit]   */	     )
/* /stats store  @p custom  [i] [score]          */	    )
/* /stats store  @p custom  [i]                  */	   )
/* /stats store  @p custom                       */	  )
/* /stats store  @p                              */	 )
/* /stats store                                  */	)
                                                       );
          
     }
     
    @SuppressWarnings("unused")
     private static <T> int dummyExecution     ( CommandContext<ServerCommandSource> context,  int arg )throws CommandSyntaxException{
          ServerCommandSource source= (ServerCommandSource) context.getSource();
          source.sendFeedback(() -> literal( "This is the dummy function. The arg is: " + arg),false);
          return -1;
     }
     @SuppressWarnings("unchecked")
     private static <T> int decodeExecutionMode( @NotNull CommandContext<ServerCommandSource> context,  int arg)throws CommandSyntaxException {
		ServerCommandSource source = (ServerCommandSource) context.getSource();
		source.sendFeedback( ()-> literal("0"),false);
		
		//Get the directory in which all the relevant UUID.json files are held.
          World world = source.getWorld();
          String dirString = arg % EN_PROJECT == 0 ?
          constructProjectDirectoryName(world, StringArgumentType.getString(context, PROJECT_NAME)) :
          constructWorldDirectoryName(world);
          File dir = new File(dirString);
          if (!dir.exists()) {
               source.sendFeedback(() -> OfflineFeedback.provideErrorFeedback(WORLD_NOT_FOUND),false);
               return -1;
          }
          File[] filesInDir = dir.listFiles();
          
          //Get the player name or UUID string argument
          String playerNameOrUUID = StringArgumentType.getString(context, PLAYERNAME);
		
		source.sendFeedback( ()-> literal("1"),false);
		
		
		//Find the file for the queried player
          File targetFile = null;
          String targetName = "";
          for (File jsonFile : filesInDir) {
               String fileName = jsonFile.getName();
               String UUID = fileName.substring(0, fileName.length() - 5);
               
               if (playerNameOrUUID.matches(UUID)) {
                    targetFile=jsonFile;
                    String parsedPlayerName= parsePlayerName(jsonFile);
                    break;
               }
          }
          if (targetFile == null) {
               JsonParser parser = new JsonParser();
               for (File jsonFile : filesInDir) {
                    String parsedPlayerName= parsePlayerName(jsonFile);
                    if (playerNameOrUUID.matches(parsedPlayerName)) {
                         targetFile = jsonFile;
					targetName= parsedPlayerName;
                         break;
                    }
               }
          }
          
          if (targetFile == null) {
               source.sendFeedback( ()-> OfflineFeedback.provideErrorFeedback(PLAYER_NOT_FOUND),false);
               return -1;
          }
          
          ServerStatHandler offlineHandler= new ServerStatHandler(source.getServer(),targetFile);

		source.sendFeedback( ()-> literal("2"),false);
		
          StatType<T>  statType   ;
          Block         statBlock	=null;
          Item          statItem	=null;
          EntityType<?> statEntity	=null;
          Identifier    statID    	=null;
		String projectName		=null;
		
		source.sendFeedback( ()-> literal("3"),false);
		
		ScoreboardObjective objective = ( (arg%EN_OBJECTIVE) * (arg%EN_STORE) ==0 ) ? ScoreboardObjectiveArgumentType.getObjective( context, OBJECTIVE): null;
          
          String              unitStr   = (arg % EN_UNIT==0) ? StringArgumentType.getString( context, UNIT): null;
          
		if (arg % EN_MINED  ==0){
               statType   = (StatType<T>)Stats.MINED;
               statBlock  = BlockArgumentType.getBlock( context, STAT ).getBlock();
              }
          else if ( (arg%EN_CRAFTED) * (arg%EN_USED) * (arg%EN_BROKEN) * (arg%EN_PICKED_UP) * (arg%EN_DROPPED) == 0 ){
               statType = arg%EN_CRAFTED  ==0 ? (StatType<T>)Stats.CRAFTED   :
                          arg%EN_USED     ==0 ? (StatType<T>)Stats.USED      :
                          arg%EN_BROKEN   ==0 ? (StatType<T>)Stats.BROKEN    :
                          arg%EN_PICKED_UP==0 ? (StatType<T>)Stats.PICKED_UP :
                                                (StatType<T>)Stats.DROPPED   ;
               statItem = ItemArgumentType.getItem( context, STAT ).getItem();
               }
          else if ((arg%EN_KILLED) * (arg%EN_KILLED_BY)==0){
               statType   = arg%EN_KILLED==0 ? (StatType<T>)Stats.KILLED: (StatType<T>)Stats.KILLED_BY;
               statEntity = EntityTypeArgumentType.getEntityType( context, STAT).getEntityType();
               }
          else if (arg % EN_CUSTOM==0){
               statType   = (StatType<T>)Stats.CUSTOM;
               statID     = (Identifier) CustomStatArgumentType.getStat( context, STAT).getID();
          }
          else {
               source.sendFeedback(()-> literal("Support for this stat type has not been implemented").formatted(Formatting.RED),false);
               return -1;
          }
          Object statSpec = statBlock!=null ? statBlock: statItem!=null? statItem : statEntity!=null ?statEntity : statID;
		
		source.sendFeedback( ()-> literal("4"),false);
		
		if (arg%EN_PROJECT==0) projectName = StringArgumentType.getString(context,PROJECT_NAME);
		
          if( arg%EN_QUERY==0)
               return (arg%EN_PROJECT==0) ?  executeProjectQUERY( context, targetName, offlineHandler, statType, (T) statSpec,            projectName ):
                      (arg%EN_UNIT==0)    ?  executeQUERY        ( source, targetName, offlineHandler, statType, (T) statSpec, unitStr                ):
                                             executeQUERY        ( source, targetName, offlineHandler, statType, (T) statSpec                         );
          
		if( arg%EN_STORE==0)
               return (arg%EN_PROJECT==0) ?  executeProjectSTORE( context, targetName, offlineHandler, statType, (T) statSpec, objective,          projectName ):
                      (arg%EN_UNIT==0)    ?  executeSTORE(         source, targetName, offlineHandler, statType, (T) statSpec, objective, unitStr              ):
                                             executeSTORE(         source, targetName, offlineHandler, statType, (T) statSpec, objective                       );
		
		source.sendFeedback( ()-> literal("5"),false);
		
		return -1;
     
    }
    
// /statistics project start dummy [projectname] <EXECUTE>
     private static int executeProjectSTART_DUMMY( CommandContext<ServerCommandSource> context, String projectName) throws CommandSyntaxException {
		//checks if projectName is reserved from use. Sends feedback and returns early failure if so.
     	if (checkIsProjectNameReserved(context, projectName)) return -1;
      
		ServerCommandSource source = context.getSource();
          
          boolean isNewProject=false;
          File directory = new File( constructProjectDirectoryName(source.getWorld(),projectName));
          if (!directory.exists()) isNewProject= directory.mkdir();
		
		boolean finalIsNewProject = isNewProject;
		source.sendFeedback(()-> ProjectFeedback.provideStartFeedback(finalIsNewProject, sanitizeString(projectName), new ArrayList<ServerPlayerEntity>()),false);
		
		//Return failure iff and only iff zero 'or fewer' players were added.
          return -1;
     }
// /statistics project start @p [projectname] <EXECUTE>
     private static int executeProjectSTART( CommandContext<ServerCommandSource> context, String projectName) throws CommandSyntaxException {
		//checks if projectName is reserved from use. Sends feedback and returns early failure if so.
     	if (checkIsProjectNameReserved(context, projectName)) return -1;
		
     	Collection<ServerPlayerEntity> players= EntityArgumentType.getPlayers(context,TARGETS);
		ServerCommandSource source = context.getSource();
  
		int numberofPlayersChecked=0;
          int numberOfPlayersAdded= 0;
		Collection<ServerPlayerEntity> playersAdded = new ArrayList<ServerPlayerEntity>();
          boolean isNewProject=false;
		
		for( ServerPlayerEntity player: players){
			ServerStatHandler handler = player.getStatHandler();
			
			File directory = new File( constructProjectDirectoryName(player.getWorld(),projectName));
			if (numberofPlayersChecked == 0 && !directory.exists()) {
				isNewProject= directory.mkdir();
			}
			
               iPlayerProjectSaver iPlayer = (iPlayerProjectSaver) player;
			if (iPlayer.addDirectory(directory)) {
				playersAdded.add(player);
				numberOfPlayersAdded += 1;
			}
			
			numberofPlayersChecked+=1;
          }
		
		boolean finalIsNewProject = isNewProject;
		source.sendFeedback(()-> ProjectFeedback.provideStartFeedback(finalIsNewProject, sanitizeString(projectName),playersAdded),false);
		
		//Return failure iff and only iff zero 'or fewer' players were added.
          return numberOfPlayersAdded<1 ? -1: numberOfPlayersAdded;
     }
// /statistics project pause @p [projectname | "all"] <EXECUTE>
	private static int executeProjectPAUSE(CommandContext<ServerCommandSource> context, String projectName) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> players= EntityArgumentType.getPlayers(context,TARGETS);
		String sanitizedProjectName = sanitizeString(projectName);
		ServerCommandSource source = context.getSource();
		boolean isPauseAll = projectName.equals(ALL);
  
		Collection<ServerPlayerEntity> playersPaused	 = new ArrayList<>();
		Collection<File> 			 projectsPaused = new ArrayList<>();
		
		int numberProjectsPaused = 0;
		for( ServerPlayerEntity player: players){
			ServerStatHandler handler = player.getStatHandler();
			iPlayerProjectSaver iPlayer = (iPlayerProjectSaver) player;
			
			if (isPauseAll){
				Collection<File> projectDirectories =   iPlayer.getProjectDirectories();
				if (projectDirectories.size()>0) { playersPaused.add(player); }
				for (File directory: projectDirectories) {
					if ( ! projectsPaused.contains(directory) ){
						projectsPaused.add(directory);
					}
				}
				numberProjectsPaused+=projectDirectories.size();
				iPlayer.softResetDirectories();
			}
			else{
				File directory = new File( constructProjectDirectoryName(player.getWorld(),projectName));
				if(iPlayer.pauseDirectory(directory)){
					
					if ( ! playersPaused.contains(player))
						playersPaused.add(player);
					
					if ( ! projectsPaused.contains(directory))
						projectsPaused.add(directory);
					
					numberProjectsPaused+=1;
				}
			}
		}
		
		source.sendFeedback(()-> ProjectFeedback.providePausedFeedback(playersPaused, projectsPaused),false);
		
		return numberProjectsPaused<1 ? -1:numberProjectsPaused;
	}
// /statistics project stop @p [projectname | "all"] <EXECUTE>
     private static int executeProjectSTOP(CommandContext<ServerCommandSource> context, String projectName) throws CommandSyntaxException {
          Collection<ServerPlayerEntity> players= EntityArgumentType.getPlayers(context,TARGETS);boolean isStopAll = projectName.equals(ALL);
		String sanitizedProjectName = sanitizeString(projectName);
		ServerCommandSource source = context.getSource();
				
		Collection<ServerPlayerEntity> playersRemoved  = new ArrayList<>();
		Collection<File> 			 projectsRemoved = new ArrayList<>();
		
		int numberProjectStopped=0;
          for( ServerPlayerEntity player: players){
			ServerStatHandler handler = player.getStatHandler();
               iPlayerProjectSaver iPlayer = (iPlayerProjectSaver) player;
			
			if (isStopAll){
				Collection<File> projectDirectories = iPlayer.getProjectDirectories();
				Collection<File> pausedDirectories  = iPlayer.getPausedDirectories();
				if (projectDirectories.size()+pausedDirectories.size()>0) playersRemoved.add(player);
				
					for (File directory: projectDirectories) {
						if ( ! projectsRemoved.contains(directory) )
							projectsRemoved.add(directory);
					}
					for (File directory: pausedDirectories) {
						if ( ! projectsRemoved.contains(directory) )
							projectsRemoved.add(directory);
					}
					numberProjectStopped+= projectDirectories.size();
					iPlayer.resetDirectories();
			}
			else{
				File directory = new File( constructProjectDirectoryName(player.getWorld(),projectName));
				handler.save();
					if(iPlayer.removeDirectory(directory)){
						if ( ! playersRemoved.contains(player))
							playersRemoved.add(player);
						if ( ! projectsRemoved.contains(directory))
							projectsRemoved.add(directory);
						numberProjectStopped+=1;
					}
				handler.save();
			}
          }
		
		source.sendFeedback(()->ProjectFeedback.provideStopFeedback(playersRemoved, projectsRemoved),false);
		
          return numberProjectStopped<1 ? -1:numberProjectStopped;
     }
// /statistics project list @p <EXECUTE>
     private static int executeProjectLIST ( CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
          Collection<ServerPlayerEntity> players= EntityArgumentType.getPlayers(context,TARGETS);
          ServerCommandSource source = context.getSource();
          
          int numberOfPlayerProjects = 0;
          
          for( ServerPlayerEntity serverPlayerEntity: players) {
               
               iPlayerProjectSaver player = (iPlayerProjectSaver) serverPlayerEntity;
               Collection<File> projectDirectories = player.getProjectDirectories();
               Collection<File> pausedProjectDirectories = player.getPausedDirectories();
               
               source.sendFeedback(() -> ProjectFeedback.provideListFeedback(serverPlayerEntity,projectDirectories,pausedProjectDirectories),false);
               
               numberOfPlayerProjects+= projectDirectories.size()+ pausedProjectDirectories.size();
          }
          
          return numberOfPlayerProjects<1 ? -1:numberOfPlayerProjects;
     }
     
// /statistics project query @p <EXECUTE>
	private static<T> int executeProjectQUERY(CommandContext<ServerCommandSource> context, String targetName, ServerStatHandler target, StatType<T> statType, T statSpec, String projectName) {
		if (checkIsProjectNameReserved(context, projectName)) return -1;
		ServerCommandSource source = context.getSource();
          
          target.save();
               int statValue = target.getStat(statType, statSpec);
          target.save();

          source.sendFeedback(() -> ProjectFeedback.provideQueryFeedback(targetName, statType, statSpec, statValue, sanitizeString(projectName), false),false);
          
		return statValue;
	}
// /statistics project store @p <EXECUTE>
	private static<T> int executeProjectSTORE(CommandContext<ServerCommandSource> context, String targetName, ServerStatHandler target, StatType<T> statType, T statSpec, ScoreboardObjective objective, String projectName){
		if (checkIsProjectNameReserved(context, projectName)) return -1;
		
		ServerCommandSource source = context.getSource();
          Scoreboard scoreboard      = source.getServer().getScoreboard();
     
          target.save();
               
               int statValue = target.getStat(statType, statSpec);
               //scoreboard.getOrCreateScore(player, objective).setScore(statValue);
          target.save();
          
          source.sendFeedback(() -> ProjectFeedback.provideStoreFeedback(targetName, statType, statSpec, statValue, objective, sanitizeString(projectName), false),false);

          
		return statValue;
	}
     
     private static String constructProjectDirectoryName ( World world, String projectName){
          String worldDir= constructWorldDirectoryName(world);
          String sanitizedProjectName = sanitizeString(projectName);
          String projectDir	= "\\" + sanitizedProjectName;
          
          return worldDir + projectDir;
     }
     private static String constructWorldDirectoryName ( World world){
          boolean isSaves	= (new File( "./saves")).exists();
          String savesDir	= "/saves";
          String worldDir	= "/" + world.toString().substring(world.toString().indexOf("[")+1,world.toString().indexOf("]")) ;
          String statsDir	= "\\stats";
          
          return "."+ (isSaves? savesDir:EMPTY) + worldDir + statsDir;
     }
     
     
     private static boolean checkIsProjectNameReserved( CommandContext<ServerCommandSource> context, String projectName){
          ServerCommandSource source= context.getSource();
          boolean isReservedProjectName = projectName.equals(ALL);
          if (isReservedProjectName){
               source.sendFeedback(()-> ProjectFeedback.provideErrorFeedback( PROJECT_NAME_RESERVED_KEY, projectName),false);
               return true;
          }
          return false;
     }
     
     private static String sanitizeString(String stringToSanitize){
          return stringToSanitize.replace(".","_");
     }
     
 
     
// /statistics query @p stat_type stat <EXECUTE>
     private static <T> int executeQUERY(ServerCommandSource source, String targetName, ServerStatHandler target, StatType<T> statType, T statSpec                    ) throws CommandSyntaxException {
		source.sendFeedback( ()-> literal("4.1"),false);
		
		//target.save();
		source.sendFeedback( ()-> literal("4.2"),false);
		
			int statValue = target.getStat(statType, statSpec);
		
		source.sendFeedback( ()-> literal("4.3"),false);
		//target.save();
		
		source.sendFeedback( ()-> literal("4.4"),false);
		
		source.sendFeedback(() -> QueryFeedback.provideBasicFeedback(targetName, statType, statSpec, statValue),false);

		return statValue;
     }
// /statistics query @p stat_type stat unit <EXECUTE>
     private static <T> int executeQUERY(ServerCommandSource source, String targetName, ServerStatHandler target, StatType<T> statType, T statSpec, String unit_arg   ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(2);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else{
               
                    target.save();
                         int statValue = target.getStat(statType, statSpec);
                    target.save();
                    
                    int adjustedStatValue = unit.convertTo( isInventory? statValue/determineStackSize(statSpec):statValue);
                    
                    source.sendFeedback(() -> QueryFeedback.provideBasicUnitFeedback(targetName, statType, statSpec, adjustedStatValue,unitKey),false);
                    return statValue;
          }
     }
     
// /statistics store @p stat_ype stat objective <EXECUTE>
     private static <T> int executeSTORE(ServerCommandSource source, String targetName, ServerStatHandler target, StatType<T> statType, T statSpec, ScoreboardObjective objective ) throws CommandSyntaxException {
          
          Scoreboard scoreboard = source.getServer().getScoreboard();
               
          target.save();
               int statValue = target.getStat(statType, statSpec);
               //TODO: CHECK THIS WORKS.
               try { scoreboard.getOrCreateScore(   source.getServer().getPlayerManager().getPlayer(targetName)   , objective).setScore(statValue); }
               catch (Exception e){ }
          target.save();
               
          source.sendFeedback(() -> GeneralFeedback.provideStoreFeedback(targetName, statType, statSpec, statValue, objective), false);
          return statValue;
          
     }
// /statistics store @p stat_ype stat objective unit <EXECUTE>
     private static <T> int executeSTORE(ServerCommandSource source, String targetName, ServerStatHandler target, StatType<T> statType, T statSpec, ScoreboardObjective objective, String unit_arg ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(2);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else {
               Scoreboard scoreboard = source.getServer().getScoreboard();
                    
               target.save();
                    int statValue = target.getStat(statType, statSpec);
                    int adjustedStatValue = unit.convertTo(isInventory ? statValue * determineStackSize(statSpec) : statValue);
                    try { scoreboard.getOrCreateScore(   source.getServer().getPlayerManager().getPlayer(targetName)   , objective).setScore(statValue); }
                    catch (Exception e){ }
               target.save();
                    
               source.sendFeedback(() -> GeneralFeedback.provideStoreFeedback(targetName, statType, statSpec, adjustedStatValue, objective), false);
               
               return statValue;
          }
     }
     
     
     private static <T> ArrayList<Object> completelyParseUnit(String unit_arg, StatType<T> statType, T statSpec){
          ArrayList<Object> toReturn = new ArrayList<Object>();
          boolean isValidUnit=  isValidUnit(unit_arg);
          
          iUnit parsedUnit = isValidUnit? parseUnitName(unit_arg): new NonStack();
          String parsedUnitType= parsedUnit.getUnitType();
          
          boolean isUnitSensical = sanityCheckUnit(parsedUnit, parsedUnitType, statType, statSpec);
          boolean isUnitInventory = ITEM_UNITS.contains(parsedUnitType);
          
          toReturn.add(parsedUnit);
          toReturn.add(isValidUnit);
          toReturn.add(isUnitSensical);
          toReturn.add(isUnitInventory);
          return toReturn;
     }
     
     private static      iUnit     parseUnitName  (String unit_arg){
         for( iUnit iu: iUnit.AllUnits() ) {if (iu.getEqUnits().contains(unit_arg)) return iu; }
     
     //Should not happen, but it makes the compiler happy
          return null;
     }
     private static <T>  boolean   isValidUnit    (String unit_arg){
          return iUnit.AllUnitNames().contains(unit_arg);
     }
     private static <T>  boolean   sanityCheckUnit( iUnit unit, String unitType, StatType<T> statType, T statSpec ) {
          //Entities
          try { Entity statEntity = ((Entity) statSpec); return false; } catch (ClassCastException ignored) {}
          
          //Blocks
          try {
               Block statBlock = (Block) statSpec;
               return ( BLOCK_STAT_ONLY.contains(unitType) || ITEM_UNITS.contains(unitType) );
          }
          catch (ClassCastException ignored){}
          
          try{ Item statItem= (Item) statSpec;
               if (ITEM_UNITS.contains(unitType)) return true;
          }
          catch (ClassCastException ignored) {}
          
          //Custom Stats
          if (CUSTOM_STAT_UNITS.contains(unitType) && !statType.equals(Stats.CUSTOM)) return false;
          try {
               Identifier statID = (Identifier) statSpec;
               return switch (unitType) {
                    case CAKE      -> customStatIsIn(statID, IS_CAKE);
                    case DAMAGE    -> customStatIsIn(statID, IS_DAMAGE);
                    case DISTANCE  -> customStatIsIn(statID, IS_DISTANCE);
                    case TIME      -> customStatIsIn(statID, IS_TIME);
                    case MC_TIME   -> customStatIsIn(statID, IS_MC_TIME);
                    default -> false;
               };
          } catch (ClassCastException ignored) {}
          
          //filler body;
          return true;
     }
     
     private static <T>  int       determineStackSize( T stat ){
          Item statItem;
          try {
               Block statBlock= (Block) stat;
               statItem= statBlock.asItem();
          }
          catch (ClassCastException ignored){
               try{ statItem= (Item) stat;} catch (ClassCastException also_ignored) {return 1;}
          }
          return statItem.getMaxCount();
     }
     
     private static String parsePlayerName(File json){
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
          String parsedPlayer = jsonObject.get("PlayerName").getAsString();
          
          return parsedPlayer;
     }
     
}
