package test;

import lombok.*;

@ToString
public class VmOptionTest {
	@Setter
	@Getter
	private int foo=0;

	@Synchronized
	private void foo(){
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] v = new String[100*1024*1024];
		final String fs="�����ڴ��������۲��ڴ�ķ����ʹ�����;";
		v[0]=fs;
		for(int i=1; i<v.length; i++){
			v[i]=v[i-1]+fs;
		}
	}

}
