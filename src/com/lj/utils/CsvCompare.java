package com.lj.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * 两个csv文件进行按指定的field进行比对.
 * 输出3个文件：.{hhmmss}[.diff]
 *
 */
public class CsvCompare {
	
	private final String IGNORE="";

	public CsvCompare() {
	}
	
	//file: *.csv
	//seq: 0,2
	public Map<String,String> getSmallFile(String file, String seq) throws Exception {
		return getSmallFile( file,  seq, seq) ;
	}
	//file: *.csv
	//seq: 0,2
	//outseq: 0
	public Map<String,String> getSmallFile(String file, String seq, String outseq) throws Exception {
		Map<String,String> rtnMap = new HashMap<String,String>();
		
		String[] seqs = seq.split("[,]");
		String[] outseqs = outseq.split("[,]");
		BufferedReader bf= new BufferedReader(new FileReader(file));
		String line = null;
		do{
			line = bf.readLine();
			if (null==line || line.trim().length()<1){
				continue;
			}
			String[] values = line.split("[,]");
			
			String key = "";
			for(String no : seqs ){
				int n = Integer.parseInt(no);
				key = key+","+values[n];
			}
			rtnMap.put(key, line);	

			if (!seq.equals(outseq)){
				key = "";
				for(String no : outseqs ){
					int n = Integer.parseInt(no);
					key = key+","+values[n];
				}
				rtnMap.put(key, IGNORE);	
			}
			
		}while (null!=line);
		bf.close();
		
		System.out.println("Finished: read "+file+"; lines[/2]: "+rtnMap.size()+" date:"+new Date());
		
		return rtnMap;
	}

	public void compareFile(String sfile, String sseq, String bfile, String bseq ) throws Exception {
		compareFile(sfile, sseq, sseq, bfile, bseq ) ;
	}
	
	//sfile: *.csv
	//sseq: 0,1
	//outseq: 0
	//bfile: *.csv
	//bseq: 0,1
	public void compareFile(String sfile, String sseq, String outseq, String bfile, String bseq ) throws Exception {
		Map<String,String> sdataMap = this.getSmallFile(sfile, sseq, outseq);
		
		String ext = "."+TextFormat.getDateTime(TextFormat.HHMMSS);
		BufferedWriter diffbw= new BufferedWriter(new FileWriter(sfile+".diff"+ext));
		BufferedWriter bw= new BufferedWriter(new FileWriter(sfile+ext));
		BufferedWriter outbw= new BufferedWriter(new FileWriter(bfile+ext));
		
		String[] bseqs = bseq.split("[,]");
		String[] outseqs = outseq.split("[,]");
		BufferedReader bf= new BufferedReader(new FileReader(bfile));
		String line = null;
		int cnt = 0;
		do{
			line = bf.readLine();
			if (null==line || line.trim().length()<1){
				continue;
			}
			String[] values = line.split("[,]");
			if(values[1].endsWith("?")){
				values[1]=values[1].substring(0,values[1].length()-1);
			}

			//
			String key = "";
			for(String no : bseqs ){
				int n = Integer.parseInt(no);
				key = key+","+values[n];
			}
			if (null!=sdataMap.get(key)){
				bw.append(line);
				bw.newLine();
				
				sdataMap.put(key,IGNORE);  //match
			}

			//
			if (!sseq.equals(outseq)){
				key = "";
				for(String no : outseqs ){
					int n = Integer.parseInt(no);
					key = key+","+values[n];
				}
				if (null!=sdataMap.get(key)){
					outbw.append(line);
					outbw.newLine();
				}
			}
			
			cnt ++;
			if (0==cnt%1000){
				bw.flush();
				outbw.flush();
				System.out.println(cnt+" waiting ... ..."+new Date());
			}

		}while (null!=line);

		bf.close();
		bw.close();
		outbw.close();
		
		//diff
		for (String s : sdataMap.values()){
			if (!s.equals(IGNORE)){
				diffbw.append(s);
				diffbw.newLine();
			}
		}
		diffbw.flush();
		diffbw.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CsvCompare test = new CsvCompare();
		try {
			System.out.println("=====start===="+new Date());
			
			String dir="G:/workspace/sim2/doc/";
			String[] params = args;
			if (params.length<1){
				params = new String[]{
						dir+"sigm.txt","0,1","0"
						,dir+"check.all.txt","0,1"
				};
				
				/*params = new String[]{
						dir+"sigm.txt","0,1","0,1"
						,dir+"sigm.txt.match0","0,1"
				};*/
			}
			System.out.println("Params: "+Arrays.toString(params));
			
			//test.getSmallFile(params[0], params[1], params[2]);
			test.compareFile(params[0], params[1], params[2], params[3], params[4]);
			
			System.out.println("=====finished===="+new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
