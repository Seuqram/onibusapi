package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.Geodesic;

/**
 * Classe que representa a trajetória de um veículo
 * 
 * @author Marcio
 */
public class TrajetoriaVeiculo
{
	/**
	 * Lista de posições da trajetória
	 */
	private List<PosicaoVeiculo> posicoes;
	
	/**
	 * Inicializa a trajetória
	 */
	public TrajetoriaVeiculo()
	{
		this.posicoes = new ArrayList<PosicaoVeiculo>();
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
	public PosicaoVeiculo pegaPosicaoIndice(int indice)
	{
		return posicoes.get(indice);
	}

	/**
	 * Verifica se dois horários são iguais
	 */
	private boolean equalTime(DateTime data1, DateTime data2)
	{
		return data1.getHourOfDay() == data2.getHourOfDay() && data1.getMinuteOfHour() == data2.getMinuteOfHour() && data1.getSecondOfMinute() == data2.getSecondOfMinute();
	}

	/**
	 * Adiciona uma posição no veículo
	 */
	public void adiciona(DateTime data, double latitude, double longitude, double velocidade)
	{
		for (PosicaoVeiculo posicao : posicoes)
			if (equalTime(posicao.getData(), data) && posicao.getLatitude() == latitude && posicao.getLongitude() == longitude && posicao.getVelocidade() == velocidade)
				return;
		
		posicoes.add(new PosicaoVeiculo(data, latitude, longitude, velocidade));
	}
	
	/**
	 * Retorna as posições da trajetória
	 */
	public Iterable<PosicaoVeiculo> pegaPosicoes()
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
	 * Ordena as posições do veículo
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
	 * Retorna a trajetória do veículo como uma trajetória convencional
	 */
	public Trajetoria asTrajetoria() 
	{
		Trajetoria trajetoria = new Trajetoria();
		
		for (PosicaoVeiculo posicao : posicoes)
			trajetoria.adiciona(new PosicaoMapa(posicao.getLatitude(), posicao.getLongitude()));
		
		return trajetoria;
	}

	/**
	 * Pega a posição em que o veículo passa em um determinado ponto a partir de uma determinada hora e minuto
	 */
	public PosicaoVeiculo pegaPosicaoProximaPassagem(double latitude, double longitude, int hora, int minuto)
	{
		for (PosicaoVeiculo posicao : posicoes)
		{
			if (posicao.horarioIgualOuPosterior(hora, minuto))
			{
				double distancia = Geodesic.distance(latitude, longitude, posicao.getLatitude(), posicao.getLongitude());
				
				if (distancia < 0.01)
					return posicao;
			}
		}

		return null;
	}

	/**
	 * Retorna a posição de um ônibus mais próxima a um ponto considerando uma posição inicial
	 */
	public PosicaoVeiculo pegaPosicaoProximaPassagem(double latitude, double longitude, PosicaoVeiculo posicaoInicial)
	{
		int indiceInicial = posicoes.indexOf(posicaoInicial);
		
		if (indiceInicial == -1)
			return null;

		double ultimaDistancia = Double.MAX_VALUE;
		PosicaoVeiculo ultimaPosicao = posicaoInicial;
		
		for (int i = indiceInicial+1; i < posicoes.size(); i++)
		{
			PosicaoVeiculo posicaoAtual = posicoes.get(i);
			double distanciaAtual = Geodesic.distance(latitude, longitude, posicaoAtual.getLatitude(), posicaoAtual.getLongitude());
			
			if (distanciaAtual > ultimaDistancia && distanciaAtual < 0.1)
				return ultimaPosicao;
			
			ultimaDistancia = distanciaAtual;
			ultimaPosicao = posicaoAtual;
		}

		return null;
	}
	
	/**
	 * Pega o número de minutos para o veículo passar em um determinado ponto a partir de uma determinada hora e minuto
	 */
	public int pegaMinutosProximaPassagemPosicao(double latitude, double longitude, int hora, int minuto)
	{
		PosicaoVeiculo posicao = pegaPosicaoProximaPassagem(latitude, longitude, hora, minuto);
		
		if (posicao != null)
			return (posicao.getData().getHourOfDay() - hora) * 60 + (posicao.getData().getMinuteOfHour() - minuto);

		return 24 * 60;
	}
}