package hydraheadhunter.cmdstats.util.units;

import hydraheadhunter.cmdstats.util.units.cake.*;
import hydraheadhunter.cmdstats.util.units.damage.*;
import hydraheadhunter.cmdstats.util.units.distance.metric.*;
import hydraheadhunter.cmdstats.util.units.distance.yankee_imperial.*;
import hydraheadhunter.cmdstats.util.units.item.blockspace.*;
import hydraheadhunter.cmdstats.util.units.item.inventory.*;
import hydraheadhunter.cmdstats.util.units.item.stack.*;
import hydraheadhunter.cmdstats.util.units.time.irl.*;
import hydraheadhunter.cmdstats.util.units.time.in_game.*;

import java.util.ArrayList;
import java.util.List;

import static hydraheadhunter.cmdstats.CommandStatistics.*;

public interface iUnit {
	String CAKE 		= "cake"		;
	String DAMAGE		= "damage"	;
	String DISTANCE	= "distance"	;
	String TIME 		= "time"		;
	String MC_TIME		= "time.mc"	;
	String STACK		= "stack"		;
	String INVENTORY	= "inventory"	;
	String BLOCK		= "block"		;
	
	String UNIT_TYPE = "Interface";
	String KEY_ROOT = join (MOD_ID, UNIT);
	String TRANSLATION_KEY = KEY_ROOT;
	double CONVERSION_FACTOR =1;
	
	ArrayList<String> CUSTOM_STAT_UNITS 	= new ArrayList<>( List.of( new String[]{CAKE,DAMAGE,DISTANCE,TIME,MC_TIME} ) );
	ArrayList<String> ITEM_UNITS 			= new ArrayList<>( List.of( new String[]{STACK,INVENTORY} ) );
	ArrayList<String> BLOCK_STAT_ONLY   	= new ArrayList<>( List.of( new String[]{BLOCK} ) );
	
	ArrayList<iUnit> CAKE_UNITS 		= new ArrayList<>( List.of(new iUnit[]{new Cake()		}) );
	ArrayList<iUnit> DAMAGE_UNITS 	= new ArrayList<>( List.of(new iUnit[]{new Heart()	}) );
	ArrayList<iUnit> STACK_UNITS 		= new ArrayList<>( List.of(new iUnit[]{new Stack() 	}) );
	ArrayList<iUnit> DISTANCE_UNITS 	= new ArrayList<>( List.of(new iUnit[]{new Kilometer()		, new Meter()		, new Inch()			, new Foot(), 			new Yard(), 		new Mile() 							}) );
	ArrayList<iUnit> INVENTORY_UNITS 	= new ArrayList<>( List.of(new iUnit[]{new Hopper()		, new Dropper()	, new Chest()			, new DoubleChest(), 	new ShulkerChest(), new DoubleShulkerChest()					}) );
	ArrayList<iUnit> BLOCK_UNITS 		= new ArrayList<>( List.of(new iUnit[]{new ChunkSlice()	, new MapLayer()	, new SubChunkCube()																			}) );
	ArrayList<iUnit> TIME_UNITS 		= new ArrayList<>( List.of(new iUnit[]{new Second()		, new Minute()		, new minecraft_day()	, new Hour(), 			new Day(), 		new Week(), new Month(), new Year()		}) );

	static ArrayList<iUnit> AllUnits(){
		ArrayList<iUnit> toReturn= new ArrayList<>();
		toReturn.addAll(CAKE_UNITS		);
		toReturn.addAll(DAMAGE_UNITS		);
		toReturn.addAll(DISTANCE_UNITS	);
		toReturn.addAll(STACK_UNITS		);
		toReturn.addAll(INVENTORY_UNITS	);
		toReturn.addAll(BLOCK_UNITS		);
		toReturn.addAll(TIME_UNITS		);
		return toReturn;
	}
	static ArrayList<String> AllUnitNames(){
		ArrayList<String> toReturn= new ArrayList<>();
		for(iUnit iu: AllUnits()){
			ArrayList<String> temp = iu.getEqUnits();
			toReturn.addAll(temp);
		}
		return toReturn;
	}
	
	static ArrayList<String> UnitTypeNames( ArrayList<iUnit> unitType ){
		ArrayList<String> toReturn= new ArrayList<>();
		//for(iUnit iu: unitType){ toReturn.addAll(iu.EQ_UNITS); }
		return toReturn;
	}
	
	ArrayList<String> getEqUnits();
	double getConversionFactor();
	String getTranslationKey();
	String getUnitType();
	
	default int 			 convertTo	(int arg){ return (int) (arg / getConversionFactor()); }
	default int 			 convertFrom	(int arg){ return (int) (arg * getConversionFactor()); }
	default int 			 Remainder	(int arg){ return        arg % (int) getConversionFactor(); }
}
