
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2008-8-7
*   create
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import org.apache.log4j.Logger;

/*
中国移动2G网内GT号码类型分为E.212、E.214、E.164共三种,其中,
E.212号码形式为"MCC+MNC+MSIN” ,用于移动用户进行位置登记；
E.214号码形式为"CC+NDC+MSIN” ,用于移动用户进行位置登记；
E.164号码形式为"MSISDN”、"移动网元ID地址” ,用于移动用户做被叫、各类移动网元之间的通信。
MSISDN	IMSI（E.212）	E.214
13SH0H1H2H3＋ABCD 
（H0为0,S＝5～9）	460+00+H1H2H3+S+xxxxxx	86 139 MSIN
13SH0H1H2H3＋ABCD 
（H0不为0,S＝5～9）	460+00+H1H2H3+R+H0+xxxxx (R=9-S)	86 139 MSIN
1340~1348+H1H2H3+ABCD	460+02+0+H0(0-8)+H1H2H3+xxxxx	86 138 MSIN
159+H0H1H2H3＋ABCD	460+02+9+H0H1H2H3+xxxxx	86 138 MSIN
158+H0H1H2H3＋ABCD	460+02+8+H0H1H2H3+xxxxx	86 138 MSIN
150+H0H1H2H3＋ABCD	460+02+3+H0H1H2H3+xxxxx	86 138 MSIN
151+H0H1H2H3＋ABCD	460+02+1+H0H1H2H3+xxxxx	86 138 MSIN
152+H0H1H2H3＋ABCD	460+02+2+H0H1H2H3+xxxxx	86 138 MSIN
187+H0H1H2H3＋ABCD	460+02+7+H0H1H2H3+xxxxx	86 138 MSIN
注：MSIN为IMSI的后10位。XXXXX（X）为MSISDN号码中ABCD扰码得到,扰码方法由各省自定义。
中国移动TD试验网内GT号码类型分类和具体作用与中国移动2G网络相同。
MSISDN	IMSI（E.212）	E.214
157+H0H1H2H3＋ABCD	460+07+7+H0H1H2H3+xxxxx	86 157 MSIN
188+H0H1H2H3＋ABCD	460+07+8+H0H1H2H3+xxxxx	86 157 MSIN
147+H0H1H2H3＋ABCD	460+07+9+H0H1H2H3+xxxxx	86 157MSIN
10648+H0H1H2H3＋ABCD	460+07+5+H0H1H2H3+xxxxx	86 157MSIN
注：MSIN为IMSI的后10位。XXXXX为MSISDN号码中ABCD扰码得到,扰码方法由各省自定义。
 */


/**
 * e214,e212,e164码制转换。
 *
 * @author xiaoran27 $author$
 * @version $Revision: 1.3 $
  */
public class EConvertUtil {
	
	public static int E212 = 212;
	public static int E214 = 214;
	public static int E164 = 164;
	
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(EConvertUtil.class);

  
  public static String imsi2msisdn(String srcGt) {
	  return e212to164(srcGt);
  }
 
  public static String e214to212(String srcGt) {
	  return eConvert(srcGt, E214, E212 );
  }
  
  public static String e212to214(String srcGt) {
	  return eConvert(srcGt, E212, E214 );
  }
  
  public static String e164to212(String srcGt) {
	  return eConvert(srcGt, E164, E212 );
  }
  
  public static String e212to164(String srcGt) {
	  return eConvert(srcGt, E212, E164 );
  }
  
  public static String e214to164(String srcGt) {
	  return eConvert(srcGt, E214, E164 );
  }
  
  public static String e164to214(String srcGt) {
	  return eConvert(srcGt, E164, E214 );
  }

  /**
   * E214,E212,E164号码相互转换.
   * 
 * @param srcGt  源GT
 * @param a 源GT编码方式(E214,E212,E164)
 * @param b 目的GT编码方式(E214,E212,E164)
 * @return 目的GT
 */
  public static String eConvert(String srcGt, int a, int b ) {
	 
	  if (a==b){
		  return srcGt;
	  }
	  
	  String desGt = null;
	  if (a==E214 && b==E212){
		  if ("86139".equals(srcGt.substring(0, 5))){
			  desGt="46000"+srcGt.substring(5);
		  }else if ("86138".equals(srcGt.substring(0, 5))){
			  desGt="46002"+srcGt.substring(5);
		  }else if ("86157".equals(srcGt.substring(0, 5))){
			  desGt="46007"+srcGt.substring(5);
		  }
	  }else if (a==E212 && b==E214){
		  if ("46000".equals(srcGt.substring(0, 5))){
			  desGt="86139"+srcGt.substring(5);
		  }else if ("46002".equals(srcGt.substring(0, 5))){
			  desGt="86138"+srcGt.substring(5);
		  }else if ("46007".equals(srcGt.substring(0, 5))){
			  desGt="86157"+srcGt.substring(5);
		  }
	  }else if (a==E164 && b==E212){
		  
	  }else if (a==E212 && b==E164){

		//根据imsi，取得msisdn的前六位，形如：1340H1H2
			String msisdn = "00000000000";
			String left = "0000";
			String imsi = srcGt;
			if (imsi.substring(4, 5).equals("0")) {
				msisdn = "13";
				String s = imsi.substring(8, 9);
				String hx = imsi.substring(5, 8);
				if (Integer.parseInt(s) >= 5) {
					msisdn += s + "0" + hx + left;
				} else {
					s = String.valueOf(9 - Integer.parseInt(s));
					String h0 = imsi.substring(9, 10);
					msisdn += s + h0 + hx + left;
				}
			} else if (imsi.subSequence(4, 5).equals("2")) {
				String s = imsi.substring(5, 6);
				String hx = imsi.substring(6, 10);
				if (s.equals("9")) {
					msisdn = "159";
				} else if (s.equals("8")) {
					msisdn = "158";
				} else if (s.equals("3")) {
					msisdn = "150";
				} else if (s.equals("0")) {
					msisdn = "134";
				} else if (s.equals("1")) {
					msisdn = "151";
				} else if (s.equals("2")) {
					msisdn = "152";
				} else if (s.equals("7")) {
					msisdn = "187";
				}
				msisdn += hx + left;
			} else if (imsi.subSequence(4, 5).equals("7")) {
				String flag = imsi.substring(5, 6);
				if (flag.equals("9")) {
					msisdn = "147";
				} else if (flag.equals("8")) {
					msisdn = "188";
				} else if (flag.equals("7")) {
					msisdn = "157";
				} else if (flag.equals("5")) {
					msisdn = "10648";
				}
				String hx = imsi.substring(6, 10);
				msisdn += hx + left;
			} else {
				logger.error(" invalidate imsi :" + imsi);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("  imsi : " + imsi + "  the msisdn :" + msisdn);
			}
			desGt = msisdn;

	  }else if (a==E214 && b==E164){
		  //214->212->164
		  desGt = eConvert(srcGt,a,E212);
		  desGt = eConvert(srcGt,E212,b);
	  }else if (a==E164 && b==E214){
	  }else {
		 ;//not support 
	  }
	  
	  if (desGt==null) logger.error(a+"->"+b+" - invalidate srcGt :" + srcGt);
	  
	  return desGt;
  }
  
  
  private static void eConvertAndPrint(String title,String e212,String e214,String e164){
	  System.out.println();
	  System.out.println(title);
	  
	  System.out.println("Orig data:");
	  System.out.println("\te212="+e212);
	  System.out.println("\te214="+e214);
	  System.out.println("\te164="+e164);
	  
	  System.out.println("Cconvert result:");
	  System.out.println("\te212->e214: "+e212+"->"+e212to214(e212));
	  System.out.println("\te212->e164: "+e212+"->"+e212to164(e212));
	  System.out.println("\te214->e212: "+e214+"->"+e214to212(e214));
	  System.out.println("\te214->e164: "+e214+"->"+e214to164(e214));
	  System.out.println("\te164->e212: "+e164+"->"+e164to212(e164));
	  System.out.println("\te164->e214: "+e164+"->"+e164to214(e164));
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
	  
//	  E.212号码形式为"MCC+MNC+MSIN”
//	  E.214号码形式为"CC+NDC+MSIN”
//	  E.164号码形式为"MSISDN”
//	  MSIN为IMSI的后10位。XXXXX（X）为MSISDN号码中ABCD扰码得到
	  
	  String e212,e214,e164;
		 
//	  MSISDN	IMSI（E.212）	E.214
//	  13SH0H1H2H3＋ABCD 
//	  （H0为0,S＝5～9）	460+00+H1H2H3+S+xxxxxx	86 139 MSIN
	  String title="13SH0H1H2H3＋ABCD（H0为0,S＝5～9）	460+00+H1H2H3+S+xxxxxx	86 139 MSIN";
	  String MCC="460",MNC="00",CC="86",NDC="139";
	  String H1H2H3="123",H0="0",H0H1H2H3=H0+H1H2H3;
	  String ABCD="9876";
	  String xxxxx=ABCD+"5",xxxxxx=xxxxx+"4";
	  String S,R;
	  String MSIN,MSISDN,MSISDN01="13",MSISDN012;
	  for(int s=5; s<=9; s++){
		  S=""+s;
		  e212=MCC+MNC+H1H2H3+S+xxxxxx;
		  MSIN=e212.substring(5);
		  e214=CC+NDC+MSIN;
		  MSISDN012=MSISDN01+S;
		  e164=MSISDN012+H0H1H2H3+ABCD;
		  eConvertAndPrint(title,e212,e214,e164);
	  }
	  
//	  13SH0H1H2H3＋ABCD 
//	  （H0不为0,S＝5～9）	460+00+H1H2H3+R+H0+xxxxx (R=9-S)	86 139 MSIN
	  title="13SH0H1H2H3＋ABCD（H0不为0,S＝5～9）	460+00+H1H2H3+R+H0+xxxxx (R=9-S)	86 139 MSIN";
	  for(int s=5; s<=9; s++){
		  S=""+s;
		  R=""+(9-s);
		  e212=MCC+MNC+H1H2H3+R+H0+xxxxx;
		  MSIN=e212.substring(5);
		  e214=CC+NDC+MSIN;
		  MSISDN012=MSISDN01+S;
		  e164=MSISDN012+H0H1H2H3+ABCD;
		  eConvertAndPrint(title,e212,e214,e164);
	  }
	  
//	  1340~1348+H1H2H3+ABCD	460+02+0+H0H1H2H3+xxxxx	86 138 MSIN
	  title="1340~1348+H1H2H3+ABCD	460+02+0+H0(0-8)+H1H2H3+xxxxx	86 138 MSIN";
	  MNC="02";
	  NDC="138";
	  MSISDN012=MSISDN01+"4";
	  String o="0";
	  for(int s=0; s<=8; s++){
		  H0=""+s;
		  e212=MCC+MNC+o+H0+H1H2H3+xxxxx;
		  MSIN=e212.substring(5);
		  e214=CC+NDC+MSIN;
		  e164=MSISDN012+s+H1H2H3+ABCD;
		  eConvertAndPrint(title,e212,e214,e164);
	  }
	  
//	  159+H0H1H2H3＋ABCD	460+02+9+H0H1H2H3+xxxxx	86 138 MSIN
	  title="159+H0H1H2H3＋ABCD	460+02+9+H0H1H2H3+xxxxx	86 138 MSIN";
	  MNC="02";
	  NDC="138";
	  o="9";
	  MSISDN012="159";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  158+H0H1H2H3＋ABCD	460+02+8+H0H1H2H3+xxxxx	86 138 MSIN
	  title="158+H0H1H2H3＋ABCD	460+02+8+H0H1H2H3+xxxxx	86 138 MSIN";
	  MNC="02";
	  NDC="138";
	  o="8";
	  MSISDN012="158";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  150+H0H1H2H3＋ABCD	460+02+3+H0H1H2H3+xxxxx	86 138 MSIN
	  title="150+H0H1H2H3＋ABCD	460+02+3+H0H1H2H3+xxxxx	86 138 MSIN";
	  MNC="02";
	  NDC="138";
	  o="3";
	  MSISDN012="150";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  151+H0H1H2H3＋ABCD	460+02+1+H0H1H2H3+xxxxx	86 138 MSIN
	  title="151+H0H1H2H3＋ABCD	460+02+1+H0H1H2H3+xxxxx	86 138 MSIN";
	  MNC="02";
	  NDC="138";
	  o="1";
	  MSISDN012="151";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  152+H0H1H2H3＋ABCD	460+02+2+H0H1H2H3+xxxxx	86 138 MSIN
	  title="152+H0H1H2H3＋ABCD	460+02+2+H0H1H2H3+xxxxx	86 138 MSIN";
	  MNC="02";
	  NDC="138";
	  o="2";
	  MSISDN012="152";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  187+H0H1H2H3＋ABCD	460+02+7+H0H1H2H3+xxxxx	86 138 MSIN
	  title="187+H0H1H2H3＋ABCD	460+02+7+H0H1H2H3+xxxxx	86 138 MSIN";
	  MNC="02";
	  NDC="138";
	  o="7";
	  MSISDN012="187";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  157+H0H1H2H3＋ABCD	460+07+7+H0H1H2H3+xxxxx	86 157 MSIN
	  title="157+H0H1H2H3＋ABCD	460+07+7+H0H1H2H3+xxxxx	86 157 MSIN";
	  MNC="07";
	  NDC="157";
	  o="7";
	  MSISDN012="157";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  188+H0H1H2H3＋ABCD	460+07+8+H0H1H2H3+xxxxx	86 157 MSIN
	  title="188+H0H1H2H3＋ABCD	460+07+8+H0H1H2H3+xxxxx	86 157 MSIN";
	  MNC="07";
	  NDC="157";
	  o="8";
	  MSISDN012="188";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  147+H0H1H2H3＋ABCD	460+07+9+H0H1H2H3+xxxxx	86 157MSIN
	  title="147+H0H1H2H3＋ABCD	460+07+9+H0H1H2H3+xxxxx	86 157MSIN";
	  MNC="07";
	  NDC="157";
	  o="9";
	  MSISDN012="147";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
//	  10648+H0H1H2H3＋ABCD	460+07+5+H0H1H2H3+xxxxx	86 157MSIN
	  title="10648+H0H1H2H3＋ABCD	460+07+5+H0H1H2H3+xxxxx	86 157MSIN";
	  MNC="07";
	  NDC="157";
	  o="5";
	  MSISDN012="10648";
	  e212=MCC+MNC+o+H0H1H2H3+xxxxx;
	  MSIN=e212.substring(5);
	  e214=CC+NDC+MSIN;
	  e164=MSISDN012+H0H1H2H3+ABCD;
	  eConvertAndPrint(title,e212,e214,e164);
	  
  }
  
}
