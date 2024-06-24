package hydraheadhunter.cmdstats.util.units.item.inventory;

import hydraheadhunter.cmdstats.util.units.iUnit;

import java.util.ArrayList;
import java.util.List;

import static hydraheadhunter.cmdstats.CommandStatistics.join;

public class DoubleChest implements iUnit {
	private static final String UNIT_TYPE = "inventory";
	private static final String UNIT_KEY = "chest.2";
	public static final double CONVERSION_FACTOR = 54;
	public static final String TRANSLATION_KEY = join(KEY_ROOT,UNIT_KEY);
	
	public static final ArrayList<String> EQ_UNITS= new ArrayList<>( List.of(new String[] {"double_chest", "db_chest", "db_ch", "DbCh" } ) );
	
	@Override	public ArrayList<String> getEqUnits() 			{ return EQ_UNITS			; }
	@Override	public double 			getConversionFactor() 	{return CONVERSION_FACTOR	; }
	@Override	public String 			getTranslationKey() 	{return TRANSLATION_KEY		; }
	@Override	public String 			getUnitType()		 	{return UNIT_TYPE			; }
	
}
