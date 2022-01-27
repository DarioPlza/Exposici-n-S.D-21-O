package exclusion_s_tema15.interfaces;

import java.rmi.*;
/**
 *
 * @author Gabriel Dario Gonzalez Peñaloza
 */
public interface Calculadora extends Remote {
    public float sumar(float num1, float num2) throws RemoteException;
    public float restar(float num1, float num2) throws RemoteException;
    public float multiplicar(float num1, float num2) throws RemoteException;
    public float dividir(float num1, float num2) throws RemoteException;
    public float modular(float num1, float num2) throws RemoteException;
    
}
