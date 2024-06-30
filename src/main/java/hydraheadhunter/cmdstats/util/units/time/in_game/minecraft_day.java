package hydraheadhunter.cmdstats.util.units.time.in_game;

import hydraheadhunter.cmdstats.util.units.iUnit;

import java.util.ArrayList;
import java.util.List;

import static hydraheadhunter.cmdstats.CommandStatistics.join;

public class minecraft_day implements iUnit {
	private static final String UNIT_TYPE = TIME;
	private static final String UNIT_KEY = "mc_day";
	public static final double CONVERSION_FACTOR = 240000;
	public static final String TRANSLATION_KEY = join(KEY_ROOT,UNIT_KEY);
	
	public static final ArrayList<String> EQ_UNITS= new ArrayList<>( List.of(new String[] {"minecraft_day", "mc_day", "minecraft_days", "mc_days", "mcd"} ) );
	
	@Override	public ArrayList<String> getEqUnits() 			{ return EQ_UNITS			; }
	@Override	public double 			getConversionFactor() 	{return CONVERSION_FACTOR	; }
	@Override	public String 			getTranslationKey() 	{return TRANSLATION_KEY		; }
	@Override	public String 			getUnitType()		 	{return UNIT_TYPE			; }
	
}
