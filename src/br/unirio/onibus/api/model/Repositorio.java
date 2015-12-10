package br.unirio.onibus.api.model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.unirio.onibus.api.reader.CarregadorLinhas;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;

/**
 * Classe de fachada que permite acesso aos dados dos onibus
 * 
 * @author marcio.barros
 */
public class Repositorio 
{
	private String diretorioBase;
	private DateTimeFormatter dateformatterLoader;

	/**
	 * Inicializa o repositorio
	 */
	public Repositorio(String diretorio)
	{
		this.diretorioBase = diretorio;
		this.dateformatterLoader = DateTimeFormat.forPattern("yyyy\\MM\\yyyy-MM-dd");
	}

	/**
	 * Carrega todas as linhas em trajetos em uma data
	 */
	public boolean carregaLinhas(DateTime data, List<String> linhas) 
	{
		String nomeArquivo = diretorioBase + "\\" + dateformatterLoader.print(data) + ".zip";
		return new CarregadorLinhas().executa(nomeArquivo, linhas);
	}
	
	/**
	 * Carrega o trajeto de uma linha de onibus
	 */
	public boolean carregaTrajeto(Linha linha)
	{
		if (linha.getTrajetoIda().conta() == 0 || linha.getTrajetoVolta().conta() == 0)
			return new CarregadorTrajeto().carregaArquivoCompactado(diretorioBase + "\\trajetos\\trajetos.zip", linha);
		
		return true;
	}
	
	/**
	 * Carrega os dados do onibus em uma data
	 */
	public boolean carregaPosicoes(Linha linha, DateTime data)
	{
		return new CarregadorPosicoes().executa(diretorioBase + "\\" + dateformatterLoader.print(data) + ".zip", linha);
	}
}