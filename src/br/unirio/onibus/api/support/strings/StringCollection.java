package br.unirio.onibus.api.support.strings;

import java.util.ArrayList;

/**
 * Utility class that represents a list of strings without duplicates
 * 
 * @author marcio.barros
 *
 */
public class StringCollection extends ArrayList<String>
{
	private static final long serialVersionUID = -1167565667836803570L;

	/**
	 * Adiciona uma string na lista
	 */
	@Override
	public boolean add(String s)
	{
		for (String s1 : this)
			if (s1.compareToIgnoreCase(s) == 0)
				return false;
		
		return super.add(s);
	}

	/**
	 * Verifica se a lista contem uma string
	 */
	public boolean contains(String s)
	{
		for (String s1 : this)
			if (s1.compareToIgnoreCase(s) == 0)
				return true;
		
		return false;
	}
}