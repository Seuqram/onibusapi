package br.unirio.onibus.api;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.joda.time.DateTime;

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
	private static String DIRETORIO_PROCESSADOS = "\\Users\\marcio\\Desktop\\onibus";
	
	/**
	 * Programa principal
	 */
	public static final void main(String[] args) throws Exception
	{
		//apresentaTemposLinhasCortadas();		
		apresentaAnimacaoVeiculo(); 
		//reduzTrajetoria();
		// TODO: fazer uma animação que mostra a posição de todos os veículos em um dia, passando por minuto
		System.out.println("FIM");
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
	 * Apresenta uma animação de um veículo
	 */
	private static void apresentaAnimacaoVeiculo() throws Exception 
	{
		DateTime data = new DateTime(2015, 1, 6, 0, 0, 0);
		
		Repositorio repositorio = new Repositorio(DIRETORIO_PROCESSADOS);
		Linha linha = new Linha("310");
		repositorio.carregaTrajeto(linha);
		repositorio.carregaPosicoes(linha, data);

		ClassificadorPosicaoVeiculo classificador = new ClassificadorPosicaoVeiculo(repositorio);
		classificador.executa(linha, data);

		// TODO: eliminar trilhas com poucos pontos (menos de 50 posições)
		
		// TODO: o próximo problema é corrigir os pontos de ida e volta que se alternam em uma sequência
		
		TrajetoriaVeiculo trajetoriaVeiculo = linha.pegaVeiculoIndice(2).getTrajetoria();
		Trajetoria trajetoriaOriginal = trajetoriaVeiculo.asTrajetoria();

		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoIda()).setCor("#0000FF"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoVolta()).setCor("#00FF00"));
		//gerador.adiciona(new DecoradorCaminhoEstaticoMarcadores(trajetoriaVeiculo));
		gerador.adiciona(new DecoradorCaminhoAnimadoMarcadores(trajetoriaVeiculo).setEspera(500));
		//gerador.adiciona(new DecoradorCaminhoAnimadoLinha(trajetoriaOriginal).setNome("original").setLargura(3).setEspera(500));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}

	/**
	 * Reduz a trajetória de ida e volta de uma linha de ônibus
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
		
		System.out.println("Numero de posições do trajeto de ida original: " + linha.getTrajetoIda().conta());
		System.out.println("Numero de posições do trajeto de ida reduzido: " + trajetoriaIdaReduzida.conta());
		System.out.println("Numero de posições do trajeto de volta original: " + linha.getTrajetoVolta().conta());
		System.out.println("Numero de posições do trajeto de volta reduzido: " + trajetoriaVoltaReduzida.conta());
		
		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoIda()).setNome("ida").setLargura(5).setCor("#7F0000"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(trajetoriaIdaReduzida).setNome("ida_").setLargura(1).setCor("#FF0000"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(linha.getTrajetoVolta()).setNome("volta").setLargura(5).setCor("#007F00"));
		gerador.adiciona(new DecoradorCaminhoEstaticoLinha(trajetoriaVoltaReduzida).setNome("volta_").setLargura(1).setCor("#00FF00"));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}
}