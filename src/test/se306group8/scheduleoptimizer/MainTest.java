package se306group8.scheduleoptimizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

public class MainTest {
	
	@Test
	void testNoArguments() {
		
		String[] args = {};
		
		Main.main(args);
		
		
		System.out.println(Logger.getLogger(Main.class.getName()).toString());
	}

}
