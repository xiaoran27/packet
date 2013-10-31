package com.lj.tools;


import java.io.DataOutputStream;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;

import com.lj.util.logging.Logger;

public class SendSm {
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private String portName;

	  public void Send(String mobile, String msg)
	  {
	    Logger.info("Send Message to " + msg + " Message: " + mobile);
	    Enumeration localEnumeration = CommPortIdentifier.getPortIdentifiers();
	    while (localEnumeration.hasMoreElements())
	    {
	      CommPortIdentifier localCommPortIdentifier = (CommPortIdentifier)localEnumeration.nextElement();
	      if (localCommPortIdentifier.getName().compareToIgnoreCase(this.portName) == 0)
	      {
	        try
	        {
	          SerialPort localSerialPort = (SerialPort)localCommPortIdentifier.open(localCommPortIdentifier.getName(), 2000);
	          DataOutputStream localDataOutputStream = new DataOutputStream(localSerialPort.getOutputStream());
	          String phone = mobile;
	          String mobileLen = getSendMessageLen(mobile);
	          phone = getSendMessage(phone);
	          String str3 = getMobile(msg);
	          String str4 = "AT+CMGF=0\r";
	          localDataOutputStream.write(str4.getBytes());
	          localDataOutputStream.flush();
	          Thread.sleep(1000L);
	          Logger.info("set mode: " + str4);
	          String str5 = "AT+CMGS=0" + mobileLen + "\r";
	          localDataOutputStream.write(str5.getBytes());
	          localDataOutputStream.flush();
	          Logger.info("message's length=" + str5);
	          Thread.sleep(1000L);
	          str5 = "0011000D91" + str3 + "000801" + phone;
	          str5 = str5 + '\26';
	          localDataOutputStream.write(str5.getBytes());
	          localDataOutputStream.flush();
	          Logger.info("message =" + str5);
	          localDataOutputStream.close();
	          localSerialPort.close();
	        }
	        catch (Exception localException)
	        {
	          Logger.error("SendAlarmFragment() - Exception e=" + localException, localException);
	        }
	        return;
	      }
	    }
	  }

	  public static String getSendMessageLen(String paramString)
	  {
	    try
	    {
	      byte[] arrayOfByte = paramString.getBytes("unicode");
	      int i = 14 + arrayOfByte.length - 1;
	      String str1 = Integer.toString(i);
	      i = arrayOfByte.length - 2;
	      paramString = Integer.toHexString(i);
	      if (paramString.length() < 2)
	        paramString = "0" + paramString;
	      int j = 2;
	      while (j < arrayOfByte.length)
	      {
	        String str2 = String.format("%X", new Object[] { Byte.valueOf(arrayOfByte[j]) });
	        if (str2.length() < 2)
	          str2 = "0" + str2;
	        paramString = paramString + str2;
	        str2 = String.format("%X", new Object[] { Byte.valueOf(arrayOfByte[(j + 1)]) });
	        if (str2.length() < 2)
	          str2 = "0" + str2;
	        paramString = paramString + str2;
	        j += 2;
	      }
	      return str1;
	    }
	    catch (Exception localException)
	    {
	      Logger.error("getSendMessageLen() - Exception e=" + localException, localException);
	    }
	    return "0";
	  }

	  public static String getSendMessage(String msg)
	  {
	    try
	    {
	      byte[] arrayOfByte = msg.getBytes("unicode");
	      int i = 14 + arrayOfByte.length - 1;
	      String str1 = Integer.toString(i);
	      i = arrayOfByte.length - 2;
	      String _msg = Integer.toHexString(i);
	      if (_msg.length() < 2)
	        _msg = "0" + _msg;
	      int j = 2;
	      while (j < arrayOfByte.length)
	      {
	        String str2 = String.format("%X", new Object[] { Byte.valueOf(arrayOfByte[(j + 1)]) });
	        if (str2.length() < 2)
	          str2 = "0" + str2;
	        _msg = _msg + str2;
	        str2 = String.format("%X", new Object[] { Byte.valueOf(arrayOfByte[j]) });
	        if (str2.length() < 2)
	          str2 = "0" + str2;
	        _msg = _msg + str2;
	        j += 2;
	      }
	      
	      return _msg;
	    }
	    catch (Exception localException)
	    {
	      Logger.error("getSendMessage() - Exception e=" + localException, localException);
	    }
	    return "0";
	  }
	  
	  public static String convertMessage(String msg)
	  {
		  String[] rtn = {"0","0"};
	    try
	    {
	      byte[] arrayOfByte = msg.getBytes("unicode");
	      int i = 14 + arrayOfByte.length - 1;
	      String str1 = Integer.toString(i);
	      i = arrayOfByte.length - 2;
	      String _msg = Integer.toHexString(i);
	      if (_msg.length() < 2)
	        _msg = "0" + _msg;
	      int j = 2;
	      while (j < arrayOfByte.length)
	      {
	        String str2 = String.format("%X", new Object[] { Byte.valueOf(arrayOfByte[(j + 1)]) });
	        if (str2.length() < 2)
	          str2 = "0" + str2;
	        _msg = _msg + str2;
	        str2 = String.format("%X", new Object[] { Byte.valueOf(arrayOfByte[j]) });
	        if (str2.length() < 2)
	          str2 = "0" + str2;
	        _msg = _msg + str2;
	        j += 2;
	      }
	      
	      return _msg;
	    }
	    catch (Exception localException)
	    {
	      Logger.error("getSendMessage() - Exception e=" + localException, localException);
	    }
	    return "0";
	  }

	  public static String getMobile(String paramString)
	  {
	    String str1 = paramString;
	    String str2 = "68";
	    str2 = str2 + str1.charAt(1);
	    str2 = str2 + str1.charAt(0);
	    str2 = str2 + str1.charAt(3);
	    str2 = str2 + str1.charAt(2);
	    str2 = str2 + str1.charAt(5);
	    str2 = str2 + str1.charAt(4);
	    str2 = str2 + str1.charAt(7);
	    str2 = str2 + str1.charAt(6);
	    str2 = str2 + str1.charAt(9);
	    str2 = str2 + str1.charAt(8);
	    str2 = str2 + "F";
	    str2 = str2 + str1.charAt(10);
	    return str2;
	  }

}
