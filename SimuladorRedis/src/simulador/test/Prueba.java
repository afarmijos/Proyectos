package simulador.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import simulador.util.Configuracion;

public class Prueba {

	public static void main(String[] args) {
		
		/*
		Cliente cliente = new Cliente();
		
		long tiempoInicio=System.nanoTime();
		cliente.setConsolaHabilitada(true);
		cliente.ejecutar(1000);
		long tiempoFin=System.nanoTime();
		
		System.out.println("Tiempo total de simulacion:"+((tiempoFin-tiempoInicio))+" ns");
		*/
		/*
		Properties propiedades= new Properties();
		propiedades.setProperty("numeroClientes", "1");
		propiedades.setProperty("modoSimulacion", "trace");
		Configuracion.safeProperties(propiedades, "config.properties");
		*/
		
		System.out.println("Prueba:"+Configuracion.getProperties("config.properties").getProperty("modoSimulacion"));
		
	}

}
