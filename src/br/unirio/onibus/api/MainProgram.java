package br.unirio.onibus.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;

import br.unirio.onibus.api.controllers.mapaminuto.PosicaoPorMinuto;
import br.unirio.onibus.api.controllers.mapaminuto.PosicaoVeiculoPelaTrajetoria;
import br.unirio.onibus.api.gmaps.DecoradorCaminhoAnimadoMarcadores;
import br.unirio.onibus.api.gmaps.DecoradorCaminhoEstaticoLinha;
import br.unirio.onibus.api.gmaps.GeradorMapas;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.Repositorio;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;
import br.unirio.onibus.api.report.classificador.ClassificadorPosicaoVeiculo;
import br.unirio.onibus.api.report.redutor.RedutorTrajetoria;
import br.unirio.onibus.api.report.tempo.CalculadorTempoPercurso;
import br.unirio.onibus.api.support.console.ConsoleArquivo;
import br.unirio.onibus.api.support.console.ConsoleTela;
import br.unirio.onibus.api.support.console.ListaConsole;

@SuppressWarnings("unused")
public class MainProgram 
{
	//private static String DIRETORIO_PROCESSADOS = "d:\\projetos\\onibus\\processados";
	private static String DIRETORIO_PROCESSADOS = "c:\\Projeto\\onibus";
	
	/**
	 * Programa principal
	 */
	public static final void main(String[] args) throws Exception
	{
		//apresentaTemposLinhasCortadas();		
		//apresentaAnimacaoVeiculo(); 
		//reduzTrajetoria();
		geraPosicaoDeUmaLinhaPelosMinutos();
		// TODO: fazer uma anima��o que mostra a posi��o de todos os ve�culos em um dia, passando por minuto
		System.out.println("FIM");
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	private static void geraPosicaoDeUmaLinhaPelosMinutos() throws IOException{
		Repositorio repositorio = new Repositorio(DIRETORIO_PROCESSADOS);
		Linha linha = new Linha("107");
		DateTime data = new DateTime(2015, 9, 1, 0, 0, 0);
		repositorio.carregaPosicoes(linha, data);
		repositorio.carregaTrajeto(linha);
		
		PosicaoVeiculoPelaTrajetoria posicaoVeiculoPelaTrajetoria = new PosicaoVeiculoPelaTrajetoria(linha.getTrajetoIda(), linha.getTrajetoVolta());
		
		HashMap<String, PosicaoVeiculoPelaTrajetoria> veiculos = posicaoVeiculoPelaTrajetoria.geraPosicaoPelosMinutosDeUmaLinha(linha);
		
		for (Map.Entry<String, PosicaoVeiculoPelaTrajetoria> veiculo : veiculos.entrySet()){
			String numeroVeiculo = veiculo.getKey();
			PosicaoVeiculoPelaTrajetoria veiculoPelaTrajetoria = veiculo.getValue();
			System.out.print(numeroVeiculo);
			veiculoPelaTrajetoria.exibirResultado();
			System.out.println();
		}
	}
	
	/**
	 * Apresenta o tempo de percurso de um conjunto de veiculos
	 */
	private static void apresentaTemposLinhasCortadas() throws FileNotFoundException
	{
//		String[] numerosLinhas = {"120", "121", "125", "129", "305", "314", "318", "332", "360", "405", "458", "481", "501", "502", "504", "505"};
		String[] numerosLinhas = {"120"};

		ListaConsole console = new ListaConsole();
		console.add(new ConsoleTela());
		console.add(new ConsoleArquivo("/Users/marcio/Desktop/tempos.data"));
		Repositorio repositorio = new Repositorio(DIRETORIO_PROCESSADOS);

		CalculadorTempoPercurso calculador = new CalculadorTempoPercurso(repositorio, console, false);
		DateTime dataInicio = new DateTime(2015, 1, 6, 0, 0, 0);
		DateTime dataTermino = new DateTime(2015, 1, 6, 0, 0, 0);

		for (String numeroLinha : numerosLinhas)
		{
			Linha linha = new Linha(numeroLinha);
			calculador.mudaTrajetoIda(linha).executaPeriodo(linha, dataInicio, dataTermino);
		}
	}

	/**
	 * Apresenta uma anima��o de um ve�culo
	 */
	private static void apresentaAnimacaoVeiculo() throws Exception 
	{
		DateTime data = new DateTime(2015, 9, 1, 0, 0, 0);
		
		Repositorio repositorio = new Repositorio(DIRETORIO_PROCESSADOS);
		Linha linha = new Linha("107");
		repositorio.carregaTrajeto(linha);
		repositorio.carregaPosicoes(linha, data);

		ClassificadorPosicaoVeiculo classificador = new ClassificadorPosicaoVeiculo(repositorio);
		classificador.executa(linha, data);

		TrajetoriaVeiculo trajetoriaVeiculo = linha.pegaVeiculoIndice(0).getTrajetoria();
		Trajetoria trajetoriaOriginal = trajetoriaVeiculo.asTrajetoria();

		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoIda()).setCor("#0000FF"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoVolta()).setCor("#00FF00"));
		gerador.adiciona(new DecoradorCaminhoAnimadoMarcadores(trajetoriaVeiculo).setEspera(500));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}

	/**
	 * Reduz a trajet�ria de ida e volta de uma linha de �nibus
	 */
	private static void reduzTrajetoria() throws IOException 
	{
		Linha linha = new Linha("120");
		DateTime data = new DateTime(2015, 7, 16, 0, 0, 0);

		Repositorio repositorio = new Repositorio(DIRETORIO_PROCESSADOS);
		repositorio.carregaPosicoes(linha, data);

		RedutorTrajetoria redutorIda = new RedutorTrajetoria(linha.getTrajetoIda());
		redutorIda.reduzMaximaDistancia(0.001);
		Trajetoria trajetoriaIdaReduzida = redutorIda.pegaTrajetoria();
		
		RedutorTrajetoria redutorVolta = new RedutorTrajetoria(linha.getTrajetoVolta());
		redutorVolta.reduzMaximaDistancia(0.001);
		Trajetoria trajetoriaVoltaReduzida = redutorVolta.pegaTrajetoria();
		
		System.out.println("Numero de posi��es do trajeto de ida original: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posi��es do trajeto de ida reduzido: " + trajetoriaIdaReduzida.conta());
		System.out.println("Numero de posi��es do trajeto de volta original: " + linha.getTrajetoVolta().conta());
		System.out.println("Numero de posi��es do trajeto de volta reduzido: " + trajetoriaVoltaReduzida.conta());
		
		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoIda()).setNome("ida").setLargura(5).setCor("#7F0000"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(trajetoriaIdaReduzida).setNome("ida_").setLargura(1).setCor("#FF0000"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoVolta()).setNome("volta").setLargura(5).setCor("#007F00"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(trajetoriaVoltaReduzida).setNome("volta_").setLargura(1).setCor("#00FF00"));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}
}