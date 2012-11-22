package test;

public class Ver {


	public Ver() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StringBuffer sb=new StringBuffer();
		
		sb.append("Ver:").append("\r\n");
		sb.append("  ").append(Version.AUTHOR).append("\r\n");
		sb.append("  ").append(Version.DATE).append("\r\n");
		sb.append("  ").append(Version.REVISION).append("\r\n");
		sb.append("  ").append(Version.TAG).append("\r\n");
		sb.append("").append("\r\n");
		
		sb.append("JAVA:").append("\r\n");
		sb.append("  JDK: 1.5+").append("\r\n");
		sb.append("").append("\r\n");
		
		System.out.println(sb);
			
	}

}

class Version {

	final static String HEADER = "$Header: /CVSDATA/packet/src/test/Ver.java,v 1.1 2007/01/16 05:43:02 xiaoran27 Exp $";
	final static String DATE = "$Date: 2007/01/16 05:43:02 $";
	final static String AUTHOR = "$Author: xiaoran27 $";
	final static String REVISION = "$Revision: 1.1 $";
	
	final static String TAG = "$Name:  $";
	
	public String toString(){
		return AUTHOR+"\r\n"+DATE+"\r\n"+REVISION+"\r\n"+TAG;
	}

}
