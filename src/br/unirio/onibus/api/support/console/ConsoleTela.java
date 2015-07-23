package br.unirio.onibus.api.support.console;

/**
 * Classe que representa um console para a tela
 * 
 * @author Marcio
 */
public class ConsoleTela implements IConsole 
{
	@Override
	public void print(String s) 
	{
		System.out.print(s);
	}

	@Override
	public void println(String s) 
	{
		System.out.println(s);
	}

	@Override
	public void println() 
	{
		System.out.println();
	}
}