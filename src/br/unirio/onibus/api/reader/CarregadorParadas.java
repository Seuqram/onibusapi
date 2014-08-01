package br.unirio.onibus.api.reader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import br.unirio.onibus.api.model.Linha;

/**
 * Classe que carrega as paradas de uma linha de ônibus
 * 
 * @author Marcio
 */
public class CarregadorParadas 
{
	/**
	 * Carrega um arquivo de paradas de uma linha de ônibus
	 */
	public void executa(String diretorio, Linha linha) throws Exception
	{
	    ZipFile zipFile = new ZipFile(diretorio + "\\trajetos\\paradas.zip");
		Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

		while (zipEntries.hasMoreElements())
		{
			ZipEntry zipEntry = zipEntries.nextElement();
			processaArquivoLinha(linha, zipEntry, zipFile);
		}

	    zipFile.close();
	}

	/**
	 * Processa o conteúdo de uma entrada do arquivo ZIP
	 */
	private void processaArquivoLinha(Linha linha, ZipEntry zipEntry, ZipFile zipFile) throws Exception 
	{
		DecimalFormatSymbols canonicalSymbols = new DecimalFormatSymbols();
		canonicalSymbols.setDecimalSeparator('.');
		canonicalSymbols.setGroupingSeparator(',');
		NumberFormat nf6 = new DecimalFormat("0.0#####", canonicalSymbols);

		if (zipEntry.getName().compareTo(linha.getIdentificador() + ".csv") == 0) 
		{
		    InputStream input = zipFile.getInputStream(zipEntry);
		    BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));

		    String line;
		    while((line = br.readLine()) != null)
		    {
		    	if (line.length() > 0 && !line.startsWith("linha"))
		    	{
		        	String[] tokens = line.trim().split(",");
		        	
		        	if (tokens.length == 6)
		        	{
		        		double latitude = nf6.parse(tokens[4].replace("\"", "")).doubleValue();
		    			double longitude = nf6.parse(tokens[5].replace("\"", "")).doubleValue();
	    				linha.adicionaParada(latitude, longitude);
		        	}
		    	}
		    }

		    br.close();
		    input.close();
		}
	}
}