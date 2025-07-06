import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SchoolRegister extends JFrame {
    private static final int SLOT_COUNT = 29;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton addButton, removeButton, editButton, saveButton, loadButton;

    public SchoolRegister() {
        setTitle("School Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        // Column names: Student Name + 29 slots
        String[] columns = new String[SLOT_COUNT + 1];
        columns[0] = "Student Name";
        for (int i = 1; i <= SLOT_COUNT; i++) {
            columns[i] = "Slot " + i;
        }

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return String.class;
                return Boolean.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        addButton = new JButton("Add Student");
        removeButton = new JButton("Remove Student");
        editButton = new JButton("Edit Student Name");
        saveButton = new JButton("Save to CSV");
        loadButton = new JButton("Load from CSV");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addStudent());
        removeButton.addActionListener(e -> removeStudent());
        editButton.addActionListener(e -> editStudentName());
        saveButton.addActionListener(e -> saveToCSV());
        loadButton.addActionListener(e -> loadFromCSV());
    }

    private void addStudent() {
        String name = JOptionPane.showInputDialog(this, "Enter student name:");
        if (name != null && !name.trim().isEmpty()) {
            Object[] row = new Object[SLOT_COUNT + 1];
            row[0] = name.trim();
            for (int i = 1; i <= SLOT_COUNT; i++) row[i] = false;
            tableModel.addRow(row);
        }
    }

    private void removeStudent() {
        int row = table.getSelectedRow();
        if (row != -1) {
            tableModel.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "Select a student to remove.");
        }
    }

    private void editStudentName() {
        int row = table.getSelectedRow();
        if (row != -1) {
            String currentName = (String) tableModel.getValueAt(row, 0);
            String newName = JOptionPane.showInputDialog(this, "Edit student name:", currentName);
            if (newName != null && !newName.trim().isEmpty()) {
                tableModel.setValueAt(newName.trim(), row, 0);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a student to edit.");
        }
    }

    private void saveToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(file)) {
                // Write header
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    pw.print(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
                // Write rows
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        Object val = tableModel.getValueAt(r, c);
                        if (val instanceof Boolean) {
                            pw.print((Boolean) val ? "Present" : "Absent");
                        } else {
                            pw.print(val);
                        }
                        if (c < tableModel.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                }
                JOptionPane.showMessageDialog(this, "Saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    private void loadFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String header = br.readLine();
                if (header == null) throw new IOException("Empty file");
                tableModel.setRowCount(0);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    Object[] row = new Object[SLOT_COUNT + 1];
                    row[0] = parts[0];
                    for (int i = 1; i <= SLOT_COUNT; i++) {
                        if (i < parts.length) {
                            row[i] = "Present".equalsIgnoreCase(parts[i]);
                        } else {
                            row[i] = false;
                        }
                    }
                    tableModel.addRow(row);
                }
                JOptionPane.showMessageDialog(this, "Loaded successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SchoolRegister().setVisible(true));
    }
} 
