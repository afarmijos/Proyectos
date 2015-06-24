package simulador.util;

public class Aleatorio {

	
	public static long generarEnteroAleatorio(int cantidadDigito){
		
		return Math.round((float)Math.random()*Math.pow(10, cantidadDigito));
		
		
	}
	
	public static void main (String args[]){
		
		System.out.println(new Aleatorio().generarEnteroAleatorio(1));
		
		System.out.println(Aleatorio.generarEnteroAleatorio(1)%2);
	
	}
	
}

