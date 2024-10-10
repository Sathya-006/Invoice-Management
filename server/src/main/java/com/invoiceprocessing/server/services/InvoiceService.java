package com.invoiceprocessing.server.services;

import com.invoiceprocessing.server.model.Invoice;

import java.util.List;

public interface InvoiceService {

    public Invoice addInvoice(Invoice invoice);

    public List<Invoice> getInvoices();

    public Invoice deleteinvoice(long id);

    public Invoice approveInvoice(long id);

    public Invoice getInvoiceById(long id);
}
