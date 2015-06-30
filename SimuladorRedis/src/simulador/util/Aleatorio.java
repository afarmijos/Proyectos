package simulador.util;

import java.util.Random;

public class Aleatorio {

	
	public static long generarEnteroAleatorio(int cantidadDigito){
		
		return Math.round((float)Math.random()*Math.pow(10, cantidadDigito));
		
		
	}
	
	/**
	 * 
	 * @param minimo Minimo Incluido
	 * @param maximo Maximo Excluido
	 * @return
	 */
	public static int generarEnteroAleatorio(int minimo, int maximo){
		
		Random random = new Random();
		int randomNumber = 0;
		
		if (maximo==minimo)
			return minimo;
		
		randomNumber = random.nextInt(maximo - minimo) + minimo;
		
		return randomNumber;
		
	}
	
	public static void main (String args[]){
		
		//System.out.println(new Aleatorio().generarEnteroAleatorio(2));
		
		System.out.println(Aleatorio.generarEnteroAleatorio(0,3));
	
	}
	
}

