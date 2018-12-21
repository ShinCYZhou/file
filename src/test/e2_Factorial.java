class Factorial{
    public static void main(String[] a){
		System.out.println(new Fac().ComputeFac(10)) ;
    }
}
class Fac extends F{ 	// e: undefined class
    public int ComputeFac(int num){
		int num_aux;
		int[] s;
		i=1; 		// e: undefined

		if (num < 1)
			num_aux = 1  //e: miss ;
		else
			num_aux = num * (this.ComputeFac(num-1));
		return num_aux ;
}
}
