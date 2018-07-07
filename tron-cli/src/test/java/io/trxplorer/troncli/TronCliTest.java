package io.trxplorer.troncli;

import org.junit.Ignore;
import org.junit.Test;

public class TronCliTest {

	
	@Test
	public void testFullNodeCli() {
		
		TronFullNodeCli cli = new TronFullNodeCli("47.254.146.147:50051",true);
		
		System.out.println(cli.getNextMaintenanceTime());
		
		
	}
	
	@Test
	public void testSolidityNodeCli() {

		TronSolidityNodeCli cli = new TronSolidityNodeCli("47.89.244.227:50051",true);
		
		System.out.println(cli.getLastBlock().getBlockHeader().getRawData().getNumber());
		
	}
	
}
