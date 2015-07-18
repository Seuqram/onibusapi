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
public class DecoradorCaminho implements IDecoradorMapas 
{
	public String nome = "path";
	public String cor = "#FF0000";
	public double opacidade = 1.0;
	public int largura = 2;
	public Trajetoria trajetoria;

	/**
	 * Inicializa o decorador
	 */
	public DecoradorCaminho(Trajetoria trajetoria) 
	{
		this.trajetoria = trajetoria;
	}
	

	/**
	 * Indica o nome do caminho
	 */
	public DecoradorCaminho setNome(String nome)
	{
		this.nome = nome;
		return this;
	}
	
	/**
	 * Indica a cor com que o caminho será desenhado
	 */
	public DecoradorCaminho setCor(String cor)
	{
		this.cor = cor;
		return this;
	}
	
	/**
	 * Indica a opacidade do desenho do caminho
	 */
	public DecoradorCaminho setOpacidade(double opacidade)
	{
		this.opacidade = opacidade;
		return this;
	}
	
	/**
	 * Indica a largura do desenho do caminho
	 */
	public DecoradorCaminho setLargura(int largura)
	{
		this.largura = largura;
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

		writer.println("var " + nome + " = new google.maps.Polyline({");
		writer.println("path: " + nome + "Coordinates,");
		writer.println("geodesic: true,");
		writer.println("strokeColor: '" + cor + "',");
		writer.println("strokeOpacity: " + opacidade + ",");
		writer.println("strokeWeight: " + largura);
		writer.println("});");

		writer.println(nome + ".setMap(map);");
	}
}