package br.unirio.onibus.api.model;

import lombok.Getter;

import org.joda.time.Seconds;

/**
 * Classe que representa um ve�culo
 * 
 * @author Marcio Barros
 */
public class Veiculo
{
	/**
	 * N�mero de s�rie do ve�culo
	 */
	private @Getter String numeroSerie;
	
	/**
	 * Posi��es do ve�culo
	 */
	private @Getter TrajetoriaVeiculo trajetoria;
	
	/**
	 * Inicializa o ve�culo
	 */
	public Veiculo(String numeroSerie)
	{
		this.numeroSerie = numeroSerie;
		this.trajetoria = new TrajetoriaVeiculo();
	}

	/**
	 * Calcula o m�ximo intervalo de tempo em uma sequ�ncia de posi��es do �nibus, em segundos
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