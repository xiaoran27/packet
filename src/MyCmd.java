import java.io.File;



public class MyCmd {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	      if (args.length  < 1 ||( args.length  >0 && args[0].matches("[-]{1,2}(([Hh][Ee][Ll][Pp])|([Hh]))"))) {
	          System.out.println("Usage:  java MyCmd program [[param]...] " );
	          System.out.println("program - a execute file.  Example: 'notepad'");
	          System.out.println("param - params of program. Example: 'abc.txt");
	          System.exit(0);
	        }
	    
	      
	      String[] dosShell = {"cmd","/c",""};
	      String[] linuxShell = {"sh","-c",""};
	      
		String[] cmdarray =File.pathSeparatorChar == ':' ? linuxShell : dosShell;
		try {
			
			for (String arg : args){
				if (arg.indexOf(' ')>=0){
					cmdarray[2]=cmdarray[2]+" \""+arg+"\" ";
				}else{
					cmdarray[2]=cmdarray[2]+" "+arg+" ";
				}
			}
			
			try {
				ProcessBuilder  pb = new ProcessBuilder ();
				pb.command(cmdarray);
				Process proc = pb.start();
			} catch (Exception e) {
				try {
					Process proc = Runtime.getRuntime().exec(cmdarray);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			Thread.sleep(5*1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
