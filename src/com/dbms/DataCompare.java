/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2009-1-6
*   create
*-----------------------------------------------------------------------------*
* V,Xiaoran27 ,2009-1-7/8
* + implements
*-----------------------------------------------------------------------------*
* V,Xiaoran27 ,2012-4-6
* M //支持86
* M //相同改modificator='sync'
*-----------------------------------------------------------------------------*
* V,Xiaoran27 ,2012-4-11
* M //ONLY: modificator='sync'
*     RNMOSHOW&RNMOSHOWN任一个相同则另一个改modificator='sync'
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.dbms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lj.utils.UuidGenerator;

/**
 * 数据比对,数据同步。
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public final class DataCompare {

	private static String[] compSqls = {
			//from ViewRnmoshown
			"select * from ViewRnmoshown where imsi=? or msisdn0=? or msisdn1=?",
			
			//from rnmoshow,rnmoshown
			"select uuid,imsi,msisdn0 from rnmoshow where imsi=?",
			"select uuid,imsi,msisdn0  from rnmoshow where msisdn0=?",
			"select uuid as uuid2,rosuuid,msisdn1,cc1 from rnmoshown where rosuuid=? and cc1='853'",  //many
			
			"select uuid as uuid2,rosuuid,msisdn1,cc1 from rnmoshown where msisdn1=? and cc1='853'",
			"select uuid,imsi,msisdn0 from rnmoshow where uuid=?"
		
	};
	
	private static String[] syncSqls = new String[]{
			//000
			" insert into  rnmoshow  ( uuid ,imsi ,msisdn0 ,servtype ,state "
			+ "  ,city ,daylmtfee ,sumlmtfee ,msisdn1 ,cc1 "
			+ "  ,crcode1 ,vlddtb1 ,vlddte1 ,reserve1 ,status1 "
			+ "  ,mscid ,vlrid ,hlrid ,status ,creator "
			+ "  ,ctime ,modificator ,mtime  ) "
			+ " values (  ':uuid', ':imsi', ':msisdn0', 1, '2000'"
			+ "  , '0200', 0, 0, ':msisdn0', '86'"
			+ "  , 'CMCC', '2008-12-31 23:59:59.999', '2099-12-31 23:59:59.0', null , '0'"
			+ "  , null , null , null , '0', 'sync' "
			+ "  , '2008-12-31 23:59:59.999', 'sync' , '2008-12-31 23:59:59.999' )" ,
			
			//XX0 not exists
			" insert into  rnmoshown  ( uuid ,rosuuid ,servtype ,daylmtfee ,sumlmtfee "
			+ "  ,msisdn1 ,cc1 ,crcode1 ,vlddtb1 ,vlddte1 "
			+ "  ,reserve1 ,status1 ,hlrid ,status ,creator "
			+ "  ,ctime ,modificator ,mtime  ) "
			+ " values (  ':uuid', ':rosuuid', 1, 0, 0"
			+ "  , ':msisdn1', '853', 'MACCT', '2008-12-31 23:59:59.999', '2099-12-31 23:59:59.0'"
			+ "  , null , '0', null , '0', 'sync' "
			+ "  , '2008-12-31 23:59:59.999', 'sync' , '2008-12-31 23:59:59.999' )" ,
			
			//001,010,011,100,101
			"update rnmoshow set imsi=':imsi', msisdn0=':msisdn0', msisdn1=':msisdn0', modificator='sync', mtime='2008-12-31 23:59:59.999' where uuid=':uuid'",
			
			//010,100,110 exists
			"update rnmoshown set msisdn1=':msisdn1', modificator='sync', mtime='2008-12-31 23:59:59.999' where uuid=':uuid'",
			
			//110,111
			"update rnmoshow set modificator='sync' where imsi=':imsi'" , //相同改modificator='sync'
			"update rnmoshown set modificator='sync' where rosuuid=':rosuuid'" , //相同改modificator='sync'
			
	};
	

	/**
	 * 把csv的数据与DBMS进行比较，结果存放在{csvfile}.comp中。
	 * 
	 * @param String csvfile CSV by ','; result is {csvfile}.comp.
	 *  format: IMSI,MSISDN0,MSISDN1
	 * @param String dbAlias
	 *            database alias
	 */
	public static int csvCompareDbms(String csvfile, String dbAlias) {
		int count = 0;

		BufferedReader br = null;
		BufferedWriter bw = null;
		PreparedStatement[] psts = null;
		try {
			if (!new File(csvfile).exists()) {
				System.out.println("Not found " + csvfile); //
				return -1;
			}
			
			bw = new BufferedWriter(new FileWriter(csvfile + ".comp"));
			br = new BufferedReader(new FileReader(csvfile));
			String line = br.readLine();
			
			long old = System.currentTimeMillis();
			System.out.println("compare start ... "+count+"; Etablished time(ms): " + (System.currentTimeMillis() - old));
			old = System.currentTimeMillis();
			
			while ((null != line) && (line.trim().length() > 0)) {
				count++;

				if (line.startsWith("--")) {
					bw.write(line);
					bw.newLine();
					
					line = br.readLine();
					continue;
				}
				String[] lineData = line.split(","); // imsi,msisdn0,msisdn1
				lineData[1] = lineData[1].startsWith("86")?lineData[1]:"86"+lineData[1]; //支持86

				// get data from DBMS
				String[] dbmsData = new String[]{"uuid","imsi","msisdn0","uuid2","rosuuid","msisdn1","cc1"};
				try {
					if (null==psts){
						psts = getPreparedStatements(compSqls, dbAlias);
					}
					dbmsData = getDataFromDbms(lineData, psts);
				} catch (SQLException e) {
					System.out.println("getData SQLException(REDO): "+Arrays.toString(lineData));
					
					psts = getPreparedStatements(compSqls, dbAlias);
					dbmsData = getDataFromDbms(lineData, psts);
				}

				//compare
				boolean same0 = lineData[0].equals(dbmsData[1]);
				boolean same1 = lineData[1].equals(dbmsData[2]);
				boolean same2 = lineData[2].equals(dbmsData[5]);
				boolean same = same0 && same1 && same2;
				StringBuilder sbOut = new StringBuilder();
				sbOut.append(line);
				if (!same) {
					sbOut.append(";");
					sbOut.append(dbmsData[1]).append(",");
					sbOut.append(dbmsData[2]).append(",");
					sbOut.append(dbmsData[5]).append(",");
					sbOut.append(dbmsData[0]).append(",");
					sbOut.append(dbmsData[3]).append(",");
					sbOut.append(same0?"1":"0");
					sbOut.append(same1?"1":"0");
					sbOut.append(same2?"1":"0");
				}
				
				bw.write(sbOut.toString());
				bw.newLine();
				
				if ( 0==count%1000 ){
					bw.flush();
					
					System.out.println("compare continue ... "+count+"; Etablished time(ms): " + (System.currentTimeMillis() - old));
					old = System.currentTimeMillis();
				}

				/* read next */
				line = br.readLine();

			}

			bw.flush();
			
			System.out.println("compare finished ... "+count+"; Etablished time(ms): " + (System.currentTimeMillis() - old));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != psts) {
				try {
					releasePreparedStatements(psts);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return count;
	}
	

	/**
	 * 把{csvfile}.comp的数据与DBMS进行同步，结果存放在{csvfile}.sync中。
	 * 
	 * @param csvfile
	 *            CSV by ','; result file is {csvfile}.sync.
	 *            format: IMSI,MSISDN0,MSISDN1;IMSI,MSISDN0,MSISDN1,UUID,UUID2,FLAG
	 * @param dbAlias
	 *            database alias
	 */
	public static int csvSyncDbms(String csvfile, String dbAlias) {
		int count = 0;

		BufferedReader br = null;
		BufferedWriter bw = null;
		PreparedStatement[] psts = null;
		Statement st = null;
		try {
			if (!new File(csvfile).exists()) {
				System.out.println("Not found " + csvfile); //
				return -1;
			}
			
			bw = new BufferedWriter(new FileWriter(csvfile + ".sync"));
			br = new BufferedReader(new FileReader(csvfile));
			String line = br.readLine();//IMSI,MSISDN0,MSISDN1,IMSI,MSISDN0,MSISDN1,UUID,UUID2,FLAG
			
			long old = System.currentTimeMillis();
			System.out.println("sync start ... "+count+"; Etablished time(ms): " + (System.currentTimeMillis() - old));
			old = System.currentTimeMillis();
			
			while ((null != line) && (line.trim().length() > 0)) {
				count++;

				if (line.startsWith("--")) {  // || line.indexOf(';')<0
					bw.write(line);
					bw.newLine();
					
					line = br.readLine();
					continue;
				}
				String[] lineData = line.replace(';', ',').split(",");//IMSI,MSISDN0,MSISDN1,IMSI,MSISDN0,MSISDN1,UUID,UUID2,FLAG
				lineData[1] = lineData[1].startsWith("86")?lineData[1]:"86"+lineData[1]; //支持86
				
				if (lineData.length<9){ //防止仅有前3个号码
					String[] lineData9 = {lineData[0],lineData[1],lineData[2],lineData[0],lineData[1],lineData[2],"UUID","UUID2","111"};
					lineData = lineData9;
				}				

				// get data from DBMS
				String[] dbmsData = new String[]{"uuid","imsi","msisdn0","uuid2","rosuuid","msisdn1","cc1"};
				try {
					if (null==psts){
						psts = getPreparedStatements(compSqls, dbAlias);
					}
					dbmsData = getDataFromDbms(lineData, psts);
				} catch (SQLException e) {
					System.out.println("sync(getData) SQLException(REDO): "+Arrays.toString(lineData));
					
					psts = getPreparedStatements(compSqls, dbAlias);
					dbmsData = getDataFromDbms(lineData, psts);
				}

				//compare
				boolean same0 = lineData[0].equals(dbmsData[1]);
				boolean same1 = lineData[1].equals(dbmsData[2]);
				boolean same2 = lineData[2].equals(dbmsData[5]);
				
				int[] rslt = {-1,-1};
//				if (same0 && same1 && same2){
//					rslt = new int[]{2};
//				}else{
					//new value
					lineData[3] = dbmsData[1];
					lineData[4] = dbmsData[2];
					lineData[5] = dbmsData[5];
					lineData[6] = dbmsData[0];
					lineData[7] = dbmsData[3];
					lineData[8] = (same0?"1":"0")+(same1?"1":"0")+(same2?"1":"0");
//				}

				// set data to DBMS
				try {
					if (null==st){
						st = getStatement(dbAlias);
					}
					rslt = setDataToDbms(lineData, st);
				} catch (SQLException e) {
					System.out.println("sync SQLException(REDO): "+Arrays.toString(lineData));
					
					st = getStatement(dbAlias);
					rslt = setDataToDbms(lineData, st);
				}
				
				String[] lineDataNew = new String[lineData.length-3];
				System.arraycopy(lineData, 3, lineDataNew, 0, lineDataNew.length);

				//sync result
				StringBuilder sbOut = new StringBuilder();
				sbOut.append(line);
				if (same0 && same1 && same2){
					//相同则保留原样
				}else{
					sbOut.append(";").append(Arrays.toString(rslt));
					sbOut.append(";").append(Arrays.toString(lineDataNew));
				}
				bw.write(sbOut.toString());
				bw.newLine();
				
				if ( 0==count%1000 ){
					bw.flush();
					
					System.out.println("sync continue ... "+count+"; Etablished time(ms): " + (System.currentTimeMillis() - old));
					old = System.currentTimeMillis();
				}

				/* read next */
				line = br.readLine();

			}

			bw.flush();
			
			System.out.println("sync finished ... "+count+"; Etablished time(ms): " + (System.currentTimeMillis() - old));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != psts) {
				try {
					releasePreparedStatements(psts);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != st) {
				try {
					releaseStatement(st);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return count;
	}
	
	
	public static Statement getStatement(String dbAlias) throws SQLException{
		Connection conn = Proxool.getConnection(dbAlias);
		return conn.createStatement();
	}
	
	public static void releaseStatement(Statement st) throws SQLException{
		st.close();
	}
	
	public static PreparedStatement[] getPreparedStatements(String[] sqls, String dbAlias) throws SQLException{
		Connection conn = Proxool.getConnection(dbAlias);
		PreparedStatement[] psts = new PreparedStatement[sqls.length];
		for (int i=0; i<sqls.length; i++){
			psts[i] = conn.prepareStatement(sqls[i]);
		}
		
		return psts;
	}
	
	public static void releasePreparedStatements(PreparedStatement[] psts) throws SQLException{
		for (int i=0; i<psts.length; i++){
			psts[i].close();
		}
	}
	
	public static String[] getDataFromDbms(String[] lineData, PreparedStatement[] psts) throws SQLException{
		/*
		//from ViewRnmoshown
		"select * from ViewRnmoshown where imsi=? or msisdn0=? or msisdn1=?",
		
		//from rnmoshow,rnmoshown
		"select uuid,imsi,msisdn0 from rnmoshow where imsi=?",
		"select uuid,imsi,msisdn0  from rnmoshow where msisdn0=?",
		"select uuid as uuid2,rosuuid,msisdn1,cc1 from rnmoshown where rosuuid=?",  //many
		
		"select uuid as uuid2,rosuuid,msisdn1,cc1 from rnmoshown where msisdn1=?",
		"select uuid,imsi,msisdn0 from rnmoshow where uuid=?"
		 */
		boolean found = false;
		ResultSet rs = null;
		String[] dbmsData = new String[]{"uuid","imsi","msisdn0","uuid2","rosuuid","msisdn1","cc1"};
/*		
		found = true;//System.getProperty("ViewRnmoshown")==null;  //slow
		if (!found) {
			//select * from ViewRnmoshown where imsi=? or msisdn0=? or msisdn1=?
			psts[0].setString(1, lineData[0]);
			psts[0].setString(2, lineData[1]);
			psts[0].setString(3, lineData[2]);
			rs = psts[0].executeQuery();
			if (rs.next()) {
	
				dbmsData[1] = rs.getString("imsi");
				dbmsData[2] = rs.getString("msisdn0");
				dbmsData[5] = rs.getString("msisdn1");
			}
			if (rs.next()) {  //可能有多个
				
				dbmsData[0] = rs.getString("imsi");
				dbmsData[3] = rs.getString("msisdn0");
				dbmsData[4] = rs.getString("msisdn1");
				dbmsData[6] = "--";
				
				return dbmsData;
			}
			rs.close();
			
		}
*/		
		found = false;//System.getProperty("ViewRnmoshown")!=null;  //quick
		//query from rnmoshown, then rnmoshow
		if(!found){
			if(!found){
				//select uuid as uuid2,rosuuid,msisdn1,cc1 from rnmoshown where msisdn1=?
				psts[4].setString(1, lineData[2]);
				rs = psts[4].executeQuery();
				if (rs.next()) {
					found = true;
					
					dbmsData[3] = rs.getString("uuid2");
					dbmsData[4] = rs.getString("rosuuid");
					dbmsData[5] = rs.getString("msisdn1");
					dbmsData[6] = rs.getString("cc1");
					
				}
				rs.close();
			}
			if(found){//found by msisdn1
				//select uuid,imsi,msisdn0 from rnmoshow where uuid=?
				psts[5].setString(1, dbmsData[4]);
				rs = psts[5].executeQuery();
				if (rs.next()) {
					found = true;
					
					dbmsData[0] = rs.getString("uuid");
					dbmsData[1] = rs.getString("imsi");
					dbmsData[2] = rs.getString("msisdn0");
				}
				rs.close();
			}
		}
		
		
		//query from rnmoshow, then rnmoshown
		if (!found) {//not found by msisdn1
			if(!found){
				//select uuid,imsi,msisdn0 from rnmoshow where imsi=?
				psts[1].setString(1, lineData[0]);
				rs = psts[1].executeQuery();
				if (rs.next()) {
					found = true;
					
					dbmsData[0] = rs.getString("uuid");
					dbmsData[1] = rs.getString("imsi");
					dbmsData[2] = rs.getString("msisdn0");
					
				}
				rs.close();
			}
			if(!found){
				//select uuid,imsi,msisdn0  from rnmoshow where msisdn0=?
				psts[2].setString(1, lineData[1]);
				rs = psts[2].executeQuery();
				if (rs.next()) {
					found = true;
					
					dbmsData[0] = rs.getString("uuid");
					dbmsData[1] = rs.getString("imsi");
					dbmsData[2] = rs.getString("msisdn0");
				}
				rs.close();
			}
			
			if(found){//found by imsi,msisdn0
				//select uuid as uuid2,rosuuid,msisdn1,cc1 from rnmoshown where rosuuid=? //many
				psts[3].setString(1, dbmsData[0]);
				rs = psts[3].executeQuery();
				while (rs.next()) {
					found = true;
					
					dbmsData[3] = rs.getString("uuid2");
					dbmsData[4] = rs.getString("rosuuid");
					dbmsData[5] = rs.getString("msisdn1");
					dbmsData[6] = rs.getString("cc1");
					
					if (lineData[1].startsWith(dbmsData[6])) break;
				}
				rs.close();
			}
			
		}
		
		return dbmsData;

	}
	
	public static int[] setDataToDbms(String[] lineData, Statement st) throws SQLException {
		String sql = null;
		List<String> sqlList = new ArrayList<String>();
		
		//lineData[]
		//IMSI,MSISDN0,MSISDN1,IMSI,MSISDN0,MSISDN1,UUID,UUID2,FLAG
		if ("000".equals(lineData[lineData.length-1])){
			
			String uuid = UuidGenerator.gererateUUID();
			sql = syncSqls[0].replaceAll(":uuid", uuid);
			sql = sql.replaceAll(":imsi", lineData[0]);
			sql = sql.replaceAll(":msisdn0", lineData[1]);
			sqlList.add(sql);
			
			String uuid2 = UuidGenerator.gererateUUID();
			sql = syncSqls[1].replaceAll(":uuid", uuid2);
			sql = sql.replaceAll(":rosuuid", uuid);
			sql = sql.replaceAll(":msisdn1", lineData[2]);
			sqlList.add(sql);
			
		}

		if ("001".equals(lineData[lineData.length-1])
			|| "010".equals(lineData[lineData.length-1])
			|| "011".equals(lineData[lineData.length-1])
			|| "100".equals(lineData[lineData.length-1])
			|| "101".equals(lineData[lineData.length-1])
			){
			
			sql = syncSqls[2].replaceAll(":imsi", lineData[0]);
			sql = sql.replaceAll(":msisdn0", lineData[1]);
			sql = sql.replaceAll(":uuid", lineData[6]);
			sqlList.add(sql);
			
			sql = syncSqls[5].replaceAll(":rosuuid", lineData[6]);  //ONLY: modificator='sync'
			sqlList.add(sql);
			
		}
		
		if ("010".equals(lineData[lineData.length-1])
			|| "100".equals(lineData[lineData.length-1])
			|| "110".equals(lineData[lineData.length-1])
			){
			if (lineData[5].matches("[0-9]{3,}")){
				sql = syncSqls[3].replaceAll(":msisdn1", lineData[2]);
				sql = sql.replaceAll(":uuid", lineData[7]);
				sqlList.add(sql);
			}else{
				String uuid2 = UuidGenerator.gererateUUID();
				sql = syncSqls[1].replaceAll(":uuid", uuid2);
				sql = sql.replaceAll(":rosuuid", lineData[6]);
				sql = sql.replaceAll(":msisdn1", lineData[2]);
				sqlList.add(sql);
			}
			
			sql = syncSqls[4].replaceAll(":imsi", lineData[0]);  //ONLY: modificator='sync'
			sqlList.add(sql);
			
		}
		
		if ("110".equals(lineData[lineData.length-1])
			|| "111".equals(lineData[lineData.length-1])
			){
			
			sql = syncSqls[4].replaceAll(":imsi", lineData[0]);  //ONLY: modificator='sync'
			sqlList.add(sql);
			
			sql = syncSqls[5].replaceAll(":rosuuid", lineData[6]);  //ONLY: modificator='sync'
			sqlList.add(sql);
			
		}
		
		for (String s : sqlList){
			st.addBatch(s);
		}
		
		int[] rtn = new int[sqlList.size()];
		try {
			st.getConnection().setAutoCommit(false);
			rtn = st.executeBatch();
			st.getConnection().commit();
		} catch (SQLException e) {
			System.out.println(Arrays.toString(lineData));
			System.out.println(sqlList);
			
			e.printStackTrace();
			st.getConnection().rollback();
		}finally{
			st.clearBatch();
			st.getConnection().setAutoCommit(true);
		}
		
		return rtn;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1 || args.length > 0
				&& (args[0].startsWith("--help"))) {
			System.out.println("Usage: java " + DataCompare.class.getName()
					+ "csvfile [flag [dbAlias]]");
			System.out.println("  csvfile is a csv file by ','. Sample:imsi,msisdn0,msisdn1");
			System.out.println("  flag is 1-comp or 2-sync or 3=1+2,default: 1");
			System.out.println("  dbAlias database alias");
			System.exit(1);
		}
		
		

		long old = System.currentTimeMillis();
		System.out.println("start ... " + old);
		int count = 0;
		try {
			Proxool.init();
			
			if (args.length < 2) {
				count = csvCompareDbms(args[0], "MDB");
			} else if (args.length < 3) {
					if ("1".equals(args[1])){
						count = csvCompareDbms(args[0], "MDB");
					}else if ("2".equals(args[1])){
						count = csvSyncDbms(args[0]+".comp","MDB");
					}else if ("3".equals(args[1])){
						count = csvCompareDbms(args[0], "MDB");
						count = csvSyncDbms(args[0]+".comp","MDB");
					}
			}else{
					if ("1".equals(args[2])){
						count = csvCompareDbms(args[0], args[1]);
					}else if ("2".equals(args[2])){
						count = csvSyncDbms(args[0]+".comp",args[1]);
					}else if ("3".equals(args[2])){
						count = csvCompareDbms(args[0], args[1]);
						count = csvSyncDbms(args[0]+".comp",args[1]);
					}else{
						count = csvCompareDbms(args[0], args[1]);
						count = csvSyncDbms(args[0]+".comp",args[2]);
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("finised ... " + old);
			System.out.println(count+"; Etablished time(ms): "
					+ (System.currentTimeMillis() - old));
			System.exit(0);
		}
	}

}
