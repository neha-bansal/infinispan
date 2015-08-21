package org.infinispan.tutorial.embedded.weatherapp;

public interface WeatherService {
   LocationWeather getWeatherForLocation(String location);
}
