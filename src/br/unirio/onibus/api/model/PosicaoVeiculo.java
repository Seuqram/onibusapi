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
	
	public PosicaoVeiculo(DateTime data, double latitude, double longitude, double velocidade)
	{
		super(latitude, longitude);
		this.data = data;
		this.velocidade = velocidade;
	}

	public boolean horarioIgualOuPosterior(int hora, int minuto)
	{
		return data.getMinuteOfDay() >= hora * 60 + minuto;
	}
}