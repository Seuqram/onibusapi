package br.unirio.onibus.api;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.onibus.api.gmaps.DecoradorCaminho;
import br.unirio.onibus.api.gmaps.DecoradorCaminhoAnimadoLinha;
import br.unirio.onibus.api.gmaps.GeradorMapas;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.Repositorio;
import br.unirio.onibus.api.model.Trajetoria;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;
import br.unirio.onibus.api.reader.CarregadorTrajeto;
import br.unirio.onibus.api.report.horarios.GeradorQuadroHorarios;
import br.unirio.onibus.api.report.horarios.PublicadorQuadroHorarios;
import br.unirio.onibus.api.report.horarios.QuadroHorarios;
import br.unirio.onibus.api.report.redutor.RedutorTrajetoria;
import br.unirio.onibus.api.report.tempo.CalculadorTempoPercurso;
import br.unirio.onibus.api.support.console.ConsoleArquivo;
import br.unirio.onibus.api.support.console.ConsoleTela;
import br.unirio.onibus.api.support.console.IConsole;

@SuppressWarnings("unused")
public class MainProgram 
{
	private static String DIRETORIO_PROCESSADOS = "d:\\projetos\\onibus\\processados";
	
	/**
	 * Programa principal
	 */
	public static final void main(String[] args) throws Exception
	{
		apresentaTemposLinhasCortadas();		
//		apresentaAnimacaoVeiculo(); 
		//reduzTrajetoria();
		// TODO: fazer uma animação que mostra a posição de todos os veículos em um dia, passando por minuto
		System.out.println("FIM");
	}
	
	/**
	 * Apresenta o tempo de percurso de um conjunto de veiculos
	 */
	private static void apresentaTemposLinhasCortadas()
	{
		//String[] numerosLinhas = {"120", "121", "125", "129", "305", "314", "318", "332", "360", "405", "458", "481", "501", "502", "504", "505"};
		String[] numerosLinhas = {"107"};

		IConsole console = new ConsoleTela();
		Repositorio repositorio = new Repositorio(DIRETORIO_PROCESSADOS);

		CalculadorTempoPercurso calculador = new CalculadorTempoPercurso(repositorio, console, true);
//		DateTime dataInicio = new DateTime(2015, 1, 1, 0, 0, 0);
//		DateTime dataTermino = new DateTime(2015, 9, 30, 0, 0, 0);
		DateTime dataInicio = new DateTime(2015, 7, 9, 0, 0, 0);
		DateTime dataTermino = new DateTime(2015, 7, 9, 0, 0, 0);

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
		DateTime data = new DateTime(2015, 7, 16, 0, 0, 0);
		
		Repositorio repositorio = new Repositorio(DIRETORIO_PROCESSADOS);
		List<QuadroHorarios> quadros = new GeradorQuadroHorarios().executa(repositorio, data);
		new PublicadorQuadroHorarios().executa(new ConsoleArquivo("quadro.txt"), quadros);
		
		Linha linha = new Linha("120");
		repositorio.carregaPosicoes(linha, data);
		TrajetoriaVeiculo trajetoriaVeiculo = linha.pegaVeiculoIndice(0).getTrajetoria();
		Trajetoria trajetoriaOriginal = trajetoriaVeiculo.asTrajetoria();

		GeradorMapas gerador = new GeradorMapas();
		gerador.adiciona(new DecoradorCaminhoAnimadoLinha(trajetoriaOriginal).setNome("original").setLargura(3).setEspera(500));
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
		gerador.adiciona(new DecoradorCaminho(linha.getTrajetoIda()).setNome("ida").setLargura(5).setCor("#7F0000"));
		gerador.adiciona(new DecoradorCaminho(trajetoriaIdaReduzida).setNome("ida_").setLargura(1).setCor("#FF0000"));
		gerador.adiciona(new DecoradorCaminho(linha.getTrajetoVolta()).setNome("volta").setLargura(5).setCor("#007F00"));
		gerador.adiciona(new DecoradorCaminho(trajetoriaVoltaReduzida).setNome("volta_").setLargura(1).setCor("#00FF00"));
		gerador.publica("saida.html");

		java.awt.Desktop.getDesktop().browse(java.net.URI.create("saida.html"));
	}
}