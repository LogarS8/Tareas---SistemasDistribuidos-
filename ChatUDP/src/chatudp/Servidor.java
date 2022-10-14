/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatudp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class Servidor extends Observable implements Runnable{
    private int puerto;
    public Servidor(int puerto){   
        this.puerto = puerto;        
    }
    @Override
    public void run() {    
        try {
            System.out.println("Servidor UDP iniciado");
            DatagramSocket socketUDP = new DatagramSocket(puerto);
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);              
                socketUDP.receive(peticion);                
                String mensaje = new String(peticion.getData());
                System.out.println(mensaje);
                this.setChanged();
                this.notifyObservers(mensaje);
                this.clearChanged();                
                int puertoCliente = peticion.getPort();
                InetAddress direccion = peticion.getAddress();                
                buffer = mensaje.getBytes();
                ByteArrayInputStream exam= new ByteArrayInputStream(buffer);
                exam.reset();
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                socketUDP.send(respuesta);                
            }
 
        } catch (SocketException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
 
}