package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa uma trajet�ria
 * 
 * @author Marcio
 */
public class Trajetoria
{
	/**
	 * Lista de posi��es do trajeto
	 */
	private List<PosicaoMapa> posicoes;
	
	/**
	 * Inicializa a trajet�ria
	 */
	public Trajetoria()
	{
		this.posicoes = new ArrayList<PosicaoMapa>();
	}
	
	/**
	 * Conta o n�mero de posi��es na trajet�ria
	 */
	public int conta()
	{
		return posicoes.size();
	}
	
	/**
	 * Retorna uma posi��o da trajet�ria, dado seu �ndice
	 */
	public PosicaoMapa pegaPosicaoIndice(int indice)
	{
		return posicoes.get(indice);
	}

	/**
	 * Retorna o �ndice de uma posi��o da trajet�ria
	 */
	public int pegaIndicePosicao(PosicaoMapa posicao) 
	{
		return posicoes.indexOf(posicao);
	}
	
	/**
	 * Adiciona uma posi��o na trajet�ria
	 */
	public void adiciona(PosicaoMapa novaPosicao) 
	{
		double latitude = novaPosicao.getLatitude();
		double longitude = novaPosicao.getLongitude();
		
		for (PosicaoMapa posicao : posicoes)
			if (posicao.igual(latitude, longitude))
				return;
		
		posicoes.add(novaPosicao);
	}
	
	/**
	 * Retorna as posi��es da trajet�ria
	 */
	public Iterable<PosicaoMapa> pegaPosicoes()
	{
		return posicoes;
	}

	/**
	 * Calcula a dist�ncia de um ponto � trajet�ria
	 */
	public double calculaDistancia(double latitude, double longitude)
	{
		double menorDistancia = Double.MAX_VALUE;
		
		if (posicoes.size() < 2)
			return menorDistancia;
		
		PosicaoMapa posicaoAnterior = posicoes.get(0);
		
		for (int i = 1; i < posicoes.size(); i++)
		{
			PosicaoMapa posicaoAtual = posicoes.get(i);
			double distancia = Geodesic.trackDistance(latitude, longitude, posicaoAnterior.getLatitude(), posicaoAnterior.getLongitude(), posicaoAtual.getLatitude(), posicaoAtual.getLongitude());
					
			if (distancia < menorDistancia)
				menorDistancia = distancia;
			
			posicaoAnterior = posicaoAtual;
		}
		
		return menorDistancia;
	}

	/**
	 * Retorna o ponto de inflex�o mais pr�ximo de uma posi��o
	 */
	public PosicaoMapa pegaPontoInflexaoMaisProximo(double latitude, double longitude) 
	{
		PosicaoMapa posicaoMaisProxima = null;
		double menorDistancia = Double.MAX_VALUE;
		
		for (PosicaoMapa posicao : posicoes)
		{
			double distancia = Geodesic.distance(latitude, longitude, posicao.getLatitude(), posicao.getLongitude());
			
			if (distancia < menorDistancia)
			{
				menorDistancia = distancia;
				posicaoMaisProxima = posicao;
			}
		}
		
		return posicaoMaisProxima;
	}
}