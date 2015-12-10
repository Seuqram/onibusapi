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
	 * Retorna pr�xima posi��o da trajet�ria
	 */
	public PosicaoMapa pegaProximaPosicao(PosicaoMapa posicao)
	{
		return posicoes.get(pegaIndicePosicao(posicao) + 1);
	}

	/**
	 * Retorna o �ndice de uma posi��o da trajet�ria
	 */
	public int pegaIndicePosicao(PosicaoMapa posicao) 
	{
		return posicoes.indexOf(posicao);
	}
	
	/**
	 * Retorna o �ndice de uma posi��o da trajet�ria
	 */
	public int pegaIndicePosicaoPelaLatitudeLongitude(double latitude, double longitude) 
	{
		for (PosicaoMapa posicaoMapa : posicoes){
			if (posicaoMapa.getLatitude() == latitude && posicaoMapa.getLongitude() == longitude)
				return posicoes.indexOf(posicaoMapa);
		}
		return -1;
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
	 * Retorna o ponto mais pr�ximo de uma posi��o
	 */
	public PosicaoMapa pegaPontoMaisProximo(double latitude, double longitude) 
	{
		return pegaPontoMaisProximo(latitude, longitude, 0);
	}

	/**
	 * Retorna o ponto mais pr�ximo de uma posi��o
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
	 * Retorna o �ndice do ponto mais pr�ximo de uma posi��o 
	 */
	public int pegaIndicePontoMaisProximo(double latitude, double longitude) 
	{
		return pegaIndicePontoMaisProximo(latitude, longitude, 0);
	}

	/**
	 * Retorna o �ndice do ponto mais pr�ximo de uma posi��o percorrendo a partir de um �ndice
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