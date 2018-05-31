package io.trxplorer.webapp.route;

import java.util.HashMap;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.Route.Chain;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.common.io.BaseEncoding;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.account.AccountDTO;
import io.trxplorer.webapp.dto.account.AccountDetailCriteriaDTO;
import io.trxplorer.webapp.dto.account.AccountListCriteria;
import io.trxplorer.webapp.service.AccountService;

@Singleton
public class AccountRoutes {

	private AccountService accountService;

	@Inject
	public AccountRoutes(AccountService transactionService) {
		this.accountService = transactionService;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.ACCOUNT_DETAIL)
	public void accountDetail(Request req,Response res,Chain chain) throws Throwable {
		
		String address = req.param("address").value(); 
		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		String tab = req.param("t").value("tx");
		
		View view = Results.html("account/account.detail");
		
		AccountDetailCriteriaDTO criteria = new AccountDetailCriteriaDTO(address);
		criteria.setLimit(limit);
		criteria.setPage(page);
		criteria.setTab(tab);
		
		AccountDTO account = this.accountService.getAccountByAddress(criteria);
		
		if (account!=null) {
			view.put("account",account);			
		}else {
			chain.next(req, res);
		}

		
		res.send(view);
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.ACCOUNT_LIST)
	public void accountList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		
		AccountListCriteria criteria = new AccountListCriteria();
		
		criteria.setLimit(limit);
		criteria.setPage(page);
		
		
		View view = Results.html("account/account.list");
		
		view.put("list",this.accountService.listAccounts(criteria));
		
		res.send(view);
	}
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.ACCOUNT_LOGIN)
	public void accountLogin(Request req,Response res) throws Throwable {
		
		View view = Results.html("account/login");
		
		
		
		res.send(view);
	}	
	



	
}
