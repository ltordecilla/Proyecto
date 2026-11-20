
package com.ferreteria.models;

public class SesionData {

    private static String usuarioActual;
    private static String rolActual;

    public static void iniciarSesion(String usuario, String rol) {
        usuarioActual = usuario;
        rolActual = rol;
    }

    public static String getUsuarioActual() {
        return usuarioActual;
    }

    public static String getRolActual() {
        return rolActual;
    }

    public static boolean esAdministrador() {
        return "ADMIN".equals(rolActual);
    }

    public static boolean esCliente() {
        return "CLIENTE".equals(rolActual);
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
    }
}
