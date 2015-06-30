package simulador.cliente;

import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import simulador.util.Aleatorio;
import simulador.util.Configuracion;

public class Cliente {

	private	String nombreCliente;
	private boolean consolaHabilitada=false;
	private int idCliente=0;
	
	Jedis jedis =null;
	Properties propiedades=null;
	ControlOperacion controlOperacion=null;
	
	
	public void conectar(){

		String ip=propiedades.getProperty("servidorRedis.ip");
		String clave=propiedades.getProperty("servidorRedis.clave");
		int puerto=0;
		
		try{
		puerto=Integer.valueOf(propiedades.getProperty("servidorRedis.puerto"))
				.intValue();
		
		}catch(NumberFormatException e){			
			puerto=0;
		}		
		
		try{
			
			jedis = new Jedis(ip,puerto);
			jedis.auth(clave);
			
		}catch(JedisDataException e){
			System.out.println("Error en la autenticacion");
			e.printStackTrace();
			
			}
		catch(Exception e){
			System.out.println("El servidor está abajo. Levante el servidor");
			e.printStackTrace();
			
		}
		jedis.incr("secuencia");
		
		
	}
	
	
	private String resolverExpresion(String expresion){
		
		String expresionResuelta=expresion;
		
		if (expresionResuelta.contains("{numeroCliente}"))
			expresionResuelta=expresionResuelta.replace("{numeroCliente}", String.valueOf(idCliente));
		
		if (expresionResuelta.contains("{nombreCliente}"))
			expresionResuelta=expresionResuelta.replace("{nombreCliente}", nombreCliente);
		
		return expresionResuelta;
	}
	
	public String asignarNombreCliente(){
		
		return resolverExpresion(propiedades.getProperty("nombreCliente"));
	}
	
	public Cliente(){
		propiedades=Configuracion.getProperties("config.properties");
		conectar();
		idCliente=Integer.valueOf(jedis.get("secuencia"));
		nombreCliente=asignarNombreCliente();
		controlOperacion= new ControlOperacion(propiedades);
		
	}
	
	private int generarOperacionAleatoria(){
		
		return Aleatorio.generarEnteroAleatorio(0,2);
	}
	
	private String generarClaveAleatoria(){
		
		String claveGenerada="";		
		
		Set<String> listaClaves=jedis.keys("Cliente*");
		
		do{			
			claveGenerada=nombreCliente+"."+"Clave"+Aleatorio.generarEnteroAleatorio(3);			
		}while (listaClaves.contains(claveGenerada));			
		
		return claveGenerada;
	}
	
	
	
	private void ejecutarOperacion(int operacion, Jedis jedisServer ){
		
		if ( "set".equals(controlOperacion.obtenerNombreOperacion(operacion))){
			
			String clave=generarClaveAleatoria();
			String valor="0";
						
			//presentarMensaje("Se seteara la clave generada="+clave+" valor="+valor);
			
			jedisServer.set(clave, valor);
						
			//presentarMensaje("Se seteo la clave generada="+clave+" valor="+valor);
			
		}
		
		if ( "get".equals(controlOperacion.obtenerNombreOperacion(operacion))){
			
			Set<String> listaClaves=jedisServer.keys("Cliente*");
			
			if (!((listaClaves==null)||(listaClaves.isEmpty())))
			{				
				String nombreClave=listaClaves.iterator().next();			
				String valor=jedisServer.get(nombreClave);			
			}
						
		} 
		
		
	}
	
	
	public void presentarMensaje(String mensaje){
		if (isConsolaHabilitada()) {
			System.out.println(nombreCliente+":"+mensaje);				
		}
	}
	
	
	
	
	
	public void ejecutarAleatorio(int cantidadOperaciones){
		
		
	    for (int contador = 1; contador <= cantidadOperaciones; ) {
			
	    	int operacionAEjecutar=generarOperacionAleatoria();
	    	
	    	if (controlOperacion.esValidaOperacion(operacionAEjecutar)){
	    		
	    		
	    		long tiempoOperacionInicio=System.nanoTime();
		    	ejecutarOperacion(operacionAEjecutar, jedis);		    	
		    	long tiempoOperacionFin=System.nanoTime();
		    	
		    	controlOperacion.contabilizarNuevaOperacion(operacionAEjecutar);
		    	contador++;
		    	
		    	String duracionTexto=String.valueOf(tiempoOperacionFin-tiempoOperacionInicio);
		    	presentarMensaje("Op:"+controlOperacion.obtenerNombreOperacion(operacionAEjecutar)+" duracion:"+duracionTexto);
	    	}
	    	
	    	
	    	
		}
	    
	    controlOperacion.presentarControl();
	    
	}

	public void ejecutar(int cantidadOperaciones ){
		
		if ("true".equals(propiedades.getProperty("modoTrace"))){
			System.out.println("Modo trace");
		}else{
			ejecutarAleatorio(cantidadOperaciones);
		}
		
	}

	public boolean isConsolaHabilitada() {
		return consolaHabilitada;
	}



	public void setConsolaHabilitada(boolean consolaHabilitada) {
		this.consolaHabilitada = consolaHabilitada;
	}
	
}
