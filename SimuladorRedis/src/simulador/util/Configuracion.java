package simulador.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

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
	
	
	
}
