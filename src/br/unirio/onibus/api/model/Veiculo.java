package br.unirio.onibus.api.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;

import org.joda.time.DateTime;

/**
 * Classe que representa um veículo, com suas posições
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
	private List<PosicaoVeiculo> posicoes;
	
	/**
	 * Inicializa o veículo
	 */
	public Veiculo(String numeroSerie)
	{
		this.numeroSerie = numeroSerie;
		this.posicoes = new ArrayList<PosicaoVeiculo>();
	}

	/**
	 * Verifica se dois horários são iguais
	 */
	private boolean equalTime(DateTime data1, DateTime data2)
	{
		return data1.getHourOfDay() == data2.getHourOfDay() && data1.getMinuteOfHour() == data2.getMinuteOfHour() && data1.getSecondOfMinute() == data2.getSecondOfMinute();
	}

	/**
	 * Adiciona uma posição no veículo
	 */
	public void adiciona(DateTime data, double latitude, double longitude, double velocidade)
	{
		for (PosicaoVeiculo posicao : posicoes)
			if (equalTime(posicao.getData(), data) && posicao.getLatitude() == latitude && posicao.getLongitude() == longitude && posicao.getVelocidade() == velocidade)
				return;
		
		posicoes.add(new PosicaoVeiculo(data, latitude, longitude, velocidade));
	}

	/**
	 * Retorna o número de posições do veículo
	 */
	public int pegaNumeroPosicoes() 
	{
		return posicoes.size();
	}

	/**
	 * Retorna uma posição do veículo, dado seu índice
	 */
	public PosicaoVeiculo pegaPosicaoIndice(int index) 
	{
		return posicoes.get(index);
	}
	
	/**
	 * Retorna as posições do veículo
	 */
	public Iterable<PosicaoVeiculo> getPosicoes()
	{
		return posicoes;
	}

	/**
	 * Ordena as posições do veículo
	 */
	public void ordenaPosicoes() 
	{
		Collections.sort(posicoes, new ComparadorPosicoes());
	}
}

class ComparadorPosicoes implements Comparator<PosicaoVeiculo> 
{
	@Override
	public int compare(PosicaoVeiculo p0, PosicaoVeiculo p1) 
	{
		return p0.getData().compareTo(p1.getData());
	}
}