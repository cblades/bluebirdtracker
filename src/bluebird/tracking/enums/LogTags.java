package bluebird.tracking.enums;

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
