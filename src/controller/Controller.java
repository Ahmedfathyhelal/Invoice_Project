
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.System.in;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.HeaderTableModel;
import model.InvoiceHeaderModel;
import model.InvoiceItemModel;
import model.ItemTableModel;
import view.Form;


public class Controller implements ActionListener, ListSelectionListener {

    private Form frame;

    public Controller(Form frame) {
        this.frame = frame;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if ("New Invoice".equals(ac)) { newInvoice();}
        else if("Delete Invoice".equals(ac)){deleteInvoice();}
        else if("New Line".equals(ac)){newLine();}
        else if("Delete Line".equals(ac)){deleteLine();}
        else if ("Load".equals(ac)){load(null, null);}
        else if ("Save".equals(ac)){save();}  
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        System.out.println("Row Selected " + selectedRow);
        if (selectedRow != -1) {
            InvoiceHeaderModel selectedInv = frame.getInvoices().get(selectedRow);
            frame.getCustomerNameArea().setText(selectedInv.getName());
            frame.getInvDateArea().setText(frame.df.format(selectedInv.getDate()));
            frame.getInvNumArea().setText(""+selectedInv.getNum());
            frame.getInvTotalArea().setText(""+selectedInv.getTotal());
            frame.setItemTableModel(new ItemTableModel(selectedInv.getItems()));
        } else {
            
        }
    }

 
    public void load(String hPath, String lPath) {
        File hFile = null;
        File lFile = null;
        if (hPath == null && lPath == null) {
            JFileChooser fc = new JFileChooser();
            JOptionPane.showMessageDialog(frame, "Choose Header File!", "Header", JOptionPane.WARNING_MESSAGE);
            int result = fc.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                hFile = fc.getSelectedFile();
                JOptionPane.showMessageDialog(frame, "Choose Line File!", "Line", JOptionPane.WARNING_MESSAGE);
                result = fc.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    lFile = fc.getSelectedFile();
                }
            }
        } else {
            hFile = new File(hPath);
            lFile = new File(lPath);
        }
        
        if (hFile != null && lFile != null) {
            try {
                List<String> hLines = readFile(hFile);
            
                
                List<String> lLines = readFile(lFile);
              
                System.out.println("check");
                for (String hLine : hLines) {
                  
                    String[] parts = hLine.split(",");
               
                    Date d = new Date();
                    int num = Integer.parseInt(parts[0]);
                    try{d = Form.df.parse(parts[1]);}catch (ParseException pex) {}
                    String name = parts[2];
                    InvoiceHeaderModel inv = new InvoiceHeaderModel(num, name, d);
                    frame.getInvoices().add(inv);
                }
                frame.setHeaderTableModel(new HeaderTableModel(frame.getInvoices()));
                
                for (String lLine : lLines) {
                 
                    String[] parts = lLine.split(",");
                  
                    int invNum = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int price = Integer.parseInt(parts[2]);
                    int count = Integer.parseInt(parts[3]);
                    InvoiceHeaderModel invoice = frame.getInvoiceByNum(invNum);
                    InvoiceItemModel item = new InvoiceItemModel(name, price, count, invoice);
                }
                System.out.println("Check");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error while loading files", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<String> readFile(File f) throws IOException {
        List<String> lines = new ArrayList<>();

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

       private void newInvoice() {
    }

    private void deleteInvoice() {
    }

    private void newLine() {
    }

    private void deleteLine() {
    }

    private void save() {
    }

}
