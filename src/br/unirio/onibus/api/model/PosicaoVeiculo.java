package br.unirio.onibus.api.model;
import lombok.Getter;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a posição de um veículo em um instante de tempo 
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
	 * Inicializa a posição do veículo
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
	 * Verifica se o horário da posição é compatível com um horário recebido
	 */
	public boolean horarioIgualOuPosterior(int hora, int minuto)
	{
		return data.getMinuteOfDay() >= hora * 60 + minuto;
	}
	
	/**
	 * Indica que a posição não faz parte dos trajetos de ida e volta do veículo
	 */
	public void setPosicaoErro()
	{
		this.tipo = TipoPosicaoVeiculo.Erro;
		this.indiceTrajeto = -1; 
	}
	
	/**
	 * Indica que a posição faz parte do trajeto de ida do veículo
	 */
	public void setPosicaoTrajetoIda(int indicePosicao)
	{
		this.tipo = TipoPosicaoVeiculo.Ida;
		this.indiceTrajeto = indicePosicao; 
	}
	
	/**
	 * Indica que a posição faz parte do trajeto de volta do veículo
	 */
	public void setPosicaoTrajetoVolta(int indicePosicao)
	{
		this.tipo = TipoPosicaoVeiculo.Volta;
		this.indiceTrajeto = indicePosicao; 
	}
}