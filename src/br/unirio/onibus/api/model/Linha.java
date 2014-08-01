package br.unirio.onibus.api.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private List<PosicaoTrajeto> trajetoIda;
	
	/**
	 * Trajeto de volta do ônibus
	 */
	private List<PosicaoTrajeto> trajetoVolta;
	
	/**
	 * Pontos de parada do ônibus
	 */
	private List<PosicaoTrajeto> pontosParada;
	
	/**
	 * Inicializa a linha
	 */
	public Linha(String identificador)
	{
		this.identificador = identificador;
		this.veiculos = new HashMap<String, Veiculo>();
		this.trajetoIda = new ArrayList<PosicaoTrajeto>();
		this.trajetoVolta = new ArrayList<PosicaoTrajeto>();
		this.pontosParada = new ArrayList<PosicaoTrajeto>();
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
		adicionaTrajeto(trajetoIda, latitude, longitude); 
	}

	/**
	 * Adiciona uma posição no trajeto de volta do ônibus
	 */
	public void adicionaTrajetoVolta(double latitude, double longitude) 
	{
		adicionaTrajeto(trajetoVolta, latitude, longitude); 
	}

	/**
	 * Adiciona uma posição em um trajeto do ônibus
	 */
	private void adicionaTrajeto(List<PosicaoTrajeto> trajeto, double latitude, double longitude) 
	{
		for (PosicaoTrajeto posicao : trajeto)
			if (posicao.igual(latitude, longitude))
				return;
		
		trajeto.add(new PosicaoTrajeto(latitude, longitude));
	}

	/**
	 * Retorna o número de posições do trajeto de ida
	 */
	public int contaPosicoesTrajetoIda() 
	{
		return trajetoIda.size();
	}
	
	/**
	 * Retorna a sequência de posições do trajeto de ida
	 */
	public Iterable<PosicaoTrajeto> getTrajetoIda() 
	{
		return trajetoIda;
	}
	
	/**
	 * Retorna o número de posições do trajeto de volta
	 */
	public int contaPosicoesTrajetoVolta() 
	{
		return trajetoVolta.size();
	}
	
	/**
	 * Retorna a sequência de posições do trajeto de volta
	 */
	public Iterable<PosicaoTrajeto> getTrajetoVolta() 
	{
		return trajetoVolta;
	}

	/**
	 * Adiciona um ponto de parada na linha de ônibus
	 */
	public void adicionaParada(double latitude, double longitude) 
	{
		for (PosicaoTrajeto posicao : pontosParada)
			if (posicao.igual(latitude, longitude))
				return;
		
		pontosParada.add(new PosicaoTrajeto(latitude, longitude));
	}

	/**
	 * Retorna o número de pontos de parada da linha
	 */
	public int contaPosicoesParada() 
	{
		return pontosParada.size();
	}
	
	/**
	 * Retorna a sequência de pontos de parada da linha
	 */
	public Iterable<PosicaoTrajeto> getPontosParada() 
	{
		return pontosParada;
	}
}