package exam;

public class ExamA1 extends ExamA {
	static {System.out.print("a");}
	
	public ExamA1(){
		System.out.print("b");
	}
	public ExamA1(int i){
		System.out.print("c");
	}
	public int f1(int a, int b){ 
		Inner inner = new Inner();
		return 0;
	}
	
	static boolean foo(char c){
		System.out.print(c);
		return true;
	}
	
	public class Inner{
	}
	
    public static void main(String[] args) {
    	ExamA1 a1 = new ExamA1();  //2b
    	Inner inner = null;
//    	inner = new Inner();  //error
//    	inner = new a1.Inner();  //error
//    	inner = new ExamA1.Inner(); //error
    	inner = new ExamA1().new Inner();  //2b
    	
    	ExamA a = new ExamA1();  //2b
    	a =  new ExamA1();  //2b
    	
    	a = new ExamA1(3);   //2c
    	a =  new ExamA1(3);  //2c
    	
    	int i = 0;
    	for(foo('A'); foo('B')&&(i<2); foo('C')){
    		i++;
    		foo('D');
    	}
    	
    }
}


