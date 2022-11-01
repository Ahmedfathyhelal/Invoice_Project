
package model;

import java.util.ArrayList;
import java.util.Date;
import view.Form;

public class InvoiceHeaderModel {
    private int num;
    private String name;
    private Date date;
    private ArrayList<InvoiceItemModel> items;

    public InvoiceHeaderModel(int num, String name, Date date) {
        this.num = num;
        this.name = name;
        this.date = date;
    }
    
    public int getTotal() {
        int total = 0;
        total = getItems().stream().map((item) -> item.getTotal()).reduce(total, Integer::sum);
        return total;
    }
    
    public ArrayList<InvoiceItemModel> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "InvoiceHeader{" + "num=" + num + ", name=" + name + ", date=" + date + '}';
    }
    
     public String getAsCSV() {
        return num+","+Form.df.format(date)+","+name;
    }
}
