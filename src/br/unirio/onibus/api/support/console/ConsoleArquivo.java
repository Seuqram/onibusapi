package br.unirio.onibus.api.support.console;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Classe que representa um console para um arquivo
 * 
 * @author Marcio
 */
public class ConsoleArquivo implements IConsole 
{
	private PrintWriter out;
	
	public ConsoleArquivo(String fileName) throws FileNotFoundException
	{
		out = new PrintWriter(fileName);
	}
	
	@Override
	public void print(String s) 
	{
		out.print(s);
	}

	@Override
	public void println(String s) 
	{
		out.println(s);
	}

	@Override
	public void println() 
	{
		out.println();
	}
}