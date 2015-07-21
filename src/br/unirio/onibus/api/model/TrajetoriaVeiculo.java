package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.onibus.api.calc.Geodesic;

/**
 * Classe que representa a trajet�ria de um ve�culo
 * 
 * @author Marcio
 */
public class TrajetoriaVeiculo
{
	/**
	 * Lista de posi��es da trajet�ria
	 */
	private List<PosicaoVeiculo> posicoes;
	
	/**
	 * Inicializa a trajet�ria
	 */
	public TrajetoriaVeiculo()
	{
		this.posicoes = new ArrayList<PosicaoVeiculo>();
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
	public PosicaoVeiculo pegaPosicaoIndice(int indice)
	{
		return posicoes.get(indice);
	}

	/**
	 * Verifica se dois hor�rios s�o iguais
	 */
	private boolean equalTime(DateTime data1, DateTime data2)
	{
		return data1.getHourOfDay() == data2.getHourOfDay() && data1.getMinuteOfHour() == data2.getMinuteOfHour() && data1.getSecondOfMinute() == data2.getSecondOfMinute();
	}

	/**
	 * Adiciona uma posi��o no ve�culo
	 */
	public void adiciona(DateTime data, double latitude, double longitude, double velocidade)
	{
		for (PosicaoVeiculo posicao : posicoes)
			if (equalTime(posicao.getData(), data) && posicao.getLatitude() == latitude && posicao.getLongitude() == longitude && posicao.getVelocidade() == velocidade)
				return;
		
		posicoes.add(new PosicaoVeiculo(data, latitude, longitude, velocidade));
	}
	
	/**
	 * Retorna as posi��es da trajet�ria
	 */
	public Iterable<PosicaoVeiculo> pegaPosicoes()
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
		
		PosicaoVeiculo posicaoAnterior = posicoes.get(0);
		
		for (int i = 1; i < posicoes.size(); i++)
		{
			PosicaoVeiculo posicaoAtual = posicoes.get(i);
			double distancia = Geodesic.trackDistance(latitude, longitude, posicaoAnterior.getLatitude(), posicaoAnterior.getLongitude(), posicaoAtual.getLatitude(), posicaoAtual.getLongitude());
					
			if (distancia < menorDistancia)
				menorDistancia = distancia;
			
			posicaoAnterior = posicaoAtual;
		}
		
		return menorDistancia;
	}

	/**
	 * Ordena as posi��es do ve�culo
	 */
	public void ordenaPosicoes() 
	{
		Collections.sort(posicoes, new Comparator<PosicaoVeiculo>() {
			public int compare(PosicaoVeiculo p0, PosicaoVeiculo p1) 
			{
				return p0.getData().compareTo(p1.getData());
			}
		});
	}

	/**
	 * Retorna a trajet�ria do ve�culo como uma trajet�ria convencional
	 */
	public Trajetoria asTrajetoria() 
	{
		Trajetoria trajetoria = new Trajetoria();
		
		for (PosicaoVeiculo posicao : posicoes)
			trajetoria.adiciona(new PosicaoMapa(posicao.getLatitude(), posicao.getLongitude()));
		
		return trajetoria;
	}
}