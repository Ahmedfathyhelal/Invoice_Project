
package model;


public class InvoiceItemModel {
    
    private String name;
    private int price;
    private int count;
    private InvoiceHeaderModel invoice;

    public InvoiceItemModel(String name, int price, int count, InvoiceHeaderModel invoice) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.invoice = invoice;
        invoice.getItems().add(this);
    }

    public int getTotal() {
        return price * count;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public InvoiceHeaderModel getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceHeaderModel invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "InvoiceItem{" + "name=" + name + ", price=" + price + ", count=" + count + '}';
    } 
    
    public String getAsCSV() {
        return getInvoice().getNum()+","+getName()+","+price+","+count;
    }
}
