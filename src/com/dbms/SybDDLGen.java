package com.dbms;

import com.sybase.ddlgen.DDLGenerator;

/**
 * gen DDL for sybase
 * 
 * @author xiaoran27
 * 
 */
public class SybDDLGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length<2){
			args = "-Usa -Pcnpm123 -SCNPM_SERVER -TU -Dmaster -TU -N% -Omaster.ddl".split(" ");
		}
		
	    try
	    {
	    	DDLGenerator.main(args) ;
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }

	    System.exit(0);
	    
	}

}
