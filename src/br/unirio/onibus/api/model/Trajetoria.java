package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa uma trajetória
 * 
 * @author Marcio
 */
public class Trajetoria
{
	/**
	 * Lista de posições do trajeto
	 */
	private List<PosicaoMapa> posicoes;
	
	/**
	 * Inicializa a trajetória
	 */
	public Trajetoria()
	{
		this.posicoes = new ArrayList<PosicaoMapa>();
	}
	
	/**
	 * Conta o número de posições na trajetória
	 */
	public int conta()
	{
		return posicoes.size();
	}
	
	/**
	 * Retorna uma posição da trajetória, dado seu índice
	 */
	public PosicaoMapa pegaPosicaoIndice(int indice)
	{
		return posicoes.get(indice);
	}
	
	/**
	 * Retorna próxima posição da trajetória
	 */
	public PosicaoMapa pegaProximaPosicao(PosicaoMapa posicao)
	{
		return posicoes.get(pegaIndicePosicao(posicao) + 1);
	}

	/**
	 * Retorna o índice de uma posição da trajetória
	 */
	public int pegaIndicePosicao(PosicaoMapa posicao) 
	{
		return posicoes.indexOf(posicao);
	}
	
	/**
	 * Retorna o índice de uma posição da trajetória
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
	 * Adiciona uma posição na trajetória
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
	 * Retorna as posições da trajetória
	 */
	public Iterable<PosicaoMapa> pegaPosicoes()
	{
		return posicoes;
	}

	/**
	 * Calcula a distância de um ponto à trajetória
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
	 * Retorna o ponto mais próximo de uma posição
	 */
	public PosicaoMapa pegaPontoMaisProximo(double latitude, double longitude) 
	{
		return pegaPontoMaisProximo(latitude, longitude, 0);
	}

	/**
	 * Retorna o ponto mais próximo de uma posição
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
	 * Retorna o índice do ponto mais próximo de uma posição 
	 */
	public int pegaIndicePontoMaisProximo(double latitude, double longitude) 
	{
		return pegaIndicePontoMaisProximo(latitude, longitude, 0);
	}

	/**
	 * Retorna o índice do ponto mais próximo de uma posição percorrendo a partir de um índice
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