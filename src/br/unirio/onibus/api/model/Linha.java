package br.unirio.onibus.api.model;
import java.util.HashMap;

import lombok.Getter;

import org.joda.time.DateTime;

/**
 * Classe que representa uma linha de ônibus, com seus veículos e posições
 * 
 * @author Marcio Barros
 */
public class Linha
{
	/**
	 * Identificador da linha
	 */
	private @Getter String identificador;
	
	/**
	 * Veículos da linha
	 */
	private HashMap<String, Veiculo> veiculos;
	
	/**
	 * Trajeto de ida do ônibus
	 */
	private @Getter Trajeto trajetoIda;
	
	/**
	 * Trajeto de volta do ônibus
	 */
	private @Getter Trajeto trajetoVolta;
	
	/**
	 * Pontos de parada do ônibus
	 */
	private @Getter Trajeto pontosParada;
	
	/**
	 * Inicializa a linha
	 */
	public Linha(String identificador)
	{
		this.identificador = identificador;
		this.veiculos = new HashMap<String, Veiculo>();
		this.trajetoIda = new Trajeto();
		this.trajetoVolta = new Trajeto();
		this.pontosParada = new Trajeto();
	}

	/**
	 * Adiciona uma posição de veículo na linha
	 */
	public void adiciona(String numeroVeiculo, DateTime data, double latitude, double longitude, double velocidade)
	{
		Veiculo veiculo = veiculos.get(numeroVeiculo);
		
		if (veiculo == null)
		{
			veiculo = new Veiculo(numeroVeiculo);
			veiculos.put(numeroVeiculo, veiculo);
		}
		
		veiculo.adiciona(data, latitude, longitude, velocidade);
	}

	/**
	 * Conta o número de veículos
	 */
	public int contaVeiculos()
	{
		return veiculos.keySet().size();
	}

	/**
	 * Retorna uma lista de veículos
	 */
	public Iterable<Veiculo> getVeiculos()
	{
		return veiculos.values();
	}

	/**
	 * Conta o número de posições registradas na linha
	 */
	public int contaPosicoes()
	{
		int count = 0;
		
		for (Veiculo veiculo : veiculos.values())
			count += veiculo.pegaNumeroPosicoes();
		
		return count;
	}

	/**
	 * Ordena as posições de todos os veículos
	 */
	public void ordenaPosicoes() 
	{
		for (Veiculo veiculo : veiculos.values())
			veiculo.ordenaPosicoes();
	}

	/**
	 * Adiciona uma posição no trajeto de ida do ônibus
	 */
	public void adicionaTrajetoIda(double latitude, double longitude) 
	{
		trajetoIda.adiciona(latitude, longitude); 
	}

	/**
	 * Adiciona uma posição no trajeto de volta do ônibus
	 */
	public void adicionaTrajetoVolta(double latitude, double longitude) 
	{
		trajetoVolta.adiciona(latitude, longitude); 
	}

	/**
	 * Adiciona um ponto de parada na linha de ônibus
	 */
	public void adicionaParada(double latitude, double longitude) 
	{
		pontosParada.adiciona(latitude, longitude);
	}
}