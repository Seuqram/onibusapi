package br.unirio.onibus.api.model;
import lombok.Getter;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a posicao de um veiculo em um instante de tempo 
 * 
 * @author Marcio Barros
 */
public class PosicaoVeiculo extends PosicaoMapa
{
	private @Getter DateTime data;
	private @Getter double velocidade;
	private @Getter TipoPosicaoVeiculo tipo;
	private @Getter int indiceTrajeto; 
	
	/**
	 * Inicializa a posicao do veiculo
	 */
	public PosicaoVeiculo(DateTime data, double latitude, double longitude, double velocidade)
	{
		super(latitude, longitude);
		this.data = data;
		this.velocidade = velocidade;
		this.tipo = TipoPosicaoVeiculo.Desconhecido;
		this.indiceTrajeto = -1;
	}

	/**
	 * Verifica se o horario da posicao e compativel com um horario recebido
	 */
	public boolean horarioIgualOuPosterior(int hora, int minuto)
	{
		return data.getMinuteOfDay() >= hora * 60 + minuto;
	}
	
	/**
	 * Indica que a posicao nao faz parte dos trajetos de ida e volta do veiculo
	 */
	public void setPosicaoErro()
	{
		this.tipo = TipoPosicaoVeiculo.Erro;
		this.indiceTrajeto = -1; 
	}
	
	/**
	 * Indica que a posicao faz parte do trajeto de ida do veiculo
	 */
	public void setPosicaoTrajetoIda(int indicePosicao)
	{
		this.tipo = TipoPosicaoVeiculo.Ida;
		this.indiceTrajeto = indicePosicao; 
	}
	
	/**
	 * Indica que a posicao faz parte do trajeto de volta do veiculo
	 */
	public void setPosicaoTrajetoVolta(int indicePosicao)
	{
		this.tipo = TipoPosicaoVeiculo.Volta;
		this.indiceTrajeto = indicePosicao; 
	}
}