package test;

import java.net.URL;
import java.util.Arrays;

import com.lj.utils.HexUtil;
import com.lj.utils.TextFormat;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
//    	System.out.println("abc");
//    	byte[] b = new byte[16];
//    	Arrays.fill(b, (byte)1);
//    	System.out.println(HexUtil.byteToHex(b));
//    	System.out.println(Arrays.toString(b));
//    	
//		System.out.println("a,b, ,, ".split("[, \t]").length);
//		
//		String outfilefmt = System.getProperty("outfilefmt","*_instant_yyyymmdd_####.txt");
//		String outfile = "/"+outfilefmt.replaceFirst("[*]", "hlrvlr");
//		outfile = outfile.replaceFirst("yyyymmdd", "20090101").replace("####", TextFormat.getDateTime(TextFormat.HHMMSS).subSequence(0, 4));
//		
//		System.out.println(outfilefmt);
//		System.out.println(outfile);
		
		String s120="0000"+Integer.toBinaryString(1);
		System.out.println(s120);
		System.out.println(s120.substring(s120.length()-4,s120.length()));
		System.out.println(Integer.parseInt(s120, 2));
//		char c = 0x90;
//		System.out.println(c);
//		System.out.println((int)c);
//		sleepTest(10);
		
		urlTest();
		
		}catch(Exception e){}
		
	}
	
	static public void urlTest() throws Exception {
		URL url = new URL("http://218.206.176.175:8181/was/0110000001323120715122234001");
		System.out.println(url.getProtocol());
		System.out.println(url.getHost());
		System.out.println(url.getPort());
		System.out.println(url.getPath());
		System.out.println(url.getFile());
	}
	
	static public void sleepTest(long ms){
		while(true){
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				try {
					Thread.currentThread().sleep(ms);
				} catch (InterruptedException e1) {
				}
			}
		}
		
	}

}
