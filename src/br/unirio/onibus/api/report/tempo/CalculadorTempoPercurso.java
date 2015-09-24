package br.unirio.onibus.api.report.tempo;

import java.text.DecimalFormat;

import org.joda.time.Minutes;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Veiculo;
import br.unirio.onibus.api.support.console.IConsole;
import br.unirio.onibus.api.support.geodesic.Geodesic;

/**
 * Classe que calcula o tempo de percurso de um ônibus em uma determinada data
 * 
 * @author marcio.barros
 */
public class CalculadorTempoPercurso
{
	public void executa(IConsole console, Linha linha, double latOrigem, double lngOrigem, double latDestino, double lngDestino, int hora, int minuto)
	{
		Veiculo veiculo = linha.pegaProximoVeiculo(latOrigem, lngOrigem, hora, minuto);
		
		if (veiculo != null)
		{
			PosicaoVeiculo posicaoLargada = veiculo.getTrajetoria().pegaPosicaoProximaPassagem(latOrigem, lngOrigem, hora, minuto);
			
			if (posicaoLargada != null)
			{
				PosicaoVeiculo posicaoChegada = veiculo.getTrajetoria().pegaPosicaoProximaPassagem(latDestino, lngDestino, posicaoLargada);
				
				if (posicaoChegada != null)
				{
					int minutos = Minutes.minutesBetween(posicaoLargada.getData(), posicaoChegada.getData()).getMinutes();
					double distanciaLargada = Geodesic.distance(latOrigem, lngOrigem, posicaoLargada.getLatitude(), posicaoLargada.getLongitude());
					double distanciaChegada = Geodesic.distance(latDestino, lngDestino, posicaoChegada.getLatitude(), posicaoChegada.getLongitude());
					
					DecimalFormat nf0 = new DecimalFormat("00");
					DecimalFormat nf3 = new DecimalFormat("0.000");
					
					StringBuffer sb = new StringBuffer();
					sb.append("Linha " + linha.getIdentificador() + " - ");
					sb.append(nf0.format(hora) + ":" + nf0.format(minuto) + " - ");
					sb.append("veiculo " + veiculo.getNumeroSerie() + " - ");
					sb.append("largada as " + nf0.format(posicaoLargada.getData().getHourOfDay()) + ":" + nf0.format(posicaoLargada.getData().getMinuteOfHour()) + " " + nf3.format(distanciaLargada) + " Km - ");
					sb.append("chegada as " + nf0.format(posicaoChegada.getData().getHourOfDay()) + ":" + nf0.format(posicaoChegada.getData().getMinuteOfHour()) + " " + nf3.format(distanciaChegada) + " Km - ");
					sb.append(minutos + " minutos");
					console.println(sb.toString());
				}
			}
		}
	}
}