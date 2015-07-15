package simulador.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class Resultado extends Thread {

	public static int idToken=-1;
	public static List<long[]> listaResultados=null;
	
	private long resultado[]= null;
	private int idCliente= -1;
	private boolean consolaHabilitada=false;
	public BufferedWriter  writer;
	
	public Resultado(ThreadGroup grupo,String name) {
		super(grupo,name);
	}
	
	
	public Resultado(ThreadGroup grupo, long idServidor, int idCliente,
			long operacion, long duracion, boolean consolaHabilitada ) {
		
		resultado= new long[4];
		resultado[0]=idServidor;
		resultado[1]=idCliente;
		resultado[2]=operacion;
		resultado[3]=duracion;
		this.idCliente=idCliente;
		this.consolaHabilitada=consolaHabilitada;
		
		setName("HiloResultadoCliente"+this.idCliente);		
	}
	
	private boolean tengoToken(){
		return (idToken==idCliente);
	}
	
	private boolean ningunoTieneToken(){
		return (idToken==-1);
	}
	
	private void cogerToken(){
			idToken=idCliente;
			presentarMensaje("Cogi el token:"+idToken);
	}
	
	private void dejarToken(){
		idToken=-1;
		presentarMensaje("Se dejo el token:"+idToken);
	}
	
	public void presentarMensaje(String mensaje){
		//if (consolaHabilitada)
			//System.out.println(getClass().getSimpleName()+":"+getName()+":"+mensaje);
			
			if (consolaHabilitada)
				try {
					writer.write(getClass().getSimpleName()+":"+getName()+":"+mensaje+"\n");					
				} catch (IOException e) {
					e.printStackTrace();
				}
		
		
	}
	
	@Override
	public void run() {        
		
		synchronized(this){
			
			while (!tengoToken()){
				presentarMensaje("Token:"+idToken);
	    		if (ningunoTieneToken()){
		    		cogerToken();
		    		break;
				}else{
				
					try {
						Thread.sleep(idCliente%2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
	    		
	    	}
			
				guardarResultado(resultado);
			
	    	
	    	dejarToken();
			
			notify();
		}
		
		
    }
	
	public void guardarResultado(long[] resultado) {
		int tamanioAntes=listaResultados.size();
		listaResultados.add(resultado);
		int tamanioDespues=listaResultados.size();
		presentarMensaje("Se anadio Resultado:"+resultado[3]+" Tamanio [antes][despues]: ["+tamanioAntes+"]["+tamanioDespues+"]");
	}
	
}
