package com.lj.packet;

import org.apache.log4j.Logger;

@Deprecated
public final class PacketCount {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(PacketCount.class);

    static private long interval = 1000L*60;

    static private long time = System.currentTimeMillis();
	static private long countSend = 0L;
	static private long countRead = 0L;
    static private long lasttime = System.currentTimeMillis();
    static private long lastCountSend = 0L;
    static private long lastCountRead = 0L;

	public PacketCount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void countSendIncre(){
        if (System.currentTimeMillis()-lasttime>interval){
            if (logger.isInfoEnabled()) {
                logger.info("countSendIncre(): " + toString());
            }
        }
		countSend ++;
	}

	public void countReadIncre(){
        if (System.currentTimeMillis()-lasttime>interval){
            if (logger.isInfoEnabled()) {
                logger.info("countReadIncre(): " + toString());
            }
        }
		countRead ++;
	}

	public void reset(){
        time = System.currentTimeMillis();
		countSend = 0;
		countRead = 0;
        lasttime = System.currentTimeMillis();
        lastCountSend = 0;
        lastCountRead = 0;
	}

	public String toString(){

	    long read=countRead;
        long write=countSend;
        StringBuffer sb= new StringBuffer();

        if (System.currentTimeMillis()-lasttime<10){
            sb.append("Elashed time(ms): ").append((System.currentTimeMillis()-time)).append(";");
            sb.append("Packet count: countRead = ").append(read).append(";").append("countSend = ").append(write).append(";");
        }else{
            sb.append("Elashed time(ms): ").append((System.currentTimeMillis()-lasttime)).append("/").append((System.currentTimeMillis()-time)).append(";");
            sb.append("Packet count(N/S): countRead = ").append(read-lastCountRead).append("/").append(read).append(";");
            sb.append("countSend = ").append(write-lastCountSend).append("/").append(write).append(";");

            lasttime = System.currentTimeMillis();
            lastCountSend = write;
            lastCountRead = read;
        }

		return sb.toString();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
