package com.tutorial.two.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class SpringModulithTwoApplicationTests {



	@Test
	void contextLoads() {
	}

	@Test
	void verifiesModuleStructure(){
		ApplicationModules.of(SpringModulithTwoApplication.class);
	}

}
