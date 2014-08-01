package br.unirio.onibus.api.model;
import lombok.Getter;

/**
 * Classe que representa uma posição no trajeto de uma linha de ônibus 
 * 
 * @author Marcio Barros
 */
public class PosicaoTrajeto
{
	private @Getter double latitude;
	private @Getter double longitude;
	
	public PosicaoTrajeto(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public boolean igual(double latitude, double longitude)
	{
		return Math.abs(this.latitude - latitude) < 0.001 && Math.abs(this.longitude - longitude) < 0.001;
	}
}