package br.unirio.onibus.api.reader;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
	/**
	 * Carrega a lista de linhas consideradas em uma data
	 */
	public void executa(String diretorio, int dia, int mes, int ano, List<String> linhas) throws Exception
	{
		NumberFormat nf2 = new DecimalFormat("00");
		NumberFormat nf4 = new DecimalFormat("0000");
		String nomeArquivo = diretorio + "\\" + nf4.format(ano) + "\\" + nf2.format(mes) + "\\" + nf4.format(ano) + "-" + nf2.format(mes) + "-" + nf2.format(dia) + ".zip";
		
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
	}
}