package br.unirio.onibus.api;

import java.util.List;

import br.unirio.onibus.api.gmaps.DecoradorCaminho;
import br.unirio.onibus.api.gmaps.GeradorMapas;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoMapa;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;
import br.unirio.onibus.api.report.RedutorTrajetoria;

public class MainProgram 
{
	public static final void main(String[] args) throws Exception
	{
		Linha linha = new Linha("107");
		
		new CarregadorPosicoes().executa("data/2014-12-03.zip", linha);
		System.out.println("Numero de posições na data: " + linha.contaPosicoes());
		
		new CarregadorTrajeto().carregaArquivo("data/trajeto 107.csv", linha);
		System.out.println("Numero de posições do trajeto de ida: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posições do trajeto de volta: " + linha.getTrajetoVolta().conta());
		
		RedutorTrajetoria redutor = new RedutorTrajetoria(linha.getTrajetoIda().pegaPosicoes());
		redutor.reduzMaximaDistancia(0.001);
		List<PosicaoMapa> posicoesReduzidas = redutor.pegaTrajetoria();
		
		System.out.println("Numero de posições do trajeto de ida original: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posições do trajeto de volta reduzido: " + posicoesReduzidas.size());
		
		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminho(linha.getTrajetoIda().pegaPosicoes()).setNome("original").setLargura(3));
		gerador.adiciona(new DecoradorCaminho(posicoesReduzidas).setNome("novo").setCor("#0000FF"));
		gerador.publica("saida.html");
		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
		
		System.out.println("FIM");
	}
}