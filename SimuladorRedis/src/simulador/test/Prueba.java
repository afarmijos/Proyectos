package simulador.test;

import simulador.cliente.Cliente;

public class Prueba {

	public static void main(String[] args) {
		
		Cliente cliente = new Cliente();
		
		long tiempoInicio=System.nanoTime();
		cliente.setConsolaHabilitada(true);
		cliente.ejecutar(1000);
		long tiempoFin=System.nanoTime();
		
		System.out.println("Tiempo total de simulacion:"+((tiempoFin-tiempoInicio))+" ns");
		
	}

}
