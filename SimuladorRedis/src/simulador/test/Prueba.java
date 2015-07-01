package simulador.test;

import java.util.Properties;

import simulador.cliente.Cliente;
import simulador.util.Configuracion;

public class Prueba {

	
	
	public static void main(String[] args) {

		
		Properties propiedades=Configuracion.getProperties("config.properties");
		boolean existenClientesPendientes=true;
		boolean consolaHabilitada=false;
		int cantidadProcesados=0;
		int cantidadAProcesar=0;
		int cantidadAProcesarParalelo=0;
		
		cantidadAProcesar=Integer.valueOf(propiedades.getProperty("cliente.cantidadClientes")).intValue();
		
		cantidadAProcesarParalelo=Integer.valueOf(propiedades.getProperty("cliente.cantidadClientesParalelo")).intValue();
		
		consolaHabilitada="true".equals(propiedades.getProperty("cliente.consolaHabilitada"));
		
		ThreadGroup grupo = new ThreadGroup("Clientes");
		
		do{
			
			cantidadAProcesarParalelo=cantidadAProcesarParalelo-grupo.activeCount();
			
			if (cantidadAProcesarParalelo>(cantidadAProcesar-cantidadProcesados))
				cantidadAProcesarParalelo=cantidadAProcesar-cantidadProcesados;
				
			for (int i=1;i<=cantidadAProcesarParalelo;i++){
				cantidadProcesados++;
				System.out.println("Creando nuevo hilo:"+cantidadProcesados);
				new Cliente(grupo,propiedades,cantidadProcesados,consolaHabilitada).start();
			}
			
			//int cantidadHilosActivos=grupo.activeCount(); 
			
			synchronized(grupo){
	           
					//do{
						try {
							
							grupo.wait();
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					//}while(cantidadHilosActivos==grupo.activeCount());
	           
			}
			
			if (cantidadProcesados==cantidadAProcesar)
				existenClientesPendientes=false;
			
		}while (existenClientesPendientes);
		
	}

}
