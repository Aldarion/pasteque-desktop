//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package fr.pasteque.pos.panels;

import java.util.*;
import javax.swing.table.AbstractTableModel;
import fr.pasteque.basic.BasicException;
import fr.pasteque.data.loader.*;
import fr.pasteque.format.Formats;
import fr.pasteque.pos.admin.CurrencyInfo;
import fr.pasteque.pos.forms.DataLogicSales;
import fr.pasteque.pos.forms.AppLocal;
import fr.pasteque.pos.forms.AppView;
import fr.pasteque.pos.util.StringUtils;

/**
 *
 * @author adrianromero
 */
public class PaymentsModel {

    private String m_sHost;
    private int m_iSeq;
    private Date m_dDateStart;
    private Date m_dDateEnd;       
            
    private Integer m_iPayments;
    private Double m_dPaymentsTotal;
    private java.util.List<PaymentsLine> m_lpayments;
    private List<CategoryLine> catSales;
    private static List<CurrencyInfo> currencies;

    private final static String[] PAYMENTHEADERS = {"Label.Payment", "label.totalcash"};
    
    private Integer m_iSales;
    private Double m_dSalesBase;
    private Double m_dSalesTaxes;
    private java.util.List<SalesLine> m_lsales;
    private Integer custCount;
    
    private final static String[] SALEHEADERS = {"label.taxcash", "label.totalcash"};
    private final static String[] CATEGORYHEADERS = {"label.catname", "label.totalcash"};

    private PaymentsModel() {
    }    
        
    public static PaymentsModel loadInstance(AppView app) throws BasicException {
        DataLogicSales dlSales = (DataLogicSales) app.getBean("fr.pasteque.pos.forms.DataLogicSales");
        
        PaymentsModel p = new PaymentsModel();
        currencies = dlSales.getCurrenciesList().list();
        
        // Propiedades globales
        p.m_sHost = app.getProperties().getHost();
        p.m_iSeq = app.getActiveCashSequence();
        p.m_dDateStart = app.getActiveCashDateStart();
        p.m_dDateEnd = null;
        
        
        // Get number of payments and total amount
        Object[] valtickets = (Object []) new StaticSentence(app.getSession()
            , "SELECT COUNT(*), SUM(PAYMENTS.TOTAL) " +
              "FROM PAYMENTS, RECEIPTS " +
              "WHERE PAYMENTS.RECEIPT = RECEIPTS.ID AND RECEIPTS.MONEY = ?"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE}))
            .find(app.getActiveCashIndex());
            
        if (valtickets == null) {
            p.m_iPayments = new Integer(0);
            p.m_dPaymentsTotal = new Double(0.0);
        } else {
            p.m_iPayments = (Integer) valtickets[0];
            p.m_dPaymentsTotal = (Double) valtickets[1];
        }  
        // Get total amount by payment type
        List l = new StaticSentence(app.getSession()            
            , "SELECT PAYMENTS.PAYMENT, PAYMENTS.CURRENCY, SUM(PAYMENTS.TOTALCURRENCY) " +
              "FROM PAYMENTS, RECEIPTS " +
              "WHERE PAYMENTS.RECEIPT = RECEIPTS.ID AND RECEIPTS.MONEY = ? " +
              "GROUP BY PAYMENTS.PAYMENT, PAYMENTS.CURRENCY"
            , SerializerWriteString.INSTANCE
            , new SerializerReadClass(PaymentsModel.PaymentsLine.class)) //new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.DOUBLE}))
            .list(app.getActiveCashIndex()); 
        
        if (l == null) {
            p.m_lpayments = new ArrayList();
        } else {
            p.m_lpayments = l;
        }
        
        // Sales
        Object[] recsales = (Object []) new StaticSentence(app.getSession(),
            "SELECT COUNT(DISTINCT RECEIPTS.ID), " +
            "SUM(TICKETLINES.UNITS * TICKETLINES.PRICE), " +
            "SUM(TICKETS.CUSTCOUNT) " +
            "FROM RECEIPTS, TICKETS, TICKETLINES " +
            "WHERE RECEIPTS.ID = TICKETLINES.TICKET " +
            "AND RECEIPTS.ID = TICKETS.ID " +
            "AND RECEIPTS.MONEY = ?",
            SerializerWriteString.INSTANCE,
            new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE, Datas.INT}))
            .find(app.getActiveCashIndex());
        if (recsales == null) {
            p.m_iSales = null;
            p.m_dSalesBase = null;
        } else {
            p.m_iSales = (Integer) recsales[0];
            p.m_dSalesBase = (Double) recsales[1];
            p.custCount = (Integer) recsales[2];
        }

        // Sales by categories
        List catSales = new StaticSentence(app.getSession(),
            "SELECT SUM(TICKETLINES.UNITS * TICKETLINES.PRICE), " +
            "CATEGORIES.NAME " +
            "FROM RECEIPTS, TICKETS, TICKETLINES, PRODUCTS, CATEGORIES " +
            "WHERE RECEIPTS.ID = TICKETLINES.TICKET " +
            "AND RECEIPTS.ID = TICKETS.ID " +
            "AND TICKETLINES.PRODUCT = PRODUCTS.ID " +
            "AND PRODUCTS.CATEGORY = CATEGORIES.ID " +
            "AND RECEIPTS.MONEY = ? " +
            "GROUP BY CATEGORIES.NAME",
            SerializerWriteString.INSTANCE,
            new SerializerReadClass(PaymentsModel.CategoryLine.class))
            .list(app.getActiveCashIndex());
        if (catSales == null) {
            p.catSales = new ArrayList();
        } else {
            p.catSales = catSales;
        }           
        
        // Total taxes amount
        Object[] rectaxes = (Object []) new StaticSentence(app.getSession(),
            "SELECT SUM(TAXLINES.AMOUNT) " +
            "FROM RECEIPTS, TAXLINES WHERE RECEIPTS.ID = TAXLINES.RECEIPT AND RECEIPTS.MONEY = ?"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());            
        if (rectaxes == null) {
            p.m_dSalesTaxes = null;
        } else {
            p.m_dSalesTaxes = (Double) rectaxes[0];
        } 
        // Total taxes by type
        List<SalesLine> asales = new StaticSentence(app.getSession(),
                "SELECT TAXCATEGORIES.NAME, SUM(TAXLINES.AMOUNT), "
                + "AVG(TAXES.RATE), SUM(TAXLINES.BASE) " +
                "FROM RECEIPTS, TAXLINES, TAXES, TAXCATEGORIES WHERE RECEIPTS.ID = TAXLINES.RECEIPT AND TAXLINES.TAXID = TAXES.ID AND TAXES.CATEGORY = TAXCATEGORIES.ID " +
                "AND RECEIPTS.MONEY = ?" +
                "GROUP BY TAXCATEGORIES.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsModel.SalesLine.class))
                .list(app.getActiveCashIndex());
        if (asales == null) {
            p.m_lsales = new ArrayList<SalesLine>();
        } else {
            p.m_lsales = asales;
        }

        return p;
    }

    public int getPayments() {
        return m_iPayments.intValue();
    }
    public boolean hasCustomersCount() {
        return custCount != null;
    }
    public int getCustomersCount() {
        return custCount;
    }
    public double getTotal() {
        return m_dPaymentsTotal.doubleValue();
    }
    public String getHost() {
        return m_sHost;
    }
    public int getSequence() {
        return m_iSeq;
    }
    public Date getDateStart() {
        return m_dDateStart;
    }
    public void setDateEnd(Date dValue) {
        m_dDateEnd = dValue;
    }
    public Date getDateEnd() {
        return m_dDateEnd;
    }
    
    public String printHost() {
        return StringUtils.encodeXML(m_sHost);
    }
    public String printSequence() {
        return Formats.INT.formatValue(m_iSeq);
    }
    public String printDateStart() {
        return Formats.TIMESTAMP.formatValue(m_dDateStart);
    }
    public String printDateEnd() {
        return Formats.TIMESTAMP.formatValue(m_dDateEnd);
    }  
    
    public String printPayments() {
        return Formats.INT.formatValue(m_iPayments);
    }

    public String printPaymentsTotal() {
        return Formats.CURRENCY.formatValue(m_dPaymentsTotal);
    }     
    
    public List<PaymentsLine> getPaymentLines() {
        return m_lpayments;
    }
    
    public int getSales() {
        return m_iSales == null ? 0 : m_iSales.intValue();
    }
    /** Prints the number of tickets */
    public String printSales() {
        return Formats.INT.formatValue(m_iSales);
    }
    public String printCustomersCount() {
        return Formats.INT.formatValue(custCount);
    }
    /** Prints the subtotal */
    public String printSalesBase() {
        return Formats.CURRENCY.formatValue(m_dSalesBase);
    }
    /** Print taxes total */
    public String printSalesTaxes() {
        return Formats.CURRENCY.formatValue(m_dSalesTaxes);
    }
    /** Print total */
    public String printSalesTotal() {            
        return Formats.CURRENCY.formatValue((m_dSalesBase == null || m_dSalesTaxes == null)
                ? null
                : m_dSalesBase + m_dSalesTaxes);
    }
    /** Get average sales per customer */
    public String printSalesPerCustomer() {
        if (custCount != 0) {
            return Formats.CURRENCY.formatValue((m_dSalesBase + m_dSalesTaxes) / custCount);
        } else {
            return "";
        }
    }
    public List<SalesLine> getSaleLines() {
        return m_lsales;
    }
    public List<CategoryLine> getCategoryLines() {
        return this.catSales;
    }

    public AbstractTableModel getPaymentsModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(PAYMENTHEADERS[column]);
            }
            public int getRowCount() {
                return m_lpayments.size();
            }
            public int getColumnCount() {
                return PAYMENTHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                PaymentsLine l = m_lpayments.get(row);
                switch (column) {
                case 0: return new Object[] {l.getType(), l.getCurrency().getName(), l.getCurrency().isMain()};
                case 1: return l;
                default: return null;
                }
            }  
        };
    }
    
    public static class SalesLine implements SerializableRead {
        
        private String m_SalesTaxName;
        private Double taxRate;
        private Double taxBase;
        private Double m_SalesTaxes;
        
        public void readValues(DataRead dr) throws BasicException {
            m_SalesTaxName = dr.getString(1);
            m_SalesTaxes = dr.getDouble(2);
            this.taxRate = dr.getDouble(3);
            this.taxBase = dr.getDouble(4);
        }
        public String printTaxName() {
            return m_SalesTaxName;
        }
        public String printTaxRate() {
        	return Formats.PERCENT.formatValue(this.taxRate);
        }
        public String printTaxes() {
            return Formats.CURRENCY.formatValue(m_SalesTaxes);
        }
        public String printTaxBase() {
            return Formats.CURRENCY.formatValue(this.taxBase);
        }
        public String getTaxName() {
            return m_SalesTaxName;
        }
        public Double getTaxRate() {
            return this.taxRate;
        }
        public Double getTaxes() {
            return m_SalesTaxes;
        }
        public Double getTaxBase() {
            return this.taxBase;
        }
    }

    public AbstractTableModel getSalesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            public int getRowCount() {
                return m_lsales.size();
            }
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                SalesLine l = m_lsales.get(row);
                switch (column) {
                case 0: return l.getTaxName();
                case 1: return l.getTaxes();
                default: return null;
                }
            }  
        };
    }
    
    public static class PaymentsLine implements SerializableRead {
        
        private String m_PaymentType;
        private CurrencyInfo currency;
        private Double m_PaymentValue;
        
        public void readValues(DataRead dr) throws BasicException {
            m_PaymentType = dr.getString(1);
            int currencyId = dr.getInt(2);
            for (CurrencyInfo currency : currencies) {
                if (currency.getID() == currencyId) {
                    this.currency = currency;
                    break;
                }
            }
            if (this.currency == null) {
                this.currency = currencies.get(0);
            }
            m_PaymentValue = dr.getDouble(3);
        }
        
        public String printType() {
            return AppLocal.getIntString("transpayment." + m_PaymentType);
        }
        public String getType() {
            return m_PaymentType;
        }
        public String printValue() {
            Formats.setAltCurrency(this.currency);
            return Formats.CURRENCY.formatValue(m_PaymentValue);
        }
        public Double getValue() {
            return m_PaymentValue;
        }
        public CurrencyInfo getCurrency() {
            return this.currency;
        }
    }

    public AbstractTableModel getCategoriesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(CATEGORYHEADERS[column]);
            }
            public int getRowCount() {
                return catSales.size();
            }
            public int getColumnCount() {
                return CATEGORYHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                CategoryLine l = catSales.get(row);
                switch (column) {
                case 0: return l.getCategory();
                case 1: return l.getValue();
                default: return null;
                }
            }  
        };
    }

    public static class CategoryLine implements SerializableRead {
        private String category;
        private Double amount;

        public void readValues(DataRead dr) throws BasicException {
            this.category = dr.getString(2);
            this.amount = dr.getDouble(1);
        }
        public String getCategory() {
            return this.category;
        }
        public String printCategory() {
            return this.category;
        }
        public String printValue() {
            return Formats.CURRENCY.formatValue(this.amount);
        }
        public Double getValue() {
            return this.amount;
        }
    }
}