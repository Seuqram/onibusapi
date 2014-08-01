package br.unirio.onibus.api;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.reader.CarregadorParadas;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;

public class MainProgram 
{
	public static final void main(String[] args) throws Exception
	{
		Linha linha = new Linha("310");
		new CarregadorPosicoes().executa("/Users/Marcio/Desktop/Processados", 29, 4, 2014, linha);
		new CarregadorTrajeto().executa("/Users/Marcio/Desktop/Processados", linha);
		new CarregadorParadas().executa("/Users/Marcio/Desktop/Processados", linha);
		System.out.println(linha.contaVeiculos() + " " + linha.contaPosicoes());
		System.out.println(linha.contaPosicoesTrajetoIda() + " " + linha.contaPosicoesTrajetoVolta());
		System.out.println(linha.contaPosicoesParada());
		System.out.println("FIM");
	}
}