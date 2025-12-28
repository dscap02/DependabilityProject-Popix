package com.popx.unit.modello;

import com.popx.modello.ProdottoBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProdottoBeanTest {

    @Test
    void prodottoBean_gettersAndSetters_workCorrectly() {
        ProdottoBean prodotto = new ProdottoBean();

        byte[] image = new byte[]{1, 2, 3};

        prodotto.setId("P001");
        prodotto.setName("Test Product");
        prodotto.setDescription("Test description");
        prodotto.setCost(19.99);
        prodotto.setPiecesInStock(10);
        prodotto.setBrand("TestBrand");
        prodotto.setImg(image);
        prodotto.setFigure("TestFigure");

        assertEquals("P001", prodotto.getId());
        assertEquals("Test Product", prodotto.getName());
        assertEquals("Test description", prodotto.getDescription());
        assertEquals(19.99, prodotto.getCost());
        assertEquals(10, prodotto.getPiecesInStock());
        assertEquals("TestBrand", prodotto.getBrand());
        assertArrayEquals(image, prodotto.getImg());
        assertEquals("TestFigure", prodotto.getFigure());

        // Optional but useful for coverage
        assertNotNull(prodotto.toString());
    }
    @Test
    void prodottoBean_parameterizedConstructor_setsAllFields() {
        byte[] image = new byte[]{1, 2, 3};

        ProdottoBean prodotto = new ProdottoBean(
                "P001",
                "Test Product",
                "Test description",
                19.99,
                10,
                "TestBrand",
                image,
                "TestFigure"
        );

        assertEquals("P001", prodotto.getId());
        assertEquals("Test Product", prodotto.getName());
        assertEquals("Test description", prodotto.getDescription());
        assertEquals(19.99, prodotto.getCost());
        assertEquals(10, prodotto.getPiecesInStock());
        assertEquals("TestBrand", prodotto.getBrand());
        assertArrayEquals(image, prodotto.getImg());
        assertEquals("TestFigure", prodotto.getFigure());
    }

    @Test
    void prodottoBean_qtyGetterAndSetter_workCorrectly() {
        ProdottoBean prodotto = new ProdottoBean();

        prodotto.setQty(5);

        assertEquals(5, prodotto.getQty());
    }
}
