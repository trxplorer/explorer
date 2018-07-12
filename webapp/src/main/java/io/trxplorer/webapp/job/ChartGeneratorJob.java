package io.trxplorer.webapp.job;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jooby.quartz.Scheduled;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.service.common.ChartService;

@Singleton
public class ChartGeneratorJob {

	private ChartService chartService;

	@Inject
	public ChartGeneratorJob(ChartService chartService) {
		this.chartService = chartService;
	}
	
	
	@Scheduled("1h; delay=1h")
	public void generateChart() throws ParseException {
		//FIXME: should be done every 24h, without regenerating everything 
		DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Date date = formatter.parse("5-1-2018");
		this.chartService.createTransactionChartSince(new Timestamp(date.getTime()));
		
	}
}
