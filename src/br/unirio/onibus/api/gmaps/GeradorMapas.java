package br.unirio.onibus.api.gmaps;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que gera o código HTML para visualização de mapas no Google Maps
 *  
 * @author Marcio
 */
public class GeradorMapas 
{
	private List<IDecoradorMapas> decoradores;
	
	/**
	 * Inicializa o gerador de mapas
	 */
	public GeradorMapas()
	{
		this.decoradores = new ArrayList<IDecoradorMapas>();
	}
	
	/**
	 * Adiciona um decorador no mapa
	 */
	public GeradorMapas adiciona(IDecoradorMapas decorador)
	{
		this.decoradores.add(decorador);
		return this;
	}
	
	/**
	 * Gera o código do mapa
	 */
	public void publica(String nomeArquivo) throws IOException
	{
		PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo));
		writer.println("<!DOCTYPE html>");
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<style type='text/css'>");
		writer.println("html, body, #map-canvas { height: 100%; margin: 0; padding: 0;}");
		writer.println("</style>");
		writer.println("<script type='text/javascript' src='https://maps.googleapis.com/maps/api/js?key=AIzaSyB4bOwAVlfqhAkvRZFAyMPwQ15UypvLKUE'></script>");
		
		writer.println("<script type='text/javascript'>");
		writer.println("function initialize() {");
		writer.println("var mapOptions = {");
		writer.println("zoom: 13,");
		writer.println("center: new google.maps.LatLng(-22.9049, -43.1917),");
		writer.println("mapTypeId: google.maps.MapTypeId.TERRAIN");
		writer.println("};");
		writer.println("");
		writer.println("var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);");
		
		for (IDecoradorMapas decorador : decoradores)
			decorador.gera(writer);

		writer.println("}");
		writer.println("google.maps.event.addDomListener(window, 'load', initialize);");
		writer.println("</script>");

		writer.println("</head>");
		writer.println("<body>");
		writer.println("<div id='map-canvas'></div>");
		writer.println("</body>");
		writer.println("</html>");
		writer.close();
	}
}