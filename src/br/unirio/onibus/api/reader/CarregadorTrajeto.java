package br.unirio.onibus.api.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
 * Classe que carrega os dados de uma linha de ônibus em uma data
 * 
 * @author Marcio
 */
public class CarregadorTrajeto 
{
	/**
	 * Carrega o trajeto de um ônibus a partir de um diretório
	 */
	public void carregaDiretorio(String diretorio, Linha linha) throws Exception
	{
		carregaArquivoCompactado(diretorio + "\\trajetos\\trajetos.zip", linha);
	}

	/**
	 * Carrega o trajeto de um ônibus a partir de um arquivo compactado
	 */
	public void carregaArquivoCompactado(String nomeArquivo, Linha linha) throws Exception
	{
	    ZipFile zipFile = new ZipFile(nomeArquivo);
		Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

		while (zipEntries.hasMoreElements())
		{
			ZipEntry zipEntry = zipEntries.nextElement();

			if (zipEntry.getName().compareTo(linha.getIdentificador() + ".csv") == 0)
			{
				InputStream input = zipFile.getInputStream(zipEntry);
			    processaArquivoLinha(linha, input);
				input.close();
			}
		}

	    zipFile.close();
	}

	/**
	 * Carrega o trajeto de um ônibus a partir de um arquivo
	 */
	public void carregaArquivo(String nomeArquivo, Linha linha) throws Exception
	{
		InputStream input = new FileInputStream(new File(nomeArquivo));
		processaArquivoLinha(linha, input);
		input.close();
	}

	/**
	 * Carrega o trajeto da linha a partir de um stream
	 */
	private void processaArquivoLinha(Linha linha, InputStream input) throws Exception 
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));

		DecimalFormatSymbols canonicalSymbols = new DecimalFormatSymbols();
		canonicalSymbols.setDecimalSeparator('.');
		canonicalSymbols.setGroupingSeparator(',');
		
		NumberFormat nf0 = new DecimalFormat("0", canonicalSymbols);
		NumberFormat nf6 = new DecimalFormat("0.0#####", canonicalSymbols);
		
		int ordemMaxima = -1;
		boolean inverteu = false;

		String line;
		while((line = br.readLine()) != null)
		{
			if (line.length() > 0 && !line.startsWith("linha"))
			{
		    	String[] tokens = line.trim().split(",");
		    	
		    	if (tokens.length == 7)
		    	{
		    		int ordem = nf0.parse(tokens[3]).intValue();
		    		double latitude = nf6.parse(tokens[5].replace("\"", "")).doubleValue();
					double longitude = nf6.parse(tokens[6].replace("\"", "")).doubleValue();
					
					if (ordem > ordemMaxima)
						ordemMaxima = ordem;
					else
						inverteu = true;
						
					if (inverteu)
						linha.adicionaTrajetoVolta(latitude, longitude);
					else
						linha.adicionaTrajetoIda(latitude, longitude);
		    	}
			}
		}

		br.close();
	}
}