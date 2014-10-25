package br.unirio.onibus.api.model;
import java.util.HashMap;

import lombok.Getter;

import org.joda.time.DateTime;

/**
 * Classe que representa uma linha de �nibus, com seus ve�culos e posi��es
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
	 * Ve�culos da linha
	 */
	private HashMap<String, Veiculo> veiculos;
	
	/**
	 * Trajeto de ida do �nibus
	 */
	private @Getter Trajeto trajetoIda;
	
	/**
	 * Trajeto de volta do �nibus
	 */
	private @Getter Trajeto trajetoVolta;
	
	/**
	 * Pontos de parada do �nibus
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
	 * Adiciona uma posi��o de ve�culo na linha
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
	 * Conta o n�mero de ve�culos
	 */
	public int contaVeiculos()
	{
		return veiculos.keySet().size();
	}

	/**
	 * Retorna uma lista de ve�culos
	 */
	public Iterable<Veiculo> getVeiculos()
	{
		return veiculos.values();
	}

	/**
	 * Conta o n�mero de posi��es registradas na linha
	 */
	public int contaPosicoes()
	{
		int count = 0;
		
		for (Veiculo veiculo : veiculos.values())
			count += veiculo.pegaNumeroPosicoes();
		
		return count;
	}

	/**
	 * Ordena as posi��es de todos os ve�culos
	 */
	public void ordenaPosicoes() 
	{
		for (Veiculo veiculo : veiculos.values())
			veiculo.ordenaPosicoes();
	}

	/**
	 * Adiciona uma posi��o no trajeto de ida do �nibus
	 */
	public void adicionaTrajetoIda(double latitude, double longitude) 
	{
		trajetoIda.adiciona(latitude, longitude); 
	}

	/**
	 * Adiciona uma posi��o no trajeto de volta do �nibus
	 */
	public void adicionaTrajetoVolta(double latitude, double longitude) 
	{
		trajetoVolta.adiciona(latitude, longitude); 
	}

	/**
	 * Adiciona um ponto de parada na linha de �nibus
	 */
	public void adicionaParada(double latitude, double longitude) 
	{
		pontosParada.adiciona(latitude, longitude);
	}
}