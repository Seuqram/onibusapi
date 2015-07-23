package br.unirio.onibus.api;

import java.io.IOException;
import java.util.List;

import br.unirio.onibus.api.gmaps.DecoradorCaminho;
import br.unirio.onibus.api.gmaps.DecoradorCaminhoAnimadoLinha;
import br.unirio.onibus.api.gmaps.GeradorMapas;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoMapa;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;
import br.unirio.onibus.api.report.horarios.GeradorQuadroHorarios;
import br.unirio.onibus.api.report.horarios.PublicadorQuadroHorarios;
import br.unirio.onibus.api.report.horarios.QuadroHorarios;
import br.unirio.onibus.api.report.redutor.RedutorTrajetoria;
import br.unirio.onibus.api.support.console.ConsoleArquivo;

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
	
		apresentaAnimacaoVeiculo(linha); 
		//reduzTrajetoria(linha);
		System.out.println("FIM");
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