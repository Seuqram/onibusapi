package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.calc.Geodesic;

/**
 * Classe que representa o trajeto de um ônibus
 * 
 * @author Marcio
 */
public class Trajeto
{
	/**
	 * Lista de posições do trajeto
	 */
	private List<PosicaoMapa> posicoesTrajeto;
	
	/**
	 * Inicializa o trajeto
	 */
	public Trajeto()
	{
		this.posicoesTrajeto = new ArrayList<PosicaoMapa>();
	}
	
	/**
	 * Conta o número de posições do trajeto
	 */
	public int conta()
	{
		return posicoesTrajeto.size();
	}
	
	/**
	 * Retorna uma posição do trajeto, dado seu índice
	 */
	public PosicaoMapa pegaPosicaoIndice(int indice)
	{
		return posicoesTrajeto.get(indice);
	}
	
	/**
	 * Adiciona uma posição no trajeto
	 */
	public void adiciona(double latitude, double longitude) 
	{
		for (PosicaoMapa posicao : posicoesTrajeto)
			if (posicao.igual(latitude, longitude))
				return;
		
		posicoesTrajeto.add(new PosicaoMapa(latitude, longitude));
	}
	
	/**
	 * Retorna as posições do trajeto
	 */
	public Iterable<PosicaoMapa> pegaPosicoes()
	{
		return posicoesTrajeto;
	}

	/**
	 * Calcula a distância de um ponto ao trajeto
	 */
	public double calculaDistancia(double latitude, double longitude)
	{
		double menorDistancia = Double.MAX_VALUE;
		
		if (posicoesTrajeto.size() < 2)
			return menorDistancia;
		
		PosicaoMapa posicaoAnterior = posicoesTrajeto.get(0);
		
		for (int i = 1; i < posicoesTrajeto.size(); i++)
		{
			PosicaoMapa posicaoAtual = posicoesTrajeto.get(i);
			double distancia = Geodesic.trackDistance(latitude, longitude, posicaoAnterior.getLatitude(), posicaoAnterior.getLongitude(), posicaoAtual.getLatitude(), posicaoAtual.getLongitude());
					
			if (distancia < menorDistancia)
				menorDistancia = distancia;
			
			posicaoAnterior = posicaoAtual;
		}
		
		return menorDistancia;
	}
}