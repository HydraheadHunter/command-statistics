package hydraheadhunter.cmdstats.command.feedback.lang;

public class KeySelector {
		
	public static String selectBasicStatTypeKey(String statTypeNameToString){
		return truncateStatTypeName(statTypeNameToString);
	}
	public static String selectStatKey( String statNameToString ){
		String sanitizedStatNameToString = statNameToString.replace(':','.');
		return truncateStatName( sanitizedStatNameToString );
	}
	public static String abbreviateMinecraft(String stringToShorten){
		return stringToShorten.replace("minecraft","mc");
	}
	
	private static String truncateStatTypeName( String statTypeNameToString){
		int indexOfLastPeriod	= statTypeNameToString.lastIndexOf('.' );
		
		String removeLastPeriod = statTypeNameToString.substring(0,indexOfLastPeriod);
		int indexOfSecondLastPeriod = removeLastPeriod.lastIndexOf('.');
		
		int indexOfLastPostrophe = statTypeNameToString.lastIndexOf('\'');
		return statTypeNameToString.substring(indexOfSecondLastPeriod+1, indexOfLastPostrophe);
	}
	private static String truncateStatName( String statNameToString){
		int indexOfLastPeriod	= statNameToString.lastIndexOf('.' );
		
		String removeLastPeriod = statNameToString.substring(0,indexOfLastPeriod);
		int indexOfSecondLastPeriod = removeLastPeriod.lastIndexOf('.');
		
		return statNameToString.substring(indexOfSecondLastPeriod+1);
	}
	
	
	
}
