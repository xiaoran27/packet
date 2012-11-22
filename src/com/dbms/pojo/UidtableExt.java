package com.dbms.pojo;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * ά��UID�ı� to SQL(insert/delete/update) String.
 *
 * @author Xiaoran27 $author$
 * @version $Revision: 1.1 $
  */
public class UidtableExt implements I2SqlString {
  private Uidtable uidtable=null;

  public UidtableExt(Uidtable uidtable) {
    this.uidtable = uidtable;
  }

  /**
   * ɾ���SQL
   *
   * @see com.xiaoran27.db.entity.pojo.ext.comm.I2SqlString#toDeleteSql()
   */
  public String toDeleteSql() {
    StringBuilder sb = new StringBuilder(" delete from ");  //DML
    sb.append(" uidtable ");  //table
    sb.append(" where 1 = 1 ");
    //KEY OR UNIQUE INDEX
    sb.append(" and ");
    if (null==uidtable.getTablename()){
      sb.append(" tablename is null ");
    }else{
      sb.append(" tablename =  '").append(StringEscapeUtils.escapeSql(uidtable.getTablename())).append("'");  //tablename
    }

    return sb.toString();
  }

  /**
   * ��ӵ�SQL
   *
   * @see com.xiaoran27.db.entity.pojo.ext.comm.I2SqlString#toInsertSql()
   */
  public String toInsertSql() {
    StringBuilder sb = new StringBuilder(" insert into ");  //DML
    sb.append(" uidtable ");  //table
    sb.append(" ( tablename ,nextuid  )");  // columns
    sb.append(" values ( ");  //values
    if (null==uidtable.getTablename()){
      sb.append(" null ");
    }else{
      sb.append(" '").append(StringEscapeUtils.escapeSql(uidtable.getTablename())).append("'");  //tablename
    }
    sb.append(",");
    sb.append(" ").append(uidtable.getNextuid());  //nextuid
    sb.append(" ) ");

    return sb.toString();
  }

  /**
   * �޸ĵ�SQL
   *
   * @see com.xiaoran27.db.entity.pojo.ext.comm.I2SqlString#toUpdateSql()
   */
  public String toUpdateSql() {
    StringBuilder sb = new StringBuilder(" update ");  //DML
    sb.append(" uidtable ");  //table
    sb.append(" set ");  //SET
    if (null==uidtable.getTablename()){
      sb.append(" tablename = null ");
    }else{
      sb.append(" tablename = '").append(StringEscapeUtils.escapeSql(uidtable.getTablename())).append("'");  //tablename
    }
    sb.append(",");
    sb.append(" nextuid = ").append(uidtable.getNextuid());  //nextuid

    sb.append(" where 1 = 1 ");
    //KEY OR UNIQUE INDEX
    sb.append(" and ");
    if (null==uidtable.getTablename()){
      sb.append(" tablename is null ");
    }else{
      sb.append(" tablename =  '").append(StringEscapeUtils.escapeSql(uidtable.getTablename())).append("'");  //tablename
    }

    return sb.toString();
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Uidtable uidtable = new Uidtable();
    uidtable.setTablename("0");  //tablename
    uidtable.setNextuid(0l);  //nextuid

     System.out.println("uidtable="+uidtable);
     System.out.println();

     UidtableExt uidtableExt = new UidtableExt(uidtable);
     System.out.println("uidtableExt="+uidtableExt);
     System.out.println("uidtableExt.toInsertSql()="+uidtableExt.toInsertSql());
     System.out.println("uidtableExt.toDeleteSql()="+uidtableExt.toDeleteSql());
     System.out.println("uidtableExt.toUpdateSql()="+uidtableExt.toUpdateSql());
  }

}

