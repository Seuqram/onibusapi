package br.unirio.onibus.api.report.redutor;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que reduz o tamanho de uma sequ�ncia de posi��es no mapa
 * 
 * @author Marcio
 */
public class RedutorTrajetoria 
{
	/**
	 * Dist�ncia infinita
	 */
	private static double INFINITY_DISTANCE = 1000000.0;
	
	/**
	 * Posi��es que ser�o consideradas na redu��o de trajet�ria
	 */
	private List<PosicaoReducaoTrajetoria> posicoes;
	
	/**
	 * Inicializa um redutor de trajet�rias
	 */
	public RedutorTrajetoria()
	{
		posicoes = new ArrayList<PosicaoReducaoTrajetoria>(); 
	}
	
	/**
	 * Inicializa um redutor de trajet�rias
	 */
	public RedutorTrajetoria(Trajetoria trajetoria)
	{
		this();
		adicionaTrajetoria(trajetoria);
	}
	
	/**
	 * Adiciona uma trajet�ria no redutor
	 */
	public void adicionaTrajetoria(Trajetoria trajetoria)
	{
		for (PosicaoMapa posicao : trajetoria.pegaPosicoes())
			posicoes.add(new PosicaoReducaoTrajetoria(posicao.getLatitude(), posicao.getLongitude()));
	}
	
	/**
	 * Retorna o resultado da redu��o na forma de uma trajet�ria
	 */
	public Trajetoria pegaTrajetoria()
	{
		Trajetoria resultado = new Trajetoria();
		
		for (PosicaoReducaoTrajetoria posicao : posicoes)
			resultado.adiciona(new PosicaoMapa(posicao.getLatitude(), posicao.getLongitude()));
		
		return resultado;
	}

	/**
	 * Prepara os custos de remo��o dos pontos de um segmento
	 */
	private void setupRemovalErrors() 
	{
		PosicaoReducaoTrajetoria anterior = posicoes.get(0);
		PosicaoReducaoTrajetoria atual = posicoes.get(1);
		PosicaoReducaoTrajetoria seguinte = posicoes.get(2);
		
		posicoes.get(0).setErroRemocao(INFINITY_DISTANCE);
		posicoes.get(posicoes.size()-1).setErroRemocao(INFINITY_DISTANCE);
		
		for (int i = 1; i < posicoes.size()-1; i++)
		{
			double distance = Geodesic.trackDistance(atual.getLatitude(), atual.getLongitude(), anterior.getLatitude(), anterior.getLongitude(), seguinte.getLatitude(), seguinte.getLongitude());
			atual.setErroRemocao(distance);
			
			anterior = atual;
			atual = seguinte;
			seguinte = posicoes.get(i+1);
		}
	}

	/**
	 * Retorna o �ndice do ponto com menor erro de remo��o
	 */
	public int pegaIndiceMinimoErroRemocao()
	{
		if (posicoes.size() <= 2)
			return 0;
		
		int index = 1;
		double lesser = posicoes.get(1).getErroRemocao();
		
		for (int i = 2; i < posicoes.size()-1; i++)
		{
			double error = posicoes.get(i).getErroRemocao();
			
			if (error < lesser)
			{
				lesser = error;
				index = i;
			}
		}
		
		return index;
	}
	
	/**
	 * Remove o ponto com menor custo de um segmento, recalculando o ponto anterior e o posterior
	 */
	private double removeMelhorPonto() 
	{
		int index = pegaIndiceMinimoErroRemocao();
		PosicaoReducaoTrajetoria current = posicoes.get(index);
		double error = current.getErroRemocao();
		
		if (index > 1 && index < posicoes.size()-1)
		{
			PosicaoReducaoTrajetoria preprevious = posicoes.get(index-2);
			PosicaoReducaoTrajetoria previous = posicoes.get(index-1);
			PosicaoReducaoTrajetoria successor = posicoes.get(index+1);
			double err = Geodesic.trackDistance(previous.getLatitude(), previous.getLongitude(), preprevious.getLatitude(), preprevious.getLongitude(), successor.getLatitude(), successor.getLongitude());
			previous.setErroRemocao(err);
		}
		
		if (index > 0 && index < posicoes.size()-2)
		{
			PosicaoReducaoTrajetoria previous = posicoes.get(index-1);
			PosicaoReducaoTrajetoria successor = posicoes.get(index+1);
			PosicaoReducaoTrajetoria postsuccessor = posicoes.get(index+2);
			double err = Geodesic.trackDistance(successor.getLatitude(), successor.getLongitude(), previous.getLatitude(), previous.getLongitude(), postsuccessor.getLatitude(), postsuccessor.getLongitude());
			successor.setErroRemocao(err);
		}
		
		posicoes.remove(index);
		return error;
	}
	
	/**
	 * Reduz a trilha para um determinado n�mero de pontos, removendo os pontos com menor impacto de erro
	 */
	public double reduzParaNumeroPontos(int pontos)
	{
		double error = 0.0;

		if (posicoes.size() > 3)
		{
			setupRemovalErrors();

			while (posicoes.size() > pontos)
				error = removeMelhorPonto();
		}
		
		return error;
	}

	/**
	 * Reduz a trilha at� que um n�vel de erro de remo��o seja atingido
	 */
	public double reduzMaximaDistancia(double maximaDistanciaAceitavel)
	{
		double error = 0.0;

		if (posicoes.size() > 3)
		{
			setupRemovalErrors();

			while (error < maximaDistanciaAceitavel)
				error = removeMelhorPonto();
		}

		return error;
	}
}

/**
 * Classe que representa uma posi��o no mapa para efeito de redu��o de trajet�ria
 * 
 * @author Marcio
 */
class PosicaoReducaoTrajetoria
{
	private @Getter double latitude;
	private @Getter double longitude;
	private @Getter @Setter double erroRemocao;
	
	public PosicaoReducaoTrajetoria(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.erroRemocao = 0.0;
	}
}