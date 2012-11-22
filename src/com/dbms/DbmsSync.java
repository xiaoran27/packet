/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-2-11
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-12-7
* M //#作为首列值，后面某个列(仅某一列)分行
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.dbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.lj.utils.Proxool;

/**
 * 两数据库表数据同步类。
 * 
 * @author wuxr
 *
 */
public class DbmsSync {

	
	/**
	 * 从源数据库中按取数据的sql读取数据，并按插入数据的sql序写入目的数据库.
	 * 注意：取数据的sql要与插入数据的sql的?顺序一致
	 * 
	 * @param proxoolPath  proxool配置文件名
	 * @param fromDb  源数据库别名
	 * @param selectSql  取数据的sql
	 * @param toDb  目的数据库别名
	 * @param insertSql 插入数据的sql(含?)
	 * @return 错误原因,若是数字表示实际成功的个数。
	 */
	public static String sync(String proxoolPath, String fromDb, String selectSql, String toDb, String insertSql ) {
		String err = "";
		
		try {
			if (null==proxoolPath || proxoolPath.length()<1){
				Proxool.init();
			}else{
				Proxool.init(proxoolPath);
			}
			
			
			Connection fromConn = Proxool.getConnection(fromDb);
			Statement fromSt = fromConn.createStatement();
			ResultSet rs = fromSt.executeQuery(selectSql);
			if (rs.next()){
				Connection toConn = Proxool.getConnection(toDb);
				boolean autoCommit = toConn.getAutoCommit();
				toConn.setAutoCommit(false);
				PreparedStatement pst = toConn.prepareStatement(insertSql);
				int fromColCount = rs.getMetaData().getColumnCount();
				int toColCount = insertSql.split("[?]").length-1;
				int rows = 0;
				do {
					String col1value = String.valueOf(rs.getObject(1));
					if ("#".equals(col1value)){  //#作为首列值，后面某个列(仅某一列)分行
						//后面有列需要分行; k1#v11,v12,v13#k2#v21,v22,v23...
						String[] kvs = null;
						int pos = -1;
						for(int i=2; ; i++){
							if (i<=fromColCount && i-1<=toColCount){
								String cval = String.valueOf(rs.getObject(i));
								if (pos < 0){
									kvs = cval.split("[#]");
									if (kvs.length>1){
										pos = i-1;  //目前仅支持一个这种格式的列
									}
								}
								pst.setObject(i-1, rs.getObject(i));  //fromColCount=toColCount+1
								
							} else if (i-1<=toColCount){
								pst.setObject(i-1, null);
							} else{
								break;
							}
						}
						if (pos>-1){
							pst.addBatch();
							rows ++;
							for (int i=1; i<kvs.length; i=i+2){
								String[] vals = kvs[i].split("[,;]");
								for (int j=0; j<vals.length; j++){
									pst.setObject(pos, (kvs[i-1]+vals[j]).trim());
									pst.addBatch();
									rows ++;
								}
							}
						}else{
							pst.addBatch();
							rows ++;
						}
					}else{
						for(int i=1; ; i++){
							if (i<=fromColCount && i<=toColCount){
								pst.setObject(i, rs.getObject(i));
							} else if (i<=toColCount){
								pst.setObject(i, null);
							} else{
								break;
							}
						}
						pst.addBatch();
						rows ++;
					}
					
					if (rows%1000 == 0){
						int[] rtns = pst.executeBatch();
						toConn.commit();
						pst.clearBatch();
					}
				}while(rs.next());
				if (rows%1000 > 0){
					int[] rtns = pst.executeBatch();
					toConn.commit();
					pst.clearBatch();
				}
				
				pst.close();
				toConn.setAutoCommit(autoCommit);
				toConn.close();
				
				rs.close();
				fromSt.close();
				fromConn.close();
				
				err = String.valueOf(rows);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			err = e.getMessage();
		}
		
		return err;
	}
	
	public static String sync(String fromDb, String selectSql, String toDb, String insertSql ) {
		return sync( "", fromDb, selectSql, toDb, insertSql );
	}
	
	
	private static void usage(){
		System.out.println("Usage:");
		System.out.println("\tjava "+DbmsSync.class.getName()+" [proxoolPath] fromDb selectSql toDb insertSql");
		System.out.println("\tproxoolPath - proxool configure file");
		System.out.println("\tfromDb - source dbalias from proxool");
		System.out.println("\tselectSql - a select SQL");
		System.out.println("\ttoDb - destination dbalias from proxool");
		System.out.println("\tinsertSql - a insert SQL");
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
/*		java com.lj.DbmsSync 
		"mcsindb" "select provinceid, msisdnbeg, msisdnend, getdate() from MsisdnProvince " "MDB" "insert into hlr (crcodestate,   msisdnb,   msisdne, ctime) values(?, ?, ?, ?)" 
		"mcsexpodb" "select '#', operid, opername, countryid, countryname, statname, countryid+'#'+ndc+'#'+mcc+'#'+mnc, getdate() from Countryoper " "MDB" "insert into Countryoper (operid, opername, countryid, countryname, statname, ccndcormccmnc, ctime) values(?, ?, ?, ?, ?, ?, ?)" 

*/		
		if (args.length<4){
			usage();
			return;
		}else if (args.length>4){
			String err = sync(args[0],args[1],args[2],args[3],args[4]);
			System.out.println(err);
		}else{
			String err = sync(args[0],args[1],args[2],args[3]);;
			System.out.println(err);
		}
		
	}

}
