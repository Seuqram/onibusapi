package br.unirio.onibus.api.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;

import org.joda.time.DateTime;

/**
 * Classe que representa um ve�culo, com suas posi��es
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
	private List<PosicaoVeiculo> posicoes;
	
	/**
	 * Inicializa o ve�culo
	 */
	public Veiculo(String numeroSerie)
	{
		this.numeroSerie = numeroSerie;
		this.posicoes = new ArrayList<PosicaoVeiculo>();
	}

	/**
	 * Verifica se dois hor�rios s�o iguais
	 */
	private boolean equalTime(DateTime data1, DateTime data2)
	{
		return data1.getHourOfDay() == data2.getHourOfDay() && data1.getMinuteOfHour() == data2.getMinuteOfHour() && data1.getSecondOfMinute() == data2.getSecondOfMinute();
	}

	/**
	 * Adiciona uma posi��o no ve�culo
	 */
	public void adiciona(DateTime data, double latitude, double longitude, double velocidade)
	{
		for (PosicaoVeiculo posicao : posicoes)
			if (equalTime(posicao.getData(), data) && posicao.getLatitude() == latitude && posicao.getLongitude() == longitude && posicao.getVelocidade() == velocidade)
				return;
		
		posicoes.add(new PosicaoVeiculo(data, latitude, longitude, velocidade));
	}

	/**
	 * Retorna o n�mero de posi��es do ve�culo
	 */
	public int pegaNumeroPosicoes() 
	{
		return posicoes.size();
	}

	/**
	 * Retorna uma posi��o do ve�culo, dado seu �ndice
	 */
	public PosicaoVeiculo pegaPosicaoIndice(int index) 
	{
		return posicoes.get(index);
	}
	
	/**
	 * Retorna as posi��es do ve�culo
	 */
	public Iterable<PosicaoVeiculo> getPosicoes()
	{
		return posicoes;
	}

	/**
	 * Ordena as posi��es do ve�culo
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