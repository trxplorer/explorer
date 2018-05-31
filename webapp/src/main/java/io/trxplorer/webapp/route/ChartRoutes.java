package io.trxplorer.webapp.route;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.Route.Chain;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.chart.ChartItemDTO;
import io.trxplorer.webapp.dto.chart.ChartType;
import io.trxplorer.webapp.service.ChartService;

@Singleton
public class ChartRoutes {

	private ChartService chartService;
	
	private HashMap<ChartType,ChartItemDTO> charts = new HashMap<>();
	
	{
		charts.put(ChartType.TOTAL_BLOCK, new ChartItemDTO("Total Blocks", ChartType.TOTAL_BLOCK, "Total blocks produced per day"));
		charts.put(ChartType.TOTAL_TX, new ChartItemDTO("Total Transactions", ChartType.TOTAL_TX, "Total transactions per day"));
		charts.put(ChartType.AVG_TX_COUNT, new ChartItemDTO("Transactions per Block", ChartType.AVG_TX_COUNT, "Average transactions per Block"));
		charts.put(ChartType.AVG_BLOCK_TIME, new ChartItemDTO("Block time", ChartType.AVG_BLOCK_TIME, "Average block time per day"));
		charts.put(ChartType.AVG_BLOCK_SIZE, new ChartItemDTO("Block size", ChartType.AVG_BLOCK_SIZE, "Average block size per day"));
	}
	
	@Inject
	public ChartRoutes(ChartService chartService) {
		this.chartService = chartService;
	}

	@GET
	@Path(TRXPlorerRoutePaths.Front.CHART_LIST)
	public void chartList(Request req, Response res,Chain chain) throws Throwable {
		
		View view = Results.html("chart/charts");
				
		view.put("charts",this.charts.values());
		
		res.send(view);
	
	}
		
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.CHART_DETAIL)
	public void chartDetail(Request req, Response res,Chain chain) throws Throwable {
		
		String name = req.param("name").value(null);
		
		ChartType type = ChartType.from(name);
		
		if (type==null) {
			chain.next(req, res);
		}
		
		
		View view = Results.html("chart/chart.detail");
		
		List<ChartItemDTO> charts = new ArrayList<>(this.charts.values());
		charts.remove(this.charts.get(type));
		
		view.put("chart",this.charts.get(type));
		view.put("charts",charts);
		
		res.send(view);
	}

	
	//// API
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.CHART_API_DATA)
	public void apiChartData(Request req, Response res) throws Throwable {
		
		String since = req.param("since").value();
		String type = req.param("type").value();
		
		ChartType chartType = ChartType.from(type);
		
		
		res.send(this.chartService.getData(chartType));
		
	}
	
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.API_CHART_TX_SINCE)
	public void apiBuildTxSince(Request req, Response res) throws Throwable {

		String timestamp = req.param("since").value();

		DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

		Date date = formatter.parse(timestamp);

		this.chartService.createTransactionChartSince(new Timestamp(date.getTime()));

		res.send("ok");
	}

}
