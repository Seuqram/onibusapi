package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa uma trajetoria
 * 
 * @author Marcio
 */
public class Trajetoria
{
	/**
	 * Lista de posicoes do trajeto
	 */
	private List<PosicaoMapa> posicoes;
	
	/**
	 * Inicializa a trajetoria
	 */
	public Trajetoria()
	{
		this.posicoes = new ArrayList<PosicaoMapa>();
	}
	
	/**
	 * Conta o numero de posicoes na trajetoria
	 */
	public int conta()
	{
		return posicoes.size();
	}
	
	/**
	 * Retorna uma posicao da trajetoria, dado seu indice
	 */
	public PosicaoMapa pegaPosicaoIndice(int indice)
	{
		return posicoes.get(indice);
	}

	/**
	 * Retorna o indice de uma posicao da trajetoria
	 */
	public int pegaIndicePosicao(PosicaoMapa posicao) 
	{
		return posicoes.indexOf(posicao);
	}
	
	/**
	 * Adiciona uma posicao na trajetoria
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
	 * Retorna as posicoes da trajetoria
	 */
	public Iterable<PosicaoMapa> pegaPosicoes()
	{
		return posicoes;
	}

	/**
	 * Calcula a distancia de um ponto a trajetoria
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
	 * Retorna o ponto mais proximo de uma posicao
	 */
	public PosicaoMapa pegaPontoMaisProximo(double latitude, double longitude) 
	{
		return pegaPontoMaisProximo(latitude, longitude, 0);
	}

	/**
	 * Retorna o ponto mais proximo de uma posicao
	 */
	public PosicaoMapa pegaPontoMaisProximo(double latitude, double longitude, int indiceInicio) 
	{
		if (posicoes.size() <= indiceInicio)
			return null;
		
		PosicaoMapa posicaoMaisProxima = posicoes.get(indiceInicio);
		double menorDistancia = Geodesic.distance(latitude, longitude, posicaoMaisProxima.getLatitude(), posicaoMaisProxima.getLongitude());;
		
		for (int i = indiceInicio+1; i < posicoes.size(); i++)
		{
			PosicaoMapa posicao = posicoes.get(i);
			double distancia = Geodesic.distance(latitude, longitude, posicao.getLatitude(), posicao.getLongitude());
			
			if (distancia < menorDistancia)
			{
				menorDistancia = distancia;
				posicaoMaisProxima = posicao;
			}
		}
		
		return posicaoMaisProxima;
	}

	/**
	 * Retorna o indice do ponto mais proximo de uma posicao 
	 */
	public int pegaIndicePontoMaisProximo(double latitude, double longitude) 
	{
		return pegaIndicePontoMaisProximo(latitude, longitude, 0);
	}

	/**
	 * Retorna o indice do ponto mais proximo de uma posicao percorrendo a partir de um indice
	 */
	public int pegaIndicePontoMaisProximo(double latitude, double longitude, int indiceInicio) 
	{
		if (posicoes.size() <= indiceInicio)
			return -1;
		
		int indiceMaisProximo = indiceInicio;
		PosicaoMapa posicao = posicoes.get(indiceInicio);		
		double menorDistancia = Geodesic.distance(latitude, longitude, posicao.getLatitude(), posicao.getLongitude());;
		
		for (int i = indiceInicio+1; i < posicoes.size(); i++)
		{
			posicao = posicoes.get(i);
			double distancia = Geodesic.distance(latitude, longitude, posicao.getLatitude(), posicao.getLongitude());
			
			if (distancia < menorDistancia)
			{
				menorDistancia = distancia;
				indiceMaisProximo = i;
			}
		}
		
		return indiceMaisProximo;
	}
}