package br.unirio.onibus.api.console;

import java.util.ArrayList;
import java.util.List;

/**
 * Console que representa uma lista de consoles
 * 
 * @author Marcio
 */
public class ListaConsole implements IConsole 
{
	private List<IConsole> consoles;
	
	public ListaConsole()
	{
		this.consoles = new ArrayList<IConsole>();
	}
	
	public void add(IConsole console)
	{
		this.consoles.add(console);
	}

	@Override
	public void print(String s) 
	{
		for (IConsole c : consoles)
			c.print(s);
	}

	@Override
	public void println(String s) 
	{
		for (IConsole c : consoles)
			c.println(s);
	}

	@Override
	public void println() 
	{
		for (IConsole c : consoles)
			c.println();
	}
}