package br.unirio.onibus.api.support.geodesic;

public class Geodesic
{
	private static final double EARTH_RADIUS = 6371.0; // em Km

	/**
	 * Metodo usado para calcular a distancia entre dois pontos em coordenadas geograficas
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2)
	{
		 return distanceHaversine(lat1, lon1, lat2, lon2);
	}

	/**
	 * Metodo usado para calcular a distancia entre dois pontos em coordenadas geograficas
	 */
	public static double distance(PosicaoMapa origem, PosicaoMapa destino)
	{
		 return distanceHaversine(origem.getLatitude(), origem.getLongitude(), destino.getLatitude(), destino.getLongitude());
	}

	/**
	 * Calcula a distancia entre dois pontos em uma esfera usando a formula do arco-cosseno
	 * 
	 * Ver em https://en.wikipedia.org/wiki/Great-circle_distance
	 */
	public static double distanceArccosine(double lat1, double lon1, double lat2, double lon2)
	{
		 double rTheta = Math.toRadians(lon1 - lon2);
		 double rLat1 = Math.toRadians(lat1);
		 double rLat2 = Math.toRadians(lat2);
		 return Math.acos(Math.sin(rLat1) * Math.sin(rLat2) + Math.cos(rLat1) * Math.cos(rLat2) * Math.cos(rTheta)) * EARTH_RADIUS;
	}

	/**
	 * Calcula a distancia em Km entre dois pontos em uma esfera usando a formula de Haversine, 
	 * que e mais precisa para curtas distancias
	 * 
	 * Ver em https://en.wikipedia.org/wiki/Great-circle_distance
	 * Ver em http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public static double distanceHaversine(double lat1, double lon1, double lat2, double lon2)
	{
		double rTheta = Math.toRadians(lon1 - lon2);
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double a = Math.pow(Math.sin((rLat2 - rLat1) / 2), 2) + Math.cos(rLat1) * Math.cos(rLat2) * Math.pow(Math.sin((rTheta) / 2), 2);
		return Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 2 * EARTH_RADIUS;
	}

	/**
	 * Calcula a distancia entre dois pontos em uma esfera usando a formula de Vincenty, 
	 * que nao tem os problemas para longas distancias da Haversine
	 * 
	 * Ver em https://en.wikipedia.org/wiki/Great-circle_distance
	 */
	public static double distanceVincenty(double lat1, double lon1, double lat2, double lon2)
	{
		double rTheta = Math.toRadians(lon1 - lon2);
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double position1 = Math.sqrt(Math.pow(Math.cos(rLat2) * Math.sin(rTheta), 2) + Math.pow(Math.cos(rLat1) * Math.sin(rLat2) - Math.sin(rLat1) * Math.cos(rLat2) * Math.cos(rTheta), 2));
		double position2 = Math.sin(rLat1) * Math.sin(rLat2) + Math.cos(rLat1) * Math.cos(rLat2) * Math.cos(rTheta);
		return Math.atan2(position1, position2) * EARTH_RADIUS;
	}

	/**
	 * Calcula o angulo de movimento entre dois pontos, em graus
	 * 
	 * Ver em http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public static double bearing(double lat1, double lon1, double lat2, double lon2)
	{
		double rTheta = Math.toRadians(lon2 - lon1);
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double y = Math.sin(rTheta) * Math.cos(rLat2);
		double x = Math.cos(rLat1) * Math.sin(rLat2) - Math.sin(rLat1) * Math.cos(rLat2) * Math.cos(rTheta);
		double theta = Math.atan2(y, x);
		double initialBearing = (Math.toDegrees(theta) + 360) % 360;
		//double initialBearing = Math.toDegrees(theta);
		return initialBearing;
	}

	/**
	 * Calcula a menor distancia entre um ponto e uma trilha definida por dois pontos - metodo mais preciso
	 * 
	 * Ver em http://www.movable-type.co.uk/scripts/latlong.html, secao "Cross-track distance"
	 */
	public static double trackDistance(double latTarget, double lonTarget, double latStart, double lonStart, double latFinish, double lonFinish)
	{
		// Calcula a distancia do ponto de interesse para o ponto de inicio do segmento
		double distanceStartTarget = distanceHaversine(latStart, lonStart, latTarget, lonTarget);

		// Calcula a distancia do ponto de interesse para o arco definido pelo segmento		
		double bearing13 = bearing(latStart, lonStart, latTarget, lonTarget);
		double bearing12 = bearing(latStart, lonStart, latFinish, lonFinish);
		double distance = Math.abs(Math.asin(Math.sin(distanceStartTarget / EARTH_RADIUS) * Math.sin(Math.toRadians(bearing13 - bearing12))) * EARTH_RADIUS);
		
		// Calcula o ponto de corte no arco
	    double sigma = distance / EARTH_RADIUS;
	    double theta = Math.toRadians(bearing12);

	    double phi1 = Math.toRadians(latTarget);
	    double phi2 = Math.asin(Math.sin(phi1) * Math.cos(sigma) + Math.cos(phi1) * Math.sin(sigma) * Math.cos(theta));

	    double lambda1 = Math.toRadians(lonTarget);
	    double lambda2 = lambda1 + Math.atan2(Math.sin(theta) * Math.sin(sigma) * Math.cos(phi1), Math.cos(sigma) - Math.sin(phi1) * Math.sin(phi2));
	    double lambda3 = (lambda2 + 3*Math.PI) % (2 * Math.PI) - Math.PI;

	    double latArcCross = Math.toDegrees(phi2);
	    double lonArcCross = Math.toDegrees(lambda3);
	    
	    // Se o ponto de corte estiver dentro do segmento de arco, retorna a distancia calculada
		if (withinArcSegment(latArcCross, lonArcCross, latStart, lonStart, latFinish, lonFinish))
			return distance;
			
		// Senao, calcula a distancia do ponto de interesse para o ponto de termino do segmento
		double distanceEndTarget = distanceHaversine(latFinish, lonFinish, latTarget, lonTarget);

		// Retorna a menor distancia entre o ponto de inicio e termino do arco
		return Math.min(distanceStartTarget, distanceEndTarget);
	}

	/**
	 * Calcular o ponto mais próximo em um arco definido por dois pontos
	 * 
	 * Ver em http://www.movable-type.co.uk/scripts/latlong.html, secao "Cross-track distance"
	 */
	public static PosicaoMapa trackClosestPoint(double latTarget, double lonTarget, double latStart, double lonStart, double latFinish, double lonFinish)
	{
		// Calcula a distancia do ponto de interesse para o ponto de inicio do segmento
		double distanceStartTarget = distanceHaversine(latStart, lonStart, latTarget, lonTarget);

		// Calcula a distancia do ponto de interesse para o arco definido pelo segmento		
		double bearing13 = bearing(latStart, lonStart, latTarget, lonTarget);
		double bearing12 = bearing(latStart, lonStart, latFinish, lonFinish);
		double distance = Math.abs(Math.asin(Math.sin(distanceStartTarget / EARTH_RADIUS) * Math.sin(Math.toRadians(bearing13 - bearing12))) * EARTH_RADIUS);
		
		// Calcula o ponto de corte no arco
	    double sigma = distance / EARTH_RADIUS;
	    double theta = Math.toRadians(bearing12);

	    double phi1 = Math.toRadians(latTarget);
	    double phi2 = Math.asin(Math.sin(phi1) * Math.cos(sigma) + Math.cos(phi1) * Math.sin(sigma) * Math.cos(theta));

	    double lambda1 = Math.toRadians(lonTarget);
	    double lambda2 = lambda1 + Math.atan2(Math.sin(theta) * Math.sin(sigma) * Math.cos(phi1), Math.cos(sigma) - Math.sin(phi1) * Math.sin(phi2));
	    double lambda3 = (lambda2 + 3*Math.PI) % (2 * Math.PI) - Math.PI;

	    double latArcCross = Math.toDegrees(phi2);
	    double lonArcCross = Math.toDegrees(lambda3);
	    
	    // Se o ponto de corte estiver dentro do segmento de arco, retorna o ponto na trajetoria
		if (withinArcSegment(latArcCross, lonArcCross, latStart, lonStart, latFinish, lonFinish))
			return new PosicaoMapa(latArcCross, lonArcCross);
			
		// Senao, calcula a distancia do ponto de interesse para o ponto de termino do segmento e retorna o extremo mais proximo
		double distanceEndTarget = distanceHaversine(latFinish, lonFinish, latTarget, lonTarget);

		if (distanceStartTarget < distanceEndTarget)
			return new PosicaoMapa(latStart, lonStart);
		
		return new PosicaoMapa(latFinish, lonFinish);
	}
	
	/**
	 * Verifica se um ponto esta dentro de um segmento de arco
	 */
	private static boolean withinArcSegment(double latTarget, double lonTarget, double latStart, double lonStart, double latFinish, double lonFinish)
	{
		double arcSegmentSize = distanceHaversine(latStart, lonStart, latFinish, lonFinish);
		double distanceCrossStart = distanceHaversine(latStart, lonStart, latTarget, lonTarget);
		double distanceCrossFinish = distanceHaversine(latFinish, lonFinish, latTarget, lonTarget);		
		return (distanceCrossStart <= arcSegmentSize && distanceCrossFinish <= arcSegmentSize);
	}
}