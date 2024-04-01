package hydraheadhunter.commandstatistics.command.feedback.lang;

import hydraheadhunter.commandstatistics.CommandStatistics;
import hydraheadhunter.commandstatistics.util.ID_IsIn;
import hydraheadhunter.commandstatistics.util.ModTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mutable;

import java.text.DecimalFormat;

public class FormatCustom {
	private static final String MINECRAFT = "minecraft"; private static final String EMPTY = "";
	private static final String BASE_KEY         = CommandStatistics.MOD_ID  + ".feedback.query";
	private static final String GRAMMAR_BASE_KEY  = CommandStatistics.MOD_ID + ".grammar";
	private static final String PLURALITY_BASE_KEY = GRAMMAR_BASE_KEY + ".plurality" ;
	private static final String JOIN_BASE_KEY      = GRAMMAR_BASE_KEY + ".join"      ;
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
	
	private static final String FORMAT = ".format";
	
	private static final String DEFAULT    = ".custom"  ;
	private static int[] A_LOT = {100, 20000, 2000000, 500000, 50000, 100 };
	
	//time conversions factors
	private static final int TICKSperSECOND = 20;	private static final int SECONDSperMINUTE = 60;	private static final int MINUTESperDAY = 20;
	private static final int MINUTESperHOUR = 60;	private static final int HOURSperRDAY     = 24;	private static final int DAYSperWEEK   = 7 ;
	private static final int WEEKSperMONTH  = 4 ;	private static final int MONTHSperYEAR    = 12;
	//distance conversion factors
	private static final int CENTIMETERperMETER = 100; private static final int METERperKILOMETER = 1000;
	private static final int INCHESperMETER     =  39; private static final int CENTIMETER_perCM_INCH = 2;
	private static final int INCHESperFOOT      =  12; private static final int FEETperMILE         = 5280;
	private static final int SLICESperCAKE      =   7;
	
	private static final String TIME       = ".time"     ;
	private static final String REAL_TIME  = ".time.irl" ;
	private static final String UNIT_SECOND       = BASE_KEY    + TIME     + ".unit.second"       ;
	private static final String UNIT_MINUTE       = BASE_KEY    + TIME     + ".unit.minute"       ;
	private static final String UNIT_HOUR         = BASE_KEY    + TIME     + ".unit.hour"         ;
	private static final String UNIT_DAY          = BASE_KEY    + TIME     + ".unit.day"          ;
	private static final String UNIT_MINECRAFT_DAY= UNIT_DAY    + "."      + MINECRAFT;
	private static final String LESSTHANONESECOND = UNIT_SECOND            + ".less_than"         ;
	private static final String UNIT_WEEK         = BASE_KEY    + TIME     + ".week"              ;
	private static final String UNIT_MONTH        = BASE_KEY    + TIME     + ".month"             ;
	private static final String UNIT_YEAR         = BASE_KEY    + TIME     + ".year"              ;
	
	private static final String DISTANCE   = ".distance";
	private static final String DISTANCE_UNIT_SYSTEM = BASE_KEY + DISTANCE +".unit.system"        ;
	private static final String UNIT_CM           = BASE_KEY    + DISTANCE + ".unit.cm"           ;
	private static final String UNIT__M           = BASE_KEY    + DISTANCE + ".unit.m"            ;
	private static final String UNIT_KM           = BASE_KEY    + DISTANCE + ".unit.km"
		;
	private static final String UNIT_IN           = BASE_KEY    + DISTANCE + ".unit.in"           ;
	private static final String UNIT_FT           = BASE_KEY    + DISTANCE + ".unit.ft"           ;
	private static final String UNIT_MI           = BASE_KEY    + DISTANCE + ".unit.mi"           ;
	
	private static final String DAMAGE     = ".damage"  ;
	private static final String UNIT_DAMAGE       = BASE_KEY    + DAMAGE   + ".unit"              ;
	private static final String UNIT_HALF_HEART   = BASE_KEY    + DAMAGE   + ".unit.half_heart"   ;
	private static final String UNIT_HEART        = BASE_KEY    + DAMAGE   + ".unit.heart"        ;
	
	private static final String CAKE       = ".cake"    ;
	private static final String UNIT_CAKE         = BASE_KEY    + CAKE   + ".unit"      ;
	private static final String UNIT_SLICE        = UNIT_CAKE            + ".slice"     ;
	
	private static final String NEG_COUNT     = ".neg"       ;
	private static final String NULL_COUNT    = ".null"      ;
	private static final String SINGLE_COUNT  = ".single"    ;
	private static final String DUAL_COUNT    = ".dual"      ;
	private static final String PLURAL_COUNT  = ".plural"    ;
	private static final String A_LOT_COUNT   = ".alot"      ;
	
	public static String provideFormat ( Identifier statSpecific, int statValue){
		String customStatType= chooseCustomStatType(statSpecific);
		String plurality = choosePlurality(customStatType,statValue, true);
		String verbKey = BASE_KEY + customStatType + statSpecific.getPath();
		String idName    = "." + ( statSpecific.getNamespace().equals(MINECRAFT) ? statSpecific.getPath():statSpecific.getNamespace()+"."+statSpecific.getPath() );
		String specialFormattingCase = chooseSpecialFormatCase(statSpecific,plurality);
		
		return BASE_KEY + customStatType + FORMAT + specialFormattingCase;
	}
	private static String chooseSpecialFormatCase(Identifier statSpecific, String plurality){
		if (ID_IsIn.customStatIsIn(statSpecific, ModTags.Identifiers.IS_TIME_SPENT)) return ".spent" +  ( plurality.equals(NULL_COUNT) ? NULL_COUNT:EMPTY);
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
		
		return BASE_KEY + customStatType + "." + statPath + verbPlurality;
	}
	
	private static String sanitizePath ( String customStatType, String statPath){
		return customStatType.equals(DAMAGE) ? statPath.substring(DAMAGE.length()):statPath;
	}
	
	private static String chooseVerbPlurality( Identifier statSpecific, int statValue){
		if (ID_IsIn.customStatIsIn(statSpecific, ModTags.Identifiers.HAS_NULL  ) && statValue == 0) return NULL_COUNT;
		if (ID_IsIn.customStatIsIn(statSpecific, ModTags.Identifiers.HAS_SINGLE) && statValue == 0) return SINGLE_COUNT;
		if (ID_IsIn.customStatIsIn(statSpecific, ModTags.Identifiers.HAS_DUAL  ) && statValue == 0) return DUAL_COUNT;
		if (ID_IsIn.customStatIsIn(statSpecific, ModTags.Identifiers.HAS_PLURAL) && statValue == 0) return PLURAL_COUNT;
		if (ID_IsIn.customStatIsIn(statSpecific, ModTags.Identifiers.HAS_ALOT  ) && statValue == 0) return PLURAL_COUNT + A_LOT_COUNT;
		
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
		return 	ID_IsIn.customStatIsIn( statSpecific, ModTags.Identifiers.IS_TIME)      ? TIME        :
				ID_IsIn.customStatIsIn( statSpecific, ModTags.Identifiers.IS_REAL_TIME) ? REAL_TIME   :
				ID_IsIn.customStatIsIn( statSpecific, ModTags.Identifiers.IS_DISTANCE)  ? DISTANCE    :
				ID_IsIn.customStatIsIn( statSpecific, ModTags.Identifiers.IS_DAMAGE)    ? DAMAGE      :
				ID_IsIn.customStatIsIn( statSpecific, ModTags.Identifiers.IS_CAKE)      ? CAKE        :
																						  			  DEFAULT     ;
		

	}
	
	private static MutableText formatTime( int statValue){
		int statSeconds  = statValue   / TICKSperSECOND;
		int statMinutes  = statSeconds / SECONDSperMINUTE;
		int statDays     = statMinutes / MINUTESperDAY;
		int spareSeconds = statSeconds % SECONDSperMINUTE;
		int spareMinutes = statMinutes % MINUTESperDAY;
		
		MutableText unit_day    =Text.translatable(UNIT_DAY    + choosePlurality( TIME, statDays     , false ));
		MutableText unit_minute =Text.translatable(UNIT_MINUTE + choosePlurality( TIME, spareMinutes , false ));
		MutableText unit_second =Text.translatable(UNIT_SECOND + choosePlurality( TIME, spareSeconds , false ));
		
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
			case 0 : toReturn = Text.translatable(LESSTHANONESECOND); break;
			case 1 : toReturn =                                                                    secondsText   ; break;
			case 2 : toReturn =                                                      minutesText                 ; break;
			case 3 : toReturn = Text.stringifiedTranslatable(TWO_WORDS  ,            minutesText , secondsText  ); break;
			case 4 : toReturn =                                           daysText                               ; break;
			case 5 : toReturn = Text.stringifiedTranslatable(TWO_WORDS  , daysText ,               secondsText  ); break;
			case 6 : toReturn = Text.stringifiedTranslatable(TWO_WORDS  , daysText , minutesText                ); break;
			case 7 : toReturn = Text.stringifiedTranslatable(THREE_WORDS, daysText , minutesText , secondsText  ); break;
			default: toReturn = null;
		}
		return hasDays ? Text.stringifiedTranslatable(UNIT_MINECRAFT_DAY, toReturn): toReturn;
		
	}
	private static MutableText formatRealTime( int statValue){
		int statSeconds  = statValue   / TICKSperSECOND;
		int statMinutes  = statSeconds / SECONDSperMINUTE;
		int statHours    = statMinutes / MINUTESperHOUR;
		int statDays     = statHours   / HOURSperRDAY;
		int spareSeconds = statSeconds % SECONDSperMINUTE;
		int spareMinutes = statMinutes % MINUTESperHOUR;
		int spareHours   = statHours   % HOURSperRDAY;
		
		MutableText unit_day    =Text.translatable( (UNIT_DAY    + choosePlurality( TIME, statDays    , false )));
		MutableText unit_hour   =Text.translatable( (UNIT_HOUR   + choosePlurality( TIME, spareHours  , false )));
		MutableText unit_minute =Text.translatable( (UNIT_MINUTE + choosePlurality( TIME, spareMinutes, false )));
		MutableText unit_second =Text.translatable( (UNIT_SECOND + choosePlurality( TIME, spareSeconds, false )));
		
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
		        switchValue += hasMinutes   ? 4:0   ;
		        switchValue += hasDays      ? 8:0   ;
		
		switch (switchValue) {
			case 0 : return Text.translatable(LESSTHANONESECOND);
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
		
		String switchValue = Text.translatable(DISTANCE_UNIT_SYSTEM).getString();
		
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
		double stat_m  = statCm / CENTIMETERperMETER;
		double statKm  = stat_m / METERperKILOMETER ;
		
		boolean hasKm = statKm  >= 1;
		boolean has_m = stat_m  >= 1;
		
		String valueString;
		MutableText toReturn;
		/**/ if (hasKm) {
			valueString= df.format(statKm);
			toReturn = Text.stringifiedTranslatable(UNIT_KM, (Text.literal(valueString)).formatted(Formatting.AQUA));
			
		}
		else if (has_m){
			valueString= df.format(stat_m);
			toReturn = Text.stringifiedTranslatable(UNIT__M, (Text.literal(valueString)).formatted(Formatting.AQUA));
			
		}
		else {
			valueString= String.valueOf(statCm);
			toReturn = Text.stringifiedTranslatable(UNIT_CM, (Text.literal(valueString)).formatted(Formatting.AQUA));
			
		
		}
		return toReturn;
	}
	private static MutableText formatDistanceCustom(int statValue){
		DecimalFormat df = new DecimalFormat("0.00");
		int   statIn  = statValue * INCHESperMETER;
			 statIn /= CENTIMETERperMETER ;
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
			toReturn = Text.stringifiedTranslatable(UNIT_MI, Text.literal(miString).formatted(Formatting.AQUA));
			
		}
		else if (hasFt){
			MutableText ftText = Text.literal(String.valueOf(statFt)).formatted(Formatting.AQUA);
				ftText = Text.stringifiedTranslatable(UNIT_FT, ftText);
			if (hasIn) {
				MutableText inText = Text.literal(String.valueOf(spareIn)).formatted(Formatting.AQUA);
					inText = Text.stringifiedTranslatable(UNIT_IN, inText);
				toReturn = Text.stringifiedTranslatable(TWO_WORDS, ftText, inText);
			}
			else
				toReturn = ftText;
		}
		else {
			MutableText inText = Text.literal(String.valueOf(statIn)).formatted(Formatting.AQUA);
			toReturn = Text.stringifiedTranslatable(UNIT_IN, inText);
			
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
				toReturn= Text.translatable(UNIT_HEART + NULL_COUNT).formatted(Formatting.AQUA);
				return Text.stringifiedTranslatable(UNIT_DAMAGE + NULL_COUNT, toReturn);
			case 1 :
				toReturn = Text.stringifiedTranslatable(UNIT_HALF_HEART).formatted(Formatting.AQUA);
				toReturn = Text.stringifiedTranslatable(JOIN_A, toReturn, Text.translatable(UNIT_HEART));
				break;
			case 2 :
				//TODO BE MORE CLEVER ABOUT CASE: 1 a heart
				if  (heartValue == 1)
					toReturn = Text.stringifiedTranslatable("commandstatistics.grammar.indefinite.single.gender_3", Text.translatable(UNIT_HEART));
				else{
					String heartKey = UNIT_HEART + PLURAL_COUNT;
					toReturn = Text.stringifiedTranslatable(heartKey, Text.literal(String.valueOf(heartValue)).formatted(Formatting.AQUA));
				}
				break;
			case 3 :
				String heartKey = UNIT_HEART + PLURAL_COUNT;
				String halfHeartKey = UNIT_HALF_HEART + ".and";
				MutableText heartValueAndAHalf = Text.stringifiedTranslatable(halfHeartKey, Text.literal(String.valueOf(heartValue))).formatted(Formatting.AQUA);
				toReturn = Text.stringifiedTranslatable(heartKey, heartValueAndAHalf);
				break;
			default:
				toReturn=null;
		}
		return Text.stringifiedTranslatable(UNIT_DAMAGE,toReturn);
	}
	private static MutableText formatCake  ( int statValue){
		int cakeValue   = statValue / SLICESperCAKE;
		int spareSlices = statValue % SLICESperCAKE;
		
		boolean hasCake = cakeValue >0;
		boolean hasSlices = spareSlices > 0;
		
		String cakePlurality  = choosePlurality(CAKE, cakeValue,   false);
		String slicePlurality = choosePlurality(CAKE, spareSlices, false);
		
		MutableText noCakes   = Text.stringifiedTranslatable((UNIT_CAKE  + NULL_COUNT )   , Text.translatable(BASE_KEY+CAKE+NULL_COUNT).formatted(Formatting.AQUA));
		MutableText cakeText  = Text.stringifiedTranslatable((UNIT_CAKE  + cakePlurality ), Text.literal(String.valueOf(cakeValue  )  ).formatted(Formatting.AQUA));
		MutableText sliceText = Text.stringifiedTranslatable((UNIT_SLICE + slicePlurality), Text.literal(String.valueOf(spareSlices)  ).formatted(Formatting.AQUA));
		
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
			case 0: return ( isFormat ) ? NULL_COUNT   : ( Text.translatable(PLURALITY_BASE_KEY + NULL_COUNT  ) ).getString();
			case 1: return ( isFormat ) ? SINGLE_COUNT : ( Text.translatable(PLURALITY_BASE_KEY + SINGLE_COUNT) ).getString();
			case 2: return ( isFormat ) ? DUAL_COUNT   : ( Text.translatable(PLURALITY_BASE_KEY + DUAL_COUNT  ) ).getString();
			default:
				String conPlural = ( Text.translatable(PLURALITY_BASE_KEY + PLURAL_COUNT) ).getString();
				String conA_Lot  = ( Text.translatable(PLURALITY_BASE_KEY + A_LOT_COUNT ) ).getString();
				return ( isFormat ) ?
					statValue >= A_LOT[3] ? PLURAL_COUNT + A_LOT_COUNT : PLURAL_COUNT :
					statValue >= A_LOT[3] ? conPlural + conA_Lot : conPlural;
		}
		}
		
		
	
}
