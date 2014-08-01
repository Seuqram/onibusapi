package br.unirio.onibus.api.console;

/**
 * Interface que representa um console abstrato
 * 
 * @author Marcio
 */
public interface IConsole 
{
	/**
	 * Imprime um texto sem saltar linha
	 */
	void print(String s);
	
	/**
	 * Imprime um texto saltando linha
	 */
	void println(String s);
	
	/**
	 * Salta uma linha
	 */
	void println();
}