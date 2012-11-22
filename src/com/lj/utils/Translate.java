/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-8-23 21:00
* M  //防止ArrayIndexOutOfBoundsException
\*************************** END OF CHANGE REPORT HISTORY ********************/


package com.lj.utils;

public class Translate {

	public static void translate2Bcd(String as,byte[] dst) {
		byte[] b = str2bigbcd(as.trim(), false);
		setA_number(b,dst);
	}

	private static byte[] str2bigbcd(String s, boolean padLeft) {
		int len = s.length();
		byte[] d = new byte[(len + 1) >> 1];
		return str2bigbcd(s, padLeft, d, 0);
	}

	private static final void zeroBytes(byte[] b) {
		int n = b.length;
		for (int i = 0; i < n; i++) {
			b[i] = 0;
		}
	}
	
	private static void setA_number(byte[] a_number,byte[] dst) {
		if (a_number != dst) {
			zeroBytes(dst);
			System.arraycopy(a_number, 0, dst, 0, Math.min(a_number.length,dst.length));  //防止ArrayIndexOutOfBoundsException			
		}
	}

    private static byte[] str2bigbcd(String s, boolean padLeft, byte[] d, int offset) {
        int len = s.length();
        int start = (((len & 1) == 1) && padLeft) ? 1 : 0;
        for (int i=start; i < len+start; i++)
        {
        	char c = s.charAt(i-start);
        	if(c >= '0' && c <= '9')
        		d [offset + (i >> 1)] |= (c-'0') << ((i & 1) != 1 ? 0 : 4);
        	if(c >= 'a' && c <= 'f')
        		d [offset + (i >> 1)] |= (c-'a'+ 0x0A) << ((i & 1) != 1 ? 0 : 4);
        	if(c >= 'A' && c <= 'F')
        		d [offset + (i >> 1)] |= (c-'A' + 0x0A) << ((i & 1) != 1 ? 0 : 4);
        }
        return d;
    }

	public static final String byteToHex(byte[] o, int size) {
		StringBuffer toHex = new StringBuffer();
		for (int i = 0; null != o && i < size; i++) {
			String s = Integer.toHexString((int) o[i]).toUpperCase();
			if (s.length() > 6) // 获取byte的HEX只
				s = s.substring(6);
			else if (s.length() == 1)
				s = "0" + s;
			toHex.append(s + " ");
		}
		return toHex.toString();
	}

	public static String translate2Str(byte[] b,int len){
		return bigbcd2Str(b,0,len,false).trim();
	}
		
    private static String bigbcd2Str(byte[] b, int offset,
            int len, boolean padLeft)
	{
		StringBuffer d = new StringBuffer(len);
		int start = (((len & 1) == 1) && padLeft) ? 1 : 0;
		for (int i=start; i < len+start; i++) {
		int shift = ((i & 1) != 1 ? 0 : 4);
		char c = Character.forDigit (
		    ((b[offset+(i>>1)] >> shift) & 0x0F), 16);
		d.append (Character.toUpperCase (c));
		}
		return d.toString();
	}
    
    public static void main(String[] args){
    	byte[] b = Translate.getBcdBytes("1234567890aBcDeF", 8);
    	System.out.println(Translate.byteToHex(b, b.length));
    	b = Translate.getBcdBytes("1234567890123456", 8);
    	System.out.println(Translate.translate2Str(b, 8));
    	System.out.println(Translate.translate2Str(b, 16));
    	
    	byte[] bs = new byte[]{6,18,68,00,53};
    	System.out.println(translate2Str(bs,10));
    }

	public static byte[] getBcdBytes(String s, int len) {
		byte[] dst = new byte[len];
		Translate.translate2Bcd(s, dst);
		return dst;
	}

}
