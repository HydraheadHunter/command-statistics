package hydraheadhunter.cmdstats.util.units.distance.yankee_imperial;

import hydraheadhunter.cmdstats.util.units.iUnit;

import java.util.ArrayList;
import java.util.List;

import static hydraheadhunter.cmdstats.CommandStatistics.join;

public class Foot implements iUnit {
	private static final String UNIT_TYPE = "distance";
	private static final String UNIT_KEY = "foot";
	public static final double CONVERSION_FACTOR = 30.48;
	public static final String TRANSLATION_KEY = join(KEY_ROOT,UNIT_KEY);
	
	public static final ArrayList<String> EQ_UNITS= new ArrayList<>( List.of(new String[] {"foot", "feet", "ft" } ) );
	
	@Override	public ArrayList<String> getEqUnits() 			{ return EQ_UNITS			; }
	@Override	public double 			getConversionFactor() 	{return CONVERSION_FACTOR	; }
	@Override	public String 			getTranslationKey() 	{return TRANSLATION_KEY		; }
	@Override	public String 			getUnitType()		 	{return UNIT_TYPE			; }
	
}
