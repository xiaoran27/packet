
/*
SYBASE 12.5 LICENSE FROM SYSAM-1.0:

#license.dat
SERVER cnpm-server ANY 29722
VENDOR SYBASE /opt/sybase/SYSAM-1_0/bin/SYBASE
#USE_SERVER

INCREMENT ASE_SERVER SYBASE 12.0 permanent uncounted EC44123C6B70 \
	HOSTID=ANY ck=50 SN=GA

#NOTE: You can edit the hostname on the server line (1st arg).
#      The (optional) daemon-path on the VENDOR line (2nd arg).
#      Most other changes will invalidate this license.


PACKAGE ASE_SECDIR SYBASE 12.5 10C95C0BAAC2 \
        COMPONENTS="ASE_ASM:12.0:1 ASE_DIRS:12.5:1"

PACKAGE ASE_XMLMGMT SYBASE 12.5 D54316AAF841 \
        COMPONENTS="ASE_EJB:12.5:1 ASE_JAVA:12.0:1 ASE_XML:12.5:1"

PACKAGE ASE_CONTMGT SYBASE 12.5 9363D3F832CE \
        COMPONENTS=ASE_XFS:12.5:1

PACKAGE ASE_EBIZ SYBASE 12.5 03A72E64A3DE COMPONENTS="ASE_EJB:12.5:1 \
        ASE_JAVA:12.0:1 ASE_ASM:12.0:1 ASE_DIRS:12.5:1 ASE_XFS:12.5:1"

PACKAGE ASE_DRECOVERY SYBASE 12.5 02693050EAAC \
        COMPONENTS="ASE_SERVER:12.0:1 REP_SERVER:12.5:1"

PACKAGE SY_RTDS SYBASE 2.0 EA019F804A92 COMPONENTS="ASE_SERVER:12.0:1 \
	ASE_MESSAGING:12.5:1 ASE_XML:12.5:1 ASE_JAVA:12.0:1 \
	REP_SERVER:12.6:1"

INCREMENT ASE_SERVER SYBASE 12.0 PERMANENT uncounted EC44123C6B70 SN=GA OVERDRAFT=10000 ck=0
*/

package com.dbms.sybase;

import com.sybase.license.LicenseEntry;

public class SybLicense {

	/**
	 * @param args
	 */
	public static void syb125License(String[] args) {
	    try{
	    	com.sybase.license.License.main(args);
	    }catch (Exception e){
	      e.printStackTrace();
	    }
	}
	
	public static void syb125LicenseEntry(String[] args) {
	    try{
	    /*
	     USAGE_SUMMARY=Usage: LicenseManager <option> <arguments>\n\n
			options:\n
			\t\t-H Displays This Help\n
			\t\t-V Prints Version and Copyright\n
			\t\t-G Run application in Graphical Mode (default)\n
			\t\t-I Run application in Interactive Console Mode\n
			\t\t-S Run application in Silent Mode.\n
			\t\t-C Run application in Console Mode.\n\n
			arguments:\t[Applicable only with -C or -S option].\n\n
			\t\tsybase=sybase directory\n\t\thost=hostname\n
			\t\tport=port number\n\t\tfeature=feature name\n
			\t\tcount=feature count\n\t\torder=order number\n
			\t\tversion=software version\n
			\t\tcode=authorization code
	     */
	    	LicenseEntry.main(args);
	    }catch (Exception e){
	      e.printStackTrace();
	    }

	}
	
	
	public static void main(String[] args) {
		syb125License(args);
		syb125LicenseEntry(args);
	}

}
