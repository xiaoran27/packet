/************************* CHANGE REPORT HISTORY *****************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2009-6-11
*   create
*-----------------------------------------------------------------------------*
* V,xiaoran27,2010-02-23
* M //prepareStatement start with 1
*-----------------------------------------------------------------------------*
* V,xiaoran27,2011-06-04
* + executeQueryRowCount(String sql)
*	executeQuery(Class clz, String sql, int start, int count)
*	executeQuery(Class clz, String sql)
* + //支持指定当前DBMS类型，以生成不同SQL
* + //为了保存线程共用连接信息
*-----------------------------------------------------------------------------*
* V,xiaoran27,2011-11-29
* + executeQueryCount(String sql)
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.dbms;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dbms.pojo.Uidtable;
import com.dbms.pojo.UidtableExt;

/**
 * 使用proxool操作DBMS. 显式Connection要其close()关闭, 隐式Connection要ProxoolMulti.close()关闭.
 * 可用于单线程中多db的连接,当前的db连接是最后一次使用的.
 * 
 * 	 mysql setMaxRows() out of range. 2147483647 > 50000000.
     mysql: select * from table where 1=1 limit start{0+} count
     hsql: select limit start{1+}, count * from table where 1=1 
 * 
 * @author Xiaoran27 $author: $
 * @version $Revision: 1.3 $
 * @ID $Id: ProxoolMulti.java,v 1.3 2011/12/14 02:02:30 xiaoran27 Exp $
  */
public class ProxoolMulti extends Proxool {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ProxoolMulti.class);
	
	static public int DBMS_MYSQL = 0;
	static public int DBMS_HSQLDB = 1;
	static public int DBMS_SYBASE = 2;
	
	//支持指定当前DBMS类型，以生成不同SQL
	static private int dbmsType = DBMS_MYSQL;

	  
    /**各线程独立使用的数据*/
    private static final ThreadLocal<DbmsConnectionInfo> dbmsConnectionInfoThreadLocal = new ThreadLocal<DbmsConnectionInfo>();
    private static final ThreadLocal<Map<String,DbmsConnectionInfo>> dbmsConnectionInfoMapThreadLocal = new ThreadLocal<Map<String,DbmsConnectionInfo>>();

    /**最大限制行数*/
    private static int maxRowsLimit = 50000000;
    
    
	public static int getDbmsType() {
		return dbmsType;
	}

	public static void setDbmsType(int dbmsType) {
		ProxoolMulti.dbmsType = dbmsType;
	}

	/** 得到当前的dbAlias.
	 * 
	 * @return
	 */
	public String getDbalias() {
		return dbmsConnectionInfoThreadLocal.get().dbAlias;
	}

	/** 设置当前的dbAlias.
	 * 
	 * @param dbalias
	 */
	public void setDbalias(String dbalias) {
		dbmsConnectionInfoThreadLocal.get().dbAlias = dbalias;
	}
	
	/** 获取指定alias的数据库连接。
	 * must invoke Proxool.init() before this.
	 * 
	 * @param alias
	 * @return
	 * @throws SQLException
	 */
	synchronized static public Connection getConnectionByAlias(String alias) throws SQLException {
    	
		
    	if (null == alias){
    		throw new SQLException("Unknown database's alias: null.");
    	}
    	Map<String,DbmsConnectionInfo> dbmsConnectionInfoMap = dbmsConnectionInfoMapThreadLocal.get();
    	if (null == dbmsConnectionInfoMap){
    		dbmsConnectionInfoMap = new HashMap<String,DbmsConnectionInfo>();
        	dbmsConnectionInfoMapThreadLocal.set(dbmsConnectionInfoMap);
    	}
		
		DbmsConnectionInfo connInfo = dbmsConnectionInfoMap.get(alias);
		if (null==connInfo){
			connInfo = new DbmsConnectionInfo();
			connInfo.dbAlias = alias;
		}
		
    	if (connInfo.conn==null || connInfo.conn.isClosed()){
    		connInfo.conn = Proxool.getConnection(alias);
    		connInfo.count = 1;
    	}else{
    		connInfo.count ++ ;
    	}
    	dbmsConnectionInfoMap.put(alias, connInfo);
    	dbmsConnectionInfoThreadLocal.set(connInfo);
    	dbmsConnectionInfoMapThreadLocal.set(dbmsConnectionInfoMap);
    	
    	if (logger.isDebugEnabled()){
    		logger.debug("NOW: getConnection(String) - connInfo: "+connInfo);
    	}
    	return connInfo.conn;
    }

    /**增加一个要执行的sql(INSERT/DELETE/UPDATE)
     *
     * @param sql 要执行的sql(INSERT/DELETE/UPDATE)
     */
    static public void addBatch(String sql){
		DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        connInfo.sqlList.add(sql);
    }

    /**增加多个要执行的sql(INSERT/DELETE/UPDATE)
     *
     * @param c 多个要执行的sql(INSERT/DELETE/UPDATE)
     */
    static public void addBatch(Collection<? extends String> c){
    	DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        connInfo.sqlList.addAll(c);

    }

    /**执行sql
     *
     * @return ture-成功提交,false-失败回滚.
     * @throws SQLException
     */
    static public boolean executeBatch()  throws SQLException {
    	DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        return executeBatch(connInfo.sqlList, false);
    }

    /**执行给定的sql组, 多作为一个事务. 多事务时,所有sql都执行, 返回false不知道是那个错误.
    *
    * @param conn DBMS连接
    * @param sql sql语句
    * @param autoCommit  是否自动提交.
    * @return ture-成功提交,false-失败
    * @throws SQLException
    */
   static public boolean executeBatch(Connection conn, String[] sql, boolean autoCommit) throws SQLException {
       return executeBatch(conn, sql, autoCommit,true);
   }

    /**执行给定的sql组, 多作为一个事务. 多事务时,所有sql都执行, 返回false不知道是那个错误.
     *
     * @param conn DBMS连接
     * @param sql sql语句
     * @param autoCommit  是否自动提交.
     * @return ture-成功提交,false-失败
     * @throws SQLException
     */
    static public boolean executeBatch(Connection conn, String[] sql, boolean autoCommit, boolean putQueue) throws SQLException {

        if (null==sql || sql.length<1){
            return true;
        }

        long old = System.currentTimeMillis();
        if (logger.isDebugEnabled()){
            logger.debug("executeBatch(Connection , String[] , boolean, boolean ) - conn="+conn+"; sql="+Arrays.toString(sql));
        }

        DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        connInfo.lastSqlError = "";
        connInfo.sqlList = new ArrayList<String>();
        connInfo.lastSqlList = new ArrayList<String>();
        for (int i = 0; i < sql.length; i++) {
        	connInfo.lastSqlList.add(sql[i]);
        }

      boolean result = true;
      synchronized(conn){
        boolean oldAutoCommit = conn.getAutoCommit();
        if (!autoCommit){
            conn.setAutoCommit(false);
        }

        Statement st = null;
        try {
            st = conn.createStatement();
            for (int i = 0; i < sql.length; i++) {
                st.addBatch(sql[i]);
            }
            int[] rtn = st.executeBatch();
            for (int i = 0; i < rtn.length; i++) {
                if (rtn[i]<0){
                    result = false;
                    break; //err
                }
            }

        } catch (SQLException e) {
            result = false;
            logger.error("executeBatch(Connection , String[] , boolean, boolean ) - "+Arrays.toString(sql),e);

            StringBuilder sqlErr = new StringBuilder();
            sqlErr.append("ErrorCode=").append(e.getErrorCode())
                  .append(", SQLState=").append(e.getSQLState())
                  .append(", Message=").append(e.getMessage());
            connInfo.lastSqlError = sqlErr.toString();

            throw e;
        } finally{
            if (!autoCommit){
                if (result){
                    conn.commit();
                }else {
                    conn.rollback();
                }
            }
            conn.setAutoCommit(oldAutoCommit);
        }
      }

      if (logger.isDebugEnabled()){
          logger.debug("executeBatch(Connection , String[] , boolean, boolean ) - cost time(ms): ="+(System.currentTimeMillis() - old));
      }

      return result;
    }
    
    /**执行给定的sql, 作为一个事务. 返回UID.
    *
    * @see #executeUpdateGeneratedKey(Connection, String, boolean)
    */
    static public int executeUpdateGeneratedKey(String sql) throws SQLException {
    	return executeUpdateGeneratedKey(getConnection(), sql, true);
    }
    
    /**执行给定的sql, 作为一个事务. 返回UID.
    *
    * @see #executeUpdateGeneratedKey(Connection, String, boolean)
    */
    static public int executeUpdateGeneratedKey(String sql, boolean putQueue) throws SQLException {
    	return executeUpdateGeneratedKey(getConnection(), sql, putQueue);
    }
    
    /**执行给定的sql, 作为一个事务. 返回UID.
    *
    * @param conn DBMS连接
    * @param sql sql语句
    * @return >0-成功提交,<=0-失败
    * @throws SQLException
    */
    static public int executeUpdateGeneratedKey(Connection conn, String sql, boolean putQueue) throws SQLException {

        if (null==sql){
            return 0;
        }

        long old = System.currentTimeMillis();
        if (logger.isDebugEnabled()){
            logger.debug("executeUpdateGeneratedKey(Connection , String , boolean ) - conn="+conn+"; sql="+sql);
        }

        DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        connInfo.lastSqlError = "";
        connInfo.sqlList = new ArrayList<String>();
        connInfo.lastSqlList = new ArrayList<String>();
        connInfo.lastSqlList.add(sql);

        int uid = -1;
        boolean result = true;
      synchronized(conn){
        boolean oldAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        Statement st = null;
        try {
            st = conn.createStatement();
            int rtn = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS );
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()){
            	uid = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
        	result = false;
            logger.error("executeUpdateGeneratedKey(Connection , String , boolean ) - "+sql,e);

            StringBuilder sqlErr = new StringBuilder();
            sqlErr.append("ErrorCode=").append(e.getErrorCode())
                  .append(", SQLState=").append(e.getSQLState())
                  .append(", Message=").append(e.getMessage());
            connInfo.lastSqlError = sqlErr.toString();

            //throw e;
        } finally{
        	if (null!=st){
        		st.close();
        	}
            if (result){
                conn.commit();
            }else {
                conn.rollback();
            }
            conn.setAutoCommit(oldAutoCommit);
        }
      }

      if (logger.isDebugEnabled()){
          logger.debug("executeUpdateGeneratedKey(Connection , String , boolean ) - cost time(ms): ="+(System.currentTimeMillis() - old));
      }

      return uid;
    }

    /** 最后执行的sql语句
     *
     * @return
     */
    static public List<String> getLastSql(){
    	 DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        return connInfo.lastSqlList;
    }

    /**最后执行sql的错误信息. ""-成功
     * @return
     */
    static public String getLastError(){
    	DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        return connInfo.lastSqlError;
    }


    /**
     * @see #executeBatch(Connection, String[], boolean)
     */
    static public boolean executeBatch(Connection conn, String sql, boolean autoCommit) throws SQLException {
        return executeBatch(conn, new String[]{sql}, autoCommit);
    }

    /*must invoke Proxool.init() before this.*/
    synchronized static public Connection getConnection() throws SQLException {
    	
    	DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
    	if (connInfo==null){
    		connInfo = new DbmsConnectionInfo();
    		dbmsConnectionInfoThreadLocal.set(connInfo);
    	}
    	String alias = connInfo.dbAlias;
    	if (null == alias){
    		throw new SQLException("Unknown database's alias: null.");
    	}
    	
    	Connection conn = connInfo.conn;
    	if (conn==null || conn.isClosed()){
          conn = Proxool.getConnection(alias);
          connInfo.conn = conn;
          dbmsConnectionInfoThreadLocal.set(connInfo);
    	}

    	conn = getConnectionByAlias(alias);  //为了保存线程共用连接信息
        return conn;
    	
    }

    /**
     * @see #executeBatch(Connection, String[], boolean)
     */
    static public boolean executeBatch(Collection<? extends String> c, boolean autoCommit) throws SQLException {
        String[] sql = c.toArray(new String[]{});
        return executeBatch(getConnection(), sql, autoCommit);
    }

    /**
     * @see #executeBatch(Connection, String[], boolean)
     */
    static public boolean executeBatch(Connection conn, Collection<? extends String> c, boolean autoCommit) throws SQLException {
         String[] sql = c.toArray(new String[]{});
         return executeBatch(conn, sql, autoCommit);
    }

    /**
     * @see #executeBatch(Connection, String[], boolean)
     */
    static public boolean executeBatch(String[] sql, boolean autoCommit) throws SQLException {
        return executeBatch(getConnection(), sql, autoCommit);
    }

    /**
     * @see #executeBatch(Connection, String[], boolean, boolean)
     */
    static public boolean executeBatch(String[] sql, boolean autoCommit, boolean putQueue) throws SQLException {
        return executeBatch(getConnection(), sql, autoCommit, putQueue);
    }

    /**
     * @see #executeBatch(Connection, String[], boolean)
     */
    static public boolean executeBatch(String sql, boolean autoCommit) throws SQLException {
        return executeBatch(new String[]{sql}, autoCommit);
    }

    /**执行一个查询sql, 并返回结果. DBMS 需要支持limit操作.
    *
    * @param sql 查询sql
    * @param start 始行(>=0)
    * @param count 行数(>=1)
    * @return 查询结果
    * @throws SQLException
    */
    static public ResultSet executeQueryLimit(String sql, int start, int count) throws SQLException {
        if (start<0 || count < 1){
            throw new SQLException( "Params is illegaled.");
        }

        String select = "select ";  //must have a space
        int pos = sql.toLowerCase().indexOf(select);
        if (pos >= 0){
        	if (dbmsType==DBMS_MYSQL){
        		if (sql.indexOf(" limit ")<1){
        			sql = sql + " limit "+start+", "+count;
        		}
        	}else if (dbmsType==DBMS_HSQLDB){
        		if (sql.indexOf(" limit ")<1){
        			sql = "select limit "+start+" "+count+" "+sql.substring(pos+select.length());
        		}
        	}else{
        		//unsupport limit,use MaxRows
        	}
        }else{
        	throw  new SQLException( "not include select.");
        }

        long old = System.currentTimeMillis();
        Connection conn = getConnection();
        Statement st = null;
        ResultSet rs = null;
        try {
            if (logger.isDebugEnabled()){
                logger.debug("executeQueryLimit(String , int,int ) - conn="+conn+"; sql="+sql);
            }

            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );  //rs要next(),否则不能rs.close()
            if (sql.indexOf(" limit ")<1){
            	st.setMaxRows(start+count);  
            }
            rs = st.executeQuery(sql);
            
            if (sql.indexOf(" limit ")<1){
//            	rs.absolute(start<1?1:start);
            	int skip = (dbmsType==DBMS_MYSQL?start:start-1);
 	           for(int i=0;i<skip;i++){  //skip 
	            	rs.next();  
	            }
            }

        } catch (SQLException e) {
            logger.error("executeQueryLimit(String ,int, int ) - "+sql,e);
            if (st!=null){
            	try {
					st.close();
				} catch (Exception e1) {
				}
            }
            throw e;
        } finally{

        }
        if (logger.isDebugEnabled()){
            logger.debug("executeQueryLimit(String ,int, int ) - cost time(ms): ="+(System.currentTimeMillis() - old));
        }

        return rs;

    }

    /**执行一个查询sql, 并返回结果.
     *
     * @param sql 查询sql
     * @param maxRows 最大行数
     * @return 查询结果
     * @throws SQLException
     */
    static public ResultSet executeQuery(String sql, int maxRows) throws SQLException {
        if (maxRows < 1){
            throw new SQLException( "Params is illegaled.");
        }

        long old = System.currentTimeMillis();
        Connection conn = getConnection();
        Statement st = null;
        ResultSet rs = null;
        try {
            if (logger.isDebugEnabled()){
                logger.debug("executeQuery(String , int ) - conn="+conn+"; sql="+sql);
            }

            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );  //rs要next(),否则不能rs.close()
            st.setMaxRows(maxRows);  //sybase 和hsqldb 不一样吗?
            rs = st.executeQuery(sql);

        } catch (SQLException e) {
            logger.error("executeQuery(String , int ) - "+sql,e);
            if (st!=null){
            	try {
					st.close();
				} catch (Exception e1) {
				}
            }
            throw e;
        } finally{

        }
        if (logger.isDebugEnabled()){
            logger.debug("executeQuery(String , int ) - cost time(ms): ="+(System.currentTimeMillis() - old));
        }

        return rs;

    }

    /**执行一个查询sql, 并返回结果.
    *
    * @param sql 查询sql
    * @return 查询结果
    * @throws SQLException
    */
   static public ResultSet executeQuery(String sql) throws SQLException {
       return executeQuery(sql, maxRowsLimit);
   }

   /**执行SQL,返回第一个统计值，一般是行数。如:select count(*) from table。
   *
   * @param sql 查询统计sql(包含统计功能的sql)
   * @return 查询结果
   * @throws SQLException
   */
   static public int executeQueryRowCount(String sql) throws SQLException {
	   ResultSet rs = executeQuery(sql, maxRowsLimit);
	   rs.next();
	   return rs.getInt(1);
   }

   /**执行一个带条件的查询sql{防止SQL注入},  并返回结果. DBMS 需要支持limit操作.
   *
   * @param sql 查询sql
   * @param paramList 参数列表
   * @param start 始行(>=0)
   * @param count 行数(>=1)
   * @return 查询结果
   * @throws SQLException
   */
   static public ResultSet executeQueryLimit(String sql, List paramList, int start, int count) throws SQLException {
       if (start<0 || count < 1){
           throw new SQLException( "Params is illegaled.");
       }

       String select = "select ";  //must have a space
       int pos = sql.toLowerCase().indexOf(select);
       if (pos >= 0){
    	   if (dbmsType==DBMS_MYSQL){
	       		if (sql.indexOf(" limit ")<1){
	       			sql = sql + " limit "+start+", "+count;
	       		}
	       	}else if (dbmsType==DBMS_HSQLDB){
	       		if (sql.indexOf(" limit ")<1){
	       			sql = "select limit "+start+" "+count+" "+sql.substring(pos+select.length());
	       		}
	       	}else{
	       		//unsupport limit,use MaxRows
	       	}
       }else{
       	  throw  new SQLException( "not include select.");
       }

       long old = System.currentTimeMillis();
       Connection conn = getConnection();
       Statement st = null;
       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           if (logger.isDebugEnabled()){
               logger.debug("executeQueryLimit(String , int,int ) - conn="+conn+"; sql="+sql);
           }

//           st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );  //rs要next(),否则不能rs.close()
//           rs = st.executeQuery(sql);


           ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY);
           if (sql.indexOf(" limit ")<1){
        	   ps.setMaxRows(start+count);  
           }
           for(int i=0; null!=paramList && i<paramList.size(); i++){
        	   ps.setObject(i+1, paramList.get(i)); //prepareStatement start with 1
           }
           rs = ps.executeQuery();
           if (sql.indexOf(" limit ")<1){
//        	   rs.absolute(start<1?1:start); //会多跳一个
        	   int skip = (dbmsType==DBMS_MYSQL?start:start-1);
	           for(int i=0;i<skip;i++){  //skip 
	           	  rs.next();  
	            }        	   
           }

       } catch (SQLException e) {
           logger.error("executeQueryLimit(String ,int, int ) - "+sql,e);
           throw e;
       } finally{

       }
       if (logger.isDebugEnabled()){
           logger.debug("executeQueryLimit(String ,int, int ) - cost time(ms): ="+(System.currentTimeMillis() - old));
       }

       return rs;

   }


   /**执行一个查询sql{防止SQL注入}, 并返回结果
    *
    * @param sql 查询sql
    * @param paramList 参数列表
    * @param maxRows 最大行数
    * @return 查询结果
    * @throws SQLException
    */
   static public ResultSet executeQuery(String sql, List paramList, int maxRows) throws SQLException {
       if (maxRows < 1){
           throw new SQLException( "Params is illegaled.");
       }

       long old = System.currentTimeMillis();
       Connection conn = getConnection();
       Statement st = null;
       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           if (logger.isDebugEnabled()){
               logger.debug("executeQuery(String , int ) - conn="+conn+"; sql="+sql);
           }

//           st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );  //rs要next(),否则不能rs.close()
//           st.setMaxRows(maxRows);  //sybase ?? hsqldb 不一样吗?
//           rs = st.executeQuery(sql);


           ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY);
           for(int i=0; null!=paramList && i<paramList.size(); i++){
        	   ps.setObject(i+1, paramList.get(i)); //prepareStatement start with 1
           }
           ps.setMaxRows(maxRows);  //sybase ?? hsqldb 不一样吗?
           rs = ps.executeQuery();

       } catch (SQLException e) {
           logger.error("executeQuery(String , int ) - "+sql,e);
           throw e;
       } finally{

       }
       if (logger.isDebugEnabled()){
           logger.debug("executeQuery(String , int ) - cost time(ms): ="+(System.currentTimeMillis() - old));
       }

       return rs;

   }

   /**执行一个查询统计sql{防止SQL注入}, 并返回结果
   *
   * @param sql 查询统计sql(包含统计功能的sql)
   * @param paramList 参数列表
   * @return 查询结果
   * @throws SQLException
   */
  static public ResultSet executeQueryCount(String sql, List paramList) throws SQLException {
      return executeQuery(sql, paramList, maxRowsLimit);
  }
  
  static public ResultSet executeQueryCount(String sql) throws SQLException {
      return executeQuery(sql, new ArrayList(), maxRowsLimit);
  }

    /**关闭一个查询结果
     *
     * @param rs
     */
    static public void close(ResultSet rs){
        try {

            if (null!=rs){
                rs.next();  //由ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY决定
                rs.close();
                //rs.getStatement().close();  //not app because ResultSet.TYPE_SCROLL_INSENSITIVE
                //rs.getStatement().getConnection().close();  //not app because ResultSet.TYPE_SCROLL_INSENSITIVE

                rs = null;
            }
        } catch (SQLException e) {
            logger.error("close(ResultSet) - e="+e,e);
        }
    }

    /**
     * 关闭DBMS连接,同时清空相关数据连接.
     */
    synchronized static public void close(){
    	DbmsConnectionInfo connInfo = dbmsConnectionInfoThreadLocal.get();
        connInfo.sqlList = new ArrayList<String>();;//防止中间异常还有sql
        try {
        	Integer connCount = connInfo.count;
        	if (null!=connCount && connCount>1){
        		connInfo.count --;
        	}else{
        		connInfo.count = 0;
	            Connection conn = connInfo.conn;
	            if (null!=conn ){
	                if (!conn.isClosed()) {
	                    conn.close();
	                }
	                connInfo.conn = null;
	               conn = null;
	            }
        	}
        	
        	if (logger.isDebugEnabled()){
        		logger.debug("NOW: close() - connInfo: "+connInfo);
        	}
        } catch (SQLException e) {
            logger.error("close() - e="+e,e);
        }
    }


    /**执行查询sql,并返回一个结果
     *
     * @param sql 查询sql
     * @param start 始行
     * @param count 返回行数
     * @return 查询结果. 注意: 结果与查询sql的对应关系
     * @throws SQLException
     */
    static public List<Object[]> executeQueryList(String sql, int start, int count) throws SQLException {
        if (start<0 || count<0){
            throw new SQLException( "Params is illegaled.");
        }

        long old = System.currentTimeMillis();
        Connection conn = getConnection();
        Statement st = null;
        ResultSet rs = null;
        List<Object[]> data = new ArrayList<Object[]>();
        Object[] aData = null;
        try {
            if (logger.isDebugEnabled()){
                logger.debug("executeQuery(String , int, int ) - conn="+conn+"; sql="+sql);
            }
            
            String select = "select ";  //must have a space
            int pos = sql.toLowerCase().indexOf(select);
            if (pos >= 0){
         	   if (dbmsType==DBMS_MYSQL){
     	       		if (sql.indexOf(" limit ")<1){
     	       			sql = sql + " limit "+start+", "+count;
     	       		}
     	       	}else if (dbmsType==DBMS_HSQLDB){
     	       		if (sql.indexOf(" limit ")<1){
     	       			sql = "select limit "+start+" "+count+" "+sql.substring(pos+select.length());
     	       		}
     	       	}else{
     	       		//unsupport limit,use MaxRows
     	       	}
            }else{
            	  throw  new SQLException( "not include select.");
            }

            //method 1：limit
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY);
            if (sql.indexOf(" limit ")<1){
            	st.setMaxRows(start+count);
       		}
            rs = st.executeQuery(sql); 
            if (sql.indexOf(" limit ")<1){
//          	   rs.absolute(start<1?1:start);
            	int skip = (dbmsType==DBMS_MYSQL?start:start-1);
 	           for(int i=0;i<skip;i++){  //skip 
             	  rs.next();  
                }        	   
             }
            boolean bmoved = sql.indexOf(" limit ")<1;  //前面已经absolute
            while(bmoved || rs.next()){
         	   bmoved = false; 
            	aData = new Object[rs.getMetaData().getColumnCount()];
                for (int i = 1; i <= aData.length; i++) {
                    aData[i-1] = rs.getObject(i);
                }
                data.add(aData);
            }
/*            
            //method 2：st.setMaxRows(start+count);
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );
            //st.setQueryTimeout(1);  //1s
            //st.setFetchSize(start+count);
            st.setMaxRows(start+count);
            rs = st.executeQuery(sql); 
            aData = new Object[rs.getMetaData().getColumnCount()];
            rs.last();
            if (rs.getRow()>=start){
                rs.absolute(start-1);
            }
            int cnt=start-1;
            while(rs.next() && rs.getRow()-start<=count){
                for (int i = 0; i < aData.length; i++) {
                    aData[i] = rs.getObject(i);
                }
                data.add(aData);
            }
            
            //method 3：fetch
            st = conn.createStatement();
            rs = st.executeQuery(sql); 
            aData = new Object[rs.getMetaData().getColumnCount()];
            while(rs.next()){
            	if (rs.getRow()<start){
            		continue;
            	}
            	if (rs.getRow()-start>count){
            		break;
            	}
                for (int i = 0; i < aData.length; i++) {
                    aData[i] = rs.getObject(i);
                }
                data.add(aData);
            }
 */           
        } catch (SQLException e) {
            logger.error("executeQuery(String , int , int ) - "+sql,e);
            throw e;
        } finally{
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != st) {
                    st.close();
                }
            } catch (SQLException e) {
            }
        }
        if (logger.isDebugEnabled()){
            logger.debug("executeQuery(String , int , int ) - cost time(ms): ="+(System.currentTimeMillis() - old));
        }

        return data;
    }
   

    /**
     *最大查询1000条结果记录.
     * 
     * @see #executeQuery(String, int, int)
     */
    static public List<Object[]> executeQueryList(String sql) throws SQLException {
        return executeQueryList(sql, (dbmsType==DBMS_MYSQL?0:1), 1000);
    }
    
    @Deprecated
    static public List<Object[]> executeQuery(String sql, int start, int count){
    	return executeQuery(sql, (dbmsType==DBMS_MYSQL?0:1), 1000);
    }
    
    /**执行查询sql,并返回一个结果
    *
    * @param clz POJO类
    * @param sql 查询sql
    * @param start 始行
    * @param count 返回行数
    * @return 查询结果. 
    * @throws SQLException
    */
   static public List executeQueryList(Class clz, String sql, int start, int count) throws SQLException {
       if (start<0 || count<0){
           throw new SQLException( "Params is illegaled.");
       }
       

       String select = "select ";  //must have a space
       int pos = sql.toLowerCase().indexOf(select);
       if (pos >= 0){
//    	   if (dbmsType==DBMS_MYSQL){
//	       		if (sql.indexOf(" limit ")<1){
//	       			sql = sql + " limit "+start+", "+count;
//	       		}
//	       	}else if (dbmsType==DBMS_HSQLDB){
//	       		if (sql.indexOf(" limit ")<1){
//	       			sql = "select limit "+start+" "+count+" "+sql.substring(pos+select.length());
//	       		}
//	       	}else{
//	       		//unsupport limit,use MaxRows
//	       	}
       }else{
       	  throw  new SQLException( "not include select.");
       }

       long old = System.currentTimeMillis();
       Connection conn = getConnection();
       Statement st = null;
       ResultSet rs = null;
       List data = new ArrayList();
       try {
           if (logger.isDebugEnabled()){
               logger.debug("executeQuery(Class, String , int, int ) - conn="+conn+"; sql="+sql);
           }
           
           //method 1：limit
           st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY);
//           if (sql.indexOf(" limit ")<1){
           	  st.setMaxRows(start+count);
//      		}
           rs = st.executeQuery(sql); 
//           if (sql.indexOf(" limit ")<1){
//	           rs.absolute(start<1?1:start); //会多跳一个
               int skip = (dbmsType==DBMS_MYSQL?start:start-1);
	           for(int i=0;i<skip;i++){  //skip 
	          	  rs.next();  
	           }         	   
//            }
           while(rs.next()){
    		   Object aData = clz.newInstance();
			    Field[] fields = clz.getDeclaredFields();
				for(int i=0; i<fields.length; i++){
				  fields[i].setAccessible(true);
				  String typename = fields[i].getType().getName();
				  Object v = rs.getObject(fields[i].getName());
				  
				  if (v instanceof Number){
					  if ("java.lang.Byte".equals(typename)||"byte".equals(typename)){
						  v = rs.getByte(fields[i].getName());
					  }else if ("java.lang.Double".equals(typename)||"double".equals(typename)){
						  v = rs.getDouble(fields[i].getName());
					  }if ("java.lang.Float".equals(typename)||"float".equals(typename)){
						  v = rs.getFloat(fields[i].getName());
					  }else if ("java.lang.Integer".equals(typename)||"int".equals(typename)){
						  v = rs.getInt(fields[i].getName());
					  }else if ("java.lang.Long".equals(typename)||"long".equals(typename)){
						  v = rs.getLong(fields[i].getName());
					  }else if ("java.lang.Short".equals(typename)||"short".equals(typename)){
						  v = rs.getShort(fields[i].getName());
					  }
				  }
				  fields[i].set(aData, v);
				}
				
				data.add(aData);
           }
/*           
           //method 2：st.setMaxRows(start+count);
           st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE ,ResultSet.CONCUR_READ_ONLY );
           //st.setQueryTimeout(1);  //1s
           //st.setFetchSize(start+count);
           st.setMaxRows(start+count);
           rs = st.executeQuery(sql); 
           rs.last();
           if (rs.getRow()>=start){
               rs.absolute(start-1);
           }
           int cnt=start-1;
           while(rs.next() && rs.getRow()-start<=count){
        	   Object aData = clz.newInstance();
     		    Field[] fields = clz.getDeclaredFields();
	      		for(int i=0; i<fields.length; i++){
	  			  fields[i].setAccessible(true);
	  			  fields[i].set(aData, rs.getObject(fields[i].getName()));
	      		}
 			  
              data.add(aData);
           }
           
           //method 3：fetch
           st = conn.createStatement();
           rs = st.executeQuery(sql); 
           while(rs.next()){
	           	if (rs.getRow()<start){
	           		continue;
	           	}
	           	if (rs.getRow()-start>count){
	           		break;
	           	}
	           	Object aData = clz.newInstance();
	  		    Field[] fields = clz.getDeclaredFields();
	      		for(int i=0; i<fields.length; i++){
	  			  fields[i].setAccessible(true);
	  			  fields[i].set(aData, rs.getObject(fields[i].getName()));
	      		}
				  
	           data.add(aData);
           }
           */
       } catch (SQLException e) {
           logger.error("executeQuery(Class, String , int , int ) - "+sql,e);
           throw e;
       } catch (Exception e) {
			logger.error("executeQuery(Class, String , int , int ) - "+sql,e);
	        throw new SQLException(e.getMessage());
       } finally{
           try {
               if (null != rs) {
                   rs.close();
               }
               if (null != st) {
                   st.close();
               }
           } catch (SQLException e) {
           }
       }
       if (logger.isDebugEnabled()){
           logger.debug("executeQuery(Class, String , int , int ) - cost time(ms): ="+(System.currentTimeMillis() - old));
       }

       return data;
   }
  

   /**
    * ??大查??1000条记??.
    * 
    * @see #executeQuery(Class,String, int, int)
    */
   static public List executeQueryList(Class clz, String sql) throws SQLException {
       return executeQueryList(clz,sql, (dbmsType==DBMS_MYSQL?0:1), 1000);
   }

    /**
     * 获取多个可用的UID. 注意: 此函数不可在事务中间使用
     *
     * @return UID >-1可用
     */
     public static long[] nextUid(String tblname, int count) throws SQLException {
        if (count<1){
            return null;
        }

        StringBuilder sqltemp = new StringBuilder();
        ResultSet rs = null;
        Long data = null;
        Uidtable uidtable = null;

        /*create table ( nextuid int not null, tablename varchar(64) not null )*/
        String sql = "select nextuid from uidtable where tablename='"+tblname+"'";
        if (data==null){
            //from DBMS
            rs = ProxoolMulti.executeQuery(sql,1);
            if (rs.next()) {
                data = rs.getLong(1);
                uidtable = new Uidtable(tblname,data);
            }else{
                data=0L;
            }
        }

        long[] uids = new long[count];
        boolean rtn = false;
        if (null!=uidtable){
            uidtable.setNextuid(uidtable.getNextuid()+count);
            sql = new UidtableExt(uidtable).toUpdateSql();
            rtn = ProxoolMulti.executeBatch(sql, false);
        }else{
            uidtable = new Uidtable();
            uidtable.setTablename(tblname);
            uidtable.setNextuid(0L+count);
            sql = new UidtableExt(uidtable).toInsertSql();
            rtn = ProxoolMulti.executeBatch(sql, false);
        }

       for (int i=count; i>0; i--){
           uids[i-1] = uidtable.getNextuid()-i+1;
       }

        return rtn?uids:null;
    }

    /**
     * 获取一个可用的UID. 注意: 此函数不可在事务中间使用
     *
     * @return UID >-1可用
     */
    public static long nextUid(String tblname) throws SQLException {
        long[] uids = nextUid(tblname,1);
        if (null==uids){
            return -1;
        }else{
            return uids[0];
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
        	
        	
            Proxool.init();
/*
            System.out.println(" ----------每次连接是不同的----不释??------------------ ");
            for (int i = 0; i < 5; i++) {
                Connection conn = Proxool.getConnection("MDB");
                System.out.println(i+" - "+conn);
            }

            System.out.println(" ----------每次连接是不固定??-----释放----------------- ");
            for (int i = 0; i < 20; i++) {
                Connection conn = Proxool.getConnection("MDB");
                System.out.println(i+" - "+conn);
                conn.close();
            }

            System.out.println(" ----------每次连接是不固定??------释放---------------- ");
            for (int i = 0; i < 20; i++) {
                Connection conn = ProxoolMulti.getConnection();
                System.out.println(i+" - "+conn);
                conn.close();
            }

            System.out.println(" -----------每次连接是固定的-----没释??---------------- ");
            for (int i = 0; i < 20; i++) {
                Connection conn = ProxoolMulti.getConnection();
                System.out.println(i+" - "+conn);
            }

*/
 /*
            System.out.println(" -----------每次连接是不固定??-----释放ProxoolMulti.close(rs)---------------- ");
            for (int i = 0; i < 20; i++) {

                ResultSet rs = ProxoolMulti.executeQuery("select count(*) from rnmoshown", 1);
                rs.afterLast();

                System.out.println(i+" - conn="+rs.getStatement().getConnection()+"; db="+rs.getStatement().getConnection().getCatalog());
                System.out.println(i+" - "+rs.getRow());
                ProxoolMulti.close(rs);
                ProxoolMulti.close();
                rs = ProxoolMulti.executeQuery("select count(*) from rnmoshown", 1);
            }

   */
            ProxoolMulti.getConnectionByAlias("mysqldb");
            int uid = ProxoolMulti.executeUpdateGeneratedKey("insert into id_test values(1,'v1')");
            System.out.println("1="+uid);
            uid = ProxoolMulti.executeUpdateGeneratedKey("insert into id_test(col1) values('v1000')");
            System.out.println("1000="+uid);
            while (true){
                try{
                    Thread.sleep(1000);

//                    ResultSet rs = ProxoolMulti.executeQueryCount("select count(*) from uidtable where 1=1");
//                    if (rs.next()){
//                        System.out.println(" count="+rs.getInt(1));
//                    }
//                    Connection conn = rs.getStatement().getConnection();
//                    System.out.println(" conn="+conn+"; db="+conn.getCatalog());
//                    ProxoolMulti.close(rs);
//                    ProxoolMulti.close();
                    
                    int rowCount = ProxoolMulti.executeQueryRowCount("select count(*) from uidtable where 1=1");
                    System.out.println(" rowCount="+rowCount);
                    
                    for(int i=0; i<10; i++){
	                    List list = ProxoolMulti.executeQueryList(Uidtable.class, "select * from uidtable",0,10);
	                    System.out.println(" list="+list.size());
	                    Thread.sleep(3000);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    ProxoolMulti.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class DbmsConnectionInfo {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(DbmsConnectionInfo.class);
	
	protected String dbAlias = "DB";
	protected Connection conn = null;
	protected int count = 0;
	
	protected List<String> sqlList = new ArrayList<String>();
	protected List<String> lastSqlList = new ArrayList<String>();
	
	protected String lastSqlError = "";
}
