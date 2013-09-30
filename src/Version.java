
public class Version {
	
    final static String HEADER = "$Header: /ljscm/cvsroot/irms/rnm/simn/src/Version.java,v 1.2 2013/04/24 06:43:19 wuxr Exp $";
    final static String DATE = "$Date: 2013/04/24 06:43:19 $";
    final static String AUTHOR = "$Author: wuxr $";
    final static String REVISION = "$Revision: 1.2 $";
    final static String TAG = "$Name:   $";

   /**
     * @param args
     */
    public static void main(String[] args) {
        StringBuffer sb=new StringBuffer();

        sb.append(System.getProperty("pname", "packet")).append(" Ver:").append("\r\n");
        sb.append("  ").append(AUTHOR).append("\r\n");
        sb.append("  ").append(DATE).append("\r\n");
        sb.append("  ").append(REVISION).append("\r\n");
        sb.append("  ").append(TAG).append("\r\n");
        sb.append("").append("\r\n");

        sb.append("JAVA:").append("\r\n");
        sb.append("  JDK: 1.6+").append("\r\n");
        sb.append("").append("\r\n");

        System.out.println(sb);

    }

}


