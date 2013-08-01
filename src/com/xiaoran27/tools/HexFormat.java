
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2012-5-4
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-7-29
* + bytes2str(byte[] bytes, boolean space)
*-----------------------------------------------------------------------------*
* V,xiaoran27,2013-8-1
* + bytes2str(byte[] bytes, 。。。, boolean space)
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.xiaoran27.tools;

import java.nio.ByteBuffer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class HexFormat {
	
	/**
	 * 字节数组转为int.
	 * 
	 * @param bytes - 字节数组(length>=4)
	 * @param pos - 开始位置
	 * @return 数字
	 */
	static public  int bytes2int(byte[] bytes, int pos){
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(bytes,pos,4);
		bb.flip();
		
		return  bb.getInt();
	}
	
	/**
	 * 字节数组转为int.
	 * 
	 * @param bytes - 字节数组(length>=4)
	 * @param pos - 开始位置
	 * @return 数字
	 */
	static public  int bigbytes2int(byte[] bytes, int pos){
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(bytes[pos+3]);
		bb.put(bytes[pos+2]);
		bb.put(bytes[pos+1]);
		bb.put(bytes[pos+0]);
		bb.flip();
		
		return  bb.getInt();
	}
	
  /**
   * 将16进制串格式化为16byte一行的串.
   * 如：
   * 原串： 83 64 fe 0b 01 fe 0b 0b 09 80 03 0f 1a 0c 12 07 00 12 04 68 
   * 格式化后：
   * 000000 83 64 fe 0b 01 fe 0b 0b 09 80 03 0f 1a 0c 12 07 
   * 000010 00 12 04 68 
   * 
   * @param rawMsu  带有空格的16进制串。如：83 64 fe 0b 01 fe 0b 0b
   *
   * @return 格式化后的串
   */
  public static String rawMsuFormat(final String rawMsu) {
	assert rawMsu != null:"rawMsu is NULL.";
    assert rawMsu.length() > 0:"rawMsu.length() is 0.";
    
    if (rawMsu == null || rawMsu.length() == 0){
    	return "";
    }

    StringBuilder sb = new StringBuilder();
    int len = rawMsu.length();

    for (int i = 0; (i * 48) < len; i++) {
      sb.append(String.format("%1$06X ", i * 16))
        .append(rawMsu.substring(i * 48,
          (((i * 48) + 48) < len) ? ((i * 48) + 48) : len)).append("\r\n");
    }

    return sb.toString();
  }
  
	//hexStr="01 23 45 67 89 ab cd ef"
  public static  byte[] str2bytes(String hexStr){
	  
		String hex = hexStr.replaceAll(" ", "");
		byte[] b = new byte[hex.length()/2];
		for (int i=0; i<b.length; i++){
			b[i]=(byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);
		}
		
		return b;
	}
  
	//{0,a,a0,f,f0,ff} -> "00 0a a0 0f f0 ff"
  public static  String bytes2str(byte[] bytes, boolean space){
		
		StringBuilder toHex = new StringBuilder();

	    for (int i = 0; (null != bytes) && (i < bytes.length); i++) {
	      if (space) {
	          toHex.append(' ');
	      }

	      char hi = Character.forDigit((bytes[i] >> 4) & 0x0F, 16);
	      char lo = Character.forDigit(bytes[i] & 0x0F, 16);
	      toHex.append(Character.toUpperCase(hi));
	      toHex.append(Character.toUpperCase(lo));

	    }

	    return toHex.toString();
	}
  
	//{0,a,a0,f,f0,ff} -> "00 0a a0 0f f0 ff"
  public static  String bytes2str(byte[] bytes, int pos, int len, boolean space){
		
		StringBuilder toHex = new StringBuilder();

	    for (int i = pos; i < Math.min(pos+len,bytes.length); i++) {
	      if (space) {
	          toHex.append(' ');
	      }

	      char hi = Character.forDigit((bytes[i] >> 4) & 0x0F, 16);
	      char lo = Character.forDigit(bytes[i] & 0x0F, 16);
	      toHex.append(Character.toUpperCase(hi));
	      toHex.append(Character.toUpperCase(lo));

	    }

	    return toHex.toString();
	}

  /**
   * @param args
   */
  public static void main(String[] args) {
    String h = rawMsuFormat("");
//    h = rawMsuFormat("");
    h = rawMsuFormat(
        "83 64 fe 0b 01 fe 0b 0b 09 80 03 0f 1a 0c 12 07 00 12 04 68 31 47 13 86 10 00 0b 12 06 00 12 04 68 31 36 12 02 00 bb 62 81 b8 48 04 4f 00 01 a2 6b 1e 28 1c 06 07 00 11 86 05 01 01 01 a0 11 60 0f 80 02 07 80 a1 09 06 07 04 00 00 01 00 10 03 6c 80 a1 81 8b 02 01 01 02 01 08 30 80 80 08 64 00 97 41 15 11 87 f2 a1 80 82 01 17 82 01 11 82 01 12 82 01 13 82 01 14 82 01 15 82 01 16 82 01 1a 82 01 1c 82 01 1d 82 01 1e 83 01 61 83 01 62 83 01 d1 00 00 a2 80 04 01 21 04 01 29 04 01 2a 04 01 41 04 01 42 04 01 51 04 01 71 04 01 72 04 01 94 04 01 11 04 01 12 04 01 13 04 01 14 04 01 a1 04 01 31 04 01 b1 04 01 b2 04 01 b3 04 01 b4 04 01 c1 04 01 c2 04 01 c3 04 01 b5 00 00 00 00 00 00 ");
    
    System.out.println(h);
  }
}
