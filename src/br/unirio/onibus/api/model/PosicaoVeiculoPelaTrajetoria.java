package br.unirio.onibus.api.model;
import java.util.HashMap;
import java.util.Iterator;

import lombok.Getter;

import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a posição de um veículo durante sua Trajetoria 
 * 
 * @author Vitor Lima
 */
public class PosicaoVeiculoPelaTrajetoria
{
	private @Getter PosicaoPorMinuto[] posicoes;
	
	private Trajetoria trajetoriaIda;
	
	private Trajetoria trajetoriaVolta;
	
	private int horaInicial = 7;
	
	private int minutoInicial = 0;
	
	private int horaFinal = 20;
	
	private int minutoFinal = 59;
	
	/**
	 * Método principal que retorna os veiculos e suas respectivas posicoes por minuto
	 */
	public HashMap<String, PosicaoVeiculoPelaTrajetoria> geraPosicaoPelosMinutosDeUmaLinha(Linha linha) {
		HashMap<String, PosicaoVeiculoPelaTrajetoria> veiculos = new HashMap<String, PosicaoVeiculoPelaTrajetoria>();
		for (Veiculo veiculo : linha.getVeiculos()){
			PosicaoVeiculoPelaTrajetoria posicaoVeiculoPelaTrajetoria = calculaPosicaoVeiculoPelaTrajetoria(veiculo);
			veiculos.put(veiculo.getNumeroSerie(), posicaoVeiculoPelaTrajetoria);
		}
		return veiculos;
	}

	/**
	 * Método que retorna as posicao de um veiculo
	 */
	private PosicaoVeiculoPelaTrajetoria calculaPosicaoVeiculoPelaTrajetoria(Veiculo veiculo) {
		PosicaoVeiculoPelaTrajetoria posicaoVeiculoPelaTrajetoria = new PosicaoVeiculoPelaTrajetoria(trajetoriaIda, trajetoriaVolta);
		
		//Deixar apenas as posições que estão entre o horário de início e fim
		veiculo.getTrajetoria().removePosicoesAntesHorario(horaInicial, minutoInicial);
		veiculo.getTrajetoria().removePosicoesDepoisHorario(horaFinal, minutoFinal);
		
		PosicaoVeiculo posicaoVeiculoAnterior = null;
		PosicaoVeiculo posicaoVeiculoPosterior = null;
		
		for (PosicaoVeiculo posicaoVeiculo : veiculo.getTrajetoria().getPosicoes()){
			posicaoVeiculoPosterior = posicaoVeiculo;
			if (posicaoVeiculoAnterior != null){
				posicaoVeiculoPelaTrajetoria.calculaPosicaoPorMinutoAproximada(posicaoVeiculoAnterior, posicaoVeiculoPosterior);
			}
			posicaoVeiculoAnterior = posicaoVeiculo;
		}
		return posicaoVeiculoPelaTrajetoria;
	}
	
	/**
	 * Inicializa a posição do veículo pela trajetoria
	 */
	public PosicaoVeiculoPelaTrajetoria(Trajetoria trajetoriaIda, Trajetoria trajetoriaVolta)
	{
		this.posicoes = new PosicaoPorMinuto[totalMinutos(horaFinal, minutoFinal) - totalMinutos(horaInicial, minutoInicial)];
		this.trajetoriaIda = trajetoriaIda;
		this.trajetoriaVolta = trajetoriaVolta;
	}
	
	/**
	 * Calcula a quantidade de minutos de um horário 
	 */
	private int totalMinutos(int hora, int minuto){
		return (hora * 60) + minuto;
	}
	
	/**
	 * Verifica se o horário está dentro do horário de início e o horário de fim
	 */
	public boolean dentroDoHorario(int hora, int minuto){
		int totalMinutos =  totalMinutos(hora, minuto);
		return totalMinutos >= totalMinutos(horaInicial, minutoInicial) && totalMinutos <= totalMinutos(horaFinal, minutoFinal);
	}
	
	/**
	 * Adiciona uma posicao em um determinado minuto
	 */
	public void adicionaPosicaoPorMinuto(int hora, int minuto, double latitude, double longitude, int indiceTrechoIda, int indiceTrechoVolta, PosicaoMapa posicaoMapaOriginal){
		if (dentroDoHorario(hora, minuto)){
			this.posicoes[totalMinutos(hora, minuto) - totalMinutos(horaInicial, minutoInicial)] = new PosicaoPorMinuto(latitude, longitude, indiceTrechoIda, indiceTrechoVolta, posicaoMapaOriginal);
		}
	}
	
	/**
	 * Recebe a posicao do veiculo e calcula a posicao do veiculo na trajetoria 
	 * - Verifica se está mais próxima da trajetoria de ida ou volta
	 * - Adiciona a nova posicao ao vetor de posicoes
	 */
	public void calculaPosicaoPorMinutoAproximada(PosicaoVeiculo posicaoVeiculoAnterior, PosicaoVeiculo posicaoVeiculoPosterior){
		PosicaoMapa posicaoMapaMaisPerto;
		
		int indicePosicaoAnteriorIda = retornaPosicaoTrajetoria(posicaoVeiculoAnterior, trajetoriaIda);
		int indicePosicaoPosteriorIda = indicePosicaoAnteriorIda + 1;
		int indicePosicaoAnteriorVolta = retornaPosicaoTrajetoria(posicaoVeiculoAnterior, trajetoriaVolta);
		int indicePosicaoPosteriorVolta = indicePosicaoAnteriorVolta + 1;
			
		PosicaoMapa posicaoMapaSimuladaIda = simulaPosicao(trajetoriaIda.pegaPosicaoIndice(indicePosicaoAnteriorIda), trajetoriaIda.pegaPosicaoIndice(indicePosicaoPosteriorIda), posicaoVeiculoAnterior);
		PosicaoMapa posicaoMapaSimuladaVolta = simulaPosicao(trajetoriaVolta.pegaPosicaoIndice(indicePosicaoAnteriorVolta), trajetoriaVolta.pegaPosicaoIndice(indicePosicaoPosteriorVolta), posicaoVeiculoAnterior);
			
		double distanciaIda = Geodesic.distance(posicaoMapaSimuladaIda.getLatitude(), posicaoMapaSimuladaIda.getLongitude(), posicaoVeiculoAnterior.getLatitude(), posicaoVeiculoAnterior.getLongitude());
		double distanciaVolta = Geodesic.distance(posicaoMapaSimuladaVolta.getLatitude(), posicaoMapaSimuladaVolta.getLongitude(), posicaoVeiculoAnterior.getLatitude(), posicaoVeiculoAnterior.getLongitude());
		
		if (distanciaIda < distanciaVolta){
			posicaoMapaMaisPerto = posicaoMapaSimuladaIda;
		}
		else {
			posicaoMapaMaisPerto = posicaoMapaSimuladaVolta;
		}
			
		this.adicionaPosicaoPorMinuto(posicaoVeiculoAnterior.getData().getHourOfDay(), posicaoVeiculoAnterior.getData().getMinuteOfHour(), posicaoMapaMaisPerto.getLatitude(), posicaoMapaMaisPerto.getLongitude(), indicePosicaoAnteriorIda, indicePosicaoAnteriorVolta, posicaoVeiculoAnterior);	
	}
	
	/**
	 * Retorna o ponto mais proóximo de um ponto e um trecho
	 */
	private PosicaoMapa simulaPosicao(PosicaoMapa primeiroPonto, PosicaoMapa segundoPonto, PosicaoMapa ponto){
		PosicaoMapa posicaoSimulada = Geodesic.trackClosestPoint(ponto.getLatitude(), ponto.getLongitude(), primeiroPonto.getLatitude(), primeiroPonto.getLongitude(), segundoPonto.getLatitude(), segundoPonto.getLongitude());
		/*System.out.println("Veiculo: " + ponto.getLatitude() + "," + ponto.getLongitude());
		System.out.println("Posicao1: " + primeiroPonto.getLatitude() + "," + primeiroPonto.getLongitude());
		System.out.println("Posicao2: " + segundoPonto.getLatitude() + "," + segundoPonto.getLongitude());
		System.out.println("Posicao Media: " + posicaoSimulada.getLatitude() + "," + posicaoSimulada.getLongitude());*/
		return posicaoSimulada;
	}

	/**
	 * Retorna o indice da posicao da trajetoria mais proxima ao ponto
	 */
	private int retornaPosicaoTrajetoria(PosicaoVeiculo posicaoVeiculo, Trajetoria trajetoria) {
		PosicaoMapa posicaoMapaAnterior = null;
		PosicaoMapa posicaoMapaPosterior = null;
		PosicaoMapa posicaoMaisProximaAnterior = null;
		PosicaoMapa posicaoMaisProximaPosterior = null;
		PosicaoMapa posicaoSimulada = null;
		double distancia = 0.0;
		double menorDistancia = 0.0;
		Iterator iterator = trajetoria.pegaPosicoes().iterator();
		while(iterator.hasNext()){
			posicaoMapaPosterior = (PosicaoMapa) iterator.next();
			if (posicaoMapaAnterior != null){
				distancia = Geodesic.trackDistance(posicaoVeiculo.getLatitude(), posicaoVeiculo.getLongitude(), posicaoMapaAnterior.getLatitude(), posicaoMapaAnterior.getLongitude(), posicaoMapaPosterior.getLatitude(), posicaoMapaPosterior.getLongitude());
				if (posicaoMaisProximaAnterior == null){
					menorDistancia = distancia;
					posicaoMaisProximaAnterior = posicaoMapaAnterior;
					posicaoMaisProximaPosterior = posicaoMapaPosterior;
				}
				else if (distancia < menorDistancia){
					menorDistancia = distancia;
					posicaoMaisProximaAnterior = posicaoMapaAnterior;
					posicaoMaisProximaPosterior = posicaoMapaPosterior;
				}
			}
			posicaoMapaAnterior = posicaoMapaPosterior;
		}
		return trajetoria.pegaIndicePosicao(posicaoMaisProximaAnterior);
	}

	/**
	 * Retorna a diferenca em segundos de duas posições
	 */
	private int diferencaEmSegundos(PosicaoVeiculo posicaoVeiculoAnterior, PosicaoVeiculo posicaoVeiculoPosterior) {
		int segundosPosterior = posicaoVeiculoPosterior.getData().getHourOfDay() * 3600 + posicaoVeiculoPosterior.getData().getMinuteOfHour() * 60 + posicaoVeiculoPosterior.getData().getSecondOfMinute();
		int segundosAnterior = posicaoVeiculoAnterior.getData().getHourOfDay() * 3600 + posicaoVeiculoAnterior.getData().getMinuteOfHour() * 60 +posicaoVeiculoAnterior.getData().getSecondOfMinute();
		return segundosPosterior - segundosAnterior;
	}
	
	/**
	 * Printa o resultado no console
	 */
	public void exibirResultado(){
		for (int i = 1; i <= (horaFinal - horaInicial) * 60; i++ ){
			if (posicoes[i] != null){
				PosicaoMapa posicaoMapaTrajeoriaIdaAnt = trajetoriaIda.pegaPosicaoIndice(posicoes[i].getIndiceTrechoIda());
				PosicaoMapa posicaoMapaTrajeoriaIdaPos = trajetoriaIda.pegaProximaPosicao(posicaoMapaTrajeoriaIdaAnt);
				PosicaoMapa posicaoMapaTrajeoriaVoltaAnt = trajetoriaVolta.pegaPosicaoIndice(posicoes[i].getIndiceTrechoVolta());
				PosicaoMapa posicaoMapaTrajeoriaVoltaPos = trajetoriaVolta.pegaProximaPosicao(posicaoMapaTrajeoriaVoltaAnt);
				double distanciaIda = Geodesic.trackDistance(posicoes[i].getPosicaoMapaOriginal().getLatitude(), posicoes[i].getPosicaoMapaOriginal().getLongitude(), posicaoMapaTrajeoriaIdaAnt.getLatitude(), posicaoMapaTrajeoriaIdaAnt.getLatitude(), posicaoMapaTrajeoriaIdaPos.getLatitude(), posicaoMapaTrajeoriaIdaPos.getLongitude());
				double distanciaVolta = Geodesic.trackDistance(posicoes[i].getPosicaoMapaOriginal().getLatitude(), posicoes[i].getPosicaoMapaOriginal().getLongitude(), posicaoMapaTrajeoriaVoltaAnt.getLatitude(), posicaoMapaTrajeoriaVoltaAnt.getLatitude(), posicaoMapaTrajeoriaVoltaPos.getLatitude(), posicaoMapaTrajeoriaVoltaPos.getLongitude());
				/*System.out.println((int)Math.floor(i/60) + horaInicial + ":" + i%60 + " - ");
				System.out.println(posicaoMapaTrajeoriaIdaAnt.toString());
				System.out.println(posicaoMapaTrajeoriaIdaPos.toString());
				System.out.println(posicaoMapaTrajeoriaVoltaAnt.toString());
				System.out.println(posicaoMapaTrajeoriaVoltaPos.toString());
				System.out.println(posicoes[i].getPosicaoMapaOriginal());*/
				if (distanciaIda < distanciaVolta){
					System.out.print("I");
				}
				else if (distanciaVolta < distanciaIda){
					System.out.print("V");
				}
				else{
					System.out.print("IGUAL");
				}
			}
			else
				System.out.print("-");
		}
	}
}