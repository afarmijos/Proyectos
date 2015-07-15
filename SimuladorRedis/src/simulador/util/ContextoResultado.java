package simulador.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ContextoResultado {

	//public static List<long[]> listaResultados=null;
	
	public ContextoResultado(){
		//listaResultados= new ArrayList<long[]>();
	}
	
	
	public static void main (String args[]){
		
		//System.out.println(new Aleatorio().generarEnteroAleatorio(2));
		
		System.out.println(Utilidades.generarEnteroAleatorio(0,3));
	
	}
	
	public static long []obtenerTiemposGuardados(List<long[]> listaResultados){
		long tiempos[]=null;
		
			tiempos= new long[listaResultados.size()];
			for (int i = 0; i < listaResultados.size(); i++) {
				tiempos[i]=listaResultados.get(i)[3];
			}
		
		return tiempos;
	}	
	
	public static double calcularTiempoPromedio(List<long[]> listaResultados,BufferedWriter writer){
		
		double promedio=0;
		
		long tiempos[]=obtenerTiemposGuardados(listaResultados);
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		try {
		for (long l : tiempos) {
			//System.out.println("ResultadoFinal:"+l);
			stats.addValue(l);
			writer.write("ResultadoFinal:"+l+"\n");
			
		}
		//System.out.println("Tamanio:"+tiempos.length);
		writer.write("Tamanio:"+tiempos.length+"\n");
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		promedio=stats.getMean();
		
		return promedio;
	}
	
	
}
