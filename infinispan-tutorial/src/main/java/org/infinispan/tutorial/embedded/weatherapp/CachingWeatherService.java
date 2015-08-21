package org.infinispan.tutorial.embedded.weatherapp;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;

public abstract class CachingWeatherService implements WeatherService {
	
	private Cache<String, LocationWeather> cache;
	
	public CachingWeatherService(Cache<String, LocationWeather> cache) {
		this.cache = cache;
	}

	@Override
	public LocationWeather getWeatherForLocation(String location) {
		LocationWeather weather = cache.get(location);
		if (weather == null) {
			weather = fetchWeather(location);
			cache.put(location, weather);
//			cache.put(location, weather, 5, TimeUnit.SECONDS);  //if all entries need to be expired with same lifespan, then cache can be configured by default to expire all the entries together.
		}
		return weather;
	}

	protected abstract LocationWeather fetchWeather(String location) ;
}
