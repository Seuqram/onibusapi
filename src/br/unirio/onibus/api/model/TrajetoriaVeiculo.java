package br.unirio.onibus.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a trajetoria de um veiculo
 * 
 * @author Marcio
 */
public class TrajetoriaVeiculo
{
	/**
	 * Lista de posicoes da trajetoria
	 */
	private List<PosicaoVeiculo> posicoes;
	
	/**
	 * Inicializa a trajetoria
	 */
	public TrajetoriaVeiculo()
	{
		this.posicoes = new ArrayList<PosicaoVeiculo>();
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
	public PosicaoVeiculo pegaPosicaoIndice(int indice)
	{
		return posicoes.get(indice);
	}

	/**
	 * Verifica se dois horarios sao iguais
	 */
	private boolean equalTime(DateTime data1, DateTime data2)
	{
		return data1.getHourOfDay() == data2.getHourOfDay() && data1.getMinuteOfHour() == data2.getMinuteOfHour() && data1.getSecondOfMinute() == data2.getSecondOfMinute();
	}

	/**
	 * Adiciona uma posicao no veiculo
	 */
	public void adiciona(DateTime data, double latitude, double longitude, double velocidade)
	{
		for (PosicaoVeiculo posicao : posicoes)
			if (equalTime(posicao.getData(), data) && posicao.getLatitude() == latitude && posicao.getLongitude() == longitude && posicao.getVelocidade() == velocidade)
				return;
		
		posicoes.add(new PosicaoVeiculo(data, latitude, longitude, velocidade));
	}

	/**
	 * Retorna o indice de uma determinada posicao do veiculo
	 */
	public int pegaIndicePosicao(PosicaoVeiculo posicao) 
	{
		return posicoes.indexOf(posicao);
	}
	
	/**
	 * Retorna as posicoes da trajetoria
	 */
	public Iterable<PosicaoVeiculo> getPosicoes()
	{
		return posicoes;
	}

	/**
	 * Remove todas as posicoes do veiculo
	 */
	public void limpa() 
	{
		posicoes.clear();
	}

	/**
	 * Calcula a distancia de um ponto a trajetoria
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
	 * Ordena as posicoes do veiculo
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
	 * Retorna o indice da primeira posicao apos um determinado horario - poderia ser uma busca binaria
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
	 * Remove uma posicao do veiculo, dado seu indice
	 */
	public void removePosicao(int indice) 
	{
		posicoes.remove(indice);
	}

	/**
	 * Remove uma posicao do veiculo, dado seu indice
	 */
	public void removePosicoesErro() 
	{
		for (int i = posicoes.size()-1; i >= 0; i--)
			if (posicoes.get(i).getTipo() == TipoPosicaoVeiculo.Erro)
				posicoes.remove(i);
	}

	/**
	 * Remove todas as posicoes dos veiculos depois de um horario
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
	 * Remove todas as posicoes dos veiculos antes de um horario
	 */
	public void removePosicoesAntesHorario(int hora, int minuto) 
	{
		int indice = pegaIndicePosicaoHorario(hora, minuto);
		
		for (int i = 0; i < indice; i++)
			posicoes.remove(0);
	}

	/**
	 * Retorna a trajetoria do veiculo como uma trajetoria convencional
	 */
	public Trajetoria asTrajetoria() 
	{
		Trajetoria trajetoria = new Trajetoria();
		
		for (PosicaoVeiculo posicao : posicoes)
			trajetoria.adiciona(new PosicaoMapa(posicao.getLatitude(), posicao.getLongitude()));
		
		return trajetoria;
	}

	/**
	 * Retorna a posicao de um onibus mais proxima a um ponto considerando uma posicao inicial
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
	 * Pega a posicao em que o veiculo passa em um determinado ponto a partir de uma determinada hora e minuto
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
	 * Pega o numero de minutos para o veiculo passar em um determinado ponto a partir de uma determinada hora e minuto
	 */
	public int pegaMinutosProximaPassagemPosicao(PosicaoMapa destino, int hora, int minuto, double erroAceitavel)
	{
		PosicaoVeiculo posicao = pegaPosicaoProximaPassagem(destino, hora, minuto, erroAceitavel);
		
		if (posicao != null)
			return (posicao.getData().getHourOfDay() - hora) * 60 + (posicao.getData().getMinuteOfHour() - minuto);

		return 24 * 60;
	}
}