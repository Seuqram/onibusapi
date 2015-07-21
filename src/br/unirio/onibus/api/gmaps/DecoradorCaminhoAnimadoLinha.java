package br.unirio.onibus.api.gmaps;

import java.io.PrintWriter;
import java.util.Iterator;

import br.unirio.onibus.api.model.PosicaoMapa;
import br.unirio.onibus.api.model.Trajetoria;

/**
 * Classe que representa um caminho que pode ser adicionado a um mapa
 * 
 * @author Marcio
 */
public class DecoradorCaminhoAnimadoLinha implements IDecoradorMapas 
{
	public String nome = "path";
	public String cor = "#FF0000";
	public double opacidade = 1.0;
	public int largura = 2;
	public int tamanhoTrilha = 5;
	public int espera = 1000;
	public Trajetoria trajetoria;

	/**
	 * Inicializa o decorador
	 */
	public DecoradorCaminhoAnimadoLinha(Trajetoria trajetoria) 
	{
		this.trajetoria = trajetoria;
	}
	

	/**
	 * Indica o nome do caminho
	 */
	public DecoradorCaminhoAnimadoLinha setNome(String nome)
	{
		this.nome = nome;
		return this;
	}
	
	/**
	 * Indica a cor com que o caminho será desenhado
	 */
	public DecoradorCaminhoAnimadoLinha setCor(String cor)
	{
		this.cor = cor;
		return this;
	}
	
	/**
	 * Indica a opacidade do desenho do caminho
	 */
	public DecoradorCaminhoAnimadoLinha setOpacidade(double opacidade)
	{
		this.opacidade = opacidade;
		return this;
	}
	
	/**
	 * Indica a largura do desenho do caminho
	 */
	public DecoradorCaminhoAnimadoLinha setLargura(int largura)
	{
		this.largura = largura;
		return this;
	}
	
	/**
	 * Indica o número de pontos na trilha a cada instante
	 */
	public DecoradorCaminhoAnimadoLinha setTamanhoTrilha(int tamanho)
	{
		this.tamanhoTrilha = tamanho;
		return this;
	}
	
	/**
	 * Indica o tempo de espera entre apresentações da trilha
	 */
	public DecoradorCaminhoAnimadoLinha setEspera(int espera)
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
		
		Iterator<PosicaoMapa> iterator = trajetoria.pegaPosicoes().iterator();
		
		if (iterator.hasNext())
		{
			PosicaoMapa posicaoAtual = iterator.next();
			writer.print("new google.maps.LatLng(" + posicaoAtual.getLatitude() + "," + posicaoAtual.getLongitude() + ")");
		
			while (iterator.hasNext())
			{
				writer.println(",");
				posicaoAtual = iterator.next();
				writer.print("new google.maps.LatLng(" + posicaoAtual.getLatitude() + "," + posicaoAtual.getLongitude() + ")");
			}
		}
		
		writer.println("];");
		
		writer.println("");
		writer.println("var len = " + nome + "Coordinates.length;");
		writer.println("var head = 2;");
		writer.println("var path = null;");
		writer.println("");
		
		writer.println("function animationLoop () {");
		writer.println("  setTimeout(function () {");
		writer.println("    if (path != null)");
		writer.println("      path.setMap(null);");
		writer.println("");
		
		writer.println("    var _myCoordinates = [];");
		writer.println("    var startingPoint = (head > " + tamanhoTrilha + ") ? head-" + tamanhoTrilha + " : 0;");
		writer.println("    var endingPoint = (head > len) ? len : head;");
		writer.println("");
				
		writer.println("    for (var j = startingPoint; j < endingPoint; j++) {");
		writer.println("      _myCoordinates.push(" + nome + "Coordinates[j]);");
		writer.println("    }");
		writer.println("");
				
		writer.println("    path = new google.maps.Polyline({");
		writer.println("      path: _myCoordinates,");
		writer.println("      geodesic: true,");
		writer.println("      strokeColor: '" + cor + "',");
		writer.println("      strokeOpacity: " + opacidade + ",");
		writer.println("      strokeWeight: " + largura);
		writer.println("    });");
		writer.println("");
		
		writer.println("    path.setMap(map);");
		writer.println("    if (++head < len + " + tamanhoTrilha + ") animationLoop();");
		writer.println("  }, " + espera + ");");
		writer.println("}");
		writer.println("");

		writer.println("animationLoop();");
	}
}