package hydraheadhunter.cmdstats.command.feedback.lang;

import hydraheadhunter.cmdstats.CommandStatistics;
import hydraheadhunter.cmdstats.util.ModTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;

import static hydraheadhunter.cmdstats.CommandStatistics.join;
import static hydraheadhunter.cmdstats.CommandStatistics.customStatIsIn;
public class FormatCustom {
	private static final String MINECRAFT          = CommandStatistics.MINECRAFT;
	private static final String EMPTY              = CommandStatistics.EMPTY;
	private static final String BASE_KEY           = join(CommandStatistics.FEEDBACK_KEY, CommandStatistics.QUERY) ;
	private static final String SYSTEM_KEY         = join(CommandStatistics.SYSTEM_KEY  , CommandStatistics.QUERY) ;
	private static final String FORMAT			  = CommandStatistics.FORMAT		 ;
	private static final String PLURALITY_BASE_KEY = CommandStatistics.PLURALITY_KEY ;
	private static final String JOIN_BASE_KEY      = CommandStatistics.JOIN_KEY      ;
	private static final String TWO_WORDS          = JOIN_BASE_KEY    + ".2"         ;
	private static final String THREE_WORDS        = JOIN_BASE_KEY    + ".3"         ;
	private static final String FOUR_WORDS         = JOIN_BASE_KEY    + ".4"         ;
	private static final String FIVE_WORDS         = JOIN_BASE_KEY    + ".5"         ;
	private static final String SIX_WORDS          = JOIN_BASE_KEY    + ".6"         ;
	private static final String JOIN_A             = TWO_WORDS        + ".a"         ;
	private static final String JOIN_OF            = TWO_WORDS        + ".of"        ;
	private static final String JOIN_AND           = TWO_WORDS        + ".and"       ;
	private static final String JOIN_PLUS          = TWO_WORDS        + ".plus"      ;
	private static final String JOIN_PARE          = TWO_WORDS + ".parenthesis"      ;
	
	private static final String DEFAULT    = "custom"  ;
	private static int[] A_LOT_int = {100, 20000, 2000000, 500000, 50000, 100 };
	
	private static final String TIME       		 = CommandStatistics.TIME      ;	private static final String REAL_TIME  		 = CommandStatistics.REAL_TIME ;
	private static final String DISTANCE  		 = CommandStatistics.DISTANCE  ;
	private static final String DAMAGE  		 = CommandStatistics.DAMAGE	 ;
	private static final String CAKE   		 = CommandStatistics.CAKE	 ;
	
	private static final String UNIT 			 = CommandStatistics.UNIT	 ;
	private static final String LESS_THAN		 = CommandStatistics.LESS_THAN ;	private static final String SPENT			 = CommandStatistics.SPENT	 ;
	private static final String SECOND			 = CommandStatistics.SECOND	 ;	private static final String MINUTE			 = CommandStatistics.MINUTE	 ;
	private static final String HOUR			 = CommandStatistics.HOUR	 ;	private static final String DAY			 = CommandStatistics.DAY		 ;
	private static final String MONTH 			 = CommandStatistics.MONTH	 ;	private static final String WEEK			 = CommandStatistics.WEEK	 ;
	private static final String YEAR			 = CommandStatistics.YEAR	 ;
	
	private static final String CENTIMETER		 = CommandStatistics.CENTIMETER;	private static final String METER			 = CommandStatistics.METER	 ;
	private static final String KILOMETER		 = CommandStatistics.KILOMETER ;
	private static final String INCH			 = CommandStatistics.INCH	 ;	private static final String FOOT			 = CommandStatistics.FOOT	 ;
	private static final String YARD			 = CommandStatistics.YARD	 ;	private static final String MILE			 = CommandStatistics.MILE	 ;
	
	private static final String HALF_HEART		 = CommandStatistics.HALF_HEART;	private static final String HEART			 = CommandStatistics.HEART	 ;
	private static final String SLICE			 = CommandStatistics.SLICE	 ;
	
	private static final String SYSTEM_TIME_FORMAT_KEY = join( SYSTEM_KEY, TIME, FORMAT );
	private static final String SECOND_KEY        = join( BASE_KEY  , TIME, UNIT, SECOND);	private static final String LESSTHANSECOND_KEY= join( SECOND_KEY, LESS_THAN 	    );
	private static final String MINUTE_KEY		 = join( BASE_KEY  , TIME, UNIT, MINUTE);	private static final String HOUR_KEY 		 = join( BASE_KEY  , TIME, UNIT, HOUR  );
	private static final String DAY_KEY 		 = join( BASE_KEY  , TIME, UNIT, DAY   );	private static final String MINECRAFT_DAY_KEY = join( DAY_KEY   , MINECRAFT         );
	private static final String WEEK_KEY          = join( BASE_KEY  , TIME, UNIT, WEEK  );	private static final String MONTH_KEY         = join( BASE_KEY  , TIME, UNIT, MONTH );
	private static final String YEAR_KEY          = join( BASE_KEY  , TIME, UNIT, YEAR  );
	
	private static final String SYSTEM_DISTANCE_FORMAT_KEY = join(SYSTEM_KEY, DISTANCE, FORMAT  	);
	private static final String CM_KEY			 = join( BASE_KEY  , DISTANCE, UNIT, CENTIMETER	);
	private static final String M__KEY			 = join( BASE_KEY  , DISTANCE, UNIT, METER    	);
	private static final String KM_KEY			 = join( BASE_KEY  , DISTANCE, UNIT, KILOMETER 	);
	private static final String IN_KEY            = join( BASE_KEY  , DISTANCE, UNIT, INCH		);
	private static final String FT_KEY            = join( BASE_KEY  , DISTANCE, UNIT, FOOT		);
	private static final String MI_KEY            = join( BASE_KEY  , DISTANCE, UNIT, MILE		);
	
	private static final String SYSTEM_DAMAGE_FORMAT_KEY = join( SYSTEM_KEY, DAMAGE, FORMAT  		);
	private static final String DAMAGE_KEY        = join( BASE_KEY  , DAMAGE  , UNIT, DAMAGE		);
	private static final String HALF_HEART_KEY    = join( BASE_KEY  , DAMAGE  , UNIT, HALF_HEART	);
	private static final String HEART_KEY         = join( BASE_KEY  , DAMAGE  , UNIT, HEART		);
	
	private static final String SYSTEM_CAKE_FORMAT_KEY = join( SYSTEM_KEY, CAKE, FORMAT		  	);
	private static final String UNIT_CAKE         = join( BASE_KEY  , CAKE    , UNIT, CAKE		);
	private static final String UNIT_SLICE        = join( BASE_KEY  , CAKE    , UNIT, SLICE		);
	
//time conversions factors
	private static final int TICKS_per_SECOND 	= CommandStatistics.TICKS_per_SECOND	 ;	private static final int SECONDS_per_MINUTE 	= CommandStatistics.SECONDS_per_MINUTE	;
	private static final int MINUTES_per_MC_DAY 	= CommandStatistics.MINUTES_per_MC_DAY   ;	private static final int MINUTES_per_HOUR 	= CommandStatistics.MINUTES_per_HOUR	;
	private static final int HOURS_per_DAY		= CommandStatistics.HOURS_per_DAY		 ;	private static final int DAYS_per_WEEK 		= CommandStatistics.DAYS_per_WEEK 		;
	private static final int WEEKS_per_MONTH 	= CommandStatistics.WEEKS_per_MONTH 	 ;	private static final int MONTHS_per_YEAR 	= CommandStatistics.MONTH_per_YEAR		;
//distance conversion factors
	private static final int CENTIMETERSperMETER = CommandStatistics.CM_per_METER;	private static final int METERSperKILOMETER  = CommandStatistics.METER_per_KM;
	private static final int INCHESperMETER      = CommandStatistics.INCHES_per_METER     ;   private static final int INCHESperFOOT       = CommandStatistics.INCH_per_FOOT       ;
	private static final int FEETperMILE         = CommandStatistics.FEET_per_MILE	      ;
	private static final int POINTSperHEART      = CommandStatistics.POINTS_per_HEART     ;
	private static final int SLICESperCAKE       = CommandStatistics.SLICE_per_CAKE       ;
	
	private static final String NEGATIVE		= CommandStatistics.NEGATIVE	;
	private static final String NULL_P			= CommandStatistics.NIL;
	private static final String SINGLE			= CommandStatistics.SINGLE  	;
	private static final String DUAL 			= CommandStatistics.DUAL     	;
	private static final String PLURAL			= CommandStatistics.PLURAL   	;
	private static final String A_LOT			= CommandStatistics.A_LOT     ;
	
	public static String provideFormat ( Identifier statSpecific, int statValue){
		String customStatType = chooseCustomStatType(statSpecific);
		String plurality      = choosePlurality(customStatType,statValue, true);
		String verbKey        = join(BASE_KEY, customStatType, statSpecific.getPath());
		String idName         = statSpecific.getNamespace().equals(MINECRAFT) ? statSpecific.getPath(): join(statSpecific.getNamespace(), statSpecific.getPath()) ;
		String specialFormattingCase = chooseSpecialFormatCase(statSpecific,plurality);
		
		return join( BASE_KEY , customStatType , FORMAT , specialFormattingCase );
	}
	private static String chooseSpecialFormatCase(Identifier statSpecific, String plurality){
		if (customStatIsIn(statSpecific, ModTags.Identifiers.IS_TIME_SPENT)) return join( SPENT, ( plurality.equals(NULL_P) ? NULL_P :EMPTY));
		return EMPTY;
	}
//////////////////////////////////////////////////////////////////////////////////////
//
//                VERBS
//
//////////////////////////////////////////////////////////////////////////////////////
	
	public static String provideVerb (Identifier statSpecific, int statValue){
		String customStatType = chooseCustomStatType( statSpecific);
		String statPath = sanitizePath(customStatType, statSpecific.getPath());
		
		String verbPlurality = chooseVerbPlurality( statSpecific, statValue);
		
		return join( BASE_KEY, customStatType, statPath, verbPlurality);
	}
	
	private static String sanitizePath ( String customStatType, String statPath){
		return customStatType.equals(DAMAGE) ? statPath.substring(DAMAGE.length()):statPath;
	}
	
	private static String chooseVerbPlurality( Identifier statSpecific, int statValue){
		if (customStatIsIn(statSpecific, ModTags.Identifiers.HAS_NULL  ) && statValue == 0) return NULL_P;
		if (customStatIsIn(statSpecific, ModTags.Identifiers.HAS_SINGLE) && statValue == 1) return SINGLE;
		if (customStatIsIn(statSpecific, ModTags.Identifiers.HAS_DUAL  ) && statValue == 2) return DUAL;
		if (customStatIsIn(statSpecific, ModTags.Identifiers.HAS_PLURAL) && statValue >  2) return PLURAL;
		if (customStatIsIn(statSpecific, ModTags.Identifiers.HAS_ALOT  ) && statValue > A_LOT_int[0]) return join(PLURAL , A_LOT);
		return EMPTY;
	}


	
//////////////////////////////////////////////////////////////////////////////////////
//
//                NUMBER FORMAT
//
//////////////////////////////////////////////////////////////////////////////////////
	
	public static MutableText provideNumberFormat( Identifier statSpecific, int statValue){
		String customStatType =chooseCustomStatType(statSpecific);
		MutableText toReturn;
		switch (customStatType) {
			case TIME      : toReturn = formatTime(     statValue ); break;
			case REAL_TIME : toReturn = formatRealTime( statValue ); break;
			case DISTANCE  : toReturn = formatDistance( statValue ); break;
			case DAMAGE    : toReturn = formatDamage(   statValue ); break;
			case CAKE      : toReturn = formatCake(     statValue ); break;
			
			default        : toReturn = Text.literal(String.valueOf(statValue)).formatted(Formatting.AQUA);
		}
		
		return toReturn;
	}
	
	private static String    chooseCustomStatType( Identifier statSpecific) {
		return 	customStatIsIn( statSpecific, ModTags.Identifiers.IS_TIME)      ? TIME        :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_REAL_TIME) ? REAL_TIME   :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_DISTANCE)  ? DISTANCE    :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_DAMAGE)    ? DAMAGE      :
				customStatIsIn( statSpecific, ModTags.Identifiers.IS_CAKE)      ? CAKE        :
																	 DEFAULT     ;
		

	}
	
	private static MutableText formatTime( int statValue){
		int statSeconds  = statValue   / TICKS_per_SECOND;
		int statMinutes  = statSeconds / SECONDS_per_MINUTE;
		int statDays     = statMinutes / MINUTES_per_MC_DAY;
		int spareSeconds = statSeconds % SECONDS_per_MINUTE;
		int spareMinutes = statMinutes % MINUTES_per_MC_DAY;
		
		MutableText unit_day    =Text.translatable( join(DAY_KEY    , choosePlurality( TIME, statDays     , false )));
		MutableText unit_minute =Text.translatable( join(MINUTE_KEY , choosePlurality( TIME, spareMinutes , false )));
		MutableText unit_second =Text.translatable( join(SECOND_KEY , choosePlurality( TIME, spareSeconds , false )));
		
		MutableText daysText    = Text.translatable(TWO_WORDS,Text.literal(String.valueOf(statDays))    .formatted(Formatting.AQUA), unit_day   );
		MutableText minutesText = Text.translatable(TWO_WORDS,Text.literal(String.valueOf(spareMinutes)).formatted(Formatting.AQUA), unit_minute);
		MutableText secondsText = Text.translatable(TWO_WORDS,Text.literal(String.valueOf(spareSeconds)).formatted(Formatting.AQUA), unit_second);
		
		boolean hasDays      = statDays     > 0     ;
		boolean hasMinutes   = spareMinutes > 0     ;
		boolean hasSeconds   = spareSeconds > 0     ;
		int     switchValue  = hasSeconds   ? 1:0   ;
		        switchValue += hasMinutes   ? 2:0   ;
		        switchValue += hasDays      ? 4:0   ;
			   
	     MutableText toReturn = null;
		switch (switchValue){
			case 0 : toReturn = Text.translatable(LESSTHANSECOND_KEY); break;
			case 1 : toReturn =                                                                    secondsText   ; break;
			case 2 : toReturn =                                                      minutesText                 ; break;
			case 3 : toReturn = Text.stringifiedTranslatable(TWO_WORDS  ,            minutesText , secondsText  ); break;
			case 4 : toReturn =                                           daysText                               ; break;
			case 5 : toReturn = Text.stringifiedTranslatable(TWO_WORDS  , daysText ,               secondsText  ); break;
			case 6 : toReturn = Text.stringifiedTranslatable(TWO_WORDS  , daysText , minutesText                ); break;
			case 7 : toReturn = Text.stringifiedTranslatable(THREE_WORDS, daysText , minutesText , secondsText  ); break;
			default: toReturn = null;
		}
		return hasDays ? Text.stringifiedTranslatable(MINECRAFT_DAY_KEY, toReturn): toReturn;
		
	}
	private static MutableText formatRealTime( int statValue){
		int statSeconds  = statValue   / TICKS_per_SECOND;
		int statMinutes  = statSeconds / SECONDS_per_MINUTE;
		int statHours    = statMinutes / MINUTES_per_HOUR;
		int statDays     = statHours   / HOURS_per_DAY;
		int spareSeconds = statSeconds % SECONDS_per_MINUTE;
		int spareMinutes = statMinutes % MINUTES_per_HOUR;
		int spareHours   = statHours   % HOURS_per_DAY;
		
		MutableText unit_day    =Text.translatable( ( join(DAY_KEY    , choosePlurality( TIME, statDays    , false ))));
		MutableText unit_hour   =Text.translatable( ( join(HOUR_KEY   , choosePlurality( TIME, spareHours  , false ))));
		MutableText unit_minute =Text.translatable( ( join(MINUTE_KEY , choosePlurality( TIME, spareMinutes, false ))));
		MutableText unit_second =Text.translatable( ( join(SECOND_KEY , choosePlurality( TIME, spareSeconds, false ))));
		
		MutableText daysText    = Text.translatable(TWO_WORDS,Text.literal(String.valueOf(statDays))    .formatted(Formatting.AQUA), unit_day   );
		MutableText hoursText   = Text.translatable(TWO_WORDS,Text.literal(String.valueOf(spareHours))  .formatted(Formatting.AQUA), unit_hour  );
		MutableText minutesText = Text.translatable(TWO_WORDS,Text.literal(String.valueOf(spareMinutes)).formatted(Formatting.AQUA), unit_minute);
		MutableText secondsText = Text.translatable(TWO_WORDS,Text.literal(String.valueOf(spareSeconds)).formatted(Formatting.AQUA), unit_second);
		
		boolean hasDays      = statDays     > 0     ;
		boolean hasHours     = spareHours   > 0     ;
		boolean hasMinutes   = spareMinutes > 0     ;
		boolean hasSeconds   = spareSeconds > 0     ;
		int     switchValue  = hasSeconds   ? 1:0   ;
		        switchValue += hasMinutes   ? 2:0   ;
		        switchValue += hasHours     ? 4:0   ;
		        switchValue += hasDays      ? 8:0   ;
		
		switch (switchValue) {
			case 0 : return Text.translatable(LESSTHANSECOND_KEY);
			case 1 : return                                                                                 secondsText   ;
			case 2 : return                                                                   minutesText                 ;
			case 3 : return Text.stringifiedTranslatable(TWO_WORDS   ,                        minutesText , secondsText  );
			case 4 : return                                                       hoursText                               ;
			case 5 : return Text.stringifiedTranslatable(TWO_WORDS   ,            hoursText ,               secondsText  );
			case 6 : return Text.stringifiedTranslatable(TWO_WORDS   ,            hoursText , minutesText                );
			case 7 : return Text.stringifiedTranslatable(THREE_WORDS ,            hoursText , minutesText , secondsText  );
			case 8 : return                                            daysText                                           ;
			case 9 : return Text.stringifiedTranslatable(TWO_WORDS   , daysText ,                           secondsText  );
			case 10: return Text.stringifiedTranslatable(TWO_WORDS   , daysText ,             minutesText                );
			case 11: return Text.stringifiedTranslatable(THREE_WORDS , daysText ,             minutesText , secondsText  );
			case 12: return Text.stringifiedTranslatable(TWO_WORDS   , daysText , hoursText                              );
			case 13: return Text.stringifiedTranslatable(THREE_WORDS , daysText , hoursText ,               secondsText  );
			case 14: return Text.stringifiedTranslatable(THREE_WORDS , daysText , hoursText , minutesText                );
			case 15: return Text.stringifiedTranslatable(FOUR_WORDS  , daysText , hoursText , minutesText , secondsText  );
			default: return null;
		}
		
	}
	private static MutableText formatDistance( int statValue){
		MutableText metricText = formatDistanceMetric   (statValue);
		MutableText customText = formatDistanceCustom   (statValue);
		
		String switchValue = Text.translatable(SYSTEM_DISTANCE_FORMAT_KEY).getString();
		
		switch (switchValue){
			case "m (c)" : return Text.stringifiedTranslatable(JOIN_PARE, metricText, customText);
			case "c (m)" : return Text.stringifiedTranslatable(JOIN_PARE, customText, metricText);
			case "custom": return customText;
			case "metric":
			default      : return metricText;
			
		}
		
	}
	private static MutableText formatDistanceMetric(int statValue){
		DecimalFormat df = new DecimalFormat("0.00");
		int statCm  = statValue;
		double stat_m  = statCm / CENTIMETERSperMETER;
		double statKm  = stat_m / METERSperKILOMETER;
		
		boolean hasKm = statKm  >= 1;
		boolean has_m = stat_m  >= 1;
		
		String valueString;
		MutableText toReturn;
		/**/ if (hasKm) {
			valueString= df.format(statKm);
			toReturn = Text.stringifiedTranslatable(KM_KEY, (Text.literal(valueString)).formatted(Formatting.AQUA));
			
		}
		else if (has_m){
			valueString= df.format(stat_m);
			toReturn = Text.stringifiedTranslatable(M__KEY, (Text.literal(valueString)).formatted(Formatting.AQUA));
			
		}
		else {
			valueString= String.valueOf(statCm);
			toReturn = Text.stringifiedTranslatable(CM_KEY, (Text.literal(valueString)).formatted(Formatting.AQUA));
			
		
		}
		return toReturn;
	}
	private static MutableText formatDistanceCustom(int statValue){
		DecimalFormat df = new DecimalFormat("0.00");
		int   statIn  = statValue * INCHESperMETER;
			 statIn /= CENTIMETERSperMETER;
		int   statFt  = statIn / INCHESperFOOT;
		float statMi  = statFt / FEETperMILE  ;
		int spareIn   = statIn % INCHESperFOOT;
		
		boolean hasMi = statMi  > 0;
		boolean hasFt = statFt  > 0;
		boolean hasIn = spareIn > 0;
		
		MutableText toReturn;
		
		String valueString;
		/**/ if (hasMi || statFt > 2600) {
			String      miString= df.format(statMi);
			toReturn = Text.stringifiedTranslatable(MI_KEY, Text.literal(miString).formatted(Formatting.AQUA));
			
		}
		else if (hasFt){
			MutableText ftText = Text.literal(String.valueOf(statFt)).formatted(Formatting.AQUA);
				ftText = Text.stringifiedTranslatable(FT_KEY, ftText);
			if (hasIn) {
				MutableText inText = Text.literal(String.valueOf(spareIn)).formatted(Formatting.AQUA);
					inText = Text.stringifiedTranslatable(IN_KEY, inText);
				toReturn = Text.stringifiedTranslatable(TWO_WORDS, ftText, inText);
			}
			else
				toReturn = ftText;
		}
		else {
			MutableText inText = Text.literal(String.valueOf(statIn)).formatted(Formatting.AQUA);
			toReturn = Text.stringifiedTranslatable(IN_KEY, inText);
			
		}
		return toReturn;
	}
	private static MutableText formatDamage( int statValue){
		int heartValue = statValue / 2 ;
		boolean hasHearts = heartValue > 0;
		boolean hasHalfHeart = statValue % 2 == 1 ;
		
		int switchValue = (hasHalfHeart ? 1:0) + (hasHearts ? 2:0);
		
		MutableText toReturn;
		switch (switchValue){
			case 0 :
				toReturn= Text.translatable( join(HEART_KEY, NULL_P) ).formatted(Formatting.AQUA);
				return Text.stringifiedTranslatable( join(DAMAGE_KEY, NULL_P), toReturn);
			case 1 :
				toReturn = Text.stringifiedTranslatable(HALF_HEART_KEY).formatted(Formatting.AQUA);
				toReturn = Text.stringifiedTranslatable(JOIN_A, toReturn, Text.translatable(HEART_KEY));
				break;
			case 2 :
				//TODO BE MORE CLEVER ABOUT CASE: 1 a heart
				if  (heartValue == 1)
					toReturn = Text.stringifiedTranslatable("commandstatistics.grammar.indefinite.single.gender_3", Text.translatable(HEART_KEY));
				else{
					String heartKey = HEART_KEY + PLURAL;
					toReturn = Text.stringifiedTranslatable(heartKey, Text.literal(String.valueOf(heartValue)).formatted(Formatting.AQUA));
				}
				break;
			case 3 :
				String heartKey = join(HEART_KEY + PLURAL);
				String halfHeartKey = join(HALF_HEART_KEY , ".and");
				MutableText heartValueAndAHalf = Text.stringifiedTranslatable(halfHeartKey, Text.literal(String.valueOf(heartValue))).formatted(Formatting.AQUA);
				toReturn = Text.stringifiedTranslatable(heartKey, heartValueAndAHalf);
				break;
			default:
				toReturn=null;
		}
		return Text.stringifiedTranslatable(DAMAGE_KEY,toReturn);
	}
	private static MutableText formatCake  ( int statValue){
		int cakeValue   = statValue / SLICESperCAKE;
		int spareSlices = statValue % SLICESperCAKE;
		
		boolean hasCake = cakeValue >0;
		boolean hasSlices = spareSlices > 0;
		
		String cakePlurality  = choosePlurality(CAKE, cakeValue,   false);
		String slicePlurality = choosePlurality(CAKE, spareSlices, false);
		
		MutableText noCakes   = Text.stringifiedTranslatable(( join(UNIT_CAKE  , NULL_P        )), Text.translatable( join(BASE_KEY,CAKE,NULL_P)).formatted(Formatting.AQUA));
		MutableText cakeText  = Text.stringifiedTranslatable(( join(UNIT_CAKE  , cakePlurality )), Text.literal(String.valueOf(cakeValue  )  ).formatted(Formatting.AQUA));
		MutableText sliceText = Text.stringifiedTranslatable(( join(UNIT_SLICE , slicePlurality)), Text.literal(String.valueOf(spareSlices)  ).formatted(Formatting.AQUA));
		
		int switchValue = (hasSlices ? 1:0) + ( hasCake ? 2:0 );
		
		switch (switchValue){
			case 0 : return noCakes;
			case 1 : return                                                    sliceText ;
			case 2 : return                                         cakeText             ;
			case 3 : return Text.stringifiedTranslatable( JOIN_AND, cakeText, sliceText) ;
			default:
		}
		
		return null;
	}
	
	public  static String choosePlurality ( String custom_stat_type, int statValue, boolean isFormat ) {
		switch (statValue) {
			case 0: return ( isFormat ) ? NULL_P : ( Text.translatable( join(PLURALITY_BASE_KEY , NULL_P)) ).getString();
			case 1: return ( isFormat ) ? SINGLE : ( Text.translatable( join(PLURALITY_BASE_KEY , SINGLE)) ).getString();
			case 2: return ( isFormat ) ? DUAL   : ( Text.translatable( join(PLURALITY_BASE_KEY , DUAL  )) ).getString();
			default:
				String conPlural = ( Text.translatable( join(PLURALITY_BASE_KEY , PLURAL)) ).getString();
				String conA_Lot  = ( Text.translatable( join(PLURALITY_BASE_KEY , A_LOT )) ).getString();
				return ( isFormat ) ?
					statValue >= A_LOT_int[3] ? join (PLURAL, A_LOT) : PLURAL :
					statValue >= A_LOT_int[3] ? join (conPlural, conA_Lot) : conPlural;
		}
		}
		
	
}
