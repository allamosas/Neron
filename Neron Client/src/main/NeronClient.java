/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import client.Cliente;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 *
 * @author Andres
 */
public class NeronClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Contraseña:");
        Scanner scanner = new Scanner(System.in);
        String cadena = scanner.nextLine();
        
        Cliente cliente = new Cliente(cadena);
        cliente.start();
    }    
}
