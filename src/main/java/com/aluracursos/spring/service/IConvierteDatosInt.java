package com.aluracursos.spring.service;

public interface IConvierteDatosInt {
    <T> T obtenerDatos (String json, Class<T> clase); //declaracion de datos
}
