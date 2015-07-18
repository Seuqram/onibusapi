package br.unirio.onibus.api;

import java.io.IOException;
import java.util.List;

import br.unirio.onibus.api.gmaps.DecoradorCaminho;
import br.unirio.onibus.api.gmaps.GeradorMapas;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoMapa;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;
import br.unirio.onibus.api.report.RedutorTrajetoria;

@SuppressWarnings("unused")
public class MainProgram 
{
	public static final void main(String[] args) throws Exception
	{
		Linha linha = new Linha("107");
		
		new CarregadorPosicoes().executa("data/2014-12-03.zip", linha);
		System.out.println("Numero de posições na data: " + linha.contaPosicoes());
		System.out.println("Numero de veículos na data: " + linha.contaVeiculos());
		
		new CarregadorTrajeto().carregaArquivo("data/trajeto 107.csv", linha);
		System.out.println("Numero de posições do trajeto de ida: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posições do trajeto de volta: " + linha.getTrajetoVolta().conta());
	
		reduzTrajetoriaVeiculo(linha); 
//		reduzTrajetoriaIda(linha);
		System.out.println("FIM");
	}

	private static void reduzTrajetoriaVeiculo(Linha linha) throws IOException 
	{
		TrajetoriaVeiculo trajetoriaVeiculo = linha.getVeiculos().iterator().next().getTrajetoria();
		Trajetoria trajetoriaOriginal = trajetoriaVeiculo.asTrajetoria();

		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminho(trajetoriaOriginal).setNome("original").setLargura(3));
		// TODO: preciso colocar labels no trajeto para entender porque ele está saltando ...
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}

	private static void reduzTrajetoriaIda(Linha linha) throws IOException 
	{
		RedutorTrajetoria redutor = new RedutorTrajetoria(linha.getTrajetoIda());
		redutor.reduzMaximaDistancia(0.001);
		Trajetoria trajetoriaReduzida = redutor.pegaTrajetoria();
		
		System.out.println("Numero de posições do trajeto de ida original: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posições do trajeto de volta reduzido: " + trajetoriaReduzida.conta());
		
		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminho(linha.getTrajetoIda()).setNome("original").setLargura(3));
		gerador.adiciona(new DecoradorCaminho(trajetoriaReduzida).setNome("novo").setCor("#0000FF"));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}
}