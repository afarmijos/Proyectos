package simulador.cliente;

import java.util.Properties;

public class ControlOperacion {

	double controlOperaciones[][]=null;
	String nombreOperacionesDefinidas[]=null;
	int cantidadTotalOperaciones=0;
	
	public ControlOperacion(Properties propiedades) {
		
		int cantidadOperacionesDefinidas=0;
		
		controlOperaciones= new double[2][3];
		
		cantidadTotalOperaciones=0;
		
		nombreOperacionesDefinidas=propiedades.getProperty("cliente.operacionesDefinidas").trim().split(";");
		
		cantidadOperacionesDefinidas=nombreOperacionesDefinidas.length;
		
		controlOperaciones= new double[cantidadOperacionesDefinidas][3];
		
		String ponderaciones[]=propiedades.getProperty("cliente.aleatorio.ponderacionOperaciones").split(";");
		
		for (int i=0;i<ponderaciones.length;i++){
			controlOperaciones[i][2]=Double.valueOf(ponderaciones[i]).doubleValue();
		}
		
		
	}
	
public boolean esValidaOperacion(int operacionAEjecutar){
		
		
		if (controlOperaciones[operacionAEjecutar][1]>controlOperaciones[operacionAEjecutar][2])
			return false;
		else
			return true;
		
	}
	
	
	
	public void contabilizarNuevaOperacion(int operacionEjecutada){
		
		controlOperaciones[operacionEjecutada][0]=controlOperaciones[operacionEjecutada][0]+1;
		cantidadTotalOperaciones=cantidadTotalOperaciones+1;
		
		for(int i=0;i<controlOperaciones.length;i++)
			controlOperaciones[i][1]=controlOperaciones[i][0]/cantidadTotalOperaciones;
		
	}
	
	public void contabilizarNuevaOperacion(String operacionEjecutada){
		
		contabilizarNuevaOperacion(obtenerIdOperacion(operacionEjecutada));
		
	}
	
	public void presentarControl(){
		
		for (int i=0;i<2;i++){
			System.out.println("");
			for (int j=0; j<3;j++ )
			  System.out.print("["+i+"]["+j+"]="+controlOperaciones[i][j]);
			
		}
		System.out.println("");
	}
	
	public String obtenerNombreOperacion(int operacion){
		return nombreOperacionesDefinidas[operacion];
	}
	
	public int obtenerIdOperacion(String operacion){
		int operacionNueva=0;
		
		for(int i=0; i<nombreOperacionesDefinidas.length;i++)
			if (nombreOperacionesDefinidas[i].equals(operacion))
				operacionNueva=i;
		
		return operacionNueva;
	}
	
	public  void prueba( int operacion){
		
	
		if (esValidaOperacion(operacion))
		{
			System.out.println("Operacion "+operacion+" es valida" );
			contabilizarNuevaOperacion(operacion);
		}else{
			System.out.println("Operacion "+operacion+" no es valida");
		}
		
	}
	
	
}
