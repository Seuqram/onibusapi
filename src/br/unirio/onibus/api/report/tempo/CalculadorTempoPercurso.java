package br.unirio.onibus.api.report.tempo;

import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Repositorio;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.model.Veiculo;
import br.unirio.onibus.api.support.console.IConsole;
import br.unirio.onibus.api.support.geodesic.Geodesic;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que calcula o tempo de percurso de um ônibus em uma determinada data
 * 
 * @author marcio.barros
 */
public class CalculadorTempoPercurso
{
	private static double ERRO_ACEITAVEL = 0.01;
	
	private Repositorio repositorio;
	private IConsole console;
	private boolean verbose;
	private PosicaoMapa origem;
	private PosicaoMapa destino;
	
	/**
	 * Inicializa o calculador
	 */
	public CalculadorTempoPercurso(Repositorio repositorio, IConsole console, boolean verbose)
	{
		this.repositorio = repositorio;
		this.console = console;
		this.verbose = verbose;
		this.origem = null;
		this.destino = null;
	}
	
	/**
	 * Muda o ponto de origem do passageiro
	 */
	public CalculadorTempoPercurso mudaTrajeto(PosicaoMapa origem, PosicaoMapa destino)
	{
		this.origem = origem;
		this.destino = destino;
		return this;
	}
	
	/**
	 * Muda os pontos de início e destino do passageiro
	 */
	public CalculadorTempoPercurso mudaTrajeto(Trajetoria trajeto)
	{
		int len = trajeto.conta();
		
		if (len >= 2)
		{
			this.origem = trajeto.pegaPosicaoIndice(0);
			this.destino = trajeto.pegaPosicaoIndice(len-1);
		}
		
		return this;
	}
	
	/**
	 * Muda os pontos de início e destino do passageiro de acordo com o trajeto de ida de uma linha
	 */
	public CalculadorTempoPercurso mudaTrajetoIda(Linha linha)
	{
		if (linha.getTrajetoIda().conta() == 0)
			if (!repositorio.carregaTrajeto(linha))
				return this;
		
		return mudaTrajeto(linha.getTrajetoIda());
	}
	
	/**
	 * Muda os pontos de início e destino do passageiro de acordo com o trajeto de volta de uma linha
	 */
	public CalculadorTempoPercurso mudaTrajetoVolta(Linha linha)
	{
		if (linha.getTrajetoIda().conta() == 0)
			if (!repositorio.carregaTrajeto(linha))
				return this;
		
		return mudaTrajeto(linha.getTrajetoVolta());
	}
	
	/**
	 * Executa os cálculos para um período
	 */
	public void executaPeriodo(Linha linha, DateTime dataInicio, DateTime dataTermino)
	{	
		if (origem == null || destino == null)
			return;
		
		dataInicio = dataInicio.withTime(0, 0, 0, 0);
		dataTermino = dataTermino.withTime(23, 59, 59, 999);
		
		while (dataTermino.isAfter(dataInicio))
		{
			executaData(linha, dataInicio);
			dataInicio = dataInicio.plusDays(1);
		}
	}

	/**
	 * Executa os cálculos para uma data
	 */
	public void executaData(Linha linha, DateTime data)
	{	
		linha.limpaVeiculos();

		if (!repositorio.carregaPosicoes(linha, data))
			return;

		for (int hora = 8; hora <= 20; hora++)
			executaDataHora(linha, data, hora);
	}

	/**
	 * Executa os cálculos para uma hora e minuto
	 */
	private void executaDataHora(Linha linha, DateTime data, int hora)
	{
		Veiculo veiculo = linha.pegaProximoVeiculo(origem, hora, 0, ERRO_ACEITAVEL * 100);
		
		if (veiculo != null)
		{
			PosicaoVeiculo posicaoLargada = veiculo.getTrajetoria().pegaPosicaoProximaPassagem(origem, hora, 0, ERRO_ACEITAVEL * 100);
			
			if (posicaoLargada != null && posicaoLargada.getData().getHourOfDay() == hora)
			{
				PosicaoVeiculo posicaoChegada = veiculo.getTrajetoria().pegaPosicaoProximaPassagem(destino, posicaoLargada, ERRO_ACEITAVEL * 100);
				
				if (posicaoChegada != null && veiculo.calculaMaximoIntervaloTempo(posicaoLargada, posicaoChegada) < 5 * 60)
				{
					if (verbose)
						publicaResultadoCompleto(linha, data, hora, veiculo, posicaoLargada, posicaoChegada);
					else
						publicaResultadoResumido(linha, data, hora, posicaoLargada, posicaoChegada);
				}
			}
		}
	}

	/**
	 * Apresenta os resultados em formato reduzido
	 */
	private void publicaResultadoResumido(Linha linha, DateTime data, int hora, PosicaoVeiculo posicaoLargada, PosicaoVeiculo posicaoChegada) 
	{
		int minutos = Minutes.minutesBetween(posicaoLargada.getData(), posicaoChegada.getData()).getMinutes();
		console.println(linha.getIdentificador() + ";" + data.getDayOfMonth() + ";" + data.getMonthOfYear() + ";" + hora + ";" + minutos);
	}

	/**
	 * Apresenta os resultados em formato completo
	 */
	private void publicaResultadoCompleto(Linha linha, DateTime data, int hora, Veiculo veiculo, PosicaoVeiculo posicaoLargada, PosicaoVeiculo posicaoChegada) 
	{
		int minutos = Minutes.minutesBetween(posicaoLargada.getData(), posicaoChegada.getData()).getMinutes();
		double distanciaLargada = Geodesic.distance(origem, posicaoLargada);
		double distanciaChegada = Geodesic.distance(destino, posicaoChegada);
		
		DecimalFormat nf0 = new DecimalFormat("00");
		DecimalFormat nf3 = new DecimalFormat("0.000");
		
		DateTime novaData = new DateTime(data.getYear(), data.getMonthOfYear(), data.getDayOfMonth(), hora, 0, 0);
		DateTimeFormatter df = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

		StringBuffer sb = new StringBuffer();
		sb.append("Linha " + linha.getIdentificador() + " - ");
		sb.append(df.print(novaData) + " - ");
		sb.append("veiculo " + veiculo.getNumeroSerie() + " - ");
		sb.append("largada as " + nf0.format(posicaoLargada.getData().getHourOfDay()) + ":" + nf0.format(posicaoLargada.getData().getMinuteOfHour()) + " " + nf3.format(distanciaLargada) + " Km - ");
		sb.append("chegada as " + nf0.format(posicaoChegada.getData().getHourOfDay()) + ":" + nf0.format(posicaoChegada.getData().getMinuteOfHour()) + " " + nf3.format(distanciaChegada) + " Km - ");
		sb.append(minutos + " minutos");
		console.println(sb.toString());
		
		int indiceLargada = veiculo.getTrajetoria().pegaIndicePosicao(posicaoLargada);
		int indiceChegada = veiculo.getTrajetoria().pegaIndicePosicao(posicaoChegada);
		
		for (int i = indiceLargada; i <= indiceChegada; i++)
		{
			PosicaoVeiculo posicao = veiculo.getTrajetoria().pegaPosicaoIndice(i);
			console.println("\t" + i + " " + posicao.getData().getHourOfDay() + ":" + posicao.getData().getMinuteOfHour() + " " + Geodesic.distance(posicao, destino));
		}
	}

}