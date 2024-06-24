package hydraheadhunter.cmdstats.command.feedback.lang;

import hydraheadhunter.cmdstats.CommandStatistics;
import hydraheadhunter.cmdstats.util.ModTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;

import static hydraheadhunter.cmdstats.CommandStatistics.*;
public class FormatCustom {
/*	private static final String BASE_KEY           = join(FEEDBACK_KEY, QUERY) ;
	private static final String LOCAL_SYSTEM_KEY   = join(SYSTEM_KEY  , QUERY) ;
	private static final String TWO_WORDS          = JOIN_KEY + ".2"         ;
	private static final String THREE_WORDS        = JOIN_KEY + ".3"         ;
	private static final String FOUR_WORDS         = JOIN_KEY + ".4"         ;
	private static final String FIVE_WORDS         = JOIN_KEY + ".5"         ;
	private static final String SIX_WORDS          = JOIN_KEY + ".6"         ;
	private static final String JOIN_A             = TWO_WORDS        + ".a"         ;
	private static final String JOIN_OF            = TWO_WORDS        + ".of"        ;
	private static final String JOIN_AND           = TWO_WORDS        + ".and"       ;
	private static final String JOIN_PLUS          = TWO_WORDS        + ".plus"      ;
	private static final String JOIN_PARE          = TWO_WORDS + ".parenthesis"      ;
	
	private static int[] A_LOT_int = {100, 20000, 2000000, 500000, 50000, 100 };
	
	private static final String SYSTEM_TIME_FORMAT_KEY = join( SYSTEM_KEY, TIME, FORMAT );
	
//time conversions factors
//distance conversion factors
	

	
	public static String provideFormat ( Identifier statSpecific, int statValue){
		String customStatType = chooseCustomStatType(statSpecific);
		String plurality      = choosePlurality(customStatType,statValue, true);
		String verbKey        = join(BASE_KEY, customStatType, statSpecific.getPath());
		String idName         = statSpecific.getNamespace().equals(MINECRAFT) ? statSpecific.getPath(): join(statSpecific.getNamespace(), statSpecific.getPath()) ;
		String specialFormattingCase = chooseSpecialFormatCase(statSpecific,plurality);
		
		return join( BASE_KEY , customStatType , FORMAT , specialFormattingCase );
	}
	private static String chooseSpecialFormatCase(Identifier statSpecific, String plurality){
		if (customStatIsIn(statSpecific, ModTags.Identifiers.IS_TIME_SPENT)) return join( SPENT, ( plurality.equals(NIL) ? NIL :EMPTY));
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
		if (customStatIsIn(statSpecific, ModTags.Identifiers.HAS_NULL  ) && statValue == 0) return NIL;
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
		double stat_m  = statCm / CM_per_METER;
		double statKm  = stat_m / METERS_per_KM;
		
		boolean hasKm = statKm  >= 1;
		boolean has_m = stat_m  >= 1;
		
		String valueString;
		MutableText toReturn;
		/**/ /*if (hasKm) {
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
		int   statIn  = statValue * INCHES_per_METER;
			 statIn /= CM_per_METER;
		int   statFt  = statIn / INCHES_per_FOOT;
		float statMi  = statFt / FEET_per_MILE  ;
		int spareIn   = statIn % INCHES_per_FOOT;
		
		boolean hasMi = statMi  > 0;
		boolean hasFt = statFt  > 0;
		boolean hasIn = spareIn > 0;
		
		MutableText toReturn;
		
		String valueString;
		/**/ /*if (hasMi || statFt > 2600) {
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
				toReturn= Text.translatable( join(HEART_KEY, NIL) ).formatted(Formatting.AQUA);
				return Text.stringifiedTranslatable( join(DAMAGE_KEY, NIL), toReturn);
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
		int cakeValue   = statValue / SLICES_per_CAKE;
		int spareSlices = statValue % SLICES_per_CAKE;
		
		boolean hasCake = cakeValue >0;
		boolean hasSlices = spareSlices > 0;
		
		String cakePlurality  = choosePlurality(CAKE, cakeValue,   false);
		String slicePlurality = choosePlurality(CAKE, spareSlices, false);
		
		MutableText noCakes   = Text.stringifiedTranslatable(( join(UNIT_CAKE  , NIL)), Text.translatable( join(BASE_KEY,CAKE, NIL)).formatted(Formatting.AQUA));
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
			case 0: return ( isFormat ) ? NIL : ( Text.translatable( join(PLURALITY_KEY, NIL)) ).getString();
			case 1: return ( isFormat ) ? SINGLE : ( Text.translatable( join(PLURALITY_KEY, SINGLE)) ).getString();
			case 2: return ( isFormat ) ? DUAL   : ( Text.translatable( join(PLURALITY_KEY, DUAL  )) ).getString();
			default:
				String conPlural = ( Text.translatable( join(PLURALITY_KEY, PLURAL)) ).getString();
				String conA_Lot  = ( Text.translatable( join(PLURALITY_KEY, A_LOT )) ).getString();
				return ( isFormat ) ?
					statValue >= A_LOT_int[3] ? join (PLURAL, A_LOT) : PLURAL :
					statValue >= A_LOT_int[3] ? join (conPlural, conA_Lot) : conPlural;
		}
		}
		
*/
}
