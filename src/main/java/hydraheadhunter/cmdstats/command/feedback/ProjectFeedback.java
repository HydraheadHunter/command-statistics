package hydraheadhunter.cmdstats.command.feedback;

import com.mojang.brigadier.arguments.StringArgumentType;import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.File;
import java.util.Collection;

import static hydraheadhunter.cmdstats.CommandStatistics.join_nl;import static hydraheadhunter.cmdstats.command.StatisticsCommand.PROJECT_NAME;import static java.lang.String.valueOf;import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.stringifiedTranslatable;

public class ProjectFeedback {

	public static Text provideStartFeedback(boolean isNewProject, String projectName, Collection<ServerPlayerEntity> playersAdded, int numberPlayersAdded){
		String literalOfToReturn = isNewProject ?
							  stringifiedTranslatable("feedback.project.start.new",  valueOf(numberPlayersAdded), projectName).getString():
							  stringifiedTranslatable("feedback.project.start"    ,  valueOf(numberPlayersAdded), projectName).getString();
		
		for (ServerPlayerEntity player: playersAdded) {
	  		literalOfToReturn= join_nl(literalOfToReturn, player.getName().getString());
        	}
	     return literal(literalOfToReturn);
    	}

    	public static Text providePausedFeedback(boolean isStopAll, Collection<ServerPlayerEntity> playersRemoved, Collection<File> projectsRemoved){
		int numberPlayersRemoved  = playersRemoved.size();
		int numberProjectsRemoved = projectsRemoved.size();
		String literalOfToReturn  = isStopAll ?
							   stringifiedTranslatable("feedback.project.stop.all",  valueOf(numberProjectsRemoved), numberPlayersRemoved).getString():
							   stringifiedTranslatable("feedback.project.stop"    ,  valueOf(numberPlayersRemoved) , ((File)projectsRemoved.toArray()[0]).getName()).getString();
		if(isStopAll){
			for( File projectDirectory: projectsRemoved)
				literalOfToReturn = join_nl(literalOfToReturn,projectDirectory.getName());
		}
		else{
			for( ServerPlayerEntity player: playersRemoved)
				literalOfToReturn = join_nl(literalOfToReturn,player.getName().getString());
		}
  		return literal(literalOfToReturn);
    	}

    	public static Text provideListFeedback(ServerPlayerEntity player, Collection<File> projectDirectories, Collection<File> pausedProjectDirectories){
   		String literalOfToReturn="";
		Text playerName = player.getName();
		if( ! projectDirectories.isEmpty()){
			literalOfToReturn= stringifiedTranslatable("feedback.project.list", playerName, valueOf(projectDirectories.size())).getString();
			for (File directory : projectDirectories) {
				literalOfToReturn= join_nl(literalOfToReturn, " "+directory.getName());
			}
			if ( ! pausedProjectDirectories.isEmpty()){
				literalOfToReturn= join_nl(literalOfToReturn, (stringifiedTranslatable("feedback.project.list.paused.also", playerName, valueOf(pausedProjectDirectories.size())).getString()));
				for (File directory : pausedProjectDirectories) {
					literalOfToReturn= join_nl(literalOfToReturn, " "+directory.getName());
				}
			}
		}
		else if ( ! pausedProjectDirectories.isEmpty()){
			literalOfToReturn= join_nl(literalOfToReturn, (stringifiedTranslatable("feedback.project.list.paused", playerName, valueOf(pausedProjectDirectories.size())).getString()));
			for (File directory : pausedProjectDirectories) {
				literalOfToReturn= join_nl(literalOfToReturn, " "+directory.getName());
			}
		}
		else {
			literalOfToReturn = stringifiedTranslatable("feedback.project.list.none", playerName).getString();
		}
   		return literal(literalOfToReturn);
    	}
	    
	public static Text provideErrorFeedback( CommandContext<ServerCommandSource> context, String errorName){
		Text toReturn;
		switch (errorName){
			case  "Project Name is Reserved":
				String projectName = StringArgumentType.getString(context,PROJECT_NAME);
				toReturn = stringifiedTranslatable("feedback.project.error.name_reserved", projectName);
				break;
			default : toReturn = literal("An undefined error has occurred");
		}
		return toReturn;
	}
	
	
}
