package br.unirio.onibus.api.model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.unirio.onibus.api.reader.CarregadorLinhas;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;

/**
 * Classe de fachada que permite acesso aos dados dos ônibus
 * 
 * @author marcio.barros
 */
public class Repositorio 
{
	private String diretorioBase;
	private DateTimeFormatter dateformatterLoader;

	/**
	 * Inicializa o repositório
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
	 * Carrega o trajeto de uma linha de ônibus
	 */
	public boolean carregaTrajeto(Linha linha)
	{
		return new CarregadorTrajeto().carregaArquivoCompactado(diretorioBase + "\\trajetos\\trajetos.zip", linha);
	}
	
	/**
	 * Carrega os dados do ônibus em uma data
	 */
	public boolean carregaPosicoes(Linha linha, DateTime data)
	{
		return new CarregadorPosicoes().executa(diretorioBase + "\\" + dateformatterLoader.print(data) + ".zip", linha);
	}
}