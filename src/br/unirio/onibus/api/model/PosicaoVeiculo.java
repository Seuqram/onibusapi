package br.unirio.onibus.api.model;
import lombok.Getter;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a posi��o de um ve�culo em um instante de tempo 
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
	 * Inicializa a posi��o do ve�culo
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
	 * Verifica se o hor�rio da posi��o � compat�vel com um hor�rio recebido
	 */
	public boolean horarioIgualOuPosterior(int hora, int minuto)
	{
		return data.getMinuteOfDay() >= hora * 60 + minuto;
	}
	
	/**
	 * Indica que a posi��o n�o faz parte dos trajetos de ida e volta do ve�culo
	 */
	public void setPosicaoErro()
	{
		this.tipo = TipoPosicaoVeiculo.Erro;
		this.indiceTrajeto = -1; 
	}
	
	/**
	 * Indica que a posi��o faz parte do trajeto de ida do ve�culo
	 */
	public void setPosicaoTrajetoIda(int indicePosicao)
	{
		this.tipo = TipoPosicaoVeiculo.Ida;
		this.indiceTrajeto = indicePosicao; 
	}
	
	/**
	 * Indica que a posi��o faz parte do trajeto de volta do ve�culo
	 */
	public void setPosicaoTrajetoVolta(int indicePosicao)
	{
		this.tipo = TipoPosicaoVeiculo.Volta;
		this.indiceTrajeto = indicePosicao; 
	}
}