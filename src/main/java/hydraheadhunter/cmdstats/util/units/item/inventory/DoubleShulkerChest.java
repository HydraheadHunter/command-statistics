package hydraheadhunter.cmdstats.util.units.item.inventory;

import hydraheadhunter.cmdstats.util.units.iUnit;

import java.util.ArrayList;
import java.util.List;

import static hydraheadhunter.cmdstats.CommandStatistics.join;

public class DoubleShulkerChest implements iUnit {
	private static final String UNIT_TYPE = "inventory";
	private static final String UNIT_KEY = "chest.shulker.2";
	public static final double CONVERSION_FACTOR = 1458;
	public static final String TRANSLATION_KEY = join(KEY_ROOT,UNIT_KEY);
	
	public static final ArrayList<String> EQ_UNITS= new ArrayList<>( List.of(new String[] {"double_shulker_chest", "double_chest_of_shulkers", "db_sk_ch", "DbSkCh", "db_shulker_chest", "db_chest_of_shulker"} ) );
	
	@Override	public ArrayList<String> getEqUnits() 			{ return EQ_UNITS			; }
	@Override	public double 			getConversionFactor() 	{return CONVERSION_FACTOR	; }
	@Override	public String 			getTranslationKey() 	{return TRANSLATION_KEY		; }
	@Override	public String 			getUnitType()		 	{return UNIT_TYPE			; }
	
}
