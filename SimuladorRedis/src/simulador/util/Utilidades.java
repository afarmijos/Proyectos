package simulador.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utilidades {

	
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
	
public static List<String> leerArchivo(String archivo) throws 
FileNotFoundException, IOException{
		
		
		List<String> contenido=new ArrayList<String>();
		
		String cadena;
	      FileReader f;
		try {
			f = new FileReader(archivo);
			
			BufferedReader b = new BufferedReader(f);
		      while((cadena = b.readLine())!=null) {
		    	  contenido.add(cadena);
		      }
		      b.close();
			
		      
		      
		} catch (FileNotFoundException e) {
			throw e;
			
		} catch (IOException e) {
			throw e;
			
		}
		
		return contenido;
		
	}
	
	public static void main (String args[]){
		
		//System.out.println(new Aleatorio().generarEnteroAleatorio(2));
		
		System.out.println(Utilidades.generarEnteroAleatorio(0,3));
	
	}
	
}

