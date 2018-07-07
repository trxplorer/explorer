package io.trxplorer.syncnode.service;

import org.jooq.DSLContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class VotingRoundService {

	private DSLContext dslContext;

	@Inject
	VotingRoundService(DSLContext dslContext){
		this.dslContext = dslContext;
	}
	
	
	
}
