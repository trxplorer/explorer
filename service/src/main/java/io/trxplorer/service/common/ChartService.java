package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import com.google.inject.Inject;

import io.trxplorer.model.tables.records.ChartDailyRecord;
import io.trxplorer.service.dto.chart.ChartDailyDataDTO;
import io.trxplorer.service.dto.chart.ChartType;


public class ChartService {

	private DSLContext dslContext;

	@Inject
	public ChartService(DSLContext dslContext) {
		this.dslContext = dslContext;
	}
	
	
	public void createTransactionChartData(int day,int month,int year) {
		
		ChartDailyRecord record = this.dslContext.select(CHART_DAILY.fields())
		.from(CHART_DAILY)
		.where(CHART_DAILY.DAY.eq(UShort.valueOf(day))
				.and(CHART_DAILY.MONTH.eq(UShort.valueOf(month))
				.and(CHART_DAILY.YEAR.eq(UShort.valueOf(year)))))
		.fetchOneInto(ChartDailyRecord.class);
		
		if (record == null) {
			
			record = new ChartDailyRecord();
			record.setDay(UShort.valueOf(day));
			record.setMonth(UShort.valueOf(month));
			record.setYear(UShort.valueOf(year));
			
		}
			
		Record txInfo = this.dslContext.select(DSL.count().as("totalTx")).from(TRANSACTION)
		.where(DSL.month(TRANSACTION.TIMESTAMP).eq(month))
		.and(DSL.day(TRANSACTION.TIMESTAMP).eq(day))
		.and(DSL.year(TRANSACTION.TIMESTAMP).eq(year))
		.fetchOne();

		 Record blocksInfo = this.dslContext.select(DSL.count().as("totalBlocks"),DSL.avg(BLOCK.SIZE).as("blockSizeAvg"),DSL.avg(BLOCK.BLOCK_TIME).as("blockTimeAvg"),DSL.avg(BLOCK.TX_COUNT).as("txCountAvg"),DSL.avg(BLOCK.TX_COUNT).divide(DSL.avg(BLOCK.BLOCK_TIME)).cast(String.class).as("tps_avg")).from(BLOCK)
		.where(DSL.month(BLOCK.TIMESTAMP).eq(month))
		.and(DSL.day(BLOCK.TIMESTAMP).eq(day))
		.and(DSL.year(BLOCK.TIMESTAMP).eq(year))
		.fetchOne();
		
		UInteger totalTx =txInfo.get("totalTx",Integer.class) == null ? UInteger.valueOf(0) : UInteger.valueOf(txInfo.get("totalTx",Integer.class)); 
		
		
		record.setTotalTx(totalTx);
		
		
		UInteger totalBlocks =blocksInfo.get("totalBlocks",Integer.class) == null ? UInteger.valueOf(0) : UInteger.valueOf(blocksInfo.get("totalBlocks",Integer.class));
		UInteger avgBlockSize =blocksInfo.get("blockSizeAvg",Integer.class) == null ? UInteger.valueOf(0) : UInteger.valueOf(blocksInfo.get("blockSizeAvg",Integer.class));
		UInteger avgBlockTime =blocksInfo.get("blockTimeAvg",Integer.class) == null ? UInteger.valueOf(0) : UInteger.valueOf(blocksInfo.get("blockTimeAvg",Integer.class));
		UInteger avgBlockTxCount =blocksInfo.get("txCountAvg",Integer.class) == null ? UInteger.valueOf(0) : UInteger.valueOf(blocksInfo.get("txCountAvg",Integer.class));
		Double tpsAvg = blocksInfo.get("tps_avg",Double.class) == null ? 0 : Double.valueOf(blocksInfo.get("tps_avg",Double.class));
		
		
		record.setAvgTxPerSecond(tpsAvg);
		record.setTotalBlock(totalBlocks);
		record.setAvgBlockSize(avgBlockSize);
		record.setAvgBlockTime(avgBlockTime);
		record.setAvgTxCount(avgBlockTxCount);
		
		record.attach(this.dslContext.configuration());
		record.store();
		
	}
	
	
	public void createTransactionChartSince(Timestamp startTimestamp) {
		
		Result<Record3<Integer, Integer, Integer>> records = this.dslContext.select(DSL.month(TRANSACTION.TIMESTAMP).as("month"),DSL.day(TRANSACTION.TIMESTAMP).as("day"),DSL.year(TRANSACTION.TIMESTAMP).as("year"))
		.from(TRANSACTION)
		.where(TRANSACTION.TIMESTAMP.gt(startTimestamp).and(DSL.year(TRANSACTION.TIMESTAMP).lt(DSL.year(DSL.currentDate()).plus(1))))
		.groupBy(DSL.month(TRANSACTION.TIMESTAMP),DSL.day(TRANSACTION.TIMESTAMP),DSL.month(TRANSACTION.TIMESTAMP))
		.fetch();
		
		Iterator<Record3<Integer, Integer, Integer>> iterator = records.iterator();
		
		while(iterator.hasNext()) {
			
			Record3<Integer, Integer, Integer> current = iterator.next();
			
			this.createTransactionChartData(current.get("day",Integer.class),current.get("month",Integer.class), current.get("year",Integer.class));
			
		}
		
		
	}
	
	public List<ChartDailyDataDTO> getData(ChartType chartType){
		
		SelectSelectStep<?> select = null;
				
		if (chartType.equals(ChartType.AVG_TX_COUNT)) {
			select = this.dslContext.select(DSL.concat(CHART_DAILY.YEAR,DSL.val("-"),CHART_DAILY.MONTH,DSL.val("-"),CHART_DAILY.DAY).as("date"),CHART_DAILY.AVG_TX_COUNT.as("data"));
		}else if (chartType.equals(ChartType.TOTAL_TX)) {
			select = this.dslContext.select(DSL.concat(CHART_DAILY.YEAR,DSL.val("-"),CHART_DAILY.MONTH,DSL.val("-"),CHART_DAILY.DAY).as("date"),CHART_DAILY.TOTAL_TX.as("data"));			
		}else if (chartType.equals(ChartType.TOTAL_BLOCK)) {
			select = this.dslContext.select(DSL.concat(CHART_DAILY.YEAR,DSL.val("-"),CHART_DAILY.MONTH,DSL.val("-"),CHART_DAILY.DAY).as("date"),CHART_DAILY.TOTAL_BLOCK.as("data"));
		}else if (chartType.equals(ChartType.AVG_BLOCK_SIZE)) {
				select = this.dslContext.select(DSL.concat(CHART_DAILY.YEAR,DSL.val("-"),CHART_DAILY.MONTH,DSL.val("-"),CHART_DAILY.DAY).as("date"),CHART_DAILY.AVG_BLOCK_SIZE.as("data"));
		}else if (chartType.equals(ChartType.AVG_BLOCK_TIME)) {
			select = this.dslContext.select(DSL.concat(CHART_DAILY.YEAR,DSL.val("-"),CHART_DAILY.MONTH,DSL.val("-"),CHART_DAILY.DAY).as("date"),CHART_DAILY.AVG_BLOCK_TIME.as("data"));
		}
		 
		
		return 	select
				.from(CHART_DAILY)
				.fetchInto(ChartDailyDataDTO.class);		
	}
	
	
}
