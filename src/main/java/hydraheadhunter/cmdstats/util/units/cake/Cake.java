package hydraheadhunter.cmdstats.util.units.cake;

import hydraheadhunter.cmdstats.util.units.iUnit;

import java.util.ArrayList;
import java.util.List;

import static hydraheadhunter.cmdstats.CommandStatistics.join;

public class Cake implements iUnit {
	private static final String UNIT_TYPE = "cake";
	private static final String UNIT_KEY = "cake";
	public static final double CONVERSION_FACTOR = 7;
	public static final String TRANSLATION_KEY = join(KEY_ROOT,UNIT_KEY);
	
	public final ArrayList<String> EQ_UNITS= new ArrayList<>( List.of(new String[] {"cake","cakes"}) );
	
	@Override	public ArrayList<String> getEqUnits() 			{return EQ_UNITS			; }
	@Override	public double 			getConversionFactor() 	{return CONVERSION_FACTOR	; }
	@Override	public String 			getTranslationKey() 	{return TRANSLATION_KEY		; }
	@Override	public String 			getUnitType()		 	{return UNIT_TYPE			; }
	
}
