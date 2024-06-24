package hydraheadhunter.cmdstats.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hydraheadhunter.cmdstats.command.argument.block.BlockArgumentType;
import hydraheadhunter.cmdstats.command.argument.custom_stat.CustomStatArgumentType;
import hydraheadhunter.cmdstats.command.argument.entity_type.EntityTypeArgumentType;
import hydraheadhunter.cmdstats.command.argument.item.ItemArgumentType;
import hydraheadhunter.cmdstats.command.feedback.*;
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
import net.minecraft.server.MinecraftServer;import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.StatHandler;import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static hydraheadhunter.cmdstats.util.ModTags.Identifiers.*;
import static hydraheadhunter.cmdstats.util.units.iUnit.*;
import static java.lang.String.valueOf;
import static net.minecraft.text.Text.literal;


@SuppressWarnings({"RedundantThrows", "BoundedWildcard"})
public class StatisticsCommand {
     private static final int EN_ADD       =  2;     private static final int EN_QUERY     =  3;     private static final int EN_REDUCE    =  5;
     private static final int EN_SET       =  7;     private static final int EN_STORE     = 11;     private static final int EN_BROKEN    = 13;
     private static final int EN_CRAFTED   = 17;     private static final int EN_CUSTOM    = 19;     private static final int EN_DROPPED   = 23;
     private static final int EN_KILLED    = 29;     private static final int EN_KILLED_BY = 31;     private static final int EN_MINED     = 37;
     private static final int EN_PICKED_UP = 41;     private static final int EN_USED      = 43;     private static final int EN_FLAT      = 47;
     private static final int EN_INT       = 53;     private static final int EN_OBJECTIVE = 59;     private static final int EN_UNIT      = 61;
     private static final int EN_PROJECT   = 67;
     private static final String NO_SUCH_UNIT_KEY = join(UNHANDLABLE_ERROR_KEY, NO_SUCH, UNIT );
     private static final CommandSyntaxException NO_SUCH_UNIT_EXCEPTION = new CommandSyntaxException( CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(), Text.translatable( NO_SUCH_UNIT_KEY ) );
     
	private static final String PROJECT_NAME_RESERVED_KEY  = "project_name_reserved";
     private static final String PROJECT_NOT_TRACKED        = "project_not_tracked";
	private static final String PROJECT_NOT_FOUND          = "project_not_found";
 
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
/* /stats projec list  @p                        */	  .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
/* /stats projec list  @p                      % */						    .executes( (context) -> executeProjectLIST ( context))
/* /stats projec list  @p                        */	  )
/* /stats projec list                            */	 )
/* /stats projec start                           */	 .then(		CommandManager.literal (START		 )
/* /stats projec start @p                        */	  .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
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
/* /stats projec pause @p                        */	  .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
/* /stats projec pause @p all                    */	   .then(		CommandManager.literal (ALL		 )
/* /stats projec pause @p all                  % */						    .executes( (context) -> executeProjectPAUSE(context, ALL))
/* /stats projec pause @p all                    */	   )
/* /stats projec pause @p [N]                    */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats projec pause @p [N]                  % */						    .executes( (context) -> executeProjectPAUSE( context, StringArgumentType.getString(context,PROJECT_NAME)))
/* /stats projec pause @p [N]                    */	   )
/* /stats projec pause @p                        */	  )
/* /stats projec pause @p                        */	 )
/* /stats projec stop                            */	 .then(		CommandManager.literal (STOP		 )
/* /stats projec stop  @p                        */	  .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
/* /stats projec stop  @p all                    */	   .then(		CommandManager.literal (ALL		 )
/* /stats projec stop  @p all                  % */						    .executes( (context) -> executeProjectSTOP ( context, ALL))
/* /stats projec stop  @p all                    */	   )
/* /stats projec stop  @p [N]                    */	   .then(		CommandManager.argument(PROJECT_NAME, StringArgumentType.word())
/* /stats projec stop  @p [N]                  % */						    .executes( (context) -> executeProjectSTOP ( context, StringArgumentType.getString(context,PROJECT_NAME)))
/* /stats projec stop  @p [N]                    */	   )
/* /stats projec stop  @p                        */	  )
/* /stats projec stop  @p                        */	 )

/* /stats pjt query                              */	 .then(		CommandManager.literal (QUERY		 )
/* /stats pjt query @p                           */	  .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
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
/* /stats pjt store @p                           */	  .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
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
/* /stats query  @p                              */	 .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
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
/* /stats query                                  */	  )

/* /stats store                                  */	.then(		CommandManager.literal (STORE		 )
/* /stats store                                $ */						    .requires( (source)  -> source.hasPermissionLevel(STORE_OP))
/* /stats store  @p                              */	 .then(		CommandManager.argument(TARGETS	 , EntityArgumentType.players())
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
/* /stats store                                  */	  )

/* /stats add                                    */	  .then(		CommandManager.literal (ADD)
/* /stats add                                  $ */						    .requires( (source)  -> source.hasPermissionLevel(ADD_OP))
/* /stats add    @p                              */	   .then(		CommandManager.argument(TARGETS,EntityArgumentType.players() )
/* /stats add    @p mined                        */	    .then(	CommandManager.literal (MINED  )
/* /stats add    @p mined   [B]                  */	     .then(	CommandManager.argument(STAT   ,BlockArgumentType.block(access))
/* /stats add    @p mined   [B]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_MINED*EN_FLAT))
/* /stats add    @p mined   [B] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p mined   [B] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_MINED*EN_INT))
/* /stats add    @p mined   [B] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p mined   [B] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_MINED*EN_INT*EN_UNIT))
/* /stats add    @p mined   [B] [int]   [unit]   */	       )
/* /stats add    @p mined   [B] [int]            */	      )
/* /stats add    @p mined   [B] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective() )
/* /stats add    @p mined   [B] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_MINED*EN_OBJECTIVE))
/* /stats add    @p mined   [B] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word() )
/* /stats add    @p mined   [B] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_MINED*EN_OBJECTIVE*EN_UNIT))
/* /stats add    @p mined   [B] [score] [unit]   */	       )
/* /stats add    @p mined   [B] [score]          */	      )
/* /stats add    @p mined   [B]                  */	     )
/* /stats add    @p mined                        */	    )
/* /stats add    @p crafted                      */	    .then(	CommandManager.literal (CRAFTED)
/* /stats add    @p crafted [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats add    @p crafted [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CRAFTED*EN_FLAT))
/* /stats add    @p crafted [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p crafted [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CRAFTED*EN_INT))
/* /stats add    @p crafted [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p crafted [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CRAFTED*EN_INT*EN_UNIT))
/* /stats add    @p crafted [I] [int]   [unit]   */	       )
/* /stats add    @p crafted [I] [int]            */	      )
/* /stats add    @p crafted [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE,   ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats add    @p crafted [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CRAFTED*EN_OBJECTIVE))
/* /stats add    @p crafted [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT,   StringArgumentType.word())
/* /stats add    @p crafted [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CRAFTED*EN_OBJECTIVE*EN_UNIT))
/* /stats add    @p crafted [I] [score] [unit]   */	       )
/* /stats add    @p crafted [I] [score]          */	      )
/* /stats add    @p crafted [I]                  */	     )
/* /stats add    @p crafted                      */	    )
/* /stats add    @p used                         */	    .then(	CommandManager.literal (USED   )
/* /stats add    @p used    [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats add    @p used    [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_USED*EN_FLAT))
/* /stats add    @p used    [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p used    [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_USED*EN_INT))
/* /stats add    @p used    [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p used    [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_USED*EN_INT*EN_UNIT))
/* /stats add    @p used    [I] [int]   [unit]   */	       )
/* /stats add    @p used    [I] [int]            */	      )
/* /stats add    @p used    [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats add    @p used    [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_USED*EN_OBJECTIVE))
/* /stats add    @p used    [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p used    [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_USED*EN_OBJECTIVE*EN_UNIT))
/* /stats add    @p used    [I] [score] [unit]   */	       )
/* /stats add    @p used    [I] [score]          */	      )
/* /stats add    @p used    [I]                  */	     )
/* /stats add    @p used                         */	    )
/* /stats add    @p broken                       */	    .then(	CommandManager.literal (BROKEN )
/* /stats add    @p broken  [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats add    @p broken  [I] +                */						    .suggests(   BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats add    @p broken  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_BROKEN*EN_FLAT))
/* /stats add    @p broken  [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p broken  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_BROKEN*EN_INT))
/* /stats add    @p broken  [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p broken  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_BROKEN*EN_INT*EN_UNIT))
/* /stats add    @p broken  [I] [int]   [unit]   */	       )
/* /stats add    @p broken  [I] [int]            */	      )
/* /stats add    @p broken  [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats add    @p broken  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_BROKEN*EN_OBJECTIVE))
/* /stats add    @p broken  [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p broken  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_BROKEN*EN_OBJECTIVE*EN_UNIT))
/* /stats add    @p broken  [I] [score] [unit]   */	       )
/* /stats add    @p broken  [I] [score]          */	      )
/* /stats add    @p broken  [I]                  */	     )
/* /stats add    @p broken                       */	    )
/* /stats add    @p picked                       */	    .then(	CommandManager.literal (PICKED_UP   )
/* /stats add    @p picked  [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats add    @p picked  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_PICKED_UP*EN_FLAT))
/* /stats add    @p picked  [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p picked  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_PICKED_UP*EN_INT))
/* /stats add    @p picked  [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p picked  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_PICKED_UP*EN_INT*EN_UNIT))
/* /stats add    @p picked  [I] [int]   [unit]   */	       )
/* /stats add    @p picked  [I] [int]            */	      )
/* /stats add    @p picked  [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats add    @p picked  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_PICKED_UP*EN_OBJECTIVE))
/* /stats add    @p picked  [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p picked  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_PICKED_UP*EN_OBJECTIVE*EN_UNIT))
/* /stats add    @p picked  [I] [score] [unit]   */	       )
/* /stats add    @p picked  [I] [score]          */	      )
/* /stats add    @p picked  [I]                  */	     )
/* /stats add    @p picked                       */	    )
/* /stats add    @p dropped                      */	    .then(	CommandManager.literal (DROPPED)
/* /stats add    @p dropped [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats add    @p dropped [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_DROPPED*EN_FLAT))
/* /stats add    @p dropped [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p dropped [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_DROPPED*EN_INT))
/* /stats add    @p dropped [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p dropped [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_DROPPED*EN_INT*EN_UNIT))
/* /stats add    @p dropped [I] [int]   [unit]   */	       )
/* /stats add    @p dropped [I] [int]            */	      )
/* /stats add    @p dropped [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ItemArgumentType.item(access))
/* /stats add    @p dropped [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_DROPPED*EN_OBJECTIVE))
/* /stats add    @p dropped [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p dropped [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_DROPPED*EN_OBJECTIVE*EN_UNIT))
/* /stats add    @p dropped [I] [score] [unit]   */	       )
/* /stats add    @p dropped [I] [score]          */	      )
/* /stats add    @p dropped [I]                  */	     )
/* /stats add    @p dropped                      */	    )
/* /stats add    @p killed  [E]                  */	    .then(	CommandManager.literal (KILLED )
/* /stats add    @p killed  [E]                  */	     .then(	CommandManager.argument(STAT   , EntityTypeArgumentType.type(access))
/* /stats add    @p killed  [E] +                */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats add    @p killed  [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_KILLED*EN_FLAT))
/* /stats add    @p killed  [E] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p killed  [E] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_KILLED*EN_INT))
/* /stats add    @p killed  [E] [int]            */	      )
/* /stats add    @p killed  [E] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats add    @p killed  [E] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_KILLED*EN_OBJECTIVE))
/* /stats add    @p killed  [E] [score]          */	      )
/* /stats add    @p killed  [E]                  */	     )
/* /stats add    @p killed                       */	    )
/* /stats add    @p killedB [E]                  */	    .then(	CommandManager.literal (KILLED_BY)
/* /stats add    @p killedB [E]                  */	     .then(	CommandManager.argument(STAT   , EntityTypeArgumentType.type(access))
/* /stats add    @p killedB [E]                # */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats add    @p killedB [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_KILLED_BY*EN_FLAT))
/* /stats add    @p killedB [E] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p killedB [E] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_KILLED_BY*EN_INT))
/* /stats add    @p killedB [E] [int]            */	      )
/* /stats add    @p killedB [E] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   , ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats add    @p killedB [E] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_KILLED_BY*EN_OBJECTIVE))
/* /stats add    @p killedB [I] [score]          */	      )
/* /stats add    @p killedB [E]                  */	     )
/* /stats add    @p killedB                      */	    )
/* /stats add    @p custom                       */	    .then(	CommandManager.literal (CUSTOM )
/* /stats add    @p custom  [I]                  */	     .then(	CommandManager.argument(STAT   , CustomStatArgumentType.stat(access))
/* /stats add    @p custom  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CUSTOM*EN_FLAT))
/* /stats add    @p custom  [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats add    @p custom  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CUSTOM*EN_INT))
/* /stats add    @p custom  [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p custom  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CUSTOM*EN_INT*EN_UNIT))
/* /stats add    @p custom  [I] [int]   [unit]   */	       )
/* /stats add    @p custom  [I] [int]            */	      )
/* /stats add    @p custom  [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats add    @p custom  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CUSTOM*EN_OBJECTIVE))
/* /stats add    @p custom  [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats add    @p custom  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_ADD*EN_CUSTOM*EN_OBJECTIVE*EN_UNIT))
/* /stats add    @p custom  [I] [score] [unit]   */	       )
/* /stats add    @p custom  [I] [score]          */	      )
/* /stats add    @p custom  [I]                  */	     )
/* /stats add    @p custom                       */	    )
/* /stats add    @p                              */	   )
/* /stats add                                    */	  )
/* /stats reduce                                 */	  .then(		CommandManager.literal (REDUCE )
/* /stats reduce                               $ */	  .requires( (source)  -> source.hasPermissionLevel(REDUCE_OP))
/* /stats reduce @p                              */	   .then(		CommandManager.argument(TARGETS, EntityArgumentType.players())
/* /stats reduce @p mined                        */	    .then(	CommandManager.literal (MINED  )
/* /stats reduce @p mined   [B]                  */	     .then(	CommandManager.argument(STAT   ,BlockArgumentType.block(access))
/* /stats reduce @p mined   [B]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_MINED*EN_FLAT))
/* /stats reduce @p mined   [B] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p mined   [B] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_MINED*EN_INT))
/* /stats reduce @p mined   [B] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p mined   [B] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_MINED*EN_INT*EN_UNIT))
/* /stats reduce @p mined   [B] [int]   [unit]   */	       )
/* /stats reduce @p mined   [B] [int]            */	      )
/* /stats reduce @p mined   [B] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p mined   [B] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_MINED*EN_OBJECTIVE))
/* /stats reduce @p mined   [B] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p mined   [B] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_MINED*EN_OBJECTIVE*EN_UNIT))
/* /stats reduce @p mined   [B] [score] [unit]   */	       )
/* /stats reduce @p mined   [B] [score]          */	      )
/* /stats reduce @p mined   [B]                  */	     )
/* /stats reduce @p mined                        */	    )
/* /stats reduce @p crafted                      */	    .then(	CommandManager.literal (		CRAFTED)
/* /stats reduce @p crafted [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats reduce @p crafted [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CRAFTED*EN_FLAT))
/* /stats reduce @p crafted [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p crafted [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CRAFTED*EN_INT))
/* /stats reduce @p crafted [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p crafted [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CRAFTED*EN_INT*EN_UNIT))
/* /stats reduce @p crafted [I] [int]   [unit]   */	       )
/* /stats reduce @p crafted [I] [int]            */	      )
/* /stats reduce @p crafted [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p crafted [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CRAFTED*EN_OBJECTIVE))
/* /stats reduce @p crafted [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p crafted [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CRAFTED*EN_OBJECTIVE*EN_UNIT))
/* /stats reduce @p crafted [I] [score] [unit]   */	       )
/* /stats reduce @p crafted [I] [score]          */	      )
/* /stats reduce @p crafted [I]                  */	     )
/* /stats reduce @p crafted                      */	    )
/* /stats reduce @p used                         */	    .then(	CommandManager.literal (USED   )
/* /stats reduce @p used    [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats reduce @p used    [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_USED*EN_FLAT))
/* /stats reduce @p used    [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p used    [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_USED*EN_INT))
/* /stats reduce @p used    [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   , StringArgumentType.word())
/* /stats reduce @p used    [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_USED*EN_INT*EN_UNIT))
/* /stats reduce @p used    [I] [int]   [unit]   */	       )
/* /stats reduce @p used    [I] [int]            */	      )
/* /stats reduce @p used    [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p used    [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_USED*EN_OBJECTIVE))
/* /stats reduce @p used    [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p used    [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_USED*EN_OBJECTIVE*EN_UNIT))
/* /stats reduce @p used    [I] [score] [unit]   */	       )
/* /stats reduce @p used    [I] [score]          */	      )
/* /stats reduce @p used    [I]                  */	     )
/* /stats reduce @p used                         */	    )
/* /stats reduce @p broken                       */	    .then(	CommandManager.literal (BROKEN )
/* /stats reduce @p broken  [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats reduce @p broken  [I] +                */						    .suggests( BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats reduce @p broken  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_BROKEN*EN_FLAT))
/* /stats reduce @p broken  [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p broken  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_BROKEN*EN_INT))
/* /stats reduce @p broken  [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p broken  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_BROKEN*EN_INT*EN_UNIT))
/* /stats reduce @p broken  [I] [int]   [unit]   */	       )
/* /stats reduce @p broken  [I] [int]            */	      )
/* /stats reduce @p broken  [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p broken  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_BROKEN*EN_OBJECTIVE))
/* /stats reduce @p broken  [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p broken  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_BROKEN*EN_OBJECTIVE*EN_UNIT))
/* /stats reduce @p broken  [I] [score] [unit]   */	       )
/* /stats reduce @p broken  [I] [score]          */	      )
/* /stats reduce @p broken  [I]                  */	     )
/* /stats reduce @p broken                       */	    )
/* /stats reduce @p picked                       */	    .then(	CommandManager.literal (PICKED_UP   )
/* /stats reduce @p picked  [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats reduce @p picked  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_PICKED_UP*EN_FLAT))
/* /stats reduce @p picked  [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p picked  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_PICKED_UP*EN_INT))
/* /stats reduce @p picked  [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p picked  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_PICKED_UP*EN_INT*EN_UNIT))
/* /stats reduce @p picked  [I] [int]   [unit]   */	       )
/* /stats reduce @p picked  [I] [int]            */	      )
/* /stats reduce @p picked  [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p picked  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_PICKED_UP*EN_OBJECTIVE))
/* /stats reduce @p picked  [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p picked  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_PICKED_UP*EN_OBJECTIVE*EN_UNIT))
/* /stats reduce @p picked  [I] [score] [unit]   */	       )
/* /stats reduce @p picked  [I] [score]          */	      )
/* /stats reduce @p picked  [I]                  */	     )
/* /stats reduce @p picked                       */	    )
/* /stats reduce @p dropped                      */	    .then(	CommandManager.literal (DROPPED)
/* /stats reduce @p dropped [I]                  */	     .then(	CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats reduce @p dropped [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_DROPPED*EN_FLAT))
/* /stats reduce @p dropped [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p dropped [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_DROPPED*EN_INT))
/* /stats reduce @p dropped [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p dropped [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_DROPPED*EN_INT*EN_UNIT))
/* /stats reduce @p dropped [I] [int]   [unit]   */	       )
/* /stats reduce @p dropped [I] [int]            */	      )
/* /stats reduce @p dropped [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p dropped [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_DROPPED*EN_OBJECTIVE))
/* /stats reduce @p dropped [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p dropped [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_DROPPED*EN_OBJECTIVE*EN_UNIT))
/* /stats reduce @p dropped [I] [score] [unit]   */	       )
/* /stats reduce @p dropped [I] [score]          */	      )
/* /stats reduce @p dropped [I]                  */	     )
/* /stats reduce @p dropped                      */	    )
/* /stats reduce @p killed  [E]                  */	    .then(	CommandManager.literal (KILLED )
/* /stats reduce @p killed  [E]                  */	     .then(	CommandManager.argument(STAT   ,EntityTypeArgumentType.type(access))
/* /stats reduce @p killed  [E] +                */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats reduce @p killed  [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_KILLED*EN_FLAT))
/* /stats reduce @p killed  [E] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p killed  [E] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_KILLED*EN_INT))
/* /stats reduce @p killed  [E] [int]            */	      )
/* /stats reduce @p killed  [E] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p killed  [E] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_KILLED*EN_OBJECTIVE))
/* /stats reduce @p killed  [E] [score]          */	      )
/* /stats reduce @p killed  [E]                  */	     )
/* /stats reduce @p killed                       */	    )
/* /stats reduce @p killedB [E]                  */	    .then(	CommandManager.literal (KILLED_BY)
/* /stats reduce @p killedB [E]                  */	     .then(	CommandManager.argument(STAT   ,EntityTypeArgumentType.type(access))
/* /stats reduce @p killedB [E]                # */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats reduce @p killedB [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_KILLED_BY*EN_FLAT))
/* /stats reduce @p killedB [E] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p killedB [E] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_KILLED_BY*EN_INT))
/* /stats reduce @p killedB [E] [int]            */	      )
/* /stats reduce @p killedB [E] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p killedB [E] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_KILLED_BY*EN_OBJECTIVE))
/* /stats reduce @p killedB [I] [score]          */	      )
/* /stats reduce @p killedB [E]                  */	     )
/* /stats reduce @p killedB                      */	    )
/* /stats reduce @p custom                       */	    .then(	CommandManager.literal (CUSTOM )
/* /stats reduce @p custom  [I]                  */	     .then(	CommandManager.argument(STAT   , CustomStatArgumentType.stat(access))
/* /stats reduce @p custom  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CUSTOM*EN_FLAT))
/* /stats reduce @p custom  [I] [int]            */	      .then(	CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats reduce @p custom  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CUSTOM*EN_INT))
/* /stats reduce @p custom  [I] [int]   [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p custom  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CUSTOM*EN_INT*EN_UNIT))
/* /stats reduce @p custom  [I] [int]   [unit]   */	       )
/* /stats reduce @p custom  [I] [int]            */	      )
/* /stats reduce @p custom  [I] [score]          */	      .then(	CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats reduce @p custom  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CUSTOM*EN_OBJECTIVE))
/* /stats reduce @p custom  [I] [score] [unit]   */	       .then(	CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats reduce @p custom  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_REDUCE*EN_CUSTOM*EN_OBJECTIVE*EN_UNIT))
/* /stats reduce @p custom  [I] [score] [unit]   */	       )
/* /stats reduce @p custom  [I] [score]          */	      )
/* /stats reduce @p custom  [I]                  */	     )
/* /stats reduce @p custom                       */	    )
/* /stats reduce @p                              */	   )
/* /stats reduce                                 */	  )
/* /stats set                                    */	  .then(		CommandManager.literal (SET)
/* /stats store                                $ */						    .requires( (source)  -> source.hasPermissionLevel(SET_OP))
/* /stats set    @p                              */	  .then(		CommandManager.argument(TARGETS, EntityArgumentType.players())
/* /stats set    @p mined                        */	  .then(		CommandManager.literal (MINED  )
/* /stats set    @p mined   [B]                  */	  .then(		CommandManager.argument(STAT   ,BlockArgumentType.block(access))
/* /stats set    @p mined   [B]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_MINED*EN_FLAT))
/* /stats set    @p mined   [B] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p mined   [B] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_MINED*EN_INT))
/* /stats set    @p mined   [B] [int]   [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p mined   [B] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_MINED*EN_INT*EN_UNIT))
/* /stats set    @p mined   [B] [int]   [unit]   */	  )
/* /stats set    @p mined   [B] [int]            */	  )
/* /stats set    @p mined   [B] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p mined   [B] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_MINED*EN_OBJECTIVE))
/* /stats set    @p mined   [B] [score] [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p mined   [B] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_MINED*EN_OBJECTIVE*EN_UNIT))
/* /stats set    @p mined   [B] [score] [unit]   */	  )
/* /stats set    @p mined   [B] [score]          */	  )
/* /stats set    @p mined   [B]                  */	  )
/* /stats set    @p mined                        */	  )
/* /stats set    @p crafted                      */	  .then(		CommandManager.literal (		CRAFTED)
/* /stats set    @p crafted [I]                  */	  .then(		CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats set    @p crafted [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CRAFTED*EN_FLAT))
/* /stats set    @p crafted [I] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p crafted [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CRAFTED*EN_INT))
/* /stats set    @p crafted [I] [int]   [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p crafted [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CRAFTED*EN_INT*EN_UNIT))
/* /stats set    @p crafted [I] [int]   [unit]   */	  )
/* /stats set    @p crafted [I] [int]            */	  )
/* /stats set    @p crafted [I] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p crafted [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CRAFTED*EN_OBJECTIVE))
/* /stats set    @p crafted [I] [score] [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p crafted [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CRAFTED*EN_OBJECTIVE*EN_UNIT))
/* /stats set    @p crafted [I] [score] [unit]   */	  )
/* /stats set    @p crafted [I] [score]          */	  )
/* /stats set    @p crafted [I]                  */	  )
/* /stats set    @p crafted                      */	  )
/* /stats set    @p used                         */	  .then(		CommandManager.literal (USED   )
/* /stats set    @p used    [I]                  */	  .then(		CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats set    @p used    [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_USED*EN_FLAT))
/* /stats set    @p used    [I] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p used    [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_USED*EN_INT))
/* /stats set    @p used    [I] [int]   [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p used    [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_USED*EN_INT*EN_UNIT))
/* /stats set    @p used    [I] [int]   [unit]   */	  )
/* /stats set    @p used    [I] [int]            */	  )
/* /stats set    @p used    [I] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p used    [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_USED*EN_OBJECTIVE))
/* /stats set    @p used    [I] [score] [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p used    [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_USED*EN_OBJECTIVE*EN_UNIT))
/* /stats set    @p used    [I] [score] [unit]   */	  )
/* /stats set    @p used    [I] [score]          */	  )
/* /stats set    @p used    [I]                  */	  )
/* /stats set    @p used                         */	  )
/* /stats set    @p broken                       */	  .then(		CommandManager.literal (BROKEN )
/* /stats set    @p broken  [I]                  */	  .then(		CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats set    @p broken  [I] +                */						    .suggests( BreakableItemSuggestionProvider.BREAKABLE_ITEMS)
/* /stats set    @p broken  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_BROKEN*EN_FLAT))
/* /stats set    @p broken  [I] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p broken  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_BROKEN*EN_INT))
/* /stats set    @p broken  [I] [int]   [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p broken  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_BROKEN*EN_INT*EN_UNIT))
/* /stats set    @p broken  [I] [int]   [unit]   */	  )
/* /stats set    @p broken  [I] [int]            */	  )
/* /stats set    @p broken  [I] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p broken  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_BROKEN*EN_OBJECTIVE))
/* /stats set    @p broken  [I] [score] [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p broken  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_BROKEN*EN_OBJECTIVE*EN_UNIT))
/* /stats set    @p broken  [I] [score] [unit]   */	  )
/* /stats set    @p broken  [I] [score]          */	  )
/* /stats set    @p broken  [I]                  */	  )
/* /stats set    @p broken                       */	  )
/* /stats set    @p picked                       */	  .then(		CommandManager.literal (PICKED_UP   )
/* /stats set    @p picked  [I]                  */	  .then(		CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats set    @p picked  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_PICKED_UP*EN_FLAT))
/* /stats set    @p picked  [I] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p picked  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_PICKED_UP*EN_INT))
/* /stats set    @p picked  [I] [int]   [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p picked  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_PICKED_UP*EN_INT*EN_UNIT))
/* /stats set    @p picked  [I] [int]   [unit]   */	  )
/* /stats set    @p picked  [I] [int]            */	  )
/* /stats set    @p picked  [I] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p picked  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_PICKED_UP))
/* /stats set    @p picked  [I] [score] [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p picked  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_PICKED_UP*EN_UNIT))
/* /stats set    @p picked  [I] [score] [unit]   */	  )
/* /stats set    @p picked  [I] [score]          */	  )
/* /stats set    @p picked  [I]                  */	  )
/* /stats set    @p picked                       */	  )
/* /stats set    @p dropped                      */	  .then(		CommandManager.literal (DROPPED)
/* /stats set    @p dropped [I]                  */	  .then(		CommandManager.argument(STAT   ,ItemArgumentType.item(access))
/* /stats set    @p dropped [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_DROPPED*EN_FLAT))
/* /stats set    @p dropped [I] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p dropped [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_DROPPED*EN_INT))
/* /stats set    @p dropped [I] [int]   [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p dropped [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_DROPPED*EN_INT*EN_UNIT))
/* /stats set    @p dropped [I] [int]   [unit]   */	  )
/* /stats set    @p dropped [I] [int]            */	  )
/* /stats set    @p dropped [I] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p dropped [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_DROPPED*EN_OBJECTIVE))
/* /stats set    @p dropped [I] [score] [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p dropped [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_DROPPED*EN_OBJECTIVE*EN_UNIT))
/* /stats set    @p dropped [I] [score] [unit]   */	  )
/* /stats set    @p dropped [I] [score]          */	  )
/* /stats set    @p dropped [I]                  */	  )
/* /stats set    @p dropped                      */	  )
/* /stats set    @p killed  [E]                  */	  .then(		CommandManager.literal (KILLED )
/* /stats set    @p killed  [E]                  */	  .then(		CommandManager.argument(STAT   , EntityTypeArgumentType.type(access))
/* /stats set    @p killed  [E]                # */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats set    @p killed  [E]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_KILLED*EN_FLAT))
/* /stats set    @p killed  [E] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p killed  [E] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_KILLED*EN_INT))
/* /stats set    @p killed  [E] [int]            */	  )
/* /stats set    @p killed  [E] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p killed  [E] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_KILLED*EN_OBJECTIVE))
/* /stats set    @p killed  [E] [score]          */	  )
/* /stats set    @p killed  [E]                  */	  )
/* /stats set    @p killed                       */	  )
/* /stats set    @p killedB [E]                  */	  .then(		CommandManager.literal(KILLED_BY)
/* /stats set    @p killedB [E]                  */	  .then(		CommandManager.argument(STAT   , EntityTypeArgumentType.type(access))
/* /stats set    @p killedB [E]                # */						    .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
/* /stats set    @p killedB [E]                % */						    .executes( (context) -> decodeExecutionMode(context, EN_SET*EN_KILLED_BY*EN_FLAT))
/* /stats set    @p killedB [E] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p killedB [E] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_KILLED_BY*EN_INT))
/* /stats set    @p killedB [E] [int]            */	  )
/* /stats set    @p killedB [E] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p killedB [E] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_KILLED_BY*EN_OBJECTIVE))
/* /stats set    @p killedB [I] [score]          */	  )
/* /stats set    @p killedB [E]                  */	  )
/* /stats set    @p killedB                      */	  )
/* /stats set    @p custom                       */	  .then(		CommandManager.literal (CUSTOM )
/* /stats set    @p custom  [I]                  */	  .then(		CommandManager.argument(STAT   , CustomStatArgumentType.stat(access))
/* /stats set    @p custom  [I]                % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CUSTOM*EN_FLAT))
/* /stats set    @p custom  [I] [int]            */	  .then(		CommandManager.argument(AMOUNT ,IntegerArgumentType.integer())
/* /stats set    @p custom  [I] [int]          % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CUSTOM*EN_INT))
/* /stats set    @p custom  [I] [int]   [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p custom  [I] [int]   [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CUSTOM*EN_INT*EN_UNIT))
/* /stats set    @p custom  [I] [int]   [unit]   */	  )
/* /stats set    @p custom  [I] [int]            */	  )
/* /stats set    @p custom  [I] [score]          */	  .then(		CommandManager.argument(OBJECTIVE   ,ScoreboardObjectiveArgumentType.scoreboardObjective())
/* /stats set    @p custom  [I] [score]        % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CUSTOM*EN_OBJECTIVE))
/* /stats set    @p custom  [I] [score] [unit]   */	  .then(		CommandManager.argument(UNIT   ,StringArgumentType.word())
/* /stats set    @p custom  [I] [score] [unit] % */						    .executes( (context) -> decodeExecutionMode( context, EN_SET*EN_CUSTOM*EN_OBJECTIVE*EN_UNIT))
/* /stats set    @p custom  [I] [score] [unit]   */	      )
/* /stats set    @p custom  [I] [score]          */	     )
/* /stats set    @p custom  [I]                  */	    )
/* /stats set    @p custom                       */	   )
/* /stats set    @p                              */	  )
/* /stats set                                    */	 )
/* /stats                                        */	)
/*                                               */;

     }
     
     @SuppressWarnings("unused")
     private static <T> int dummyExecution     ( CommandContext<ServerCommandSource> context,  int arg )throws CommandSyntaxException{
          ServerCommandSource source= (ServerCommandSource) context.getSource();
          source.sendFeedback(() -> literal( "This is the dummy function. The arg is: " + arg),false);
          return -1;
     }
     @SuppressWarnings("unchecked")
     private static <T> int decodeExecutionMode( @NotNull CommandContext<ServerCommandSource> context,  int arg)throws CommandSyntaxException{
          ServerCommandSource source= (ServerCommandSource) context.getSource();
          Collection<ServerPlayerEntity> players= EntityArgumentType.getPlayers(context,TARGETS);
          StatType<T>  statType   ;
          Block         statBlock	=null;
          Item          statItem	=null;
          EntityType<?> statEntity	=null;
          Identifier    statID    	=null;
		String projectName		=null;
          ScoreboardObjective objective = ( (arg%EN_OBJECTIVE) * (arg%EN_STORE) ==0 ) ? ScoreboardObjectiveArgumentType.getObjective( context, OBJECTIVE): null;
          int                 amount    = ( (arg%EN_QUERY)*(arg%EN_STORE)==0 ) ? -1:
                                          (  arg%EN_FLAT==0 )                  ? ( (arg%EN_SET==0) ?0:1 ) :
                                          (  arg%EN_INT==0 )                   ? IntegerArgumentType.getInteger( context, AMOUNT): -1;
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
		
		if (arg%EN_PROJECT==0) projectName = StringArgumentType.getString(context,PROJECT_NAME);
		
          if( arg%EN_QUERY==0)
               return (arg%EN_PROJECT==0) ?  executeProjectQUERY( context, players, statType, (T) statSpec,            projectName ):
                      (arg%EN_UNIT==0)    ?  executeQUERY( source, players, statType, (T) statSpec, unitStr ):
                                             executeQUERY( source, players, statType, (T) statSpec );
          if( arg%EN_STORE==0)
               return (arg%EN_PROJECT==0) ?  executeProjectSTORE( context, players, statType, (T) statSpec, objective, projectName ):
                      (arg%EN_UNIT==0)    ?  executeSTORE( source, players, statType, (T) statSpec, objective, unitStr ):
                                             executeSTORE( source, players, statType, (T) statSpec, objective );
          if( arg % EN_ADD   ==0){
               if ((arg % EN_FLAT) * (arg % EN_INT) == 0) return ( arg % EN_UNIT == 0 ) ? executeADD   (source, players, statType, (T) statSpec, amount,    unitStr) :
                                                                                          executeADD   (source, players, statType, (T) statSpec, amount            ) ;
               if (arg % EN_OBJECTIVE == 0) return ( arg % EN_UNIT == 0 )               ? executeADD   (source, players, statType, (T) statSpec, objective, unitStr) :
                                                                                          executeADD   (source, players, statType, (T) statSpec, objective         ) ;
          }
          if( arg % EN_REDUCE==0){
               if ((arg % EN_FLAT) * (arg % EN_INT) == 0) return ( arg % EN_UNIT == 0 ) ? executeREDUCE(source, players, statType, (T) statSpec, amount,    unitStr) :
                                                                                          executeREDUCE(source, players, statType, (T) statSpec, amount            ) ;
               if (arg % EN_OBJECTIVE == 0) return ( arg % EN_UNIT == 0 )               ? executeREDUCE(source, players, statType, (T) statSpec, objective, unitStr) :
                                                                                          executeREDUCE(source, players, statType, (T) statSpec, objective         ) ;
          }
          if( arg % EN_SET   ==0){
               if ((arg % EN_FLAT) * (arg % EN_INT) == 0) return ( arg % EN_UNIT == 0 ) ? executeSET   (source, players, statType, (T) statSpec, amount,    unitStr) :
                                                                                          executeSET   (source, players, statType, (T) statSpec, amount            ) ;
               if (arg % EN_OBJECTIVE == 0) return ( arg % EN_UNIT == 0 )               ? executeSET   (source, players, statType, (T) statSpec, objective, unitStr) :
                                                                                          executeSET   (source, players, statType, (T) statSpec, objective         ) ;
          }
         
         return -1;
    }
    
// /statistics project start dummy [projectname] <EXECUTE>
     private static int executeProjectSTART_DUMMY( CommandContext<ServerCommandSource> context, String projectName) throws CommandSyntaxException {
		//checks if projectName is reserved from use. Sends feedback and returns early failure if so.
     	if (checkIsProjectNameReserved(context, projectName)) return -1;
      
		ServerCommandSource source = context.getSource();
          
          boolean isNewProject=false;
          File directory = new File( constructProjectDirectoryName(source,projectName));
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
			
			File directory = new File( constructProjectDirectoryName(player,projectName));
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
				File directory = new File( constructProjectDirectoryName(player,projectName));
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
				File directory = new File( constructProjectDirectoryName(player,projectName));
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
	private static<T> int executeProjectQUERY(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, String projectName) {
		if (checkIsProjectNameReserved(context, projectName)) return -1;
		ServerCommandSource source = context.getSource();
		
		int toReturn=0;
		for (ServerPlayerEntity player: targets){
			ServerStatHandler handler	= player.getStatHandler();
			iStatHandlerMixin iHandler	= (iStatHandlerMixin)(StatHandler)handler;
			File targetFile			= new File( constructProjectFileName(player,projectName) );
			
			handler.save();
				ServerStatHandler targetHandler = iHandler.getProjectStatHandler(targetFile);
				if (targetHandler==null) {
                         File targetDir= new File( constructProjectDirectoryName(player,projectName));
                         if(targetDir.exists())  source.sendFeedback(()-> ProjectFeedback.provideErrorFeedback(PROJECT_NOT_TRACKED, player, projectName),false);
                         else                    source.sendFeedback(()-> ProjectFeedback.provideErrorFeedback(PROJECT_NOT_FOUND  ,         projectName),false);
					return -1;
				}
				int statValue = targetHandler.getStat(statType, statSpec);
                    boolean projectActive= ((iStatHandlerMixin) handler).isProjectActive(targetHandler);
			handler.save();

			source.sendFeedback(() -> ProjectFeedback.provideQueryFeedback(player, statType, statSpec, statValue, sanitizeString(projectName),projectActive),false);

			toReturn +=statValue;
		}
		return toReturn;
	}
// /statistics project store @p <EXECUTE>
	private static<T> int executeProjectSTORE(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective objective, String projectName){
		if (checkIsProjectNameReserved(context, projectName)) return -1;
		
		ServerCommandSource source = context.getSource();
		int toReturn=0;
		for (ServerPlayerEntity player : targets) {
			ServerStatHandler handler	= player.getStatHandler();
			Scoreboard scoreboard         = source.getServer().getScoreboard();
			iStatHandlerMixin iHandler	= (iStatHandlerMixin)(StatHandler)handler;
			File targetFile			= new File( constructProjectFileName(player,projectName) );
			
			ServerStatHandler targetHandler = iHandler.getProjectStatHandler(targetFile);
			handler.save();
                    if (targetHandler==null) {
                         File targetDir= new File( constructProjectDirectoryName(player,projectName));
                         if (targetDir.exists())  source.sendFeedback(()-> ProjectFeedback.provideErrorFeedback( join(STORE,PROJECT_NOT_TRACKED), player, projectName),false);
                         else                     source.sendFeedback(()-> ProjectFeedback.provideErrorFeedback( join(STORE,PROJECT_NOT_FOUND  ),         projectName),false);
                         return -1;
                    }
                    int statValue = targetHandler.getStat(statType, statSpec);
                    boolean projectActive= ((iStatHandlerMixin) handler).isProjectActive(targetHandler);
				scoreboard.getOrCreateScore(player, objective).setScore(statValue);
			handler.save();
			
		     source.sendFeedback(() -> ProjectFeedback.provideStoreFeedback(player, statType, statSpec, statValue, objective, sanitizeString(projectName), projectActive),false);

			toReturn +=statValue;
		}
		return toReturn;
	}
     
     private static String constructProjectFileName      ( ServerPlayerEntity player, String projectName){
          return constructProjectDirectoryName(player,projectName) +
          "\\" + join( player.getUuidAsString(), "json");
     }
     private static String constructProjectDirectoryName ( ServerPlayerEntity player, String projectName){
          World world = player.getWorld();
          MinecraftServer server= world.getServer();
          String sanitizedProjectName = sanitizeString(projectName);
          
          boolean isSaves	= (new File( "./saves")).exists();
          String savesDir	= "/saves";
          String worldDir	= "/" + world.toString().substring(world.toString().indexOf("[")+1,world.toString().indexOf("]")) ;
          String statsDir	= "\\stats";
          String projectDir	= "\\" + sanitizedProjectName;
          
          return "." + (isSaves? savesDir:EMPTY) + worldDir + statsDir + projectDir;
     }
     private static String constructProjectDirectoryName ( ServerCommandSource source, String projectName){
          World world = source.getWorld();
          MinecraftServer server= world.getServer();
          String sanitizedProjectName = sanitizeString(projectName);
          
          boolean isSaves	= (new File( "./saves")).exists();
          String savesDir	= "/saves";
          String worldDir	= "/" + world.toString().substring(world.toString().indexOf("[")+1,world.toString().indexOf("]")) ;
          String statsDir	= "\\stats";
          String projectDir	= "\\" + sanitizedProjectName;
          
          return "." + (isSaves? savesDir:EMPTY) + worldDir + statsDir + projectDir;
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
     private static <T> int executeQUERY(ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec                    ) throws CommandSyntaxException {
          int toReturn=0;
          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               handler.save();
               int statValue = handler.getStat(statType, statSpec);
               handler.save();

               source.sendFeedback(() -> QueryFeedback.provideBasicFeedback(player, statType, statSpec, statValue),false);

               toReturn +=statValue;
          }
          return toReturn;}
// /statistics query @p stat_type stat unit <EXECUTE>
     private static <T> int executeQUERY(ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, String unit_arg   ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(2);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else{
               int toReturn=0;
               for (ServerPlayerEntity player : targets) {
                    ServerStatHandler handler = player.getStatHandler();
                    handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                    handler.save();
                    
                    int adjustedStatValue = unit.convertTo( isInventory? statValue*determineStackSize(statSpec):statValue);
                    source.sendFeedback(() -> QueryFeedback.provideBasicUnitFeedback(player, statType, statSpec, adjustedStatValue,unitKey),false);
     
                    toReturn +=statValue;
               }
               return toReturn;
          }
     }

     // /statistics store @p stat_ype stat objective <EXECUTE>
     private static <T> int executeSTORE(ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective objective ) throws CommandSyntaxException {
          int toReturn=0;
          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               Scoreboard scoreboard = source.getServer().getScoreboard();
               handler.save();
                    int statValue = handler.getStat(statType, statSpec);
                    scoreboard.getOrCreateScore(player, objective).setScore(statValue);
               handler.save();
               
               source.sendFeedback(() -> GeneralFeedback.provideStoreFeedback(player, statType, statSpec, statValue, objective),false);

               toReturn +=statValue;
          }
          return toReturn;
     }
// /statistics store @p stat_ype stat objective unit <EXECUTE>
     private static <T> int executeSTORE(ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective objective, String unit_arg ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(2);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else {
               int toReturn = 0;
               for (ServerPlayerEntity player : targets) {
                    ServerStatHandler handler = player.getStatHandler();
                    Scoreboard scoreboard = source.getServer().getScoreboard();
                    
                    handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                         int adjustedStatValue = unit.convertTo(isInventory ? statValue * determineStackSize(statSpec) : statValue);
                         scoreboard.getOrCreateScore(player, objective).setScore(adjustedStatValue);
                    handler.save();
                    
                    source.sendFeedback(() -> GeneralFeedback.provideStoreFeedback(player, statType, statSpec, adjustedStatValue, objective), false);
                    
                    toReturn += statValue;
               }
               return toReturn;
          }
     }
     
// /statistics add @p stat_type stat [ _1_ & int ] [ ____ ] <EXECUTE>
     private static <T> int  executeADD         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, int amount              ) throws CommandSyntaxException {
          int toReturn=0;
          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               handler.save();
               int statValue = handler.getStat(statType, statSpec);
               player.increaseStat( statType.getOrCreateStat(statSpec), amount);
               handler.save();

               source.sendFeedback(() -> GeneralFeedback.provideBasicAddFeedback(player, statType, statSpec, statValue, amount),false);
               toReturn+=amount;

          }
          return toReturn;
     }
// /statistics add @p stat_type stat [ objective ] [ ____ ] <EXECUTE>
     private static <T> int  executeADD         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective obj ) throws CommandSyntaxException {
          int toReturn=0;

          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               Scoreboard scoreboard = player.getScoreboard();
               int amount    = scoreboard.getOrCreateScore(player,obj).getScore();

               handler.save();
                    int statValue = handler.getStat(statType, statSpec);
                    player.increaseStat( statType.getOrCreateStat(statSpec), amount);
               handler.save();
               
               
               source.sendFeedback(() -> GeneralFeedback.provideObjectiveAddFeedback(player, statType, statSpec, statValue, amount, obj),false);
               toReturn+=amount;

          }
          return toReturn;
     }
// /statistics add @p stat_type stat [ _1_ & int ] [ unit_arg ] <EXECUTE> //TODO correct feedback.
     private static <T> int  executeADD         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, int amount, String unit_arg ) throws CommandSyntaxException {
         ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
         iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
         boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
         boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
         boolean isInventory       = (boolean)    parsedUnitInfo.get(3);
         
         String unitKey = unit.getTranslationKey();
         
         if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
         else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
         else {
              int toReturn = 0;
              for (ServerPlayerEntity player : targets) {
                   int adjustedAmount = unit.convertFrom( isInventory? amount*determineStackSize(statSpec):amount);
                   
                   ServerStatHandler handler = player.getStatHandler();
                   handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                         player.increaseStat( statType.getOrCreateStat(statSpec), adjustedAmount);
                   handler.save();
                   
                        source.sendFeedback(() -> GeneralFeedback.provideUnitAddFeedback(player, statType, statSpec, statValue, amount, adjustedAmount, unitKey),false);
                   
              }
              return toReturn;
         }
     }
// /statistics add @p stat_type stat [ objective ] [ unit ] <EXECUTE> //TODO correct feedback.
     private static <T> int  executeADD         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective obj, String unit_arg ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit               = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit      = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit   = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory      = (boolean)    parsedUnitInfo.get(3);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else {
               int toReturn = 0;
               for (ServerPlayerEntity player : targets) {
                    
                    ServerStatHandler handler = player.getStatHandler();
                    Scoreboard scoreboard = player.getScoreboard();
                    int amount = scoreboard.getOrCreateScore(player, obj).getScore();
                    int adjustedAmount = unit.convertFrom( isInventory? amount*determineStackSize(statSpec):amount);
                    
                    handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                         player.increaseStat(statType.getOrCreateStat(statSpec), adjustedAmount);
                    handler.save();
                    
                    source.sendFeedback(() -> GeneralFeedback.provideObjectiveUnitAddFeedback(player, statType, statSpec, statValue, amount, adjustedAmount, obj, unitKey),false);
                    
               }
               return toReturn;
          }
     }
	
// /statistics set @p stat_type stat [ _0_ & int ] [ ____ ] <EXECUTE>
     private static <T> int  executeSET         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, int amount              ) throws CommandSyntaxException {
          int toReturn=0;
          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               handler.save();
                    int statValue = handler.getStat(statType, statSpec);
                    player.resetStat(statType.getOrCreateStat(statSpec));
                         handler.save();
                    player.increaseStat(statType.getOrCreateStat(statSpec), amount);
               handler.save();
               
               source.sendFeedback(() -> GeneralFeedback.provideBasicSetFeedback(player, statType, statSpec, statValue, amount),false);
               
               toReturn+=amount;
          }
          return toReturn;
     }
// /statistics set @p stat_type stat [ objective ] [ ____ ] <EXECUTE>
     private static <T> int  executeSET         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective obj ) throws CommandSyntaxException {
          int toReturn=0;
          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               Scoreboard scoreboard = player.getScoreboard();
               int amount    = scoreboard.getOrCreateScore(player,obj).getScore();
               
               handler.save();
               int statValue = handler.getStat(statType, statSpec);
               player.resetStat(statType.getOrCreateStat(statSpec));
               handler.save();
               player.increaseStat(statType.getOrCreateStat(statSpec), amount);
               handler.save();
               
               source.sendFeedback(() -> GeneralFeedback.provideObjectiveSetFeedback(player, statType, statSpec, statValue, amount, obj), false);
               
               toReturn+=amount;
          }
          return toReturn;
     }
// /statistics set @p stat_type stat [ _0_ & int ] [ unit ] <EXECUTE> //TODO correct feedback
     private static <T> int  executeSET         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, int amount, String unit_arg ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(3);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else {
               int adjustedAmount = unit.convertFrom( isInventory? amount*determineStackSize(statSpec):amount);
               
               int toReturn = 0;
               for (ServerPlayerEntity player : targets) {
                    ServerStatHandler handler = player.getStatHandler();
                    handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                         player.resetStat(statType.getOrCreateStat(statSpec));
                    handler.save();
                         player.increaseStat(statType.getOrCreateStat(statSpec), adjustedAmount);
                    handler.save();
                    
                    source.sendFeedback(() -> GeneralFeedback.provideUnitSetFeedback(player, statType, statSpec, statValue, amount, adjustedAmount, unitKey),false);
                    
                    
               }
               return toReturn;
               
          }
     }
// /statistics set @p stat_type stat [ objective ] [ unit ] <EXECUTE> //TODO correct feedback.
     private static <T> int  executeSET         (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective obj, String unit_arg ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(3);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else {
               
               int toReturn=0;
               for (ServerPlayerEntity player : targets) {
                    ServerStatHandler handler = player.getStatHandler();
                    
                    Scoreboard scoreboard = player.getScoreboard();
                    int amount         = scoreboard.getOrCreateScore(player,obj).getScore();
                    int adjustedAmount = unit.convertFrom( isInventory? amount*determineStackSize(statSpec):amount);
                    
                    handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                         player.resetStat(statType.getOrCreateStat(statSpec));
                    handler.save();
                         player.increaseStat(statType.getOrCreateStat(statSpec), adjustedAmount);
                    handler.save();
                    
                    source.sendFeedback(() -> GeneralFeedback.provideObjectiveUnitSetFeedback(player, statType, statSpec, statValue, amount, adjustedAmount, obj, unitKey), false);
               
               }
               return toReturn;
          }
     }

// /statistics reduce @p stat_type stat [ ___ | int | objective] [ --- | unit ]
// /statistics reduce @p stat_type stat [ _1_ | int ] [ ____ ]
     private static <T> int  executeREDUCE      (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, int amount              ) throws CommandSyntaxException {
          int toReturn=0;
          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               handler.save();
                    int statValue     = handler.getStat(statType, statSpec);
                    int statValueNext = Math.max(MINIMUM_STAT_VALUE, statValue - amount);
                    player.resetStat(statType.getOrCreateStat(statSpec));
                         handler.save();
                    player.increaseStat(statType.getOrCreateStat(statSpec), statValueNext);
               handler.save();

               source.sendFeedback(() -> GeneralFeedback.provideBasicReduceFeedback(player, statType, statSpec, statValue, amount), false);

               toReturn+=amount;
          }
          return toReturn;
     }
// /statistics reduce @p stat_type stat [ objective ] [ ____ ]
     private static <T> int  executeREDUCE      (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective obj ) throws CommandSyntaxException {
          int toReturn=0;
          for (ServerPlayerEntity player : targets) {
               ServerStatHandler handler = player.getStatHandler();
               Scoreboard scoreboard = player.getScoreboard();
               int amount    = scoreboard.getOrCreateScore(player,obj).getScore();
               
               handler.save();
                    int statValue     = handler.getStat(statType, statSpec);
                    int statValueNext = Math.max(MINIMUM_STAT_VALUE, statValue - amount);
                    player.resetStat(statType.getOrCreateStat(statSpec));
               handler.save();
                    player.increaseStat(statType.getOrCreateStat(statSpec), statValueNext);
               handler.save();
               
               source.sendFeedback(() -> GeneralFeedback.provideObjectiveReduceFeedback(player, statType, statSpec, statValue, amount, obj), false);
               toReturn+=amount;
          }
          return toReturn;
     }
// /statistics reduce @p stat_type stat [ int ] [ unit ] // TODO correct feedback.
     private static <T> int  executeREDUCE      (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, int amount, String unit_arg ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(3);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else {
               int adjustedAmount = unit.convertFrom( isInventory? amount*determineStackSize(statSpec):amount);
               int toReturn = 0;
               
               for (ServerPlayerEntity player : targets) {
                    ServerStatHandler handler = player.getStatHandler();
                    handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                         int statValueNext = Math.max(MINIMUM_STAT_VALUE, statValue - adjustedAmount);
                         player.resetStat(statType.getOrCreateStat(statSpec));
                    handler.save();
                         player.increaseStat(statType.getOrCreateStat(statSpec), statValueNext);
                    handler.save();
                    
                    source.sendFeedback(() -> GeneralFeedback.provideUnitReduceFeedback(player, statType, statSpec, statValue, amount, adjustedAmount, unitKey), false);
                    toReturn+=amount;
                    
               }
               return toReturn;
          }
     }
// /statistics reduce @p stat_type stat [ objective ] [ unit ] // TODO correct feedback
     private static <T> int  executeREDUCE      (ServerCommandSource source, Collection<ServerPlayerEntity> targets, StatType<T> statType, T statSpec, ScoreboardObjective obj, String unit_arg ) throws CommandSyntaxException {
          ArrayList<Object> parsedUnitInfo = completelyParseUnit(unit_arg,statType,statSpec);
          iUnit unit                = (iUnit)      parsedUnitInfo.get(0);
          boolean isValidUnit       = (boolean)    parsedUnitInfo.get(1);
          boolean isSensicalUnit    = (boolean)    parsedUnitInfo.get(2);
          boolean isInventory       = (boolean)    parsedUnitInfo.get(3);
          
          String unitKey = unit.getTranslationKey();
          
          if     ( !isValidUnit     )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.unrecognized"   , unit_arg), false);    return -1;}
          else if( !isSensicalUnit  )    { source.sendFeedback(() -> GeneralFeedback.provideErrorFeedback("unit.nonsense"       , unit_arg), false);    return -1;}
          else {
               int toReturn = 0;
               
               for (ServerPlayerEntity player : targets) {
                    ServerStatHandler handler = player.getStatHandler();
                    Scoreboard scoreboard = player.getScoreboard();
                    int amount = scoreboard.getOrCreateScore(player, obj).getScore();
                    int adjustedAmount = unit.convertFrom( isInventory? amount*determineStackSize(statSpec):amount);
                    
                    handler.save();
                         int statValue = handler.getStat(statType, statSpec);
                         int statValueNext = Math.max(MINIMUM_STAT_VALUE, statValue-adjustedAmount);
                         player.resetStat(statType.getOrCreateStat(statSpec));
                    handler.save();
                         player.increaseStat( statType.getOrCreateStat(statSpec), statValueNext);
                    handler.save();
                         
                    
                         source.sendFeedback(() -> GeneralFeedback.provideObjectiveUnitReduceFeedback(player, statType, statSpec, statValue, amount, adjustedAmount, obj, unitKey), false);
                    toReturn+=amount;
                    
               }
               return toReturn;
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
     
}
