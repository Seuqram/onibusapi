package br.unirio.onibus.api.gmaps;

import java.io.PrintWriter;

/**
 * Classe que representa um elemento genérico que pode ser adicionado a um mapa
 * 
 * @author Marcio
 */
public interface IDecoradorMapas 
{
	void gera(PrintWriter writer);
}