package br.unirio.onibus.api;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.joda.time.Minutes;

import br.unirio.onibus.api.gmaps.DecoradorCaminho;
import br.unirio.onibus.api.gmaps.DecoradorCaminhoAnimadoLinha;
import br.unirio.onibus.api.gmaps.GeradorMapas;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;
import br.unirio.onibus.api.model.Veiculo;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;
import br.unirio.onibus.api.report.horarios.GeradorQuadroHorarios;
import br.unirio.onibus.api.report.horarios.PublicadorQuadroHorarios;
import br.unirio.onibus.api.report.horarios.QuadroHorarios;
import br.unirio.onibus.api.report.redutor.RedutorTrajetoria;
import br.unirio.onibus.api.support.console.ConsoleArquivo;
import br.unirio.onibus.api.support.console.ConsoleTela;
import br.unirio.onibus.api.support.console.IConsole;

@SuppressWarnings("unused")
public class MainProgram 
{
	/**
	 * Programa principal
	 */
	public static final void main(String[] args) throws Exception
	{
		Linha linha = new Linha("107");
		new CarregadorPosicoes().executa("data/2014-12-03.zip", linha);
		new CarregadorTrajeto().carregaArquivo("data/trajeto 107.csv", linha);
	
		IConsole console = new ConsoleTela();
		calculaTempoPercurso(console, linha, -22.9049, -43.1917, -22.9434, -43.16, 15, 00);
		
		//apresentaAnimacaoVeiculo(linha); 
		//reduzTrajetoria(linha);
		System.out.println("FIM");
	}
	
	private static void calculaTempoPercurso(IConsole console, Linha linha, double latOrigem, double lngOrigem, double latDestino, double lngDestino, int hora, int minuto)
	{
		// TODO: gerar os resultados para diversos dias
		
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

					DecimalFormat nf0 = new DecimalFormat("00");
					// TODO: melhorar a forma de depurar este programa
					console.println("Veiculo " + veiculo.getNumeroSerie() + " - largada as " + nf0.format(posicaoLargada.getData().getHourOfDay()) + ":" + nf0.format(posicaoLargada.getData().getMinuteOfHour() + " "));
					System.out.println("Achou - " + minutos + " minutos!!!");
				}
			}
		}
	}

	// TODO: fazer uma animação que mostra a posição de todos os veículos em um dia, passando por minuto

	/**
	 * Apresenta uma animação de um veículo
	 */
	private static void apresentaAnimacaoVeiculo(Linha linha) throws Exception 
	{
		System.out.println("Numero de posições na data: " + linha.contaPosicoes());
		System.out.println("Numero de veículos na data: " + linha.contaVeiculos());
		
		List<QuadroHorarios> quadros = new GeradorQuadroHorarios().executa("/Users/Marcio/Desktop/onibus/processado", 16, 7, 2015);
		new PublicadorQuadroHorarios().executa(new ConsoleArquivo("quadro.txt"), quadros);
		
		TrajetoriaVeiculo trajetoriaVeiculo = linha.pegaVeiculoIndice(34).getTrajetoria();
		Trajetoria trajetoriaOriginal = trajetoriaVeiculo.asTrajetoria();

		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminhoAnimadoLinha(trajetoriaOriginal).setNome("original").setLargura(3).setEspera(500));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}

	/**
	 * Reduz a trajetória de ida e volta de uma linha de ônibus
	 */
	private static void reduzTrajetoria(Linha linha) throws IOException 
	{
		RedutorTrajetoria redutorIda = new RedutorTrajetoria(linha.getTrajetoIda());
		redutorIda.reduzMaximaDistancia(0.001);
		Trajetoria trajetoriaIdaReduzida = redutorIda.pegaTrajetoria();
		
		RedutorTrajetoria redutorVolta = new RedutorTrajetoria(linha.getTrajetoVolta());
		redutorVolta.reduzMaximaDistancia(0.001);
		Trajetoria trajetoriaVoltaReduzida = redutorVolta.pegaTrajetoria();
		
		System.out.println("Numero de posições do trajeto de ida original: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posições do trajeto de ida reduzido: " + trajetoriaIdaReduzida.conta());
		System.out.println("Numero de posições do trajeto de volta original: " + linha.getTrajetoVolta().conta());
		System.out.println("Numero de posições do trajeto de volta reduzido: " + trajetoriaVoltaReduzida.conta());
		
		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminho(linha.getTrajetoIda()).setNome("ida").setLargura(5).setCor("#7F0000"));
		gerador.adiciona(new DecoradorCaminho(trajetoriaIdaReduzida).setNome("ida_").setLargura(1).setCor("#FF0000"));
		gerador.adiciona(new DecoradorCaminho(linha.getTrajetoVolta()).setNome("volta").setLargura(5).setCor("#007F00"));
		gerador.adiciona(new DecoradorCaminho(trajetoriaVoltaReduzida).setNome("volta_").setLargura(1).setCor("#00FF00"));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}
}