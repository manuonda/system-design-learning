package com.manuonda.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class DddBiblioteApplicationTests {
    static ApplicationModules modules = ApplicationModules.of(DddLibraryApplication.class);

	@Test
	void contextLoads() {
		modules.verify();
	}

}
