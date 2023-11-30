package ru.antisessa.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.antisessa.RefuelServiceMain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = RefuelServiceMain.class)
class TemporaryCarServiceTest {
    private final TemporaryCarService temporaryCarService;

    @Autowired
    TemporaryCarServiceTest(TemporaryCarService temporaryCarService) {
        this.temporaryCarService = temporaryCarService;
    }

// Assert false: test_patched123, Kurama, kUraMa, Test_Patched123
// Assert true:  Mike, MyCar, My Summer Car

    @Test
    void checkUniqueName() {
        assertFalse(temporaryCarService.checkUniqueName("test_patched123"));
        assertFalse(temporaryCarService.checkUniqueName("Kurama"));
        assertFalse(temporaryCarService.checkUniqueName("kUraMa"));
        assertFalse(temporaryCarService.checkUniqueName("Test_Patched123"));

        assertTrue(temporaryCarService.checkUniqueName("Mike"));
        assertTrue(temporaryCarService.checkUniqueName("MyCar"));
        assertTrue(temporaryCarService.checkUniqueName("My Summer Car"));
    }
}