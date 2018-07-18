package io.trxplorer.webapp.route;

import java.util.HashMap;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.Route.Chain;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.service.common.TransactionService;
import io.trxplorer.service.dto.transaction.TransactionCriteria;
import io.trxplorer.service.dto.transaction.TransactionModel;
import io.trxplorer.webapp.job.QuickStatsJob;

@Singleton
public class TransactionRoutes {

	private TransactionService txService;
	private QuickStatsJob quickStats;

	@Inject
	public TransactionRoutes(TransactionService transactionService,QuickStatsJob quickStats) {
		this.txService = transactionService;
		this.quickStats = quickStats;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.TRANSACTION_DETAIL)
	public void transactionDetail(Request req,Response res,Chain chain) throws Throwable {
		
		String txId = req.param("txid").value(); 
		
		View view = Results.html("transaction/transaction.detail");
		
		TransactionModel tx = this.txService.getTxByHash(txId);
		
		if (tx==null) {
			chain.next(req, res);
		}else {
			view.put("tx",tx);
			res.send(view);			
		}

	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.TRANSACTION_LIST)
	public void transactionList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		int block = req.param("block").intValue(-1);
		
		TransactionCriteria criteria = new TransactionCriteria();
		
		criteria.setLimit(limit);
		criteria.setPage(page);
		criteria.setBlock(block==-1?null:block);
		
		
		
		View view = Results.html("transaction/transaction.list");
		
		HashMap<String, Object> stats = new HashMap<>();
		stats.put("totalTx", quickStats.getTotalTx());
		stats.put("totalTx6h", quickStats.getTx6h());
		stats.put("totalTx24h", quickStats.getTx24h());
		stats.put("totalTxConfirmed", quickStats.getTotalTxConfirmed());
		
		view.put("list",this.txService.listTransactions(criteria));
		view.put("stats", stats);
		
		res.send(view);
	}
	
	
	
}
