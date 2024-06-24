package hydraheadhunter.cmdstats.util.units.time.irl;

import hydraheadhunter.cmdstats.util.units.iUnit;

import java.util.ArrayList;
import java.util.List;

import static hydraheadhunter.cmdstats.CommandStatistics.join;

public class Year implements iUnit {
	private static final String UNIT_TYPE = "time";
	private static final String UNIT_KEY = "year";
	public static final double CONVERSION_FACTOR = 631139040;
	public static final String TRANSLATION_KEY = join(KEY_ROOT,UNIT_KEY);
	
	public static final ArrayList<String> EQ_UNITS= new ArrayList<>( List.of(new String[] {"year", "years", "yr"} ) );
	@Override	public String 			getUnitType()		 	{return UNIT_TYPE			; }
	
	@Override	public ArrayList<String> getEqUnits() 			{ return EQ_UNITS			; }
	@Override	public double 			getConversionFactor() 	{return CONVERSION_FACTOR	; }
	@Override	public String 			getTranslationKey() 	{return TRANSLATION_KEY		; }
	
}
