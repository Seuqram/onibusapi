package br.unirio.onibus.api;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;

public class MainProgram 
{
	public static final void main(String[] args) throws Exception
	{
		Linha linha = new Linha("107");
		
		new CarregadorPosicoes().executa("/Users/Marcio/Desktop/2014-12-03.zip", linha);
		System.out.println("Numero de posições na data: " + linha.contaPosicoes());
		
		new CarregadorTrajeto().carregaArquivo("/Users/Marcio/Desktop/107.csv", linha);
		System.out.println("Numero de posições do trajeto de ida: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posições do trajeto de volta: " + linha.getTrajetoVolta().conta());
		
		System.out.println("FIM");
	}
}