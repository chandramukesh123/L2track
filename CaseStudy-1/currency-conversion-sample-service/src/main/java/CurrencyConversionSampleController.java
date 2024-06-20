@GetMapping("/currency-converter-sample/fromCurrency/{fromCurrency}/toCurrency/{toCurrency}/quantity/{quantity}")
package com.gfg.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// To invoke an external service, RestTemplate() constructor
// is used
@RestController
public class CurrencyConversionSampleController {
	@GetMapping(
		"/currency-converter-sample/fromCurrency/{fromCurrency}/toCurrency/{toCurrency}/quantity/{quantity}")
	// where {from} and {to} represents the
	// column
	// returns a bean back
	public CurrencyConversionSampleBean
	convertCurrency(@PathVariable String fromCurrency,
					@PathVariable String toCurrency,
					@PathVariable BigDecimal quantity)
	{

		// setting variables to currency exchange service
		Map<String, String> uriVariables = new HashMap<>();
		// urlParams should match properly
		uriVariables.put("fromCurrency", fromCurrency);
		uriVariables.put("toCurrency", toCurrency);
		// calling the currency-exchange-sample-service
		// http://localhost:8000/currency-exchange-sample/fromCurrency/{fromCurrency}/toCurrency/{toCurrency}
		// is the service that got called from part 1. Its
		// response is received and via
		// CurrencyConversionSampleBean we are getting the
		// results back
		ResponseEntity<CurrencyConversionSampleBean>
			responseEntity
			= new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange-sample/fromCurrency/{fromCurrency}/toCurrency/{toCurrency}",
				CurrencyConversionSampleBean.class,
				uriVariables);
		CurrencyConversionSampleBean response
			= responseEntity.getBody();
		// creating a new response bean and getting the
		// response back and taking it into Bean Hence the
		// output bean should have all the fields that is
		// received from the response
		return new CurrencyConversionSampleBean(
			response.getId(), fromCurrency, toCurrency,
			response.getConversionMultiple(), quantity,
			quantity.multiply(
				response.getConversionMultiple()),
			response.getPort());
	}
}
