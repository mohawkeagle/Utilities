package br.com.gmp.comps.list.dual;

import br.com.gmp.comps.list.dual.model.SortedListModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

/**
 *
 * @author kaciano
 */
public class DualListBox extends JPanel {

    private JList sourceList;
    private SortedListModel sourceListModel;
    private JList destList;
    private SortedListModel destListModel;
    private JButton addButton;
    private JButton removeButton;

    public DualListBox() {
        initScreen();
    }

    public void clearSourceListModel() {
        sourceListModel.clear();
    }

    public void clearDestinationListModel() {
        destListModel.clear();
    }

    public void addSourceElements(ListModel newValue) {
        fillListModel(sourceListModel, newValue);
    }

    public void setSourceElements(ListModel newValue) {
        clearSourceListModel();
        addSourceElements(newValue);
    }

    public void addDestinationElements(ListModel newValue) {
        fillListModel(destListModel, newValue);
    }

    private void fillListModel(SortedListModel model, ListModel newValues) {
        int size = newValues.getSize();
        for (int i = 0; i < size; i++) {
            model.add(newValues.getElementAt(i));
        }
    }

    public void addSourceElements(Object newValue[]) {
        fillListModel(sourceListModel, newValue);
    }

    public void setSourceElements(Object newValue[]) {
        clearSourceListModel();
        addSourceElements(newValue);
    }

    public void addDestinationElements(Object newValue[]) {
        fillListModel(destListModel, newValue);
    }

    private void fillListModel(SortedListModel model, Object newValues[]) {
        model.addAll(newValues);
    }

    private void clearSourceSelected() {
        Object selected[] = sourceList.getSelectedValues();
        for (int i = selected.length - 1; i >= 0; --i) {
            sourceListModel.removeElement(selected[i]);
        }
        sourceList.getSelectionModel().clearSelection();
    }

    private void clearDestinationSelected() {
        Object selected[] = destList.getSelectedValues();
        for (int i = selected.length - 1; i >= 0; --i) {
            destListModel.removeElement(selected[i]);
        }
        destList.getSelectionModel().clearSelection();
    }

    private void initScreen() {
        setLayout(new GridLayout(0, 2));
        sourceListModel = new SortedListModel();
        sourceList = new JList(sourceListModel);

        addButton = new JButton(">>");
        addButton.addActionListener(new AddListener());
        removeButton = new JButton("<<");
        removeButton.addActionListener(new RemoveListener());

        destListModel = new SortedListModel();
        destList = new JList(destListModel);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Available Elements:"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(sourceList), BorderLayout.CENTER);
        leftPanel.add(addButton, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());

        rightPanel.add(new JLabel("Selected Elements:"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(destList), BorderLayout.CENTER);
        rightPanel.add(removeButton, BorderLayout.SOUTH);

        add(leftPanel);
        add(rightPanel);
    }

    private class AddListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object selected[] = sourceList.getSelectedValues();
            addDestinationElements(selected);
            clearSourceSelected();
        }
    }

    private class RemoveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object selected[] = destList.getSelectedValues();
            addSourceElements(selected);
            clearDestinationSelected();
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("Dual List Box Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DualListBox dual = new DualListBox();
        dual.addSourceElements(new String[]{"One", "Two", "Three"});
        dual.addSourceElements(new String[]{"Four", "Five", "Six"});
        dual.addSourceElements(new String[]{"Seven", "Eight", "Nine"});
        dual.addSourceElements(new String[]{"Ten", "Eleven", "Twelve"});
        dual.addSourceElements(new String[]{"Thirteen", "Fourteen", "Fifteen"});
        dual.addSourceElements(new String[]{"Sixteen", "Seventeen", "Eighteen"});
        dual.addSourceElements(new String[]{"Nineteen", "Twenty", "Thirty"});
        frame.add(dual, BorderLayout.CENTER);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }
}
