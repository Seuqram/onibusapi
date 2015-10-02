package br.unirio.onibus.api.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.unirio.onibus.api.model.Linha;

/**
 * Classe que carrega os dados de uma linha de ônibus em uma data
 * 
 * @author Marcio
 */
public class CarregadorPosicoes 
{
	/**
	 * Carrega um arquivo de uma linha de ônibus em uma data
	 */
	public void executa(String diretorio, int dia, int mes, int ano, Linha linha) throws Exception
	{
		NumberFormat nf2 = new DecimalFormat("00");
		NumberFormat nf4 = new DecimalFormat("0000");
		String nomeArquivo = diretorio + "\\" + nf4.format(ano) + "\\" + nf2.format(mes) + "\\" + nf4.format(ano) + "-" + nf2.format(mes) + "-" + nf2.format(dia) + ".zip";
		executa(nomeArquivo, linha);
	}

	/**
	 * Carrega um arquivo de uma linha de ônibus em uma data
	 */
	public boolean executa(String nomeArquivo, Linha linha)
	{		
		try
		{
		    ZipFile zipFile = new ZipFile(nomeArquivo);
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
	
			while (zipEntries.hasMoreElements())
			{
				ZipEntry zipEntry = zipEntries.nextElement();
				processaArquivoLinha(linha, zipEntry, zipFile);
			}
	
		    zipFile.close();
		    linha.ordenaPosicoes();
		    return true;
		}
		catch(IOException ioe)
		{
		    return false;
		}
		catch(ParseException pe)
		{
			return false;
		}
	}

	/**
	 * Processa o conteúdo de uma entrada do arquivo ZIP 
	 */
	private void processaArquivoLinha(Linha linha, ZipEntry zipEntry, ZipFile zipFile) throws IOException, ParseException 
	{
		DecimalFormatSymbols canonicalSymbols = new DecimalFormatSymbols();
		canonicalSymbols.setDecimalSeparator('.');
		canonicalSymbols.setGroupingSeparator(',');
		
		NumberFormat nf6 = new DecimalFormat("0.0#####", canonicalSymbols);
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");

		if (zipEntry.getName().compareTo(linha.getIdentificador() + ".csv") == 0) 
		{
		    InputStream input = zipFile.getInputStream(zipEntry);
		    BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));

		    String line;
		    while((line = br.readLine()) != null)
		    {
		    	if (line.length() > 0)
		    	{
		        	String[] tokens = line.trim().split(",");
		        	
		        	if (tokens.length == 5)
		        	{
			        	String numeroSerie = tokens[0];
		    			DateTime data = formatter.parseDateTime(tokens[1]);
		    			double latitude = nf6.parse(tokens[2]).doubleValue();
		    			double longitude = nf6.parse(tokens[3]).doubleValue();
		    			double velocidade = nf6.parse(tokens[4]).doubleValue();
			        	linha.adiciona(numeroSerie, data, latitude, longitude, velocidade);
		        	}
		    	}
		    }
		    
		    br.close();
		    input.close();
		}
	}
}