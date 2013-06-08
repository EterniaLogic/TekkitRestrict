package com.github.dreadslicer.tekkitrestrict;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JMenu;
import javax.swing.JSplitPane;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import com.github.dreadslicer.tekkitrestrict.gui.GUIItemListPerm;
import com.github.dreadslicer.tekkitrestrict.gui.GUIItemListPermLimit;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import java.awt.Font;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import javax.swing.JMenuBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import java.util.LinkedList;
import java.util.List;

import javax.swing.UIManager;

public class TRConfigGUI {

	private JFrame frmTekkitRestrictConfiguration;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TRConfigGUI window = new TRConfigGUI();
					window.frmTekkitRestrictConfiguration.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TRConfigGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	int isnew = 1;
	private JTable table;
	private JTable table_1;
	private JTable table_2;
	private JTable table_3;
	private JTable table_4;
	private JTable table_5;
	@SuppressWarnings("serial")
	private void initialize() {
		frmTekkitRestrictConfiguration = new JFrame();
		frmTekkitRestrictConfiguration.setResizable(false);
		
		frmTekkitRestrictConfiguration.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			@Override
			public void ancestorResized(HierarchyEvent hierarchyevent) {
				if(isnew <= 0)
					frmTekkitRestrictConfiguration.setBounds(100, 100, 636, 427);
				else isnew--;
			}
		});
		frmTekkitRestrictConfiguration.setForeground(SystemColor.textText);
		frmTekkitRestrictConfiguration.getContentPane().setBackground(new Color(153, 153, 153));
		frmTekkitRestrictConfiguration.setBackground(SystemColor.controlDkShadow);
		frmTekkitRestrictConfiguration.setTitle("Tekkit Restrict Configuration");
		frmTekkitRestrictConfiguration.setBounds(100, 100, 636, 427);
		
		//frmTekkitRestrictConfiguration.setMinBounds(100, 100, 636, 427);
		frmTekkitRestrictConfiguration.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTekkitRestrictConfiguration.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setForeground(new Color(0, 204, 255));
		panel.setBackground(new Color(204, 204, 204));
		panel.setBounds(0, 0, 622, 24);
		frmTekkitRestrictConfiguration.getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JMenuBar menuBar = new JMenuBar();
		panel.add(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		mnNewMenu.setBackground(new Color(0, 0, 51));
		mnNewMenu.setForeground(new Color(0, 0, 102));
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnNewMenu.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnNewMenu.add(mntmSave);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnNewMenu.add(mntmExit);
		
		JMenu mnNewMenu_1 = new JMenu("Help");
		menuBar.add(mnNewMenu_1);
		mnNewMenu_1.setBackground(new Color(0, 0, 51));
		mnNewMenu_1.setForeground(new Color(0, 0, 102));
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setForeground(new Color(0, 153, 204));
		tabbedPane.setBackground(new Color(51, 51, 51));
		tabbedPane.setBounds(0, 26, 622, 326);
		frmTekkitRestrictConfiguration.getContentPane().add(tabbedPane);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(204, 204, 204));
		panel_3.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("General", null, panel_3, null);
		panel_3.setLayout(null);
		
		JLabel lblEnabled = new JLabel("General");
		lblEnabled.setBackground(new Color(204, 204, 204));
		lblEnabled.setForeground(new Color(0, 0, 0));
		panel_3.add(lblEnabled);
		lblEnabled.setFont(new Font("Dialog", Font.BOLD, 12));
		lblEnabled.setBounds(19, 7, 70, 15);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("RPTimer Modifier");
		chckbxNewCheckBox.setBackground(new Color(204, 204, 204));
		chckbxNewCheckBox.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxNewCheckBox);
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setToolTipText("Modifies minimum time allowed for a Red Power Timer.");
		chckbxNewCheckBox.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxNewCheckBox.setBounds(29, 18, 144, 23);
		
		final JCheckBox chckbxBlockLimiter = new JCheckBox("Block Limiter");
		
		chckbxBlockLimiter.setBackground(new Color(204, 204, 204));
		chckbxBlockLimiter.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxBlockLimiter);
		chckbxBlockLimiter.setSelected(true);
		chckbxBlockLimiter.setToolTipText("Limits # of blocks allowed to place of that type.");
		chckbxBlockLimiter.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxBlockLimiter.setBounds(29, 57, 144, 23);
		
		final JCheckBox chckbxItemDisabler = new JCheckBox("Item Disabler");
		
		chckbxItemDisabler.setBackground(new Color(204, 204, 204));
		chckbxItemDisabler.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxItemDisabler);
		chckbxItemDisabler.setSelected(true);
		chckbxItemDisabler.setToolTipText("Disables a specific type");
		chckbxItemDisabler.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxItemDisabler.setBounds(29, 77, 144, 23);
		
		JCheckBox chckbxOpenalc = new JCheckBox("OpenAlc Command");
		chckbxOpenalc.setBackground(new Color(204, 204, 204));
		chckbxOpenalc.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxOpenalc);
		chckbxOpenalc.setSelected(true);
		chckbxOpenalc.setToolTipText("Enables the use of \"/openalc [player] [color]\" for admins that have the tekkitrestrict.alc OR tekkitrestrict.admin permission.");
		chckbxOpenalc.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxOpenalc.setBounds(29, 97, 144, 23);
		
		final JCheckBox chckbxSafezones = new JCheckBox("SafeZones");
		
		chckbxSafezones.setBackground(new Color(204, 204, 204));
		chckbxSafezones.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxSafezones);
		chckbxSafezones.setSelected(true);
		chckbxSafezones.setToolTipText("Layered over WorldGuard, Factions, PreciousStones or GriefPrevention zones. Prevents griefing using Certain Tekkit-based items.");
		chckbxSafezones.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxSafezones.setBounds(29, 37, 144, 23);
		
		JLabel lblHacks = new JLabel("Anti-Hacks");
		lblHacks.setBackground(new Color(204, 204, 204));
		lblHacks.setForeground(new Color(0, 0, 0));
		panel_3.add(lblHacks);
		lblHacks.setBounds(249, 7, 101, 15);
		
		JCheckBox chckbxFlyHack = new JCheckBox("Fly Hack");
		chckbxFlyHack.setBackground(new Color(204, 204, 204));
		chckbxFlyHack.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxFlyHack);
		chckbxFlyHack.setSelected(true);
		chckbxFlyHack.setToolTipText("Prevents a player from flying while not using a ring or jetpack.");
		chckbxFlyHack.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxFlyHack.setBounds(259, 19, 144, 23);
		
		JCheckBox chckbxMovementSpeedHack = new JCheckBox("Movement Speed Hack");
		chckbxMovementSpeedHack.setBackground(new Color(204, 204, 204));
		chckbxMovementSpeedHack.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxMovementSpeedHack);
		chckbxMovementSpeedHack.setSelected(true);
		chckbxMovementSpeedHack.setToolTipText("Prevents a player from moving too fast and lagging the server.");
		chckbxMovementSpeedHack.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxMovementSpeedHack.setBounds(259, 37, 168, 23);
		
		JCheckBox chckbxForcefieldHack = new JCheckBox("Forcefield Hack");
		chckbxForcefieldHack.setBackground(new Color(204, 204, 204));
		chckbxForcefieldHack.setForeground(new Color(0, 0, 0));
		panel_3.add(chckbxForcefieldHack);
		chckbxForcefieldHack.setSelected(true);
		chckbxForcefieldHack.setToolTipText("Prevents a player from hitting the sorrounding mobs/players while not facing them.");
		chckbxForcefieldHack.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxForcefieldHack.setBounds(259, 56, 181, 23);
		
		JLabel lblAntidupes = new JLabel("Anti-Dupes");
		lblAntidupes.setBackground(new Color(204, 204, 204));
		lblAntidupes.setForeground(new Color(0, 0, 0));
		lblAntidupes.setBounds(251, 90, 101, 15);
		panel_3.add(lblAntidupes);
		
		JCheckBox chckbxRmFurnaceDupe = new JCheckBox("RM Furnace Dupe");
		chckbxRmFurnaceDupe.setBackground(new Color(204, 204, 204));
		chckbxRmFurnaceDupe.setForeground(new Color(0, 0, 0));
		chckbxRmFurnaceDupe.setToolTipText("Prevents a player from duping with the RM Furnace.");
		chckbxRmFurnaceDupe.setSelected(true);
		chckbxRmFurnaceDupe.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxRmFurnaceDupe.setBounds(259, 103, 144, 23);
		panel_3.add(chckbxRmFurnaceDupe);
		
		JCheckBox chckbxAlchemyBagDupe = new JCheckBox("Alchemy Bag Dupe");
		chckbxAlchemyBagDupe.setBackground(new Color(204, 204, 204));
		chckbxAlchemyBagDupe.setForeground(new Color(0, 0, 0));
		chckbxAlchemyBagDupe.setToolTipText("Prevents a player from throwing items out of their inventory/bag when they have a Void Ring or BlackHoleBand in their Alchemy Bag.");
		chckbxAlchemyBagDupe.setSelected(true);
		chckbxAlchemyBagDupe.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxAlchemyBagDupe.setBounds(259, 121, 168, 23);
		panel_3.add(chckbxAlchemyBagDupe);
		
		JCheckBox chckbxTransmutationDupe = new JCheckBox("Transmutation Dupe");
		chckbxTransmutationDupe.setBackground(new Color(204, 204, 204));
		chckbxTransmutationDupe.setForeground(new Color(0, 0, 0));
		chckbxTransmutationDupe.setToolTipText("Prevents player from Shift+Clicking an item out of the Transmutation table's input slots.");
		chckbxTransmutationDupe.setSelected(true);
		chckbxTransmutationDupe.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxTransmutationDupe.setBounds(259, 140, 168, 23);
		panel_3.add(chckbxTransmutationDupe);
		
		JCheckBox chckbxProjectTableDupe = new JCheckBox("Project Table Dupe");
		chckbxProjectTableDupe.setBackground(new Color(204, 204, 204));
		chckbxProjectTableDupe.setForeground(new Color(0, 0, 0));
		chckbxProjectTableDupe.setToolTipText("Prevent a player from duping using a project table.");
		chckbxProjectTableDupe.setSelected(true);
		chckbxProjectTableDupe.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxProjectTableDupe.setBounds(259, 160, 200, 23);
		panel_3.add(chckbxProjectTableDupe);
		
		JCheckBox chckbxTankCartDupe = new JCheckBox("Tank Cart dupe");
		chckbxTankCartDupe.setBackground(new Color(204, 204, 204));
		chckbxTankCartDupe.setForeground(new Color(0, 0, 0));
		chckbxTankCartDupe.setToolTipText("Prevent a player from duping using a Tank Cart.");
		chckbxTankCartDupe.setSelected(true);
		chckbxTankCartDupe.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxTankCartDupe.setBounds(259, 180, 200, 23);
		panel_3.add(chckbxTankCartDupe);
		
		JLabel lblPerformance = new JLabel("Performance");
		lblPerformance.setBackground(new Color(204, 204, 204));
		lblPerformance.setForeground(new Color(0, 0, 0));
		lblPerformance.setBounds(17, 143, 101, 15);
		panel_3.add(lblPerformance);
		
		JCheckBox chckbxToggleInvThread = new JCheckBox("Throtte Inventory Thread");
		chckbxToggleInvThread.setBackground(new Color(204, 204, 204));
		chckbxToggleInvThread.setForeground(new Color(0, 0, 0));
		chckbxToggleInvThread.setToolTipText("Prevents a player from hitting the sorrounding mobs/players while not facing them.");
		chckbxToggleInvThread.setSelected(true);
		chckbxToggleInvThread.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxToggleInvThread.setBounds(27, 178, 200, 23);
		panel_3.add(chckbxToggleInvThread);
		
		JCheckBox chckbxAutomaticallyUnloadChunks = new JCheckBox("Chunk Unloader");
		chckbxAutomaticallyUnloadChunks.setBackground(new Color(204, 204, 204));
		chckbxAutomaticallyUnloadChunks.setForeground(new Color(0, 0, 0));
		chckbxAutomaticallyUnloadChunks.setToolTipText("*Performance Issues* Unloads chunks from your server.");
		chckbxAutomaticallyUnloadChunks.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxAutomaticallyUnloadChunks.setBounds(27, 158, 155, 23);
		panel_3.add(chckbxAutomaticallyUnloadChunks);
		
		JLabel lblEe = new JLabel("Gem Armor");
		lblEe.setBackground(new Color(204, 204, 204));
		lblEe.setForeground(new Color(0, 0, 0));
		lblEe.setBounds(17, 205, 101, 15);
		panel_3.add(lblEe);
		
		JCheckBox chckbxDisableoffensiveMode = new JCheckBox("Disable \"Offensive Mode\"");
		chckbxDisableoffensiveMode.setBackground(new Color(204, 204, 204));
		chckbxDisableoffensiveMode.setForeground(new Color(0, 0, 0));
		chckbxDisableoffensiveMode.setSelected(true);
		chckbxDisableoffensiveMode.setToolTipText("*Performance Issues* Unloads chunks from your server.");
		chckbxDisableoffensiveMode.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxDisableoffensiveMode.setBounds(27, 223, 216, 23);
		panel_3.add(chckbxDisableoffensiveMode);
		
		JCheckBox chckbxDisablemovementMode = new JCheckBox("Disable \"Movement Mode\"");
		chckbxDisablemovementMode.setBackground(new Color(204, 204, 204));
		chckbxDisablemovementMode.setForeground(new Color(0, 0, 0));
		chckbxDisablemovementMode.setToolTipText("Prevents a player from hitting the sorrounding mobs/players while not facing them.");
		chckbxDisablemovementMode.setSelected(true);
		chckbxDisablemovementMode.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxDisablemovementMode.setBounds(27, 243, 200, 23);
		panel_3.add(chckbxDisablemovementMode);
		
		final JCheckBox chckbxLoggingEnabled = new JCheckBox("Logging Enabled");
		
		
		chckbxLoggingEnabled.setBackground(new Color(204, 204, 204));
		chckbxLoggingEnabled.setForeground(new Color(0, 0, 0));
		chckbxLoggingEnabled.setToolTipText("Prevents a player from duping with the RM Furnace.");
		chckbxLoggingEnabled.setSelected(true);
		chckbxLoggingEnabled.setFont(new Font("Dialog", Font.PLAIN, 12));
		chckbxLoggingEnabled.setBounds(29, 117, 144, 23);
		panel_3.add(chckbxLoggingEnabled);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(204, 204, 204));
		panel_4.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Micro Permissions", null, panel_4, null);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
		panel_4.add(tabbedPane_2);
		
		JPanel panel_18 = new JPanel();
		tabbedPane_2.addTab("Disable Item", null, panel_18, null);
		panel_18.setLayout(null);
		
		table = new JTable();
		table.setBounds(12, 40, 588, 168);
		panel_18.add(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"default", ""},
			},
			new String[] {
				"New column", "New column"
			}
		) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, Object.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(124);
		table.getColumnModel().getColumn(0).setMaxWidth(124);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				int col = table.getSelectedColumn();
				int row = table.getSelectedRow();
				if(row != -1 && col != 0){
					try {
						
						GUIItemListPerm frame = new GUIItemListPerm(frmTekkitRestrictConfiguration);
						
						try{frame.resultName = (String)table.getValueAt(row, 0);}catch(Exception e){}
						try{
							String read = (String)table.getValueAt(row, 1);
							if(read.contains(";")){
								List<String> res = new LinkedList<String>();
								for(String a:read.split(";")) res.add(a);
								frame.resultTypes = res;
							}else if(read != "null") frame.resultTypes.add(read);
						}catch(Exception e){}
						frame.refElem();
						frame.setVisible(true);
						if(frame.resultready){
							table.setValueAt(frame.resultName, row, 0);
							String resTypes = "";
							for(int i=0;i<frame.resultTypes.size();i++){
								String tt3 = ";";
								if(i>=frame.resultTypes.size()-1) tt3="";
								String tt = frame.resultTypes.get(i).toString() + tt3;
								if(frame.resultTypes.get(i).toString() != "")
									resTypes+=tt;
							}
							table.setValueAt(resTypes, row, 1);
						}
						frame.resultTypes.clear();
						frame.dispose();
						//table.setValueAt(frame.result, table.getSelectedRow(), col);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"default",""}
			},
			new String[] {
				"New column", "New column"
			}
		));
		
		table_1 = new JTable();
		table_1.setBounds(12, 12, 588, 16);
		panel_18.add(table_1);
		table_1.setForeground(new Color(255, 255, 255));
		table_1.setBackground(new Color(153, 153, 153));
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
				{"Permission name", "Item IDs"},
			},
			new String[] {
				"New column", "New column"
			}
		));
		table_1.getColumnModel().getColumn(0).setPreferredWidth(124);
		table_1.getColumnModel().getColumn(0).setMaxWidth(124);
		table_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JLabel lblTekkitrestrictnoitem = new JLabel("tekkitrestrict.noitem.");
		lblTekkitrestrictnoitem.setBounds(-322, 47, 152, 15);
		panel_18.add(lblTekkitrestrictnoitem);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel xx = (DefaultTableModel)table.getModel();
				
				GUIItemListPerm frame = new GUIItemListPerm(frmTekkitRestrictConfiguration);
				frame.resultName="default"; 
				frame.setVisible(true);
				if(frame.resultready){
					String resTypes = "";
					for(int i=0;i<frame.resultTypes.size();i++){
						String tt3 = ";";
						if(i>=frame.resultTypes.size()-1) tt3="";
						String tt = frame.resultTypes.get(i).toString() + tt3;
						resTypes+=tt;
					}
					xx.addRow(new Object[]{frame.resultName,resTypes});
				}
				table.setModel(xx);
				frame.dispose();
			}
		});
		btnAdd.setBounds(22, 220, 61, 21);
		panel_18.add(btnAdd);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel Jmodelx = ((DefaultTableModel) table.getModel());
				int index = table.getSelectedRow();
				if(index != -1)
					Jmodelx.removeRow(index);
			}
		});
		btnRemove.setBounds(95, 220, 95, 21);
		panel_18.add(btnRemove);
		
		JPanel panel_17 = new JPanel();
		tabbedPane_2.addTab("Limit Blocks", null, panel_17, null);
		panel_17.setLayout(null);
		
		table_2 = new JTable();
		table_2.setModel(new DefaultTableModel(
			new Object[][] {
				{"Permission Name", "Item IDs", "Limit"},
			},
			new String[] {
				"New column", "New column", "New column"
			}
		));
		table_2.getColumnModel().getColumn(0).setPreferredWidth(117);
		table_2.getColumnModel().getColumn(0).setMaxWidth(117);
		table_2.getColumnModel().getColumn(1).setPreferredWidth(383);
		table_2.getColumnModel().getColumn(2).setMaxWidth(75);
		table_2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_2.setForeground(Color.WHITE);
		table_2.setBackground(UIManager.getColor("Button.disabledText"));
		table_2.setBounds(12, 12, 578, 16);
		panel_17.add(table_2);
		
		table_3 = new JTable();
		table_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				int col = table_3.getSelectedColumn();
				int row = table_3.getSelectedRow();
				if(row != -1 && col != 0){
					try {
						//System.out.println(row+",col "+col);
						GUIItemListPermLimit frame = new GUIItemListPermLimit(frmTekkitRestrictConfiguration);
						try{frame.resultName = (String)table_3.getValueAt(row, 0);}catch(Exception e){}
						try{
							String read = (String)table_3.getValueAt(row, 1);
							if(read.contains(";")){
								List<String> res = new LinkedList<String>();
								for(String a:read.split(";")) res.add(a);
								frame.resultTypes = res;
							}else if(read != "null") frame.resultTypes.add(read);
						}catch(Exception e){}
						try{frame.resultLimit = (String)table_3.getValueAt(row, 2);}catch(Exception e){}
						frame.refElem();
						frame.setVisible(true);
						if(frame.resultready){
							table_3.setValueAt(frame.resultName, row, 0);
							String resTypes = "";
							for(int i=0;i<frame.resultTypes.size();i++){
								String tt3 = ";";
								if(i>=frame.resultTypes.size()-1) tt3="";
								String tt = frame.resultTypes.get(i).toString() + tt3;
								if(frame.resultTypes.get(i).toString() != "")
									resTypes+=tt;
							}
							table_3.setValueAt(resTypes, row, 1);
							table_3.setValueAt(frame.resultLimit, row, 2);
						}
						frame.dispose();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		table_3.setModel(new DefaultTableModel(
			new Object[][] {
				{"default", "", "5"},
			},
			new String[] {
				"New column", "New column", "New column"
			}
		));
		table_3.getColumnModel().getColumn(0).setPreferredWidth(117);
		table_3.getColumnModel().getColumn(0).setMaxWidth(117);
		table_3.getColumnModel().getColumn(1).setPreferredWidth(260);
		table_3.getColumnModel().getColumn(2).setMaxWidth(117);
		table_3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_3.setBounds(12, 37, 578, 172);
		panel_17.add(table_3);
		
		JButton button = new JButton("Add");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel xx = (DefaultTableModel)table_3.getModel();
				
				GUIItemListPermLimit frame = new GUIItemListPermLimit(frmTekkitRestrictConfiguration);
				frame.resultName="default"; 
				frame.resultLimit = "10";
				frame.setVisible(true);
				if(frame.resultready){
					String resTypes = "";
					for(int i=0;i<frame.resultTypes.size();i++){
						String tt3 = ";";
						if(i>=frame.resultTypes.size()-1) tt3="";
						String tt = frame.resultTypes.get(i).toString() + tt3;
						resTypes+=tt;
					}
					xx.addRow(new Object[]{frame.resultName,resTypes,frame.resultLimit});
				}
				table_3.setModel(xx);
				frame.dispose();
			}
		});
		button.setBounds(22, 221, 61, 21);
		panel_17.add(button);
		
		JButton button_1 = new JButton("Remove");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel Jmodelx = ((DefaultTableModel) table_3.getModel());
				int index = table_3.getSelectedRow();
				if(index != -1)
					Jmodelx.removeRow(index);
			}
		});
		button_1.setBounds(95, 221, 95, 21);
		panel_17.add(button_1);
		
		JPanel panel_19 = new JPanel();
		tabbedPane_2.addTab("Limited Creative", null, panel_19, null);
		panel_19.setLayout(null);
		
		table_4 = new JTable();
		table_4.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_4.setForeground(Color.WHITE);
		table_4.setBackground(UIManager.getColor("Button.disabledText"));
		table_4.setBounds(12, 12, 588, 16);
		table_4.setModel(new DefaultTableModel(
				new Object[][] {
						{"Permission name", "Item IDs"},
					},
					new String[] {
						"New column", "New column"
					}
				));
		panel_19.add(table_4);
		
		table_5 = new JTable();
		table_5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				int col = table_5.getSelectedColumn();
				int row = table_5.getSelectedRow();
				if(row != -1 && col != 0){
					try {
						//System.out.println(row+",col "+col);
						GUIItemListPerm frame = new GUIItemListPerm(frmTekkitRestrictConfiguration);
						frame.setName("");
						try{frame.resultName = (String)table_5.getValueAt(row, 0);}catch(Exception e){}
						try{
							String read = (String)table_5.getValueAt(row, 1);
							if(read.contains(";")){
								List<String> res = new LinkedList<String>();
								for(String a:read.split(";")) res.add(a);
								frame.resultTypes = res;
							}else if(read != "null") frame.resultTypes.add(read);
						}catch(Exception e){}
						frame.refElem();
						frame.setVisible(true);
						if(frame.resultready){
							table_5.setValueAt(frame.resultName, row, 0);
							String resTypes = "";
							for(int i=0;i<frame.resultTypes.size();i++){
								String tt3 = ";";
								if(i>=frame.resultTypes.size()-1) tt3="";
								String tt = frame.resultTypes.get(i).toString() + tt3;
								if(frame.resultTypes.get(i).toString() != "")
									resTypes+=tt;
							}
							table_5.setValueAt(resTypes, row, 1);
							//DefaultListModel Jmodelx = ((DefaultListModel) ((ListModel)table.getModel()));
						}
						frame.resultTypes.clear();
						frame.dispose();
						//table.setValueAt(frame.result, table.getSelectedRow(), col);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		table_5.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_5.setBounds(12, 37, 588, 172);
		table_5.setModel(new DefaultTableModel(
				new Object[][] {
						{"default", ""},
					},
					new String[] {
						"New column", "New column"
					}
				));
		panel_19.add(table_5);
		
		JButton button_2 = new JButton("Add");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel xx = (DefaultTableModel)table_5.getModel();
				
				GUIItemListPerm frame = new GUIItemListPerm(frmTekkitRestrictConfiguration);
				frame.resultName="default"; 
				frame.setVisible(true);
				if(frame.resultready){
					String resTypes = "";
					for(int i=0;i<frame.resultTypes.size();i++){
						String tt3 = ";";
						if(i>=frame.resultTypes.size()-1) tt3="";
						String tt = frame.resultTypes.get(i).toString() + tt3;
						resTypes+=tt;
					}
					xx.addRow(new Object[]{frame.resultName,resTypes});
				}
				table_5.setModel(xx);
				frame.dispose();
			}
		});
		button_2.setBounds(22, 221, 61, 21);
		panel_19.add(button_2);
		
		JButton button_3 = new JButton("Remove");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel Jmodelx = ((DefaultTableModel) table_5.getModel());
				int index = table_5.getSelectedRow();
				if(index != -1)
					Jmodelx.removeRow(index);
			}
		});
		button_3.setBounds(95, 221, 95, 21);
		panel_19.add(button_3);
		
		JPanel panel_13 = new JPanel();
		panel_13.setBackground(new Color(204, 204, 204));
		panel_13.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("SafeZones", null, panel_13, null);
		panel_13.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Safe Zone Options");
		lblNewLabel_1.setBounds(12, 12, 144, 15);
		panel_13.add(lblNewLabel_1);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Remove Mobs");
		chckbxNewCheckBox_1.setBackground(new Color(204, 204, 204));
		chckbxNewCheckBox_1.setSelected(true);
		chckbxNewCheckBox_1.setBounds(22, 32, 129, 23);
		panel_13.add(chckbxNewCheckBox_1);
		
		JCheckBox chckbxDischargeEePower = new JCheckBox("Discharge EE Power Items");
		chckbxDischargeEePower.setSelected(true);
		chckbxDischargeEePower.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		chckbxDischargeEePower.setBounds(22, 50, 217, 23);
		panel_13.add(chckbxDischargeEePower);
		
		JCheckBox chckbxDisableRingOf = new JCheckBox("Disable Ring of Arcana");
		chckbxDisableRingOf.setSelected(true);
		chckbxDisableRingOf.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		chckbxDisableRingOf.setBounds(22, 70, 217, 23);
		panel_13.add(chckbxDisableRingOf);
		
		JCheckBox chckbxDisableFlying = new JCheckBox("Disable Flying");
		chckbxDisableFlying.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		chckbxDisableFlying.setBounds(22, 88, 217, 23);
		panel_13.add(chckbxDisableFlying);
		
		JLabel lblNewLabel_2 = new JLabel("Enabled plugins");
		lblNewLabel_2.setBounds(280, 12, 119, 15);
		panel_13.add(lblNewLabel_2);
		
		JCheckBox chckbxGriefPrevention = new JCheckBox("Grief Prevention");
		chckbxGriefPrevention.setSelected(true);
		chckbxGriefPrevention.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		chckbxGriefPrevention.setBounds(290, 32, 217, 23);
		panel_13.add(chckbxGriefPrevention);
		
		JCheckBox chckbxFactions = new JCheckBox("Factions");
		chckbxFactions.setSelected(true);
		chckbxFactions.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		chckbxFactions.setBounds(290, 50, 217, 23);
		panel_13.add(chckbxFactions);
		
		JCheckBox chckbxDisableRingOf_1 = new JCheckBox("Towny");
		chckbxDisableRingOf_1.setSelected(true);
		chckbxDisableRingOf_1.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		chckbxDisableRingOf_1.setBounds(290, 70, 217, 23);
		panel_13.add(chckbxDisableRingOf_1);
		
		JCheckBox chckbxPreciousstones = new JCheckBox("PreciousStones");
		chckbxPreciousstones.setSelected(true);
		chckbxPreciousstones.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		chckbxPreciousstones.setBounds(290, 88, 217, 23);
		panel_13.add(chckbxPreciousstones);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(204, 204, 204));
		panel_2.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Disable Clicks", null, panel_2, null);
		
		final JPanel panel_10 = new JPanel();
		panel_10.setBackground(new Color(204, 204, 204));
		panel_10.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Disable Items", null, panel_10, null);
		
		final JPanel panel_11 = new JPanel();
		panel_11.setBackground(new Color(204, 204, 204));
		panel_11.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Limit Blocks", null, panel_11, null);
		
		
		JPanel panel_8 = new JPanel();
		panel_8.setBackground(new Color(0, 0, 0));
		panel_8.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Mods", null, panel_8, null);
		panel_8.setLayout(new BoxLayout(panel_8, BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		panel_8.add(tabbedPane_1);
		
		JPanel panel_12 = new JPanel();
		tabbedPane_1.addTab("Set EMC", null, panel_12, null);
		panel_12.setLayout(null);
		
		JPanel panel_14 = new JPanel();
		tabbedPane_1.addTab("Max EU", null, panel_14, null);
		panel_14.setLayout(null);
		
		JPanel panel_15 = new JPanel();
		tabbedPane_1.addTab("Max EE Charge", null, panel_15, null);
		panel_15.setLayout(null);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(new Color(204, 204, 204));
		panel_7.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Limited Creative", null, panel_7, null);
		
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setBackground(new Color(204, 204, 204));
		splitPane.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Logging", null, splitPane, null);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(new Color(204, 204, 204));
		panel_5.setForeground(new Color(0, 0, 0));
		splitPane.setRightComponent(panel_5);
		panel_5.setLayout(null);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(new Color(204, 204, 204));
		panel_6.setForeground(new Color(0, 0, 0));
		splitPane.setLeftComponent(panel_6);
		panel_6.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBackground(new Color(204, 204, 204));
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setBounds(0, 0, 70, 15);
		panel_6.add(lblNewLabel);
		
		JPanel panel_9 = new JPanel();
		panel_9.setBackground(new Color(204, 204, 204));
		panel_9.setForeground(new Color(0, 0, 0));
		tabbedPane.addTab("Performance", null, panel_9, null);
		
		JPanel panel_16 = new JPanel();
		tabbedPane.addTab("[Plugins]", null, panel_16, null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(153, 153, 153));
		panel_1.setBounds(0, 355, 622, 36);
		frmTekkitRestrictConfiguration.getContentPane().add(panel_1);
		
		JLabel label = new JLabel("");
		
		JButton btnNewButton = new JButton("Save");
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		panel_1.add(label);
		
		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				System.exit(0);
			}
		});
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		panel_1.add(btnNewButton_1);
		panel_1.add(btnNewButton);
		
		chckbxSafezones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				tabbedPane.setEnabledAt(3, chckbxSafezones.isSelected());
			}
		});
		
		chckbxItemDisabler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				tabbedPane.setEnabledAt(4, chckbxItemDisabler.isSelected());
			}
		});
		
		chckbxBlockLimiter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				tabbedPane.setEnabledAt(4, chckbxBlockLimiter.isSelected());
			}
		});
		
		
		
		chckbxLoggingEnabled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				tabbedPane.setEnabledAt(8, chckbxLoggingEnabled.isSelected());
			}
		});
		
	}

	@SuppressWarnings("unused")
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
