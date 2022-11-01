package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import view.InvoiceHeaderDialog;
import view.InvoiceLineDialog;
import view.Form;

public class Controller implements ActionListener, ListSelectionListener {

    private Form frame;
    private InvoiceHeaderDialog invoiceDialog;
    private InvoiceLineDialog lineDialog;

    public Controller(Form frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();

        if ("New Invoice".equals(ac)) {
            newInvoice();
        } else if ("Delete Invoice".equals(ac)) {
            deleteInvoice();
        } else if ("New Line".equals(ac)) {
            newLine();
        } else if ("Delete Line".equals(ac)) {
            deleteLine();
        } else if ("Load".equals(ac)) {
            load(null, null);
        } else if ("Save".equals(ac)) {
            save();
        } else if ("newInvoiceOK".equals(ac)) {
            newInvoiceOK();
        } else if ("newInvoiceCancel".equals(ac)) {
            newInvoiceCancel();
        } else if ("newLineOK".equals(ac)) {
            newLineOK();
        } else if ("newLineCancel".equals(ac)) {
            newLineCancel();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow != -1) {
            InvoiceHeaderModel selectedInv = frame.getInvoices().get(selectedRow);
            frame.getCustomerNameArea().setText(selectedInv.getName());
            frame.getInvDateArea().setText(frame.df.format(selectedInv.getDate()));
            frame.getInvNumArea().setText("" + selectedInv.getNum());
            frame.getInvTotalArea().setText("" + selectedInv.getTotal());
            frame.setItemTableModel(new ItemTableModel(selectedInv.getItems()));
        } else {
            frame.getCustomerNameArea().setText("");
            frame.getInvDateArea().setText("");
            frame.getInvNumArea().setText("");
            frame.getInvTotalArea().setText("");
            frame.setItemTableModel(new ItemTableModel());
        }
    }

    private void newInvoice() {
        invoiceDialog = new InvoiceHeaderDialog(frame);
        invoiceDialog.setVisible(true);
    }

    private void newInvoiceOK() {
        String dateStr = invoiceDialog.getInvDateField().getText();
        String name = invoiceDialog.getCustNameField().getText();
        try {
            Date date = frame.df.parse(dateStr);
            int num = frame.getNextInvNum();
            InvoiceHeaderModel invoice = new InvoiceHeaderModel(num, name, date);
            frame.getInvoices().add(invoice);
            frame.getHeaderTableModel().fireTableDataChanged();
            newInvoiceCancel();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(frame, "Error in date expression", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void newInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
    }

    private void deleteInvoice() {
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow == -1) {

        } else {
            frame.getInvoices().remove(selectedRow);
            frame.getHeaderTableModel().fireTableDataChanged();
        }
    }

    private void newLine() {
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow == -1) {

        } else {
            lineDialog = new InvoiceLineDialog(frame);
            lineDialog.setVisible(true);
        }
    }

    private void newLineOK() {
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow == -1) {

        } else {
            String name = lineDialog.getItemNameField().getText();
            String countStr = lineDialog.getItemCountField().getText();
            String priceStr = lineDialog.getItemPriceField().getText();
            try {
                int count = Integer.parseInt(countStr);
                int price = Integer.parseInt(priceStr);
                InvoiceHeaderModel invoice = frame.getInvoices().get(selectedRow);
                InvoiceItemModel item = new InvoiceItemModel(name, price, count, invoice);
                frame.getHeaderTableModel().fireTableDataChanged();
                frame.getInvoicesTable().setRowSelectionInterval(selectedRow, selectedRow);
                newLineCancel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Error in number format", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void newLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

    private void deleteLine() {
        int selectedInvoiceIndex = frame.getInvoicesTable().getSelectedRow();
        int selectedItemIndex = frame.getLinesTable().getSelectedRow();
        if (selectedInvoiceIndex != -1 && selectedItemIndex != -1) {
            frame.getInvoices().get(selectedInvoiceIndex).getItems().remove(selectedItemIndex);
            frame.getHeaderTableModel().fireTableDataChanged();
            frame.getInvoicesTable().setRowSelectionInterval(selectedInvoiceIndex, selectedInvoiceIndex);
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
                frame.getInvoices().clear();
                for (String hLine : hLines) {

                    String[] parts = hLine.split(",");
                    if (parts.length < 3) {
                        JOptionPane.showMessageDialog(frame, "Error in header format: " + hLine, "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    Date d = new Date();
                    int num = Integer.parseInt(parts[0]);
                    try {
                        d = Form.df.parse(parts[1]);
                    } catch (ParseException pex) {

                    }
                    String name = parts[2];
                    InvoiceHeaderModel inv = new InvoiceHeaderModel(num, name, d);
                    frame.getInvoices().add(inv);
                }
                frame.setHeaderTableModel(new HeaderTableModel(frame.getInvoices()));

                for (String lLine : lLines) {

                    String[] parts = lLine.split(",");
                    if (parts.length < 4) {
                        JOptionPane.showMessageDialog(frame, "Error in lines format: " + lLine, "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    int invNum = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int price = Integer.parseInt(parts[2]);
                    int count = Integer.parseInt(parts[3]);
                    InvoiceHeaderModel invoice = frame.getInvoiceByNum(invNum);
                    if (invoice != null) {
                        InvoiceItemModel item = new InvoiceItemModel(name, price, count, invoice);
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error while loading files: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<String> readFile(File f) throws FileNotFoundException , IOException {
        List<String> lines = new ArrayList<>();

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    private void save() {
        JFileChooser fc = new JFileChooser();
        File hFile = null;
        File lFile = null;
        JOptionPane.showMessageDialog(frame, "Select Header File", "Header File", JOptionPane.QUESTION_MESSAGE);
        int result = fc.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            hFile = fc.getSelectedFile();
            JOptionPane.showMessageDialog(frame, "Select Line File", "Line File", JOptionPane.QUESTION_MESSAGE);
            result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                lFile = fc.getSelectedFile();
            }
        }

        if (hFile != null && lFile != null) {
            String hData = "";
            String lData = "";
            for (InvoiceHeaderModel header : frame.getInvoices()) {
                hData += header.getAsCSV();
                hData += "\n";

                for (InvoiceItemModel item : header.getItems()) {
                    lData += item.getAsCSV();
                    lData += "\n";
                }
            }
            try {
                FileWriter hfw = new FileWriter(hFile);
                FileWriter lfw = new FileWriter(lFile);
                hfw.write(hData);
                lfw.write(lData);
                hfw.flush();
                lfw.flush();
                hfw.close();
                lfw.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error while writing files: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
