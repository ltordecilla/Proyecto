
package com.ferreteria.models;

import java.util.ArrayList;
import java.util.List;

public class UsuarioData {

    public static class Usuario {

        private String nombre;
        private String identificacion;
        private String correo;
        private String telefono;
        private String usuario;
        private String contrasena;
        private String rol;

        public Usuario(String nombre, String identificacion,
                String correo, String telefono,
                String usuario, String contrasena, String rol) {

            this.nombre = nombre;
            this.identificacion = identificacion;
            this.correo = correo;
            this.telefono = telefono;
            this.usuario = usuario;
            this.contrasena = contrasena;
            this.rol = rol;
        }

        public String getNombre() {
            return nombre;
        }

        public String getIdentificacion() {
            return identificacion;
        }

        public String getCorreo() {
            return correo;
        }

        public String getTelefono() {
            return telefono;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getContrasena() {
            return contrasena;
        }

        public String getRol() {
            return rol;
        }
    }

    public static List<Usuario> usuarios = new ArrayList<>();

    public static boolean existeUsuario(String usuario) {

        return usuarios.stream().anyMatch(u ->
                u.getUsuario().equalsIgnoreCase(usuario)
        );
    }

    public static boolean existeCorreo(String correo) {

        return usuarios.stream().anyMatch(u ->
                u.getCorreo().equalsIgnoreCase(correo)
        );
    }

    public static boolean existeIdentificacion(String identificacion) {

        return usuarios.stream().anyMatch(u ->
                u.getIdentificacion().equals(identificacion)
        );
    }

    public static void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public static Usuario validarLogin(String usuario, String contrasena) {

        return usuarios.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(usuario)
                        && u.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }
}
