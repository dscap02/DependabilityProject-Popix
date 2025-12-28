package com.popx.unit.modello;

import com.popx.modello.OrdineBean;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrdineBeanTest {

    @Test
    void ordineBean_gettersAndSetters_workCorrectly() {
        OrdineBean ordine = new OrdineBean();

        Date data = new Date(System.currentTimeMillis());

        ordine.setId(1);
        ordine.setCustomerEmail("cliente@example.com");
        ordine.setDataOrdine(data);
        ordine.setSubtotal(99.99f);
        ordine.setStatus("IN_CORSO");

        assertEquals(1, ordine.getId());
        assertEquals("cliente@example.com", ordine.getCustomerEmail());
        assertEquals(data, ordine.getDataOrdine());
        assertEquals(99.99f, ordine.getSubtotal(), 0.0001f);
        assertEquals("IN_CORSO", ordine.getStatus());

        assertNotNull(ordine.toString());
    }
    @Test
    void ordineBean_parameterizedConstructor_setsAllFields() {
        Date data = new Date(System.currentTimeMillis());

        OrdineBean ordine = new OrdineBean(
                99.99f,
                "cliente@example.com",
                data
        );

        assertEquals(99.99f, ordine.getSubtotal(), 0.0001f);
        assertEquals("cliente@example.com", ordine.getCustomerEmail());
        assertEquals(data, ordine.getDataOrdine());
    }

}
