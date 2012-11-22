package test;

import org.apache.commons.configuration.Configuration;

import com.lj.config.Config;

public class ConfigTest implements Runnable {
	
	private Configuration cfg = null; //Config.loadAndWatchConfig("msg.properties");
	private int id=0;

	public ConfigTest(Configuration cfg,int id) {
		this.cfg=cfg;
		this.id=id;
	}
	
	public String toThreadName(){
		return this.getClass().getName()+"#"+id;
	}

	public void run() {
		while(true){
			try {
				System.out.println(Thread.currentThread().getName()+cfg.getLong("nolong"));
				
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println(e.getMessage()+System.currentTimeMillis());
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Configuration cfg = Config.loadAndWatchConfig("msg.properties");
			
			ConfigTest[] cfgRunnables = new ConfigTest[10];
			for(int i=0; i<cfgRunnables.length; i++){
				cfgRunnables[i] = new ConfigTest(cfg,i);
				new Thread(cfgRunnables[i],cfgRunnables[i].toThreadName()).start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
