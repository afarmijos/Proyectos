package simulador.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import simulador.cliente.Cliente;
import simulador.util.Configuracion;

public class Prueba {
	
	public static boolean conectar(Properties propiedades){

		String ip=propiedades.getProperty("servidorRedis.ip");
		String clave=propiedades.getProperty("servidorRedis.clave");
		int puerto=0;
		Jedis jedis =null;
		boolean conexionExistente=true;
		
		try{
		puerto=Integer.valueOf(propiedades.getProperty("servidorRedis.puerto"))
				.intValue();
		
		}catch(NumberFormatException e){			
			puerto=0;
			conexionExistente=false;
		}		
		
		try{
			
			jedis = new Jedis(ip,puerto);
			jedis.auth(clave);
			
		}catch(JedisDataException e){
			System.out.println("Error en la autenticacion");
			e.printStackTrace();
			conexionExistente=false;
			}
		catch(Exception e){
			System.out.println("El servidor está abajo. Levante el servidor");
			e.printStackTrace();
			conexionExistente=false;
		}
		//jedis.incr("secuencia");
		
		return conexionExistente;
	}
	
	
	
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
		
		if (!conectar(propiedades)){
			System.out.println("Levante el servidor.");
			return;
		}
		
		ThreadGroup grupo = new ThreadGroup("Clientes");
		
		do{
			
			cantidadAProcesarParalelo=cantidadAProcesarParalelo-grupo.activeCount();
			
			if (cantidadAProcesarParalelo>(cantidadAProcesar-cantidadProcesados))
				cantidadAProcesarParalelo=cantidadAProcesar-cantidadProcesados;
			
			List<Cliente> listaClientes= new ArrayList<Cliente>();
			
			for (int i=1;i<=cantidadAProcesarParalelo;i++){
				
				cantidadProcesados++;
				System.out.println("Creando nuevo hilo:"+cantidadProcesados);
				Cliente clienteNuevo=new Cliente(grupo,propiedades,cantidadProcesados,consolaHabilitada);
				listaClientes.add(clienteNuevo);
				clienteNuevo.start();
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
			
			for (Cliente cliente : listaClientes) {
				System.out.println("NombreClientesHilos:"+cliente.getNombreCliente() );
			}
			
			if (cantidadProcesados==cantidadAProcesar)
				existenClientesPendientes=false;
			
		}while (existenClientesPendientes);
		
	}

}
