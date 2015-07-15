package simulador.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import simulador.util.Configuracion;

public class OtherTest {
	
	public static byte[] objectToArray(Object objeto) throws IOException{
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os= new ObjectOutputStream(out);
		os.writeObject(objeto);
		return out.toByteArray();
	}
	
	public static Object arrayToObject( byte[] contenido) throws IOException, ClassNotFoundException{
		
		ByteArrayInputStream in= new ByteArrayInputStream(contenido);
		ObjectInputStream is= new ObjectInputStream(in);
		
		return is.readObject();
	}
	
	
	public static void main(String[] args) {
		
		Properties propiedades=Configuracion.getProperties("config.properties");		
		
		String ip=propiedades.getProperty("servidorRedis.ip");
		String clave=propiedades.getProperty("servidorRedis.clave");
		int puerto=0;
		Jedis jedis =null;
		
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
		
		try {
		
		List<long[]> resultados= new ArrayList<long[]>();
		resultados.add(new long[] {1,1,1,25});
		resultados.add(new long[] {1,2,2,32});
			
		jedis.set("resultados".getBytes(), objectToArray(resultados) );
		
		List<long[]> lista=(List<long[]>)arrayToObject(jedis.get("resultados".getBytes()));
		
		for (long[] elemento : lista) {
			System.out.println("["+elemento[0]+"],["+elemento[1]+"],["+elemento[2]+"],["+elemento[3]+"]");
		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(jedis.get("secuencia"));
		
	}

}
