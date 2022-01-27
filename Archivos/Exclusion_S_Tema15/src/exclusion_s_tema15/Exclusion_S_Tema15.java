package exclusion_s_tema15;
import exclusion_s_tema15.interfaces.Calculadora;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author DarioPlza
 */
public class Exclusion_S_Tema15 extends UnicastRemoteObject implements Calculadora{
    
    public Exclusion_S_Tema15() throws RemoteException{
        
    }

    @Override
    public float sumar(float num1, float num2 ) throws RemoteException{
        return num1+num2;
    }

    @Override
    public float restar(float num1, float num2) throws RemoteException {
        return num1-num2;
    }

    @Override
    public float multiplicar(float num1, float num2) throws RemoteException {
        return num1*num2;
    }

    @Override
    public float dividir(float num1, float num2) throws RemoteException {
        if(num2==0){
            System.out.println("División imposible de realizar");
            JOptionPane.showMessageDialog(null, "Error al tratar de diviir entre 0",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        else
            return num1/num2;
    }

    @Override
    public float modular(float num1, float num2) throws RemoteException {
        return num1%num2;
    }
    
    public static void main(String[] args) {
        
        servidor();
    }
    static void calculadora(){
        try{
            Calculadora cal;
            Registry registroRMI;

            cal=new Exclusion_S_Tema15();
            registroRMI = LocateRegistry.createRegistry(1099);
            registroRMI.rebind("sumar", cal);
            registroRMI.rebind("restar", cal);
            registroRMI.rebind("multiplicar", cal);
            registroRMI.rebind("dividir", cal);
            registroRMI.rebind("modular", cal);
            
            System.out.println("Servidor funcionando");
        } catch (Exception e) {
            System.err.println("Error en el servidor");
            e.printStackTrace();
        }
    }
    
    static void servidor(){
        DatagramSocket socket = null;
        ArrayList<String> seccionCritica=new ArrayList<String>();
        ArrayList<DatagramPacket> solicitudes=new ArrayList<DatagramPacket>();
        String sc="";
        
        try {
            socket = new DatagramSocket(6788);
            byte[] recibo= new byte[1024];
            
            calculadora();
            
            while(true){
            DatagramPacket mensaje = new DatagramPacket(recibo, recibo.length);
            socket.receive(mensaje);
            
            String accion=new String(recibo, 0, mensaje.getLength());
            String[] aux=accion.split("_");
            String solicitud=aux[0];
            String usuario=aux[1];
            
            System.out.println(accion);
            
            //Pedir acceso al servidor
            if(solicitud.equals("Ingresar")){
                if(sc.equals("")){
                    sc=usuario;
                    System.out.println("Usuario "+sc+" en sección crítica");
                    
                    String aceptado="Aceptado";
                    byte[] buffer=aceptado.getBytes("UTF8");
                    DatagramPacket respuesta=new DatagramPacket(buffer, aceptado.length(),
                    mensaje.getAddress(), mensaje.getPort());
                    socket.send(respuesta);
                }
                else{
                    seccionCritica.add(usuario);
                    solicitudes.add(mensaje);
                    System.out.println("En espera...");
                    System.out.println(seccionCritica);
                }
            }
            //Salir del servidor
            if(solicitud.equals("Finalizar")){
                if(sc.equals(usuario)){
                    if(seccionCritica.isEmpty()){
                        sc="";
                        System.out.println("Sección crítica vacia");
                    }
                    else{
                        System.out.println(usuario+" eliminado de sección crítica");
                        sc=seccionCritica.get(0);
                        System.out.println("Usuario "+sc+" en sección crítica");
                        
                        String aceptado="Aceptado";
                        byte[] buffer=aceptado.getBytes("UTF8");
                        
                        DatagramPacket respuesta=new DatagramPacket(buffer, aceptado.length(),
                        solicitudes.get(0).getAddress(), solicitudes.get(0).getPort());
                        socket.send(respuesta);
                        
                        seccionCritica.remove(0);
                        solicitudes.remove(0);  
                        
                        if(!seccionCritica.isEmpty()){
                            System.out.println("En espera...");
                            System.out.println(seccionCritica);
                        }
                        
                    }
                }
                else{
                    int lugar=seccionCritica.indexOf(usuario);
                    seccionCritica.remove(lugar);
                }
            }
            }
            
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

}
