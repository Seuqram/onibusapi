package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

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
	 * Retorna o índice de uma determinada posição do veículo
	 */
	public int pegaIndicePosicao(PosicaoVeiculo posicao) 
	{
		return posicoes.indexOf(posicao);
	}
	
	/**
	 * Retorna as posições da trajetória
	 */
	public Iterable<PosicaoVeiculo> getPosicoes()
	{
		return posicoes;
	}

	/**
	 * Remove todas as posições do veículo
	 */
	public void limpa() 
	{
		posicoes.clear();
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
	 * Retorna o índice da primeira posição após um determinado horário - poderia ser uma busca binária
	 */
	private int pegaIndicePosicaoHorario(int hora, int minuto)
	{
		for (int i = 0; i < posicoes.size(); i++)
		{
			PosicaoVeiculo posicao = posicoes.get(i);
			
			if (posicao.getData().getHourOfDay() >= hora && posicao.getData().getMinuteOfHour() >= minuto)
				return i;
		}
		
		return -1;
	}

	/**
	 * Remove uma posição do veículo, dado seu índice
	 */
	public void removePosicao(int indice) 
	{
		posicoes.remove(indice);
	}

	/**
	 * Remove uma posição do veículo, dado seu índice
	 */
	public void removePosicoesErro() 
	{
		for (int i = posicoes.size()-1; i >= 0; i--)
			if (posicoes.get(i).getTipo() == TipoPosicaoVeiculo.Erro)
				posicoes.remove(i);
	}

	/**
	 * Remove todas as posições dos veículos depois de um horário
	 */
	public void removePosicoesDepoisHorario(int hora, int minuto) 
	{
		int indice = pegaIndicePosicaoHorario(hora, minuto);
		
		if (indice != -1)
		{
			while (posicoes.size() > indice)
				posicoes.remove(indice);
		}
	}

	/**
	 * Remove todas as posições dos veículos antes de um horário
	 */
	public void removePosicoesAntesHorario(int hora, int minuto) 
	{
		int indice = pegaIndicePosicaoHorario(hora, minuto);
		
		for (int i = 0; i < indice; i++)
			posicoes.remove(0);
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
	 * Retorna a posição de um ônibus mais próxima a um ponto considerando uma posição inicial
	 */
	public PosicaoVeiculo pegaPosicaoProximaPassagem(PosicaoMapa destino, PosicaoVeiculo posicaoInicial, double erroAceitavel)
	{
		int indiceInicial = posicoes.indexOf(posicaoInicial);
		
		if (indiceInicial == -1)
			return null;

		double ultimaDistancia = Double.MAX_VALUE;
		PosicaoVeiculo ultimaPosicao = posicaoInicial;
		
		for (int i = indiceInicial+1; i < posicoes.size(); i++)
		{
			PosicaoVeiculo posicaoAtual = posicoes.get(i);
			double distanciaAtual = Geodesic.distance(destino, posicaoAtual);
			
			if (distanciaAtual > ultimaDistancia && distanciaAtual < erroAceitavel)
				return ultimaPosicao;
			
			ultimaDistancia = distanciaAtual;
			ultimaPosicao = posicaoAtual;
		}

		return null;
	}

	/**
	 * Pega a posição em que o veículo passa em um determinado ponto a partir de uma determinada hora e minuto
	 */
	public PosicaoVeiculo pegaPosicaoProximaPassagem(PosicaoMapa destino, int hora, int minuto, double erroAceitavel)
	{
		for (PosicaoVeiculo posicao : posicoes)
		{
			if (posicao.horarioIgualOuPosterior(hora, minuto))
			{
				double distancia = Geodesic.distance(destino, posicao);
				
				if (distancia < erroAceitavel)
					return posicao;
			}
		}

		return null;
	}
	
	/**
	 * Pega o número de minutos para o veículo passar em um determinado ponto a partir de uma determinada hora e minuto
	 */
	public int pegaMinutosProximaPassagemPosicao(PosicaoMapa destino, int hora, int minuto, double erroAceitavel)
	{
		PosicaoVeiculo posicao = pegaPosicaoProximaPassagem(destino, hora, minuto, erroAceitavel);
		
		if (posicao != null)
			return (posicao.getData().getHourOfDay() - hora) * 60 + (posicao.getData().getMinuteOfHour() - minuto);

		return 24 * 60;
	}
}