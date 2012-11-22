
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-9-29
*   create
*   支持-D设置smtp等参数，若发布成功则使用默认的smtp.163.com再发送一次。
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

/**
 * 对一些资源进行监控，若有异常发送mail告警
 *
 * @author $author$
 * @version $Revision: 1.2 $
  */
public class Monitor {
	public static final String tfRegex="((1)|([Yy]|[Yy][Ee][Ss])|([Tt]|([Tt][Rr][Uu][Ee])))";
	
  /**
   * @param args
   */
  public static void main(String[] args) {
	  //checkDL("192.168.1.101", "192.168.1.101", 13800, 13801, new String[]{"formsmtp@163.com"});
	  if (args.length<5){
		  System.out.println("USAGE: java "+ Monitor.class.getName()+ " ip0 ip1 port dbport ccmail0 [ccmail...]");
		  System.out.println("\t ip0 IP0. Sample: 192.168.2.182");
		  System.out.println("\t ip1 IP1. Sample: 192.168.2.183");
		  System.out.println("\t port service port. Sample: 13800");
		  System.out.println("\t dbport DBMS port. Sample: 13801");
		  System.out.println("\t ccmail0 [ccmail...] CC mail address");
		  
		  System.out.println();
		  System.out.println("FOR TEST:");
		  System.out.println("\t send mail: java "+ Monitor.class.getName()+ " onlySendmailTest");
		  if (args.length>0 && args[0].equals("onlySendmailTest")){
			  sendmail("formsmtp@163.com", new String[]{"formsmtp@163.com"}, "mail test at "+new Date(), "This is test mail.\r\n"+new Date());
		  }
	  }else{
		  String[] ccmails = new String[args.length-4];
		  System.arraycopy(args, 4, ccmails, 0, ccmails.length);
		  checkDL(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), ccmails);
	  }
  }
  

  /**
   * 检测DL数据总量是否匹配(误差在1000内)和端口是否正常(主备)
   * 
	 * @param ip0  主备IP0
	 * @param ip1 主备IP1
	 * @param port 业务监听端口
	 * @param dbport 数据库端口
	 * @param ccmails 接收人
	 */
	public static void checkDL(String ip0, String ip1, int port, int dbport, String[] ccmails) {
	  
	  final String tab = "\t\t";
	  final String crlf = "\r\n";
	  final String splitStr = "-----CHECK AT "+new Date();
	  
	  final int DIFF=1000;
	  int[] rtn = {-1,-1,-1,-1,-1,-1,-1,-1};  //twice port , six count
	  
	  rtn[0] = telnet(ip0,port);
	  rtn[1] = telnet(ip1,port);
		  
	  rtn[2] = dbms("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://"+ip0+":"+dbport+"/simndb","sa","","select count(*) from rnmishow");
	  rtn[3] = dbms("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://"+ip1+":"+dbport+"/simndb","sa","","select count(*) from rnmishow");
	  rtn[4] = dbms("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://"+ip0+":"+dbport+"/simndb","sa","","select count(*) from rnmoshow");
	  rtn[5] = dbms("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://"+ip1+":"+dbport+"/simndb","sa","","select count(*) from rnmoshow");
	  rtn[6] = dbms("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://"+ip0+":"+dbport+"/simndb","sa","","select count(*) from rnmoshown");
	  rtn[7] = dbms("org.hsqldb.jdbcDriver","jdbc:hsqldb:hsql://"+ip1+":"+dbport+"/simndb","sa","","select count(*) from rnmoshown");
	  
	  StringBuffer sbSubject = new StringBuffer("SUBJECT:");
	  StringBuffer sbContent = new StringBuffer();
	  sbContent.append(tab).append(ip0).append(tab).append(ip1).append(crlf);
	  boolean porterr = (rtn[0]<0 && rtn[1]<0) 
	  	|| (rtn[0]>=0 && rtn[1]>=0);
	  boolean dataerr =  (Math.abs(rtn[2] - rtn[3]) > DIFF) 
	  	|| (Math.abs(rtn[4] - rtn[5]) > DIFF) 
	  	|| (Math.abs(rtn[6] - rtn[7]) > DIFF);
	  
	  if ( porterr || dataerr ){
		  sbSubject.append("ERR(");
	  }else{
		  sbSubject.append("CHK(");
	  }
	  sbSubject.append("porterr=").append(porterr).append(",dataerr=").append(dataerr);
	  sbSubject.append(") at ").append(new Date());
	  
	  sbContent.append("ACT-SBY").append(tab).append(rtn[0]<0?"STANDBY":"ACTIVE");
	  sbContent.append(tab).append(rtn[1]<0?"STANDBY":"ACTIVE").append(crlf);
	  sbContent.append("COUNT-IN").append(tab).append(rtn[2]).append(tab).append(rtn[3]).append(crlf);
	  sbContent.append("COUNT-OUT0").append(tab).append(rtn[4]).append(tab).append(rtn[5]).append(crlf);
	  sbContent.append("COUNT-OUT1").append(tab).append(rtn[6]).append(tab).append(rtn[7]).append(crlf);
  
	  System.out.println(splitStr);
	  System.out.println(sbSubject);
	  System.out.println("CONTENT:");
	  System.out.println(sbContent);
	  System.out.println();
	  
	  if ( porterr || dataerr ){
		  sbContent.insert(0, crlf).insert(0,splitStr);
		  int t = sendmail("formsmtp@163.com",ccmails,sbSubject.toString(),sbContent.toString());
	  }
	  
  }

  /**
   * 连接数据库,并执行统计SQL,返回统计值. 若有异常,返回小于0的值.
   *
   * @param drivers 数据库驱动类
   * @param url 连接串
   * @param pwd 用户
   * @param countSql 统计SQL
   *
   * @return SQL统计值或异常错误值。-1 - 驱动没找到；-2 - SQL异常；-3 - 其他异常
   */
  static public int dbms(String drivers, String url, String user, String pwd,
    String countSql) {
    int rtn = -1;
    Connection conn = null;
    Statement st = null;
    
    try {
		Class.forName(drivers);
		conn = DriverManager.getConnection(url, user, pwd );
		st = conn.createStatement(); 
		ResultSet rs = st.executeQuery(countSql);
		if (rs.next()){
			rtn = rs.getInt(1);
		}
		st.close();
		conn.close();
	} catch (ClassNotFoundException e) {
		rtn = -1;
		e.printStackTrace();
	} catch (SQLException e) {
		rtn = -2;
		e.printStackTrace();
	} catch (Exception e) {
		rtn = -3;
		e.printStackTrace();
	} finally {
		try {
			if (null!=st){
				st.close();
			}
			if (null!=conn){
				conn.close();
			}
		} catch (SQLException e) {
			// ignore
		}
	}
    
    return rtn;
  }

  /**
   * telnet到指定服务器端口,检测器是否正常.
   *
   * @param ip IP
   * @param port PORT
   * @param msgb 发送的消息。默认是ping:0C 00 00 10 01 04 00 02 00 00 00 00 FF FF
   *
   * @return <0-失败;>=0-成功. 0-连接成功;1-发送消息成功;2-收到应答
   */
  static public int telnet(String ip, int port, byte[] msgb) {
    int rtn = -1;
    final byte[] defMsgb = {0x0c,0x00,0x00,0x10,0x01,0x04,0x00,0x02,0x00,0x00,0x00,0x00,-1,-1};;
    
    SocketChannel sc = null;
    try {
		sc = SocketChannel.open();
		sc.socket().connect(new InetSocketAddress(ip, port), 30*1000);
		sc.socket().setSoTimeout(60*1000);
		sc.configureBlocking(true);
		sc.finishConnect();
		
		ByteBuffer bb = ByteBuffer.allocateDirect(256);
		if (null==msgb){
			msgb = defMsgb;
		}
		bb.put(msgb);
		bb.flip();
		int msgn = sc.write(bb);  //ignore failure
		if (msgn>0){
			rtn=1;
		}
		bb.clear();
		msgn = sc.read(bb);
		if (msgn>0){
			rtn=2;
		}

	} catch (IOException e) {
		rtn = -2;
		e.printStackTrace();
	}  finally {
		try {
			if (null!=sc){
				sc.socket().close();
				sc.close();
			}
		} catch (Exception e) {
			// ignore
		}
	}

    return rtn;
  }
  
  /**
   * telnet到指定服务器端口(发送指定的消息ping),检测器是否正常.
   *
   * @see 
   */
  static public int telnet(String ip, int port) {
	  return telnet(ip,port,null);
  }
  
  /**
   * 发送mail.
   *
 * @param smtp  smtp主机
 * @param smtpAuth 是否需要smtp验证
 * @param user 验证用户
 * @param pwd 验证密码
 * @param sender 发送人
 * @param reciver 接收人
 * @param ccs 抄送人
 * @param subject 主题
 * @param content 内容
 * @param files 附件
 * @return >=0 - success
 */
  static public int sendmail(String smtp, boolean smtpAuth, String user, String pwd
		  ,String sender,String reciver,String[] ccs
		  ,String subject,String content,String[] files) {
	  int rtn = -1;

	  	MultiPartEmail email = new MultiPartEmail();
	  	
		email.setHostName(smtp);
		if (smtpAuth){
			email.setAuthentication(user,pwd);
		}
		try {
			email.setFrom(sender);
			email.addTo(reciver);
			if (null!=ccs){
				for(String cc : ccs){
					email.addCc(cc);
				}
			}
			
			email.setSubject(subject);
			email.setMsg(content);
			//email.attach(attachment);

			String s = email.send();
			
			rtn = 0;
		} catch (EmailException e) {
			rtn = -2;
			e.printStackTrace();
		}
  
	  return rtn;
  }
  
  /**
   * 发送mail(无需指定发送信息).
   *
   * @see 
   */
  static public int sendmail(String reciver,String[] ccs
		  ,String subject,String content,String[] files) {
	  String smtphost = System.getProperty("smtphost", "smtp.163.com");
	  String sender = System.getProperty("sender", "formsmtp@163.com");
	  boolean smtpauth = System.getProperty("smtpauth", "1").matches(tfRegex);
	  String user = System.getProperty("user", "formsmtp@163.com");
	  String pwd = System.getProperty("pwd", "Xiaoran27");
	  int rtn = sendmail(smtphost, smtpauth, user, pwd
			  ,sender,reciver,ccs
			  , subject, content, files);
	  if (rtn<0){
		  rtn = sendmail("smtp.163.com", smtpauth, "formsmtp@163.com", "Xiaoran27"
				  ,"formsmtp@163.com",reciver,ccs
				  , subject, content, files);
	  }
	  return rtn;
  }
  
  /**
   * 发送不带附件的mail(无需指定发送信息).
   *
   * @see 
   */
  static public int sendmail(String reciver,String[] ccs,String subject,String content) {
	  return sendmail(reciver, ccs, subject, content, null);
  }
}
