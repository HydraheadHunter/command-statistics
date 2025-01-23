package hydraheadhunter.cmdstats.command.feedback;

import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatType;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static hydraheadhunter.cmdstats.CommandStatistics.join;
import static hydraheadhunter.cmdstats.CommandStatistics.*;
import static java.lang.String.valueOf;
import static net.minecraft.text.Text.*;
import static hydraheadhunter.cmdstats.command.feedback.CommonFields.*;
public class GeneralFeedback {
	
	private static final String FEEDBACK_KEY  	= join(MOD_ID      , "feedback" );
	private static final String BASIC_KEY     	=                    "basic"     ;
	private static final String OBJECTIVE_KEY 	=                    "objective" ;
	private static final String ZERO_KEY      	=                    "zero"      ;
	private static final String STORE_KEY     	= join(FEEDBACK_KEY, "store"    );
	private static final String ADD_KEY       	= join(FEEDBACK_KEY, "add"      );
	private static final String SET_KEY       	= join(FEEDBACK_KEY, "set"      );
	private static final String REDUCE_KEY    	= join(FEEDBACK_KEY, "reduce"   );
	private static final String REDUCE_NEG_KEY	= join(REDUCE_KEY  , "neg"      );
	
	public static 	   MutableText provideErrorFeedback(String errorKey, String ... additionalInfo){
		return switch (errorKey) {
			case "unit.unrecognized", "unit.nonsense" -> {
				assert additionalInfo != null;
				yield stringifiedTranslatable(join(FEEDBACK_KEY, "error", errorKey), Arrays.stream(additionalInfo).toArray());
			}
			default -> null;
		};
		
		
	}
	
	public static <T> MutableText provideStoreFeedback( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective){
		MutableText objectiveText = ((MutableText) objective.getDisplayName())                             .formatted(OBJECTIVE_FORMAT);
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, statValue)                             ;
		
		return translatable(STORE_KEY, objectiveText, queryText);
	}

	public static <T> MutableText provideStoreFeedback( String playerName        , StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective){
		MutableText objectiveText = ((MutableText) objective.getDisplayName())                                  .formatted(OBJECTIVE_FORMAT);
		MutableText queryText = QueryFeedback.provideBasicFeedback( playerName, statType, statSpec, statValue )                             ;
		
		return translatable(STORE_KEY, objectiveText, queryText);
	}
	
	public static <T> MutableText provideUnitStoreFeedback( ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective, String unitKey){
		MutableText objectiveText = ((MutableText) objective.getDisplayName())                                          .formatted(OBJECTIVE_FORMAT);
		MutableText queryText = QueryFeedback.provideBasicUnitFeedback( player, statType, statSpec, statValue, unitKey)                             ;
	
		return translatable(STORE_KEY, objectiveText, queryText);
	}
	public static <T> MutableText provideUnitStoreFeedback( String playerName        , StatType<T> statType, T statSpec, int statValue, ScoreboardObjective objective, String unitKey){
		MutableText objectiveText = ((MutableText) objective.getDisplayName())                                              .formatted(OBJECTIVE_FORMAT);
		MutableText queryText = QueryFeedback.provideBasicUnitFeedback( playerName, statType, statSpec, statValue, unitKey)                             ;
		
		return translatable(STORE_KEY, objectiveText, queryText);
	}
	
	public static <T> MutableText provideBasicAddFeedback        (ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int valueDiff){
		MutableText valueDiffText= literal( valueOf(valueDiff))                                                      .formatted(VALUE_DIFF_FORMAT) ;
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, statValue+valueDiff)                               ;
		
		return translatable( join(ADD_KEY,BASIC_KEY), valueDiffText,queryText );
	}
	public static <T> MutableText provideUnitAddFeedback         (ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int unitDiff, int valueDiff, String unitKey){
		MutableText unitDiffText		= literal( valueOf(unitDiff))												.formatted(VALUE_DIFF_FORMAT) ;
		MutableText unitText		= translatable( unitKey );
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, statValue+valueDiff)                               ;
		
		return translatable( join(ADD_KEY,BASIC_KEY,UNIT), unitDiffText,unitText, queryText );
	}
	public static <T> MutableText provideObjectiveAddFeedback    (ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int valueDiff, ScoreboardObjective objective){
		MutableText valueDiffText=                literal( valueOf(valueDiff))                                       .formatted(VALUE_DIFF_FORMAT) ;
		MutableText objectiveText= ((MutableText) objective.getDisplayName())                                        .formatted(OBJECTIVE_FORMAT ) ;
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, statValue+valueDiff)                               ;
		
		return translatable( join(ADD_KEY,OBJECTIVE_KEY), valueDiffText, objectiveText, queryText );
	}
	public static <T> MutableText provideObjectiveUnitAddFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int unitDiff, int valueDiff, ScoreboardObjective objective, String unitKey){
		MutableText unitDiffText		= literal( valueOf(unitDiff))												.formatted(VALUE_DIFF_FORMAT) ;
		MutableText unitText		= translatable( unitKey );
		MutableText objectiveText= ((MutableText) objective.getDisplayName())                                        .formatted(OBJECTIVE_FORMAT ) ;
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, statValue+valueDiff)                               ;
		
		return translatable( join(ADD_KEY,OBJECTIVE_KEY,UNIT), unitDiffText, unitText, objectiveText, queryText );
	}
	
	public static <T> MutableText provideBasicSetFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int newValue){
		MutableText oldValueText= literal( valueOf(statValue))                                                      .formatted(VALUE_DIFF_FORMAT) ;
		MutableText newValueText= literal( valueOf(newValue))                                                       .formatted(VALUE_FORMAT     ) ;
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, newValue)                               ;
		
		return translatable( join(SET_KEY,BASIC_KEY), queryText, oldValueText, newValueText );
	}
	//TODO FIX THIS FEEDBACK;
	public static <T> MutableText provideUnitSetFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int unitValue, int newValue, String unit){
		MutableText oldValueText = literal( valueOf(statValue))                                                    .formatted(VALUE_DIFF_FORMAT) ;
		MutableText newValueText= literal( valueOf(newValue))                                                      .formatted(VALUE_FORMAT     ) ;
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, newValue)                               ;
		
		return translatable( join(SET_KEY,BASIC_KEY,UNIT), queryText, oldValueText, newValueText );
	}
	public static <T> MutableText provideObjectiveSetFeedback    (ServerPlayerEntity player, StatType<T> statType, T statSpec, int oldValue, int newValue, ScoreboardObjective objective){
		MutableText oldValueText=                literal( valueOf(oldValue))                                         .formatted(VALUE_DIFF_FORMAT) ;
		MutableText newValueText=                literal( valueOf(newValue))                                         .formatted(VALUE_FORMAT     ) ;
		MutableText objectiveText= ((MutableText) objective.getDisplayName())                                        .formatted(OBJECTIVE_FORMAT ) ;
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, newValue)                                          ;
		
		return translatable( join(SET_KEY,OBJECTIVE_KEY), objectiveText, queryText, oldValueText, newValueText );
	}
	public static <T> MutableText provideObjectiveUnitSetFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int oldValue, int unitValue, int newValue, ScoreboardObjective objective, String unitKey){
		MutableText oldValueText=                literal( valueOf(oldValue))                                         .formatted(VALUE_DIFF_FORMAT) ;
		MutableText newValueText=                literal( valueOf(newValue))                                         .formatted(VALUE_FORMAT     ) ;
		MutableText objectiveText= ((MutableText) objective.getDisplayName())                                        .formatted(OBJECTIVE_FORMAT ) ;
		MutableText queryText = QueryFeedback.provideBasicFeedback( player, statType, statSpec, newValue)                                          ;
		
		return translatable( join(SET_KEY,OBJECTIVE_KEY,UNIT), objectiveText, queryText, oldValueText, newValueText );
	}
	
	public static <T> MutableText provideBasicReduceFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int valueDiff){
		MutableText negResultText = translatable(REDUCE_NEG_KEY)                                                          .formatted(ERROR_FORMAT      );
		MutableText valueDiffText = literal(valueOf(valueDiff))                                                           .formatted(VALUE_DIFF_FORMAT );
		MutableText oldValueText = literal(valueOf(statValue))                                                            .formatted(VALUE_DIFF_FORMAT );
		MutableText queryText     = QueryFeedback.provideBasicFeedback(player, statType, statSpec, statValue - valueDiff);
		MutableText queryZeroText = QueryFeedback.provideBasicFeedback(player, statType, statSpec, 0);
		
		
		return statValue>=valueDiff ? translatable( join(REDUCE_KEY, BASIC_KEY          ),                valueDiffText, queryText                      ) :
		                              translatable( join(REDUCE_KEY, BASIC_KEY, ZERO_KEY), negResultText,                queryZeroText, oldValueText    ) ;
		
	}
	public static <T> MutableText provideUnitReduceFeedback (ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int unitDiff, int valueDiff, String unitKey){
		MutableText negResultText = translatable(REDUCE_NEG_KEY)                                                          .formatted(ERROR_FORMAT      );
		MutableText unitDiffText = literal(valueOf(unitDiff))                                                             .formatted(VALUE_DIFF_FORMAT );
		MutableText oldValueText = literal(valueOf(statValue))                                                            .formatted(VALUE_DIFF_FORMAT );
		MutableText unitText	= translatable(unitKey);
		MutableText queryText     = QueryFeedback.provideBasicFeedback(player, statType, statSpec, statValue - valueDiff);
		MutableText queryZeroText = QueryFeedback.provideBasicFeedback(player, statType, statSpec, 0);
		
		return statValue>=valueDiff ? translatable( join(REDUCE_KEY, BASIC_KEY, UNIT    ),                unitDiffText, unitText,  queryText                      ) :
		                              translatable( join(REDUCE_KEY, BASIC_KEY, ZERO_KEY), negResultText,                          queryZeroText, oldValueText    ) ;
		
	}
	public static <T> MutableText provideObjectiveReduceFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int valueDiff, ScoreboardObjective objective){
		MutableText negResultText = translatable(REDUCE_NEG_KEY)                                                          .formatted(ERROR_FORMAT      );
		MutableText objectiveNameText= ((MutableText) objective.getDisplayName())                                         .formatted(OBJECTIVE_FORMAT  );
		MutableText valueDiffText = literal(valueOf(valueDiff))                                                           .formatted(VALUE_DIFF_FORMAT );
		MutableText oldValueText = literal(valueOf(statValue))                                                            .formatted(VALUE_DIFF_FORMAT );
		MutableText queryText     = QueryFeedback.provideBasicFeedback(player, statType, statSpec, statValue - valueDiff);
		MutableText queryZeroText = QueryFeedback.provideBasicFeedback(player, statType, statSpec, 0);
		
		return statValue>=valueDiff ? translatable( join(REDUCE_KEY, OBJECTIVE_KEY          ),                 valueDiffText, objectiveNameText, queryText                      ) :
		                              translatable( join(REDUCE_KEY, BASIC_KEY    , ZERO_KEY), negResultText,                                    queryZeroText, oldValueText    ) ;
		
	}
	public static <T> MutableText provideObjectiveUnitReduceFeedback(ServerPlayerEntity player, StatType<T> statType, T statSpec, int statValue, int unitDiff, int valueDiff, ScoreboardObjective objective, String unitKey){
		MutableText negResultText = translatable(REDUCE_NEG_KEY)                                                          .formatted(ERROR_FORMAT      );
		MutableText objectiveNameText= ((MutableText) objective.getDisplayName())                                         .formatted(OBJECTIVE_FORMAT  );
		MutableText unitDiffText = literal(valueOf(unitDiff))                                                             .formatted(VALUE_DIFF_FORMAT );
		MutableText oldValueText = literal(valueOf(statValue))                                                            .formatted(VALUE_DIFF_FORMAT );
		MutableText unitText	= translatable(unitKey);
		MutableText queryText     = QueryFeedback.provideBasicFeedback(player, statType, statSpec, statValue - valueDiff);
		MutableText queryZeroText = QueryFeedback.provideBasicFeedback(player, statType, statSpec, 0);
		
		return statValue>=valueDiff ? translatable( join(REDUCE_KEY, OBJECTIVE_KEY, UNIT    ),                 unitDiffText, unitText, objectiveNameText, queryText                      ) :
		                              translatable( join(REDUCE_KEY, BASIC_KEY    , ZERO_KEY), negResultText,                                              queryZeroText, oldValueText    ) ;
		
	}

	
}
