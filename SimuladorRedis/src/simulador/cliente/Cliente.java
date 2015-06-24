package simulador.cliente;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import simulador.util.Aleatorio;

public class Cliente {

	private	String nombreCliente;
	private boolean consolaHabilitada=false;
	Jedis jedis =null;
	
	public String conectar(){

		
		try{
			
			jedis = new Jedis("localhost", 6379);
			jedis.auth("mypass");
			
		}catch(JedisDataException e){
			System.out.println("Error en la autenticacion");
			e.printStackTrace();
			return null;
			}
		catch(Exception e){
			System.out.println("El servidor está abajo. Levante el servidor");
			e.printStackTrace();
			return null;
		}
		
		jedis.incr("secuencia");
		return "Cliente"+jedis.get("secuencia");
		
	}
	
	public Cliente(String nombre){
		nombreCliente=conectar();
	}
	
	public Cliente(){
		nombreCliente=conectar();
	}
	
	private long generarOperacionAleatoria(){
		
		return Aleatorio.generarEnteroAleatorio(1)%2;
	}
	
	private String generarClaveAleatoria(){
		
		String claveGenerada="";
		
		
		
		Set<String> listaClaves=jedis.keys("Cliente*");
		
		do{
			
			claveGenerada=nombreCliente+"."+"Clave"+Aleatorio.generarEnteroAleatorio(3);
			
		}while (listaClaves.contains(claveGenerada));
			
			
		
		return claveGenerada;
	}
	
	private String generarValorAleatorio(){
		
		String claveGenerada="";
		
		claveGenerada=nombreCliente+"."+"Clave"+1;
		
		return claveGenerada;
	}
	
	private void ejecutarOperacion(long operacion, Jedis jedisServer ){
		
		if (operacion==0){
			
			String clave=generarClaveAleatoria();
			String valor="0";
						
			presentarMensaje("Se seteara la clave generada="+clave+" valor="+valor);
			
			jedisServer.set(clave, valor);
						
			presentarMensaje("Se seteo la clave generada="+clave+" valor="+valor);
			
		}
		
		if (operacion==1){
			
			presentarMensaje("Extraer el primer valor del primer key"+nombreCliente);
			
			Set<String> listaClaves=jedisServer.keys("Cliente*");
			
			if ((listaClaves==null)||(listaClaves.isEmpty())){
				presentarMensaje("No hay claves ingresadas. No se pudo extraer valores.");
			}else{
			
				String nombreClave=listaClaves.iterator().next();			
				String valor=jedisServer.get(nombreClave);
				
				presentarMensaje("Se extrajo el valor"+valor+" de la clave "+nombreClave);
				
			}
			
			
			
						
		} 
		
		
	}
	
	
	public void presentarMensaje(String mensaje){
		if (isConsolaHabilitada()) {
			System.out.println(nombreCliente+":"+mensaje);				
		}
	}
	
	public void ejecutar(int cantidadOperaciones){
		
		
	    for (int i = 1; i <= cantidadOperaciones; i++) {
			
	    	long operacionAleatoria=generarOperacionAleatoria();	    	
	    	ejecutarOperacion(operacionAleatoria, jedis);
	    	
		}
	    
	    
	}



	public boolean isConsolaHabilitada() {
		return consolaHabilitada;
	}



	public void setConsolaHabilitada(boolean consolaHabilitada) {
		this.consolaHabilitada = consolaHabilitada;
	}
	
}
