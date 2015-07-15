package simulador.cliente;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.Jedis;
import simulador.util.Configuracion;
import simulador.util.Resultado;
import simulador.util.Utilidades;

public class Cliente extends Thread {

	//private	String nombreCliente;
	private boolean consolaHabilitada=false;
	private int idCliente=0;
	public BufferedWriter  writer;
	
	
	Jedis jedis =null;
	Properties propiedades=null;
	ControlOperacion controlOperacion=null;
	ThreadGroup grupoHiloResultado=null;
	
	// Solo es para prueba, valor nunca sera usado
	private String valor;
	
	public Cliente( ThreadGroup grupoHilo ,Properties propiedades, 
			int idCliente, boolean consolaHabilitada, 
			ThreadGroup grupoHiloResultado){
		
		super(grupoHilo,"");
		
		this.propiedades=propiedades;		
		this.idCliente=idCliente;
		this.consolaHabilitada=consolaHabilitada;
		jedis=Configuracion.conectar(propiedades);
		//nombreCliente=asignarNombreCliente();
		setName(asignarNombreCliente());
		controlOperacion= new ControlOperacion(propiedades);
		this.grupoHiloResultado=grupoHiloResultado;
	}
	
	
	
	
	
	
	private String resolverExpresion(String expresion){
		
		String expresionResuelta=expresion;
		
		if (expresionResuelta.contains("{numeroCliente}"))
			expresionResuelta=expresionResuelta.replace("{numeroCliente}", String.valueOf(idCliente));
		
		if (expresionResuelta.contains("{nombreCliente}"))
			expresionResuelta=expresionResuelta.replace("{nombreCliente}", getName());
		
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
		
		Set<String> listaClaves=jedis.keys(getName()+"*");
		
		do{			
			claveGenerada=getName()+"."+"Clave"+Utilidades.generarEnteroAleatorio(0,1000000);			
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
				valor = jedisServer.get(nombreClave);			
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
		//if (isConsolaHabilitada()) {
			//System.out.println(getClass().getSimpleName()+":"+getName()+":"+mensaje);				
		//}
		
		if (consolaHabilitada)
			try {
				writer.write(getClass().getSimpleName()+":"+getName()+":"+mensaje+"\n");
			} catch (IOException e) {
				e.printStackTrace();
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
		    	long duracion=tiempoOperacionFin-tiempoOperacionInicio;
		    	
		    	controlOperacion.contabilizarNuevaOperacion(operacionAEjecutar);
		    	contador++;
		    	
		    	presentarMensaje("Op:"+controlOperacion.obtenerNombreOperacion(operacionAEjecutar)+" duracion:"+duracion);
		    			    	
		    	Resultado resultado=new Resultado(grupoHiloResultado,1,idCliente, operacionAEjecutar, duracion, consolaHabilitada);
		    	resultado.writer=writer;
		    	resultado.start();
		    	
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
	    	long duracion=tiempoOperacionFin-tiempoOperacionInicio;
	    	controlOperacion.contabilizarNuevaOperacion(parametros[0]);
	    	
	    	
	    	
	    	presentarMensaje("Op:"+parametros[0]+" duracion:"+duracion);
	    	
	    	Resultado resultado=new Resultado(grupoHiloResultado,1, idCliente, controlOperacion.obtenerIdOperacion(parametros[0]) , duracion, consolaHabilitada);
	    	resultado.writer=writer;
	    	resultado.start();
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
        	//System.out.println("Cliente:"+idCliente+" - Ejecutando en paralelo. ");
		synchronized(this){
			
	        if ("true".equals(propiedades.getProperty("cliente.modoTrace"))){
				ejecutarTrace();
			}else{
				ejecutarAleatorio();
			}
			
			notify();
		}
		
		
    }
	

	public boolean isConsolaHabilitada() {
		return consolaHabilitada;
	}



	public void setConsolaHabilitada(boolean consolaHabilitada) {
		this.consolaHabilitada = consolaHabilitada;
	}
	

	public int getIdCliente() {
		return idCliente;
	}



	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	
}
