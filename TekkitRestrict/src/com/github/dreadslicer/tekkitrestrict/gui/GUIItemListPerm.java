package com.github.dreadslicer.tekkitrestrict.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.ListModel;

import com.github.dreadslicer.tekkitrestrict.ItemStack;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class GUIItemListPerm extends JDialog {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIItemListPerm frame = new GUIItemListPerm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	
	public GUIItemListPerm(JFrame Owner){
		super(Owner,true);
		init();
	}

	/**
	 * Create the frame.
	 */
	public String resultName = "";
	public List<String> resultTypes = new LinkedList<String>();
	public boolean resultready=false,done=false;
	public Object waitobject = new Object();
	private JTextField txtDefault;
	@SuppressWarnings("rawtypes")
	private JList Ilist;
	@SuppressWarnings({"rawtypes", "cast", "unchecked", "deprecation"})
	public void init() {
		final GUIItemListPerm THIS = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Permission node name");
		lblNewLabel.setBounds(12, 12, 180, 15);
		contentPane.add(lblNewLabel);
		
		txtDefault = new JTextField();
		//txtDefault.setText("default");
		txtDefault.setText(resultName);
		txtDefault.setBounds(22, 27, 170, 19);
		contentPane.add(txtDefault);
		txtDefault.setColumns(10);
		
		JLabel lblNodeItems = new JLabel("Node Items");
		lblNodeItems.setBounds(12, 50, 180, 15);
		contentPane.add(lblNodeItems);
		
		Ilist = new JList();
		Ilist.setBounds(22, 66, 402, 153);
		DefaultListModel dlm = new DefaultListModel();
		Ilist.setModel(dlm);
		contentPane.add(Ilist);
		
		JButton btnAdd = new JButton("Done");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultTypes.clear();
				THIS.resultName = THIS.txtDefault.getText();
				//THIS.resultTypes
				DefaultListModel Jmodelx = ((DefaultListModel) ((ListModel)Ilist.getModel()));
				for(int i=0;i<Jmodelx.size();i++){
					String x = (String)Jmodelx.get(i);
					resultTypes.add(x);
				}
				THIS.resultready = true;
				THIS.hide();
				THIS.dispose();
			}
		});
		btnAdd.setFont(new Font("Dialog", Font.BOLD, 12));
		btnAdd.setBounds(353, 231, 71, 21);
		contentPane.add(btnAdd);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				THIS.dispose();
			}
		});
		btnCancel.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCancel.setBounds(260, 231, 81, 21);
		contentPane.add(btnCancel);
		
		JButton btnAdd_1 = new JButton("Add");
		btnAdd_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//JTextField textField = new JTextField(10);
				//String msgString1 = "blah";
		        //String msgString2 = "blah2";
		        String s = (String)JOptionPane.showInputDialog("Item(s) - Ranges can be denoted by using a '-'");
				
		        @SuppressWarnings("unused")
				ItemStack[] gg = TRNoItem.gettRangedItemValues(s);
				
				DefaultListModel Jmodelx = ((DefaultListModel) ((ListModel)Ilist.getModel()));
				Jmodelx.addElement(s);
			}
		});
		btnAdd_1.setFont(new Font("Dialog", Font.BOLD, 12));
		btnAdd_1.setBounds(12, 231, 61, 21);
		contentPane.add(btnAdd_1);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultListModel Jmodelx = ((DefaultListModel) ((ListModel)Ilist.getModel()));
				int index = Ilist.getSelectedIndex();
				if(index != -1)
					Jmodelx.remove(index);
			}
		});
		btnRemove.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRemove.setBounds(74, 231, 89, 21);
		contentPane.add(btnRemove);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void refElem(){
		DefaultListModel dlm = (DefaultListModel)Ilist.getModel();
		for(String a:this.resultTypes) dlm.addElement(a);
		txtDefault.setText(resultName);
	}
}
