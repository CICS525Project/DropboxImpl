package authentication;


public class Test {
	
	public static void main(String args[]) {
		Authentication auth = new Authentication();
		
		System.out.println(auth.createUser("john", "321"));
	}

}
