package br.unirio.onibus.api.model;
import lombok.Getter;

import org.joda.time.DateTime;

/**
 * Classe que representa a posição de um veículo em um instante de tempo 
 * 
 * @author Marcio Barros
 */
public class PosicaoVeiculo
{
	private @Getter DateTime data;
	private @Getter double latitude;
	private @Getter double longitude;
	private @Getter double velocidade;
	
	public PosicaoVeiculo(DateTime data, double latitude, double longitude, double velocidade)
	{
		this.data = data;
		this.latitude = latitude;
		this.longitude = longitude;
		this.velocidade = velocidade;
	}
}