package bluebird.tracking.enums;

/*
 * Enum for holding all application tags for logging purposes (for consistency across the app)
 * 
 * @author Matthew Stratton
 */
public enum LogTags {
	DATABASE("Database"),
	CONTENT_PROVIDER("Content Provider");
	
	String tag;
	
	LogTags(String tag){
		this.tag = tag;
	}
	
	public String toString(){
		return tag;
	}
}
