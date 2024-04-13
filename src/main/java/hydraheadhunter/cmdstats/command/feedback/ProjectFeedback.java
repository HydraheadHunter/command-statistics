package hydraheadhunter.cmdstats.command.feedback;

import com.mojang.brigadier.arguments.StringArgumentType;import com.mojang.brigadier.context.CommandContext;
import net.minecraft.scoreboard.ScoreboardObjective;import net.minecraft.server.command.ServerCommandSource;import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;import net.minecraft.text.MutableText;import net.minecraft.text.Text;
import net.minecraft.util.Formatting;import net.minecraft.util.Nameable;

import java.io.File;
import java.util.Collection;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static hydraheadhunter.cmdstats.CommandStatistics.EMPTY;import static java.lang.String.valueOf;
import static net.minecraft.text.Text.*;

public class ProjectFeedback {
	private static final String LOCAL_KEY = PROJECT;
	private static final String FORMAT_BASE_KEY= join(FEEDBACK_KEY,FORMAT,LOCAL_KEY);
	private static final String LIST_KEY 			= join(FORMAT_BASE_KEY,LIST);
	private static final String LIST_PAUSE_KEY		= join(LIST_KEY,PAUSE);
	private static final String LIST_PAUSE_ALSO_KEY 	= join(LIST_PAUSE_KEY,"also");
	private static final String LIST_NIL_KEY 		= join(LIST_KEY,NIL);
	private static final String START_KEY 			= join(FORMAT_BASE_KEY,START);
	private static final String START_NIL_KEY 		= join(START_KEY,NIL);
	private static final String START_NEW_KEY 		= join(START_KEY,"new");
	private static final String PAUSE_KEY 			= join(FORMAT_BASE_KEY,PAUSE);
	private static final String PAUSE_ALL_KEY		= join(PAUSE_KEY,ALL);
	private static final String STOP_KEY 			= join(FORMAT_BASE_KEY,STOP);
	private static final String STOP_ALL_KEY		= join(STOP_KEY,ALL);
	private static final String QUERY_KEY 			= join(FORMAT_BASE_KEY,QUERY);
	private static final String STORE_KEY			= join(FORMAT_BASE_KEY,STORE);
	
	private static final String NAME_RESERVED_ERROR_KEY  = join (ERROR_KEY,LOCAL_KEY,"reserved" );
	private static final String NAME_NOT_FOUND_ERROR_KEY = join (ERROR_KEY,LOCAL_KEY,"not_found");
	private static final String IMPOSSIBLE_SWITCH = "THIS RETURN VALUE SHOULD BE IMPOSSIBLE TO REACH!? HOW DID YOU MANAGE THAT?";

//Feedback for /statistics project ...

// /statistics project list @p
	public static MutableText provideListFeedback(ServerPlayerEntity player, Collection<File> projectDirectories, Collection<File> pausedDirectories){
       
     		MutableText playerName 					= ((MutableText) player.getName())							.formatted(Formatting.RED)	;
     		MutableText projectDirectoriesList  		= constructProjectList(projectDirectories)			.formatted(Formatting.RED)	;
     		MutableText pausedProjectDirectoriesList	= constructProjectList(pausedDirectories)			.formatted(Formatting.RED)	;
     		MutableText projectDirectoriesSize			= (MutableText) literal( valueOf(projectDirectories.size()))	.formatted(Formatting.RED)	;
     		MutableText pausedDirectoriesSize			= (MutableText) literal( valueOf(pausedDirectories.size()))		.formatted(Formatting.RED)	;
     		
     		int switchvalue= (projectDirectories.isEmpty()?0:1) + (pausedDirectories.isEmpty()?0:2);
     		return switch (switchvalue) {
     			case 0  -> 	translatable(		LIST_NIL_KEY,			playerName																);
     			case 1  -> 	translatable(		LIST_KEY,				playerName, projectDirectoriesSize, 	projectDirectoriesList		, literal(EMPTY)	);
     			case 2  -> 	translatable(		LIST_PAUSE_KEY,		playerName, pausedDirectoriesSize,		pausedProjectDirectoriesList					);
     			case 3  -> 	translatable(		LIST_KEY,				playerName, projectDirectoriesSize,	projectDirectoriesList		,
     							translatable(	LIST_PAUSE_ALSO_KEY,	playerName, pausedDirectoriesSize,		pausedProjectDirectoriesList	)				);
     			default -> (MutableText)literal(IMPOSSIBLE_SWITCH).formatted(Formatting.RED);
     		
     		};
     	}
// /statistics project start @p [projectname]
	public static MutableText provideStartFeedback(boolean isNewProject, String projectName, Collection<ServerPlayerEntity> playersAdded){
		MutableText toReturn;
		MutableText playerCountText	= literal( valueOf(playersAdded.size()));
		MutableText projectNameText 	= literal(projectName);
		MutableText playersAddedList	= constructPlayerList(playersAdded);
		String plurality = choosePlurality( playersAdded.size());
		
		int switchValue= (isNewProject?0:1) + (playersAdded.size()<1?0:2);
		return switch (switchValue) {
			case 0  ->	translatable(		START_NIL_KEY, 			projectNameText									).formatted(Formatting.RED)	;
			case 1  ->	translatable( 		START_NIL_KEY, 			projectNameText									).formatted(Formatting.RED)	;
			case 2  ->	translatable( join(	START_KEY		,plurality),	playerCountText, 	projectNameText,	playersAddedList	)						;
			case 3  ->	translatable( join(	START_NEW_KEY	,plurality),	playerCountText, 	projectNameText,	playersAddedList	)						;
			default ->	(MutableText) literal(IMPOSSIBLE_SWITCH).formatted(Formatting.RED);
		};
	}
	
    	public static MutableText providePausedFeedback(boolean isPauseAll, Collection<ServerPlayerEntity> playersPaused, Collection<File> projectsPaused, String targetProjectName){
		MutableText projectsRemovedList	= constructProjectList(projectsPaused)					.formatted(Formatting.GOLD)	;
		MutableText playersRemovedList 	= constructPlayerList(playersPaused)					.formatted(Formatting.GOLD)	;
		MutableText projectsRemovedCount	= (MutableText) literal( valueOf(projectsPaused.size() ))	.formatted(Formatting.GOLD)	;
		MutableText playerRemovedCount	= (MutableText) literal( valueOf(playersPaused.size()  ))	.formatted(Formatting.GOLD)	;
		String projectPlurality			= choosePlurality(projectsPaused.size())				.formatted(Formatting.GOLD)	;
		String playerPlurality			= choosePlurality(playersPaused.size())					.formatted(Formatting.GOLD)	;
		MutableText targetProjectNameText	= literal(targetProjectName);
		
		int switchValue = (isPauseAll?0:1) + (projectsPaused.size()<1?0:2);
		return switch (switchValue) {
			case 0 -> translatable( join(PAUSE_ALL_KEY,NIL)																												).formatted(Formatting.RED)	;
			case 1 -> translatable( join(PAUSE_KEY,NIL)	,																							targetProjectNameText	).formatted(Formatting.RED)	;
			case 2 -> translatable( join(PAUSE_ALL_KEY	,	playerPlurality,	projectPlurality	),	playerRemovedCount,	projectsRemovedCount,	playersRemovedList,	projectsRemovedList		)						;
			case 3 -> translatable( join(PAUSE_KEY		,	playerPlurality					),	playerRemovedCount,	projectsRemovedCount,	playersRemovedList,	projectsRemovedList		)						;
			default -> literal(IMPOSSIBLE_SWITCH).formatted(Formatting.RED);
		};
		
	}

	
	public static MutableText provideStoppedFeedback(boolean isPauseAll, Collection<ServerPlayerEntity> playersPaused, Collection<File> projectsPaused, String targetProjectName){
		MutableText projectsRemovedList	= constructProjectList(projectsPaused)					.formatted(Formatting.GOLD)	;
		MutableText playersRemovedList 	= constructPlayerList(playersPaused)					.formatted(Formatting.GOLD)	;
		MutableText projectsRemovedCount	= (MutableText) literal( valueOf(projectsPaused.size() ))	.formatted(Formatting.GOLD)	;
		MutableText playerRemovedCount	= (MutableText) literal( valueOf(playersPaused.size()  ))	.formatted(Formatting.GOLD)	;
		String projectPlurality			= choosePlurality(projectsPaused.size())				.formatted(Formatting.GOLD)	;
		String playerPlurality			= choosePlurality(playersPaused.size())					.formatted(Formatting.GOLD)	;
		MutableText targetProjectNameText	= literal(targetProjectName);
		
		int switchValue = (isPauseAll?0:1) + (projectsPaused.size()<1?0:2);
		return switch (switchValue) {
			case 0 -> translatable( join(PAUSE_ALL_KEY,NIL)																												).formatted(Formatting.RED)	;
			case 1 -> translatable( join(PAUSE_KEY,NIL)	,																							targetProjectNameText	).formatted(Formatting.RED)	;
			case 2 -> translatable( join(PAUSE_ALL_KEY	,	playerPlurality,	projectPlurality	),	playerRemovedCount,	projectsRemovedCount,	playersRemovedList,	projectsRemovedList		)						;
			case 3 -> translatable( join(PAUSE_KEY		,	playerPlurality					),	playerRemovedCount,	projectsRemovedCount,	playersRemovedList,	projectsRemovedList		)						;
			default -> literal(IMPOSSIBLE_SWITCH).formatted(Formatting.RED);
		};
		
	}
	
	public static<T> MutableText provideQueryFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, String projectName) {
		MutableText projectNameText = literal(projectName)           .formatted(Formatting.GOLD);
		MutableText queryText = QueryFeedback.provideFeedback(player,statType,statSpec,statValue);
		return stringifiedTranslatable(QUERY_KEY, projectNameText, queryText);
		
	}
 
	public static<T> MutableText provideStoreFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective, String projectName) {
		MutableText projectNameText = literal(projectName)           .formatted(Formatting.GOLD);
		MutableText objectiveText = (MutableText)objective.getDisplayName();
		MutableText queryText = QueryFeedback.provideFeedback(player,statType,statSpec,statValue);
		return stringifiedTranslatable(STORE_KEY, projectNameText, objectiveText, queryText);
	}
	
	public static MutableText provideErrorFeedback( CommandContext<ServerCommandSource> context, String errorName, Object... args){
		MutableText toReturn;
		String projectName;
		if      (errorName.equals(NAME_RESERVED_ERROR_KEY)){
			projectName = StringArgumentType.getString(context,PROJECT_NAME);
			toReturn = translatable(NAME_RESERVED_ERROR_KEY, projectName);
		}
		else if (errorName.equals(NAME_NOT_FOUND_ERROR_KEY)){
			projectName = StringArgumentType.getString(context,PROJECT_NAME);
			Text playerName = (args!=null && args.length>1 && args[0]!=null) ? ((Nameable) args[0]).getName():literal("player undefined");
			toReturn = translatable(NAME_NOT_FOUND_ERROR_KEY,playerName,projectName);
		}
		else {
			toReturn = translatable(errorName);
		}
		
		return toReturn.formatted(Formatting.RED);
	}
	
	
	private static MutableText constructProjectList(Collection<File> collection){
		if (collection==null) return (MutableText) literal("");
		String literalOfToReturn = "";
		for( File directory: collection){
				literalOfToReturn= join_nl(literalOfToReturn, "  "+directory.getName());
		}
		return (MutableText) literal(literalOfToReturn);
	}
	private static MutableText constructPlayerList(Collection<ServerPlayerEntity> collection){
		if (collection==null) return (MutableText) literal("");
		String literalOfToReturn = "";
		for( ServerPlayerEntity player: collection){
				literalOfToReturn= join_nl(literalOfToReturn, "  "+player.getName().getString());
		}
		return (MutableText) literal(literalOfToReturn);
	}
	
	private static String choosePlurality (int count){
		String internalPlurality = switch (count) {
			case 0  -> NIL		;
			case 1  -> SINGLE	;
			case 2  -> DUAL	;
			default -> PLURAL	;
		};
		String langAdjustedPlurality= translatable( join(PLURALITY_KEY,internalPlurality)).getString();
		return langAdjustedPlurality;
	}
}
