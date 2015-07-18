package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.calc.Geodesic;

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
//
//	/**
//	 * Ordena as posições do veículo
//	 */
//	public void ordenaPosicoes(Comparator<PosicaoMapa> comparador) 
//	{
//		Collections.sort(posicoes, comparador);
//	}
}