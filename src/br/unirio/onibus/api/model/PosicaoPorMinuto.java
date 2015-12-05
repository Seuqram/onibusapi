package br.unirio.onibus.api.model;
import lombok.Getter;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a posi��o de um ve�culo em um instante de tempo 
 * 
 * @author Marcio Barros
 */
public class PosicaoPorMinuto extends PosicaoMapa
{
	private @Getter int indiceTrechoIda;
	private @Getter int indiceTrechoVolta;
	private @Getter PosicaoMapa posicaoMapaOriginal;
	
	/**
	 * Inicializa a posi��o do ve�culo no minuto
	 */
	public PosicaoPorMinuto(double latitude, double longitude, int indiceTrechoIda, int indiceTrechoVolta, PosicaoMapa posicaoMapaOriginal)
	{
		super(latitude, longitude);
		this.indiceTrechoIda = indiceTrechoIda;
		this.indiceTrechoVolta = indiceTrechoVolta;
		this.posicaoMapaOriginal = posicaoMapaOriginal;
	}

}