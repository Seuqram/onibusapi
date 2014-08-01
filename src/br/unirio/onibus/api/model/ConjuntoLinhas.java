package br.unirio.onibus.api.model;
import java.util.HashMap;

import org.joda.time.DateTime;

/**
 * Classe que representa um conjunto de linhas de ônibus, com seus veículos e posições
 * 
 * @author Marcio Barros
 */
public class ConjuntoLinhas
{
	private HashMap<String, Linha> linhas;
	
	/**
	 * Inicializa o conjunto de linhas
	 */
	public ConjuntoLinhas()
	{
		this.linhas = new HashMap<String, Linha>();
	}
	
	/**
	 * Adiciona uma entrada no conjunto de linhas
	 */
	public void adiciona(String numeroLinha, String numeroVeiculo, DateTime data, double latitude, double longitude, double velocidade)
	{
		Linha linha = linhas.get(numeroLinha);
		
		if (linha == null)
		{
			linha = new Linha(numeroLinha);
			linhas.put(numeroLinha, linha);
		}
		
		linha.adiciona(numeroVeiculo, data, latitude, longitude, velocidade);
	}
	
	/**
	 * Retorna o número de linhas
	 */
	public int contaLinhas()
	{
		return linhas.keySet().size();
	}
	
	/**
	 * Retorna a lista de linhas
	 */
	public Iterable<Linha> getLinhas()
	{
		return linhas.values();
	}

	/**
	 * Retorna o número de posições registradas no conjunto de linhas
	 */
	public int contaPosicoes()
	{
		int count = 0;
		
		for (Linha linha : linhas.values())
			count += linha.contaPosicoes();
		
		return count;
	}

	/**
	 * Remove todos os dados da memória
	 */
	public void limpa()
	{
		linhas.clear();
	}
}