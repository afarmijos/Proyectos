package simulador.cliente;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import simulador.util.Configuracion;
import simulador.util.Utilidades;

public class Cliente extends Thread {

	private	String nombreCliente;
	private boolean consolaHabilitada=false;
	private int idCliente=0;
	
	Jedis jedis =null;
	Properties propiedades=null;
	ControlOperacion controlOperacion=null;
	
	public Cliente( ThreadGroup grupoHilo ,Properties propiedades, int idCliente, boolean consolaHabilitada){
		
		super(grupoHilo,"");
		
		this.propiedades=propiedades;		
		this.idCliente=idCliente;
		this.consolaHabilitada=consolaHabilitada;
		conectar();
		nombreCliente=asignarNombreCliente();
		controlOperacion= new ControlOperacion(propiedades);
		
		
	}
	
	
	
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
		//jedis.incr("secuencia");
		
		
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
		
		return resolverExpresion(propiedades.getProperty("cliente.nombreCliente"));
	}
	
	
	
	private int generarOperacionAleatoria(){
		
		return Utilidades.generarEnteroAleatorio(0,2);
	}
	
	private String generarClaveAleatoria(){
		
		String claveGenerada="";		
		
		Set<String> listaClaves=jedis.keys(nombreCliente+"*");
		
		do{			
			claveGenerada=nombreCliente+"."+"Clave"+Utilidades.generarEnteroAleatorio(0,1000000);			
		}while (listaClaves.contains(claveGenerada));			
		
		return claveGenerada;
	}
	
	public void presentarTodasClaves(){
		
		Set<String> listaClaves=jedis.keys("*");
		
		for (String clave : listaClaves) {
			System.out.println(clave+" tamanio:"+jedis.get(clave).length());
		}
		
	}
	
	
	
	private void ejecutarOperacion(int operacion, Jedis jedisServer ){
		
		if ( "set".equals(controlOperacion.obtenerNombreOperacion(operacion))){
			
			String clave=generarClaveAleatoria();
			String valor="0";
			
			jedisServer.set(clave, valor);
			
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
	
	
	private void ejecutarOperacion(String operacion, String clave, String valor ,Jedis jedisServer ){
		
		if ( "set".equals(operacion)){
			int tamanioValor=Integer.valueOf(valor).intValue();
			
			String nuevoValor=new String(new byte[tamanioValor]);
			
			jedisServer.set(clave, nuevoValor);
		}
		
		if ( "get".equals(operacion)){
			
			jedisServer.get(clave);
						
		} 
		
		
	}
	
	public void presentarMensaje(String mensaje){
		if (isConsolaHabilitada()) {
			System.out.println(nombreCliente+":"+mensaje);				
		}
	}
	
	
	
	
	
	public void ejecutarAleatorio(){
		
		
		int cantidadOperaciones=Integer.valueOf(propiedades.getProperty("cliente.aleatorio.cantidadOperaciones")).intValue();
		
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
	    
	    //controlOperacion.presentarControl();
	    
	}

	public void ejecutarTrace(){
		
		String rutaArchivo="";
		rutaArchivo=propiedades.getProperty("cliente.trace.rutaArchivo");
		rutaArchivo=rutaArchivo+ resolverExpresion(propiedades.getProperty("cliente.trace.formatoArchivo"));
		
		List<String> operacionesTrace=null;
		
		try {
			operacionesTrace = Utilidades.leerArchivo(rutaArchivo);
		} catch (IOException e) {
			System.out.println("Error al leer el archivo trace.");
			e.printStackTrace();
		}
		
		System.out.println(rutaArchivo);
		for (String valor : operacionesTrace) {			
			String parametros[]=valor.split(propiedades.getProperty("cliente.trace.separadorColumnasArchivo"));			
			
			
			long tiempoOperacionInicio=System.nanoTime();
			ejecutarOperacion(parametros[0], parametros[1], parametros[2], jedis);		    	
	    	long tiempoOperacionFin=System.nanoTime();
	    	
	    	controlOperacion.contabilizarNuevaOperacion(parametros[0]);
	    	
	    	
	    	String duracionTexto=String.valueOf(tiempoOperacionFin-tiempoOperacionInicio);
	    	presentarMensaje("Op:"+parametros[0]+" duracion:"+duracionTexto);
	    	
	    	/*
	    	try {
	    		long tiempo=100*(idCliente);
	        	presentarMensaje("Durmiendo "+(tiempo/1000)+" s");
				Thread.sleep(tiempo);
			} catch (InterruptedException e) {
				System.out.println("Interrupcion indesperada");
				e.printStackTrace();
			}*/
	    	
		}
		
		
		
	}
	
	@Override
	public void run() {        
        
		synchronized(this){
	        long tiempoInicio=System.nanoTime();
			
	        if ("true".equals(propiedades.getProperty("cliente.modoTrace"))){
				ejecutarTrace();
			}else{
				ejecutarAleatorio();
			}
	        
	        //controlOperacion.presentarControl();
	        
			long tiempoFin=System.nanoTime();
			presentarMensaje("Tiempo total de simulacion:"+((tiempoFin-tiempoInicio))+" ns");
			
			notify();
		}
		
		
    }
	

	public boolean isConsolaHabilitada() {
		return consolaHabilitada;
	}



	public void setConsolaHabilitada(boolean consolaHabilitada) {
		this.consolaHabilitada = consolaHabilitada;
	}
	
}
