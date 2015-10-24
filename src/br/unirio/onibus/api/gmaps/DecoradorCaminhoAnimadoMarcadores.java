package br.unirio.onibus.api.gmaps;

import java.io.PrintWriter;
import java.util.Iterator;

import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.TipoPosicaoVeiculo;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;

/**
 * Classe que representa um caminho que pode ser adicionado a um mapa
 * 
 * @author Marcio
 */
public class DecoradorCaminhoAnimadoMarcadores implements IDecoradorMapas 
{
	public String nome = "path";
	public int espera = 1000;
	public TrajetoriaVeiculo trajetoria;

	/**
	 * Inicializa o decorador
	 */
	public DecoradorCaminhoAnimadoMarcadores(TrajetoriaVeiculo trajetoria) 
	{
		this.trajetoria = trajetoria;
	}

	/**
	 * Indica o nome do caminho
	 */
	public DecoradorCaminhoAnimadoMarcadores setNome(String nome)
	{
		this.nome = nome;
		return this;
	}
	
	/**
	 * Indica o tempo de espera entre apresentações da trilha
	 */
	public DecoradorCaminhoAnimadoMarcadores setEspera(int espera)
	{
		this.espera = espera;
		return this;
	}
	
	/**
	 * Gera o código que representa o caminho
	 */
	@Override
	public void gera(PrintWriter writer) 
	{
		writer.println("var " + nome + "Coordinates = [");
		
		Iterator<PosicaoVeiculo> iterator = trajetoria.getPosicoes().iterator();
		
		if (iterator.hasNext())
		{
			PosicaoVeiculo posicaoAtual = iterator.next();
			geraCodigoMarcador(writer, posicaoAtual);
		
			while (iterator.hasNext())
			{
				posicaoAtual = iterator.next();
				writer.println(",");
				geraCodigoMarcador(writer, posicaoAtual);
			}
		}
		
		writer.println("];");
		
		writer.println("");
		writer.println("var len = " + nome + "Coordinates.length;");
		writer.println("var pos = 1;");
		writer.println("var marker = null;");
		writer.println("");
		
		writer.println("function animationLoop () {");
		writer.println("  setTimeout(function () {");
		writer.println("    if (marker != null)");
		writer.println("      marker.setMap(null);");
		writer.println("");
		writer.println("    marker = " + nome + "Coordinates[pos];");
		writer.println("    marker.setMap(map);");
		writer.println("");
		writer.println("    if (++pos < len) animationLoop();");
		writer.println("  }, " + espera + ");");
		writer.println("}");
		writer.println("");

		writer.println("animationLoop();");
	}

	/**
	 * Gera o código que escreve um marcador
	 */
	private void geraCodigoMarcador(PrintWriter writer, PosicaoVeiculo posicao) 
	{
		String cor = pegaCorPosicaoVeiculo(posicao);
		writer.println("    new google.maps.Marker({");
		writer.println("      position: new google.maps.LatLng(" + posicao.getLatitude() + "," + posicao.getLongitude() + "),");
		writer.println("      icon: 'http://maps.google.com/mapfiles/ms/icons/" + cor + "-dot.png'");
		writer.println("    })");
	}

	/**
	 * Retorna uma cor de acordo com o tipo de posição do veículo
	 */
	private String pegaCorPosicaoVeiculo(PosicaoVeiculo posicaoAtual) 
	{
		if (posicaoAtual.getTipo() == TipoPosicaoVeiculo.Ida)
			return "blue";
		
		if (posicaoAtual.getTipo() == TipoPosicaoVeiculo.Volta)
			return "green";

		return "yellow";
	}
}