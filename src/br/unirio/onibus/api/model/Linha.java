package br.unirio.onibus.api.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private List<PosicaoTrajeto> trajetoIda;
	
	/**
	 * Trajeto de volta do �nibus
	 */
	private List<PosicaoTrajeto> trajetoVolta;
	
	/**
	 * Pontos de parada do �nibus
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
		adicionaTrajeto(trajetoIda, latitude, longitude); 
	}

	/**
	 * Adiciona uma posi��o no trajeto de volta do �nibus
	 */
	public void adicionaTrajetoVolta(double latitude, double longitude) 
	{
		adicionaTrajeto(trajetoVolta, latitude, longitude); 
	}

	/**
	 * Adiciona uma posi��o em um trajeto do �nibus
	 */
	private void adicionaTrajeto(List<PosicaoTrajeto> trajeto, double latitude, double longitude) 
	{
		for (PosicaoTrajeto posicao : trajeto)
			if (posicao.igual(latitude, longitude))
				return;
		
		trajeto.add(new PosicaoTrajeto(latitude, longitude));
	}

	/**
	 * Retorna o n�mero de posi��es do trajeto de ida
	 */
	public int contaPosicoesTrajetoIda() 
	{
		return trajetoIda.size();
	}
	
	/**
	 * Retorna a sequ�ncia de posi��es do trajeto de ida
	 */
	public Iterable<PosicaoTrajeto> getTrajetoIda() 
	{
		return trajetoIda;
	}
	
	/**
	 * Retorna o n�mero de posi��es do trajeto de volta
	 */
	public int contaPosicoesTrajetoVolta() 
	{
		return trajetoVolta.size();
	}
	
	/**
	 * Retorna a sequ�ncia de posi��es do trajeto de volta
	 */
	public Iterable<PosicaoTrajeto> getTrajetoVolta() 
	{
		return trajetoVolta;
	}

	/**
	 * Adiciona um ponto de parada na linha de �nibus
	 */
	public void adicionaParada(double latitude, double longitude) 
	{
		for (PosicaoTrajeto posicao : pontosParada)
			if (posicao.igual(latitude, longitude))
				return;
		
		pontosParada.add(new PosicaoTrajeto(latitude, longitude));
	}

	/**
	 * Retorna o n�mero de pontos de parada da linha
	 */
	public int contaPosicoesParada() 
	{
		return pontosParada.size();
	}
	
	/**
	 * Retorna a sequ�ncia de pontos de parada da linha
	 */
	public Iterable<PosicaoTrajeto> getPontosParada() 
	{
		return pontosParada;
	}
}