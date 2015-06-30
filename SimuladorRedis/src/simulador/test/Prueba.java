package simulador.test;

import simulador.cliente.Cliente;

public class Prueba {

	
	
	public static void main(String[] args) {
		
		
		Cliente cliente = new Cliente();
		
		/*
		prueba(cliente,0);
		prueba(cliente,0);
		prueba(cliente,1);
		prueba(cliente,1);
		prueba(cliente,1);
		prueba(cliente,0);
		prueba(cliente,1);
		prueba(cliente,0);
		prueba(cliente,0);
		prueba(cliente,1);
		prueba(cliente,0);
		prueba(cliente,0);
		
		cliente.presentarControl();
		*/
		long tiempoInicio=System.nanoTime();
		cliente.setConsolaHabilitada(true);
		cliente.ejecutar(800);
		long tiempoFin=System.nanoTime();
		
		System.out.println("Tiempo total de simulacion:"+((tiempoFin-tiempoInicio))+" ns");
		
		/*
		Properties propiedades= new Properties();
		propiedades.setProperty("numeroClientes", "1");
		propiedades.setProperty("modoSimulacion", "trace");
		Configuracion.safeProperties(propiedades, "config.properties");
		*/
		
		//System.out.println("Prueba:"+Configuracion.getProperties("config.properties").getProperty("modoSimulacion"));
		
	}

}
