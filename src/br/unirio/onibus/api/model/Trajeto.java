package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.calc.Geodesic;

/**
 * Classe que representa o trajeto de um �nibus
 * 
 * @author Marcio
 */
public class Trajeto
{
	/**
	 * Lista de posi��es do trajeto
	 */
	private List<PosicaoTrajeto> posicoesTrajeto;
	
	/**
	 * Inicializa o trajeto
	 */
	public Trajeto()
	{
		this.posicoesTrajeto = new ArrayList<PosicaoTrajeto>();
	}
	
	/**
	 * Conta o n�mero de posi��es do trajeto
	 */
	public int conta()
	{
		return posicoesTrajeto.size();
	}
	
	/**
	 * Retorna uma posi��o do trajeto, dado seu �ndice
	 */
	public PosicaoTrajeto pegaPosicaoIndice(int indice)
	{
		return posicoesTrajeto.get(indice);
	}
	
	/**
	 * Adiciona uma posi��o no trajeto
	 */
	public void adiciona(double latitude, double longitude) 
	{
		for (PosicaoTrajeto posicao : posicoesTrajeto)
			if (posicao.igual(latitude, longitude))
				return;
		
		posicoesTrajeto.add(new PosicaoTrajeto(latitude, longitude));
	}
	
	/**
	 * Retorna as posi��es do trajeto
	 */
	public Iterable<PosicaoTrajeto> pegaPosicoes()
	{
		return posicoesTrajeto;
	}

	/**
	 * Calcula a dist�ncia de um ponto ao trajeto
	 */
	public double calculaDistancia(double latitude, double longitude)
	{
		double menorDistancia = Double.MAX_VALUE;
		
		if (posicoesTrajeto.size() < 2)
			return menorDistancia;
		
		PosicaoTrajeto posicaoAnterior = posicoesTrajeto.get(0);
		
		for (int i = 1; i < posicoesTrajeto.size(); i++)
		{
			PosicaoTrajeto posicaoAtual = posicoesTrajeto.get(i);
			double distancia = Geodesic.trackDistance(latitude, longitude, posicaoAnterior.getLatitude(), posicaoAnterior.getLongitude(), posicaoAtual.getLatitude(), posicaoAtual.getLongitude());
					
			if (distancia < menorDistancia)
				menorDistancia = distancia;
			
			posicaoAnterior = posicaoAtual;
		}
		
		return menorDistancia;
	}
}