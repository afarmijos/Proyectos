package simulador.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import simulador.cliente.Cliente;
import simulador.util.Configuracion;
import simulador.util.ContextoResultado;
import simulador.util.Resultado;
import simulador.util.Utilidades;

public class MainTest {
		
	
	public static void main(String[] args) {
		BufferedWriter writer = null;
		String ruta="C:/Users/Alex/Google Drive/Phd/Clases/SistemasOperativosAvanzados/Prueba1.txt";
				
        try {
            //create a temporary file
            File logFile = new File(ruta);
            writer = new BufferedWriter(new FileWriter(logFile));            
        } catch (Exception e) {
            e.printStackTrace();
        } 
		
		
		Properties propiedades=Configuracion.getProperties("config.properties");
		Jedis jedis=null;
		boolean existenClientesPendientes=true;
		boolean consolaHabilitada=false;
		int cantidadProcesados=0;
		int cantidadAProcesar=0;
		int cantidadAProcesarParalelo=0;
		
		ThreadGroup grupoResultados = new ThreadGroup("Resultados");
		Resultado resultado= new Resultado(grupoResultados,"ResultadoGeneral");
		resultado.listaResultados= new ArrayList<long[]>();
		resultado.writer=writer;
		
		cantidadAProcesar=Integer.valueOf(propiedades.getProperty("cliente.cantidadClientes")).intValue();
		
		cantidadAProcesarParalelo=Integer.valueOf(propiedades.getProperty("cliente.cantidadClientesParalelo")).intValue();
		
		consolaHabilitada="true".equals(propiedades.getProperty("cliente.consolaHabilitada"));
		
		jedis=Configuracion.conectar(propiedades);
		if (jedis==null){
			System.out.println("Levante el servidor.");
			return;
		}
		
		
		
		ThreadGroup grupo = new ThreadGroup("Clientes");
		
		System.out.println("Iniciando Prueba");
		do{
			
			cantidadAProcesarParalelo=cantidadAProcesarParalelo-grupo.activeCount();
			
			if (cantidadAProcesarParalelo>(cantidadAProcesar-cantidadProcesados))
				cantidadAProcesarParalelo=cantidadAProcesar-cantidadProcesados;
			
			List<Cliente> listaClientes= new ArrayList<Cliente>();
			
			for (int i=1;i<=cantidadAProcesarParalelo;i++){
				
				cantidadProcesados++;				
				Cliente clienteNuevo=new Cliente(grupo,propiedades,cantidadProcesados,
										consolaHabilitada,grupoResultados);
				clienteNuevo.writer=writer;
				listaClientes.add(clienteNuevo);
				clienteNuevo.start();
			}
			System.out.println("Clientes ejecutandose en paralelo:"+cantidadAProcesarParalelo);
			System.out.println("Clientes ejecutados en total:"+cantidadProcesados);
			
			synchronized(grupo){
	           
						try {
							
							grupo.wait();
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	           
			}			
			
			if (cantidadProcesados==cantidadAProcesar)
				existenClientesPendientes=false;
			
		}while (existenClientesPendientes);
		
		System.out.println("Finalizando Prueba");
		System.out.println("Mostrando Resultados");
		
		
		double tiempoPromedioOperaciones=ContextoResultado.calcularTiempoPromedio(resultado.listaResultados,writer);
		
		try {
			writer.write("Tiempo Promedio de Operaciones:"+Utilidades.formateoToString(tiempoPromedioOperaciones,"####.##"));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
