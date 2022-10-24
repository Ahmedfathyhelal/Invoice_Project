
package model;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import view.Form;


public class HeaderTableModel extends AbstractTableModel {

    private ArrayList<InvoiceHeaderModel> invoices;
    private String[] columns = {"Num", "Name", "Date", "Total"};

    public HeaderTableModel(ArrayList<InvoiceHeaderModel> invoices) {
        this.invoices = invoices;
    }
   
    @Override
    public int getRowCount() {
        return invoices.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InvoiceHeaderModel inv = invoices.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return inv.getNum();
            case 1: 
                return inv.getName();
            case 2:
                return Form.df.format(inv.getDate());
            case 3:
                return inv.getTotal();
            default:
                return "";
        }
        
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }
}
