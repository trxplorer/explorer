package io.trxplorer.troncli;

import org.junit.Ignore;
import org.junit.Test;

public class TronCliTest {
	
	@Ignore
	@Test
	public void testFullNodeCli() {
		
		TronFullNodeCli cli = new TronFullNodeCli("47.91.246.252:50051",true);
		
		System.out.println(cli.getLastBlock());
		
		
	}
	
	@Test
	public void testSolidityNodeCli() {

		TronSolidityNodeCli cli = new TronSolidityNodeCli("39.105.66.80:50051",true);
		
		System.out.println(cli.getLastBlock());
		
	}
	
}
