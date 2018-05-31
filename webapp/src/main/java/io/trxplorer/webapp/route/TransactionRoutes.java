package io.trxplorer.webapp.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.transaction.TransactionCriteriaDTO;
import io.trxplorer.webapp.service.TransactionService;

@Singleton
public class TransactionRoutes {

	private TransactionService txService;

	@Inject
	public TransactionRoutes(TransactionService transactionService) {
		this.txService = transactionService;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.TRANSACTION_DETAIL)
	public void transactionDetail(Request req,Response res) throws Throwable {
		
		String txId = req.param("txid").value(); 
		
		View view = Results.html("transaction/transaction.detail");
		
		view.put("tx",this.txService.getTxByHash(txId));
		
		res.send(view);
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.TRANSACTION_LIST)
	public void transactionList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		String block = req.param("block").value(null);
		
		TransactionCriteriaDTO criteria = new TransactionCriteriaDTO();
		
		criteria.setLimit(limit);
		criteria.setPage(page);
		criteria.setBlock(block);
		
		
		View view = Results.html("transaction/transaction.list");
		
		view.put("list",this.txService.listTransactions(criteria));
		
		res.send(view);
	}
	
	
	
}
