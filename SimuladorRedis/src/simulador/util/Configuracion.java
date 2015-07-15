package simulador.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

public class Configuracion {

	public static Properties getProperties(String nameFile){
	
		Properties defaultProps = null;
		FileInputStream in;
		try {
			in = new FileInputStream(nameFile);
			
			defaultProps = new Properties();
			defaultProps.load(in);
			in.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return defaultProps;
		
	}
	
	public static void safeProperties(Properties propiedades, String nameFile){
		
		try {
			OutputStream out = new FileOutputStream(nameFile);			
			propiedades.store(out, "");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public static Jedis conectar(Properties propiedades){
		return conectar(propiedades.getProperty("servidorRedis.ip"), 
				propiedades.getProperty("servidorRedis.clave"), 
				propiedades.getProperty("servidorRedis.puerto"));
	}
	
	public static Jedis conectar(String ip, String clave, String cadenaPuerto){

		int puerto=0;
		Jedis jedis =null;
		
		try{
			puerto=Integer.valueOf(cadenaPuerto)
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
		
		return jedis;
	}
	
	
	
}
