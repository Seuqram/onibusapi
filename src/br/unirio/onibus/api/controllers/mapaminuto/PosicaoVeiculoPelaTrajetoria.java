package br.unirio.onibus.api.controllers.mapaminuto;

import java.util.HashMap;
import java.util.Iterator;

import lombok.Getter;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.TipoPosicaoVeiculo;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.model.Veiculo;
import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a posicao de um veiculo durante sua Trajetoria
 * 
 * @author Vitor Lima
 */
public class PosicaoVeiculoPelaTrajetoria
{
	private @Getter PosicaoPorMinuto[] posicoes;

	private Trajetoria trajetoriaIda;

	private Trajetoria trajetoriaVolta;

	private static final int HORA_INICIAL = 7;
 
	private static final int MINUTO_INICIAL = 0;

	private static final int HORA_FINAL = 20;

	private static final int MINUTO_FINAL = 59;

	/**
	 * Inicializa a posicao do veiculo pela trajetoria
	 */
	public PosicaoVeiculoPelaTrajetoria(Trajetoria trajetoriaIda, Trajetoria trajetoriaVolta)
	{
		this.posicoes = new PosicaoPorMinuto[totalMinutos(HORA_FINAL, MINUTO_FINAL) - totalMinutos(HORA_INICIAL, MINUTO_INICIAL)];
		this.trajetoriaIda = trajetoriaIda;
		this.trajetoriaVolta = trajetoriaVolta;
	}

	/**
	 * Metodo principal que retorna os veiculos e suas respectivas posicoes por minuto
	 */
	public HashMap<String, PosicaoVeiculoPelaTrajetoria> geraPosicaoPelosMinutosDeUmaLinha(Linha linha)
	{
		HashMap<String, PosicaoVeiculoPelaTrajetoria> veiculos = new HashMap<String, PosicaoVeiculoPelaTrajetoria>();
		
		for (Veiculo veiculo : linha.getVeiculos())
		{
			PosicaoVeiculoPelaTrajetoria posicaoVeiculoPelaTrajetoria = calculaPosicaoVeiculoPelaTrajetoria(veiculo);
			veiculos.put(veiculo.getNumeroSerie(), posicaoVeiculoPelaTrajetoria);
		}
		
		return veiculos;
	}

	/**
	 * Metodo que retorna as posicao de um veiculo
	 */
	private PosicaoVeiculoPelaTrajetoria calculaPosicaoVeiculoPelaTrajetoria(Veiculo veiculo)
	{
		PosicaoVeiculoPelaTrajetoria posicaoVeiculoPelaTrajetoria = new PosicaoVeiculoPelaTrajetoria(trajetoriaIda, trajetoriaVolta);

		// Deixar apenas as posicoes que estao entre o horario de inicio e fim
		// TODO: fazer a implementacao sem remover os pontos das trajetorias - elas podem ser usadas para outra coisa ...
		veiculo.getTrajetoria().removePosicoesAntesHorario(HORA_INICIAL, MINUTO_INICIAL);
		veiculo.getTrajetoria().removePosicoesDepoisHorario(HORA_FINAL, MINUTO_FINAL);

		PosicaoVeiculo posicaoVeiculoAnterior = null;

		for (PosicaoVeiculo posicaoVeiculo : veiculo.getTrajetoria().getPosicoes())
		{
			PosicaoVeiculo posicaoVeiculoPosterior = posicaoVeiculo;
			
			if (posicaoVeiculoAnterior != null)
				posicaoVeiculoPelaTrajetoria.calculaPosicaoPorMinutoAproximada(posicaoVeiculoAnterior, posicaoVeiculoPosterior);

			posicaoVeiculoAnterior = posicaoVeiculo;
		}

		return posicaoVeiculoPelaTrajetoria;
	}

	/**
	 * Calcula a quantidade de minutos de um horario
	 */
	private int totalMinutos(int hora, int minuto)
	{
		return (hora * 60) + minuto;
	}

	/**
	 * Verifica se o horario esta dentro do horario de inicio e o horario de fim
	 */
	public boolean dentroDoHorario(int hora, int minuto)
	{
		if (hora < HORA_INICIAL || hora > HORA_FINAL)
			return false;
		
		if (hora == HORA_INICIAL && minuto < MINUTO_INICIAL)
			return false;
		
		if (hora == HORA_FINAL && minuto > MINUTO_FINAL)
			return false;
		
		return true;
	}

	/**
	 * Adiciona uma posicao em um determinado minuto
	 */
	public void adicionaPosicaoPorMinuto(int hora, int minuto, double latitude, double longitude, int indiceTrechoIda, int indiceTrechoVolta, PosicaoMapa posicaoMapaOriginal, TipoPosicaoVeiculo tipoPosicao)
	{
		if (dentroDoHorario(hora, minuto))
		{
			int indice = totalMinutos(hora, minuto) - totalMinutos(HORA_INICIAL, MINUTO_INICIAL);
			this.posicoes[indice] = new PosicaoPorMinuto(latitude, longitude, indiceTrechoIda, indiceTrechoVolta, posicaoMapaOriginal, tipoPosicao);
		}
	}

	/**
	 * Recebe a posicao do veiculo e calcula a posicao do veiculo na trajetoria
	 * - Verifica se esta mais proxima da trajetoria de ida ou volta - Adiciona
	 * a nova posicao ao vetor de posicoes
	 */
	public void calculaPosicaoPorMinutoAproximada(PosicaoVeiculo posicaoVeiculoAnterior, PosicaoVeiculo posicaoVeiculoPosterior)
	{
		int indicePosicaoAnteriorIda = retornaPosicaoTrajetoria(posicaoVeiculoAnterior, trajetoriaIda);
		int indicePosicaoPosteriorIda = indicePosicaoAnteriorIda + 1;
		PosicaoMapa posicaoMapaSimuladaIda = simulaPosicao(trajetoriaIda.pegaPosicaoIndice(indicePosicaoAnteriorIda), trajetoriaIda.pegaPosicaoIndice(indicePosicaoPosteriorIda), posicaoVeiculoAnterior);

		double distanciaIda = Geodesic.distance(
				posicaoMapaSimuladaIda.getLatitude(),
				posicaoMapaSimuladaIda.getLongitude(),
				posicaoVeiculoAnterior.getLatitude(),
				posicaoVeiculoAnterior.getLongitude());
		
		int indicePosicaoAnteriorVolta = retornaPosicaoTrajetoria(posicaoVeiculoAnterior, trajetoriaVolta);
		int indicePosicaoPosteriorVolta = indicePosicaoAnteriorVolta + 1;
		PosicaoMapa posicaoMapaSimuladaVolta = simulaPosicao(trajetoriaVolta.pegaPosicaoIndice(indicePosicaoAnteriorVolta), trajetoriaVolta.pegaPosicaoIndice(indicePosicaoPosteriorVolta), posicaoVeiculoAnterior);

		double distanciaVolta = Geodesic.distance(
				posicaoMapaSimuladaVolta.getLatitude(),
				posicaoMapaSimuladaVolta.getLongitude(),
				posicaoVeiculoAnterior.getLatitude(),
				posicaoVeiculoAnterior.getLongitude());
		
		PosicaoMapa posicaoMapaMaisPerto;
		TipoPosicaoVeiculo tipoPosicao;

		if (Math.abs(distanciaIda - distanciaVolta) < 0.001)
		{
			posicaoMapaMaisPerto = null;
			tipoPosicao = TipoPosicaoVeiculo.Desconhecido;
		}
		if (distanciaIda < distanciaVolta)
		{
			posicaoMapaMaisPerto = posicaoMapaSimuladaIda;
			tipoPosicao = TipoPosicaoVeiculo.Ida;
		} 
		else
		{
			posicaoMapaMaisPerto = posicaoMapaSimuladaVolta;
			tipoPosicao = TipoPosicaoVeiculo.Volta;
		}

		this.adicionaPosicaoPorMinuto(posicaoVeiculoAnterior.getData()
				.getHourOfDay(), posicaoVeiculoAnterior.getData()
				.getMinuteOfHour(), posicaoMapaMaisPerto.getLatitude(),
				posicaoMapaMaisPerto.getLongitude(), indicePosicaoAnteriorIda,
				indicePosicaoAnteriorVolta, posicaoVeiculoAnterior, tipoPosicao);
	}

	/**
	 * Retorna o ponto mais proximo de um ponto e um trecho
	 */
	private PosicaoMapa simulaPosicao(PosicaoMapa primeiroPonto, PosicaoMapa segundoPonto, PosicaoMapa ponto)
	{
		PosicaoMapa posicaoSimulada = Geodesic.trackClosestPoint(
				ponto.getLatitude(), ponto.getLongitude(),
				primeiroPonto.getLatitude(), primeiroPonto.getLongitude(),
				segundoPonto.getLatitude(), segundoPonto.getLongitude());
		/*
		 * System.out.println("Veiculo: " + ponto.getLatitude() + "," +
		 * ponto.getLongitude()); System.out.println("Posicao1: " +
		 * primeiroPonto.getLatitude() + "," + primeiroPonto.getLongitude());
		 * System.out.println("Posicao2: " + segundoPonto.getLatitude() + "," +
		 * segundoPonto.getLongitude()); System.out.println("Posicao Media: " +
		 * posicaoSimulada.getLatitude() + "," +
		 * posicaoSimulada.getLongitude());
		 */
		return posicaoSimulada;
	}

	/**
	 * Retorna o indice da posicao da trajetoria mais proxima ao ponto
	 */
	private int retornaPosicaoTrajetoria(PosicaoVeiculo posicaoVeiculo, Trajetoria trajetoria)
	{
		PosicaoMapa posicaoMapaAnterior = null;
		PosicaoMapa posicaoMaisProximaAnterior = null;
		double menorDistancia = 0.0;

		Iterator<PosicaoMapa> iterator = trajetoria.pegaPosicoes().iterator();
		
		while (iterator.hasNext())
		{
			PosicaoMapa posicaoMapaPosterior = (PosicaoMapa) iterator.next();
		
			if (posicaoMapaAnterior != null)
			{
				double distancia = Geodesic.trackDistance(
						posicaoVeiculo.getLatitude(),
						posicaoVeiculo.getLongitude(),
						posicaoMapaAnterior.getLatitude(),
						posicaoMapaAnterior.getLongitude(),
						posicaoMapaPosterior.getLatitude(),
						posicaoMapaPosterior.getLongitude());
				
				if (posicaoMaisProximaAnterior == null || distancia < menorDistancia)
				{
					menorDistancia = distancia;
					posicaoMaisProximaAnterior = posicaoMapaAnterior;
				}
			}
			
			posicaoMapaAnterior = posicaoMapaPosterior;
		}
		
		return trajetoria.pegaIndicePosicao(posicaoMaisProximaAnterior);
	}

	/**
	 * Retorna a diferenca em segundos de duas posicoes
	 */
	// private int diferencaEmSegundos(PosicaoVeiculo posicaoVeiculoAnterior,
	// PosicaoVeiculo posicaoVeiculoPosterior) {
	// int segundosPosterior = posicaoVeiculoPosterior.getData().getHourOfDay()
	// * 3600 + posicaoVeiculoPosterior.getData().getMinuteOfHour() * 60 +
	// posicaoVeiculoPosterior.getData().getSecondOfMinute();
	// int segundosAnterior = posicaoVeiculoAnterior.getData().getHourOfDay() *
	// 3600 + posicaoVeiculoAnterior.getData().getMinuteOfHour() * 60
	// +posicaoVeiculoAnterior.getData().getSecondOfMinute();
	// return segundosPosterior - segundosAnterior;
	// }

	/**
	 * Printa o resultado no console
	 */
	public void exibirResultado()
	{
		for (int i = 1; i <= (HORA_FINAL - HORA_INICIAL) * 60; i++)
		{
			if (posicoes[i] != null)
			{
//				PosicaoMapa posicaoMapaTrajeoriaIdaAnt = trajetoriaIda
//						.pegaPosicaoIndice(posicoes[i].getIndiceTrechoIda());
//				PosicaoMapa posicaoMapaTrajeoriaIdaPos = trajetoriaIda
//						.pegaProximaPosicao(posicaoMapaTrajeoriaIdaAnt);
//				PosicaoMapa posicaoMapaTrajeoriaVoltaAnt = trajetoriaVolta
//						.pegaPosicaoIndice(posicoes[i].getIndiceTrechoVolta());
//				PosicaoMapa posicaoMapaTrajeoriaVoltaPos = trajetoriaVolta
//						.pegaProximaPosicao(posicaoMapaTrajeoriaVoltaAnt);
//				double distanciaIda = Geodesic.trackDistance(posicoes[i]
//						.getPosicaoMapaOriginal().getLatitude(), posicoes[i]
//						.getPosicaoMapaOriginal().getLongitude(),
//						posicaoMapaTrajeoriaIdaAnt.getLatitude(),
//						posicaoMapaTrajeoriaIdaAnt.getLatitude(),
//						posicaoMapaTrajeoriaIdaPos.getLatitude(),
//						posicaoMapaTrajeoriaIdaPos.getLongitude());
//				double distanciaVolta = Geodesic.trackDistance(posicoes[i]
//						.getPosicaoMapaOriginal().getLatitude(), posicoes[i]
//						.getPosicaoMapaOriginal().getLongitude(),
//						posicaoMapaTrajeoriaVoltaAnt.getLatitude(),
//						posicaoMapaTrajeoriaVoltaAnt.getLatitude(),
//						posicaoMapaTrajeoriaVoltaPos.getLatitude(),
//						posicaoMapaTrajeoriaVoltaPos.getLongitude());
				/*
				 * System.out.println((int)Math.floor(i/60) + horaInicial + ":"
				 * + i%60 + " - ");
				 * System.out.println(posicaoMapaTrajeoriaIdaAnt.toString());
				 * System.out.println(posicaoMapaTrajeoriaIdaPos.toString());
				 * System.out.println(posicaoMapaTrajeoriaVoltaAnt.toString());
				 * System.out.println(posicaoMapaTrajeoriaVoltaPos.toString());
				 * System.out.println(posicoes[i].getPosicaoMapaOriginal());
				 */
				if (posicoes[i].getTipoPosicao() == TipoPosicaoVeiculo.Ida)
				{
					System.out.print("I");
				} 
				else if (posicoes[i].getTipoPosicao() == TipoPosicaoVeiculo.Ida)
				{
					System.out.print("V");
				} 
				else
				{
					System.out.print("-");
				}
			} 
			else
			{
				System.out.print("-");
			}
		}
	}
}