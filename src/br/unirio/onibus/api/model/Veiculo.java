package br.unirio.onibus.api.model;

import lombok.Getter;

import org.joda.time.Seconds;

/**
 * Classe que representa um veículo
 * 
 * @author Marcio Barros
 */
public class Veiculo
{
	/**
	 * Número de série do veículo
	 */
	private @Getter String numeroSerie;
	
	/**
	 * Posições do veículo
	 */
	private @Getter TrajetoriaVeiculo trajetoria;
	
	/**
	 * Inicializa o veículo
	 */
	public Veiculo(String numeroSerie)
	{
		this.numeroSerie = numeroSerie;
		this.trajetoria = new TrajetoriaVeiculo();
	}

	/**
	 * Calcula o máximo intervalo de tempo em uma sequência de posições do ônibus, em segundos
	 */
	public int calculaMaximoIntervaloTempo(PosicaoVeiculo posicaoInicio, PosicaoVeiculo posicaoTermino) 
	{
		int indiceInicio = trajetoria.pegaIndicePosicao(posicaoInicio);
		int indiceTermino = trajetoria.pegaIndicePosicao(posicaoTermino);
		int maximo = 0;
		
		for (int i = indiceInicio+1; i <= indiceTermino; i++)
		{
			PosicaoVeiculo anterior = trajetoria.pegaPosicaoIndice(i-1);
			PosicaoVeiculo atual = trajetoria.pegaPosicaoIndice(i);
			int tempo = Seconds.secondsBetween(anterior.getData(), atual.getData()).getSeconds();
			
			if (tempo > maximo) 
				maximo = tempo;
		}
		
		return maximo;
	}
}