package conexiones;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;


public class Cliente extends Conexion {
    
   
    public Cliente() throws IOException{
        super("cliente");
    } 
    public String leerDatosServidor(){
        String dato = null;
        try {         
            //Flujo de datos del servidor hacia el cliente
            entrada = new DataInputStream(cs.getInputStream());
            dato = entrada.readUTF();              
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return dato;
    }
    
    public void enviarDatosServidor(String dato){
        try {
            salida = new DataOutputStream(cs.getOutputStream());
            salida.writeUTF(dato);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    
    public void limpiarSalida(){
        try {
            salida.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    
    public void cerrarCliente() {
        try {
            cs.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
       
    }

    
    public static void main(String[] args) throws IOException {
        Cliente cliente = new Cliente();
        StringTokenizer separador = new StringTokenizer(cliente.leerDatosServidor());
        System.out.println("x = " + separador.nextToken() + "  y = " + separador.nextToken());
        cliente.cerrarCliente();
    }
}
