package com.bestbuy.sdp.provisioning.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.kcb.KeyCode;

public class KeyCodeTest {

	@Test
	public void testCompareTo(){
		KeyCode a = new KeyCode();
		a.setVendorID("AAA");
		a.setSKU("AAAAA");
		a.setValue("AAAAAAAAAA");

		KeyCode b = new KeyCode();
		b.setVendorID("BBB");
		b.setSKU("BBBBB");
		b.setValue("BBBBBBBBB");

		//Logger.log(a.compareTo(b));
		assertTrue(a.compareTo(b) < 0);
	}
}
