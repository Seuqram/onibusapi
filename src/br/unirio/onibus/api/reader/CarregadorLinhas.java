package br.unirio.onibus.api.reader;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Classe que carrega as linhas de ônibus consideradas em uma data
 * 
 * @author Marcio
 */
public class CarregadorLinhas 
{
	public boolean executa(String nomeArquivo, List<String> linhas)
	{
		try
		{
		    ZipFile zipFile = new ZipFile(nomeArquivo);
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
	
			while (zipEntries.hasMoreElements())
			{
				ZipEntry zipEntry = zipEntries.nextElement();
				String filename = zipEntry.getName();
				
				if (filename.endsWith(".csv"))
					linhas.add(filename.substring(0, filename.length()-4));
			}
	
		    zipFile.close();
			return true;
		}
		catch(IOException ioe)
		{
			return false;
		}
	}
}