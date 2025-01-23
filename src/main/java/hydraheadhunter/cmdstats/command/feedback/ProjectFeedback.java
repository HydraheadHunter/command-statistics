package hydraheadhunter.cmdstats.command.feedback;

import com.mojang.brigadier.arguments.StringArgumentType;import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.kinds.IdF;
import net.minecraft.scoreboard.ScoreboardObjective;import net.minecraft.server.command.ServerCommandSource;import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;import net.minecraft.text.MutableText;import net.minecraft.text.Text;
import net.minecraft.util.Formatting;import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Mutable;

import java.io.File;
import java.util.Collection;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static hydraheadhunter.cmdstats.CommandStatistics.EMPTY;import static java.lang.String.valueOf;
import static net.minecraft.text.Text.*;

import static hydraheadhunter.cmdstats.command.feedback.CommonFields.*;
public class ProjectFeedback {
	private static final String LOCAL_KEY = PROJECT;
	
	private static final String BASE_KEY 			= join(FEEDBACK_KEY,LOCAL_KEY);
	private static final String START_KEY			= join(BASE_KEY, START	);
	private static final String PAUSE_KEY			= join(BASE_KEY, PAUSE	);
	private static final String STOP_KEY			= join(BASE_KEY, STOP	);
	private static final String LIST_KEY			= join(BASE_KEY, LIST	);
	private static final String QUERY_KEY			= join(BASE_KEY, QUERY	);
	private static final String STORE_KEY			= join(BASE_KEY, STORE	);
	
	private static final String NO_ONE				= "no_one";
	private static final String PLAYER				= "player";
	private static final String PLAYERS			= "players";
	private static final String NO_PLAYERS			= "no_players";
	private static final String NO_PROJECTS			= "no_projects";
	private static final String PROJECTS			= "projects";
	private static final String NEW				= "new";
	
	
//Feedback for /statistics project ...
	
	public static MutableText provideStartFeedback(boolean isNewProject, String projectName, Collection<ServerPlayerEntity> playersAdded){
		MutableText playerCountText = literal( valueOf(playersAdded.size()))							.formatted(VALUE_FORMAT);
		MutableText playerNameText  = constructPlayerText(playersAdded);							   //.formatted internally.
		MutableText projectNameText = literal(projectName)										.formatted(ON_PROJECT_NAME_FORMAT	);
		
		String Player_Count_Key     = switch (playersAdded.size()) {
			case 0  -> NO_PLAYERS;
			case 1  -> PLAYER;
			default -> PLAYERS;
		};
		
		return isNewProject ? 	translatable ( join(START_KEY, Player_Count_Key, NEW), playerNameText, projectNameText, playerCountText):
							translatable ( join(START_KEY, Player_Count_Key	  ), playerNameText, projectNameText, playerCountText);
		
	}
	
	public static MutableText providePausedFeedback( Collection<ServerPlayerEntity> playersPaused, Collection<File> projectsPaused){
		MutableText projectNameText 		= constructProjectText(projectsPaused);
		MutableText playerNameText		= constructPlayerText(playersPaused);
		MutableText projectCountText		= literal( valueOf(projectsPaused.size()));
		MutableText playerCountText		= literal( valueOf(playersPaused .size()));
		
		String Player_Count_Key     = playersPaused.size() > 1 ? PLAYERS:PLAYER;
		
		return switch(projectsPaused.size()) {
			case 0  -> translatable ( join(PAUSE_KEY,NO_PROJECTS)).formatted(ERROR_FORMAT);
			case 1  -> translatable ( join(PAUSE_KEY,Player_Count_Key		 ), projectNameText, playerNameText, projectCountText, playerCountText);
			default -> translatable ( join(PAUSE_KEY,Player_Count_Key,PROJECTS), projectNameText, playerNameText, projectCountText, playerCountText);
		};
		
	}
	
	public static MutableText provideStopFeedback  ( Collection<ServerPlayerEntity> playersPaused, Collection<File> projectsPaused){
		MutableText projectNameText 		= constructProjectText(projectsPaused).formatted(STOP_PROJECT_NAME_FORMAT);
		MutableText playerNameText		= constructPlayerText(playersPaused);
		MutableText projectCountText		= literal( valueOf(projectsPaused.size()));
		MutableText playerCountText		= literal( valueOf(playersPaused .size()));
		
		String Player_Count_Key     = playersPaused.size() > 1 ? PLAYERS:PLAYER;
		
		return switch(projectsPaused.size()) {
			case 0  -> translatable ( join(STOP_KEY,NO_PROJECTS)).formatted(ERROR_FORMAT);
			case 1  -> translatable ( join(STOP_KEY,Player_Count_Key		 ), projectNameText, playerNameText, projectCountText, playerCountText);
			default -> translatable ( join(STOP_KEY,Player_Count_Key,PROJECTS), projectNameText, playerNameText, projectCountText, playerCountText);
		};
		
	}

	public static MutableText provideListFeedback(ServerPlayerEntity player, Collection<File> projectDirectories, Collection<File> pausedDirectories){
		MutableText playerNameText  	= ((MutableText)player.getName())					.formatted(PLAYER_NAME_FORMAT		);
		MutableText projectCountText	= literal(valueOf(projectDirectories.size()))		.formatted(VALUE_FORMAT			);
		MutableText pausedCountText 	= literal(valueOf(pausedDirectories .size()))		.formatted(VALUE_DIFF_FORMAT		);
		MutableText projectsText		= constructProjectList(projectDirectories)			.formatted(ON_PROJECT_NAME_FORMAT	);
		MutableText pausedText		= constructProjectList(pausedDirectories)			.formatted(OFF_PROJECT_NAME_FORMAT	);
		
		String activeProjectCountKey = switch (projectDirectories.size()){
			case 0 ->  NO_PROJECTS;
			case 1 ->  PROJECT;
			default -> PROJECTS;
		};
		String inactiveProjectCountKey = pausedDirectories.size()<2 ? PROJECT:PROJECTS;
		
		String translation_key =  pausedDirectories.isEmpty() ? 	join(LIST_KEY, activeProjectCountKey						):
														join(LIST_KEY, activeProjectCountKey, inactiveProjectCountKey	);
		
		return translatable(translation_key, playerNameText, projectCountText, pausedCountText, projectsText, pausedText );
	}
	
	public static <T> MutableText provideQueryFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, String projectName, boolean projectActive){
		MutableText projectNameText	= literal(projectName)										.formatted( projectActive? ON_PROJECT_NAME_FORMAT:OFF_PROJECT_NAME_FORMAT);
		MutableText queryText		= QueryFeedback.provideBasicFeedback(player,statType,statSpec,statValue);
		
		return translatable(QUERY_KEY, projectNameText, queryText);
	}
	public static <T> MutableText provideQueryFeedback(String         playerName, StatType<T> statType, T statSpec, int statValue, String projectName, boolean projectActive){
		MutableText projectNameText	= literal(projectName)										.formatted( projectActive? ON_PROJECT_NAME_FORMAT:OFF_PROJECT_NAME_FORMAT);
		MutableText queryText		= QueryFeedback.provideBasicFeedback(playerName,statType,statSpec,statValue);
		
		return translatable(QUERY_KEY, projectNameText, queryText);
	}
	
	
	
	
	public static <T> MutableText provideStoreFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective, String projectName, boolean projectActive){
		MutableText objectiveNameText	= ((MutableText) objective.getDisplayName())							.formatted( OBJECTIVE_FORMAT 										);
		MutableText projectNameText	= literal(projectName)											.formatted( projectActive? ON_PROJECT_NAME_FORMAT:OFF_PROJECT_NAME_FORMAT	);
		MutableText queryText		= QueryFeedback.provideBasicFeedback(player,statType,statSpec,statValue);
		
		return translatable(STORE_KEY, objectiveNameText, projectNameText, queryText);
	}
	public static <T> MutableText provideStoreFeedback(String         playerName, StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective, String projectName, boolean projectActive){
		MutableText objectiveNameText	= ((MutableText) objective.getDisplayName())							.formatted( OBJECTIVE_FORMAT 										);
		MutableText projectNameText	= literal(projectName)											.formatted( projectActive? ON_PROJECT_NAME_FORMAT:OFF_PROJECT_NAME_FORMAT	);
		MutableText queryText		= QueryFeedback.provideBasicFeedback(playerName,statType,statSpec,statValue);
		
		return translatable(STORE_KEY, objectiveNameText, projectNameText, queryText);
	}
	
	public static MutableText provideErrorFeedback(String error_key,	ServerPlayerEntity player, 	String projectName){
		MutableText playerNameText 	= (MutableText) player.getName();
		MutableText projectNameText	= literal(projectName);
		
		return translatable( join(BASE_KEY,ERROR,error_key), playerNameText, projectNameText);
	}
	public static MutableText provideErrorFeedback(String error_key,	String playerName, 	String projectName){
		MutableText playerNameText 	= literal(playerName );
		MutableText projectNameText	= literal(projectName);
		
		return translatable( join(BASE_KEY,ERROR,error_key), playerNameText, projectNameText);
	}
	
	public static MutableText provideErrorFeedback(String error_key,							String projectName){
		MutableText projectNameText	= literal(projectName);
		return translatable( join(BASE_KEY,ERROR,error_key), projectNameText);
	}
	
	
	private static MutableText constructPlayerText(Collection<ServerPlayerEntity> players){
		if (players==null) return literal(EMPTY);
		return switch ( players.size() ) {
			case 0 ->   translatable( join(BASE_KEY, NO_ONE) )							.formatted(ERROR_FORMAT			);
			case 1 ->   ((MutableText) ((ServerPlayerEntity) players.toArray()[0]).getName())	.formatted(PLAYER_NAME_FORMAT		);
			default ->  constructPlayerList(players)									.formatted(PLAYER_NAME_FORMAT		);
		};
	}
	private static MutableText constructPlayerList(Collection<ServerPlayerEntity> players){
		String literalOfToReturn = "";
		for( ServerPlayerEntity player: players){
			literalOfToReturn= join_nl(literalOfToReturn, "  "+player.getName().getString());
		}
		return literal(literalOfToReturn);
	}
	
	private static MutableText constructProjectText( Collection<File> projects){
		if (projects==null) return literal(EMPTY);
		return switch ( projects.size() ) {
			case 0 ->   translatable( join(BASE_KEY, NO_ONE) )							.formatted(ERROR_FORMAT			);
			case 1 ->   literal( ((File) projects.toArray()[0]).getName() )					.formatted(OFF_PROJECT_NAME_FORMAT	);
			default ->  constructProjectList(projects)									.formatted(OFF_PROJECT_NAME_FORMAT	);
		};
	}
	private static MutableText constructProjectList( Collection<File> projects){
		String literalOfToReturn = "";
		for( File project: projects){
			literalOfToReturn= join_nl(literalOfToReturn, "  "+project.getName() );
		}
		return literal(literalOfToReturn);
		
	}
	
	
	
	
	
/*
	
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
		if (collection==null) return literal("");
		String literalOfToReturn = "";
		for( File directory: collection){
				literalOfToReturn= join_nl(literalOfToReturn, "  "+directory.getName());
		}
		return literal(literalOfToReturn);
	}
	
	private static String choosePlurality (int count, String... mood){
		String internalPlurality = switch (count) {
			case 0  -> NIL		;
			case 1  -> SINGLE	;
			case 2  -> DUAL	;
			default -> PLURAL	;
		};
		String langAdjustedPlurality;
		if( mood!= null && mood.length>0){
			langAdjustedPlurality= translatable( join(PLURALITY_KEY,mood[0],internalPlurality)).getString();
		}
		else{
			langAdjustedPlurality= translatable( join(PLURALITY_KEY,     internalPlurality)).getString();
		}
		return langAdjustedPlurality;
	}
*/
}
