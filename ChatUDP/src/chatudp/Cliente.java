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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger; 
public class Cliente implements Runnable{    
    private int puerto;
    private String mensaje;    
    public Cliente(int puerto , String mensaje){        
        this.puerto = puerto;
        this.mensaje = mensaje;        
    }

    @Override
    public void run() {    
        try {               
            InetAddress direccionServidor = InetAddress.getByName("192.168.20.92"); 
            DatagramSocket socketUDP = new DatagramSocket();    
            byte[] buffer = new byte[1024];
            ByteArrayInputStream exam= new ByteArrayInputStream(buffer);
            String mensajeString = mensaje;
            buffer = mensajeString.getBytes();
            DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, puerto);
            socketUDP.send(pregunta);
            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(peticion);
            mensajeString = new String(peticion.getData());
            System.out.println(mensajeString);
            exam.reset();                
            if(mensajeString == "salir"){                   
            socketUDP.close();                   
        } 
        } catch (SocketException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}